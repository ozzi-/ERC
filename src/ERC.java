import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ERC {

	// (@import) url("http..
	private static Pattern cssURLPattern = Pattern.compile("@import(\\s)?(\\\"|'|\\(|url)(\\\"|'|\\()*([^;]*)(\\\"|')");
	// (xhr.)open("POST","http..
	private static Pattern jsXHRPattern = Pattern.compile("open(\\s)?\\((\\s)?(\\\"|')[a-zA-Z]*(\\\"|')(\\s)?,(\\s)?(\\\"|')([^;]*)(\\\"|')");
	
	private static boolean quietVal;
	private static boolean jsonOutput;
	private static ArrayList<String> errors = new ArrayList<String>();
	private static ArrayList<Touple> findings = new ArrayList<Touple>();
	
	private static StringBuilder jsonOutputB = new StringBuilder();
	private static boolean strictVal;

	public static void main(String[] args) throws MalformedURLException {
		
		
		jsonOutputB.append("{");
		
		Options options = new Options();
		
		Option input = new Option("u", "url", true, "URL to scan for external content");
		input.setRequired(true);
		options.addOption(input);

		Option suffix = new Option("j", "json", false, "output results as JSON");
		options.addOption(suffix);
		
		Option quiet = new Option("q", "quiet", false, "don't output further HTTP call notices");
		options.addOption(quiet);
		
		Option strict = new Option("s", "strict", false, "consider subdomains as external");
		options.addOption(strict);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("java -jar erc.jar", options);
			System.exit(1);
		}

		String url = cmd.getOptionValue("url");
		jsonOutput = cmd.hasOption("json");
		quietVal = cmd.hasOption("quiet");
		strictVal = cmd.hasOption("strict");
		// TODO UNIT TESTS
		try {
			doCheck(addProtcol(url));
		} catch (javax.net.ssl.SSLHandshakeException e) {
			// https://stackoverflow.com/questions/23777817/redirect-to-url-that-doesnot-have-www-by-jsoup
			try {
				doCheck(addProtcol("www."+url));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(jsonOutput) {
			jsonOutputB.append("\"findings\": [");
			for (Touple touple : findings) {
				jsonOutputB.append("{");
				jsonOut(touple.getA(), touple.getB());
				jsonOutputB.append("},");
			}
			jsonOutputB.append("],");
			jsonOutputB.append("\"errors\": [");
			for (String error : errors) {
				jsonOutputB.append("{");
				jsonOutputB.append("\""+error+"\"");
				jsonOutputB.append("},");
			}
			jsonOutputB.append("],");
			jsonOutputB.append("}");
			System.out.println(jsonOutputB.toString());
		}else{
			System.out.println("\r\nDone.");
		}
	}

	private static void doCheck(String url) throws IOException {
		String cleanURL = clean(url).getSt();
		Document doc = Jsoup.connect(url).followRedirects(true).get();
		if(jsonOutput) {
			jsonOut("taskRunning",url);
		}else{
			System.out.println("ERC running running for - "+url+"\r\n***************************************************");			
		}
		checkExternals(cleanURL, doc, "link", "href");
		checkExternals(cleanURL, doc, "script", "src");
		checkExternals(cleanURL, doc, "iframe", "src");
		checkExternals(cleanURL, doc, "object", "data");
		checkExternals(cleanURL, doc, "style", "inv");
		checkExternals(cleanURL, doc, "img", "src");
		checkExternals(cleanURL, doc, "embed", "src");
		checkExternals(cleanURL, doc, "source", "src");
		checkExternals(cleanURL, doc, "track", "src");
	}

	private static void jsonOut(String key, String value) {
		jsonOutputB.append("\""+key+"\":"+"\""+value+"\",");
	}

	private static void checkExternals(String baseURL, Document doc, String tag, String attribute) {
		Elements elements = doc.select(tag);
		for (Element element : elements) {
			String attribValue = element.attr(attribute);
			GR gr = clean(attribValue);
			String cleanHref = gr.getSt();
			checkForXHRJS(baseURL, tag, element, cleanHref);
			checkForResourceCSS(baseURL, tag, element, cleanHref);
			// string might be empty in case of inline JS, where there is no src attribute
			if (isntEmpty(cleanHref)) {
				if (isExternal(baseURL, gr)) {
					if(jsonOutput) {
						findings.add(new Touple(tag,cleanHref));
					}else {
						System.out.println("-> Found <" + tag + "> loading resource from " + cleanHref);						
					}
				}
			}
		}
	}

	private static void checkForResourceCSS(String baseURL, String tag, Element element, String cleanHref) {
		if (tag.equals("link")) {
			String cssHref = element.attr("href").toLowerCase();
			String rel = element.attr("rel").toLowerCase();
			if (rel.equals("stylesheet") || cssHref.endsWith("css")) {
				try {
					cssHref = makeFull(baseURL, cssHref);
					String res = getHTTP(addProtcol(cssHref));
					checkForExternalCSS(baseURL, res, false);
				} catch (Exception e) {
					if(jsonOutput) {
						errors.add("Error loading external stylesheet - " + e.getMessage());
					}else{
						System.out.println("-! Error loading external stylesheet - " + e.getMessage());
						e.printStackTrace();						
					}
				}
			}
		}
		if (tag.equals("style")) {
			String css = element.select(tag).html();
			checkForExternalCSS(baseURL, css, true);
		}
	}

	private static String makeFull(String baseURL, String refHref) {
		if(refHref.toLowerCase().startsWith("//")) {
			refHref=addProtcol(refHref);
		}else if(!refHref.toLowerCase().startsWith("http")) {
			refHref = baseURL+(baseURL.endsWith("/")?"":"/")+refHref;
		}
		return refHref;
	}

	private static void checkForExternalCSS(String baseURL, String css, boolean inline) {
		Matcher m = cssURLPattern.matcher(css);
		while (m.find()) {
			String checkURL = m.group(4);
			if (!baseURL.startsWith(removePath(clean(checkURL).getSt()))) {
				if(jsonOutput) {
					findings.add(new Touple((inline ? "css-inline" : "css-external") ,checkURL));
				}else{
					System.out.println("-> Found " + (inline ? "inline" : "in external") + " <style> loading external (@import) resource from " + checkURL);					
				}
			}
		}
	}

	private static void checkForExternalJS(String baseURL, String js, boolean inline) {
		Matcher m = jsXHRPattern.matcher(js);
		while (m.find()) {
			String checkURL = m.group(8);
			if (!baseURL.startsWith(removePath(clean(checkURL).getSt()))) {
				if(jsonOutput) {
					findings.add(new Touple((inline ? "js-inline" : "js-external") ,checkURL));

				}else{
					System.out.println("-> Found " + (inline ? "inline" : "in external") + " JavaScript XHR loading external resource from " + checkURL);					
				}
			}
		}
	}

	private static void checkForXHRJS(String baseURL, String tag, Element element, String cleanHref) {
		if (tag.equals("script")) {
			if (isntEmpty(cleanHref)) {
				String jsHref = element.attr("src").toLowerCase();
				try {
					jsHref = makeFull(baseURL, jsHref);
					String res = getHTTP(addProtcol(jsHref));
					checkForExternalJS(baseURL, res, false);
				} catch (Exception e) {
					if(jsonOutput) {
						errors.add("-! Error loading external script - "+e.getClass().getSimpleName()+" - " + e.getMessage());
					}else{
						System.out.println("-! Error loading external script - "+e.getClass().getSimpleName()+" - " + e.getMessage());						
					}
				}
			} else {
				String js = element.select(tag).html();
				checkForExternalJS(baseURL, js, true);
			}
		}
	}

	private static String getHTTP(String url) throws Exception {
			if(!quietVal) {
				if(!jsonOutput) {
					System.out.print("-- Making HTTP GET for "+url);					
				}
			}
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setReadTimeout(5000);
			int status = 0;
			try {
				status = conn.getResponseCode();
				if(!quietVal) {
					if(!jsonOutput) {
						System.out.print(" -> got response code = "+status+"\r\n");											
					}
				}
				if (status != HttpURLConnection.HTTP_OK) {
					if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
						String newUrl = conn.getHeaderField("Location");
						conn = (HttpURLConnection) new URL(newUrl).openConnection();
					}
				}
				
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer html = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					html.append(inputLine);
				}
				in.close();
				return html.toString();
			}catch (Exception e) {}
			return "";
	}

	private static String addProtcol(String url) {
		if (url.toLowerCase().startsWith("//")) {
			url = "https:" + url;
		}
		if (!url.toLowerCase().startsWith("http")) {
			url = "https://" + url;
		}
		return url;
	}

	private static boolean isExternal(String baseURL, GR gr) {
		baseURL = removePath(baseURL);
		String checkURL = gr.getSt();
		if (checkURL.startsWith("/")) {
			return false;
		}
		if (gr.isChanged()) {
			boolean sameURL;
			if(strictVal) {
				sameURL = checkURL.startsWith(baseURL);				
			}else {
				sameURL = checkURL.contains(baseURL);
				if(!sameURL) {
					sameURL = baseURL.contains(removePath(checkURL));
				}
			}
			return !sameURL;
		}
		return false;
	}

	
	
	static int nthLastIndexOf(int nth, String ch, String string) {
	    if (nth <= 0) return string.length();
	    return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
	}

	private static String removePath(String baseURL) {
		baseURL = baseURL.contains("/") ? baseURL.substring(0, baseURL.indexOf("/")) : baseURL;
		return baseURL;
	}

	private static boolean isntEmpty(String cleanHref) {
		return !cleanHref.equals("");
	}

	private static GR clean(String src) {
		src = src.toLowerCase();
		ArrayList<String> starts = new ArrayList<String>();
		starts.add("https://");
		starts.add("http://");
		starts.add("//");
		starts.add("www.");
		String cleaned = removeIfStartsWith(src, starts);
		boolean changed = cleaned.length() != src.length();
		return new GR(cleaned, changed);
	}

	private static String removeIfStartsWith(String href, ArrayList<String> starts) {
		for (String start : starts) {
			if (href.startsWith(start)) {
				href = href.substring(start.length());
			}
		}
		return href;
	}
}
