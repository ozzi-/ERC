package ERC;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Checker {

	// (@import) url("http..
	private static Pattern cssURLPattern = Pattern.compile("@import(\\s)?(\\\"|'|\\(|url)(\\\"|'|\\()*([^;]*)(\\\"|')");
	// (xhr.)open("POST","http..
	private static Pattern jsXHRPattern = Pattern.compile("open(\\s)?\\((\\s)?(\\\"|')[a-zA-Z]*(\\\"|')(\\s)?,(\\s)?(\\\"|')([^;]*)(\\\"|')");
	
	static void checkForExternals(String baseURL, Document doc, String tag, String attribute, ArrayList<String> sld, Settings settings) {
		Elements elements = doc.select(tag);
		for (Element element : elements) {
			String attribValue = element.attr(attribute);
			GR gr = GR.clean(attribValue);
			String cleanHref = gr.getSt();
			checkForExternalXHRJS(baseURL, tag, element, cleanHref, settings);
			checkForExternalResourceCSS(baseURL, tag, element, cleanHref, settings);
			// string might be empty in case of inline JS, where there is no src attribute
			if (!cleanHref.equals("")) {
				if (URLHelpers.isExternal(baseURL, gr, settings.isStrict(),sld)) {
					if(settings.isJsonOutput()) {
						Finding.findings.add(new Finding(tag,cleanHref));
					}else {
						System.out.println("-> Found <" + tag + "> loading resource from " + cleanHref);						
					}
				}
			}
		}
	}
	
	private static void checkForExternalXHRJSinExternalFile(String baseURL, String js, boolean inline, boolean jsonOutput) {
		checkForExternalResource(baseURL, js, inline, jsXHRPattern, 8, "js", "JavaScript XHR loading external", jsonOutput); 
	}

	private static void checkForExternalCSSinExternalFile(String baseURL, String css, boolean inline, boolean jsonOutput) {
		checkForExternalResource(baseURL, css, inline, cssURLPattern, 4, "css", "<style> loading external (@import)", jsonOutput); 
	}

	private static void checkForExternalResourceCSS(String baseURL, String tag, Element element, String cleanHref, Settings settings) {
		if (tag.equals("link")) {
			String cssHref = element.attr("href").toLowerCase();
			String rel = element.attr("rel").toLowerCase();
			if (rel.equals("stylesheet") || cssHref.endsWith("css")) {
				try {
					cssHref = URLHelpers.makeURLComplete(baseURL, cssHref);
					String res = URLHelpers.getHTTP(URLHelpers.addProtcol(cssHref),settings.isJsonOutput(),settings.isQuiet(), settings.getProxyObj());
					checkForExternalCSSinExternalFile(baseURL, res, false, settings.isJsonOutput());
				} catch (Exception e) {
					if(settings.isJsonOutput()) {
						Error.errors.add("Error loading external stylesheet - " + e.getMessage());
					}else{
						System.out.println("-! Error loading external stylesheet - " + e.getMessage());
						e.printStackTrace();						
					}
				}
			}
		}
		if (tag.equals("style")) {
			String css = element.select(tag).html();
			checkForExternalCSSinExternalFile(baseURL, css, true, settings.isJsonOutput());
		}
	}


	private static void checkForExternalResource(String baseURL, String css, boolean inline, Pattern pattern, int captureGroupIndex, String type, String tag, boolean jsonOutput) {
		Matcher m = pattern.matcher(css);
		while (m.find()) {
			String checkURL = m.group(captureGroupIndex);
			if (!baseURL.startsWith(URLHelpers.removePath(GR.clean(checkURL).getSt()))) {
				if(jsonOutput) {
					Finding.findings.add(new Finding((inline ? type+"-inline" : type+"-external") ,checkURL));
				}else{
					System.out.println("-> Found " + (inline ? "inline" : "in external") + " "+tag+" resource from " + checkURL);					
				}
			}
		}
	}

	private static void checkForExternalXHRJS(String baseURL, String tag, Element element, String cleanHref,Settings settings) {
		if (tag.equals("script")) {
			if (cleanHref.equals("")) {
				String js = element.select(tag).html();
				checkForExternalXHRJSinExternalFile(baseURL, js, true, settings.isJsonOutput());
			} else {
				String jsHref = element.attr("src").toLowerCase();
				try {
					jsHref = URLHelpers.makeURLComplete(baseURL, jsHref);
					String res = URLHelpers.getHTTP(URLHelpers.addProtcol(jsHref),settings.isJsonOutput(),settings.isQuiet(),settings.getProxyObj());
					checkForExternalXHRJSinExternalFile(baseURL, res, false, settings.isJsonOutput());
				} catch (Exception e) {
					if(settings.isJsonOutput()) {
						Error.errors.add("-! Error loading external script - "+e.getClass().getSimpleName()+" - " + e.getMessage());
					}else{
						System.out.println("-! Error loading external script - "+e.getClass().getSimpleName()+" - " + e.getMessage());						
					}
				}
			}
		}
	}	
}
