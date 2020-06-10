import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class ERC {
	
	private static boolean quietVal;
	private static boolean jsonOutput;
	static ArrayList<String> errors = new ArrayList<String>();
	static ArrayList<Touple> findings = new ArrayList<Touple>();
	
	private static StringBuilder jsonOutputB = new StringBuilder();
	private static boolean strictVal;
	private static boolean exitCodeVal;

	public static void main(String[] args) throws MalformedURLException {
		

		jsonOutputB.append("{");
		
		CommandLine cmd = parseArgs(args);
		String url = cmd.getOptionValue("url");
		jsonOutput = cmd.hasOption("json");
		quietVal = cmd.hasOption("quiet");
		strictVal = cmd.hasOption("strict");
		exitCodeVal = cmd.hasOption("exitcode");
				
		// TODO UNIT TESTS
		try {
			doCheck(URLHelpers.addProtcol(url));
		} catch (javax.net.ssl.SSLHandshakeException e) {
			// https://stackoverflow.com/questions/23777817/redirect-to-url-that-doesnot-have-www-by-jsoup
			try {
				doCheck(URLHelpers.addProtcol("www."+url));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (Exception e) {
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
		if(exitCodeVal) {
			int amountFindings = findings.size()>127?127:findings.size();
			System.exit(amountFindings);
		}
	}

	private static CommandLine parseArgs(String[] args) {
		Options options = new Options();
		
		Option input = new Option("u", "url", true, "URL to scan for external content");
		input.setRequired(true);
		options.addOption(input);

		Option suffix = new Option("j", "json", false, "output results as JSON");
		options.addOption(suffix);
		
		Option exitcode = new Option("e", "exitcode", false, "amount of findings will be returned as exit code");
		options.addOption(exitcode);
		
		Option quiet = new Option("q", "quiet", false, "don't output further HTTP call notices");
		options.addOption(quiet);
		
		Option strict = new Option("s", "strict", false, "consider subdomains as external content");
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
		return cmd;
	}
	
	// 	TODO proxy support
	// TODO user agent?
	private static void doCheck(String url) throws Exception {
		String cleanURL = GR.clean(url).getSt();
		String res = URLHelpers.getHTTP(url, false, true);
		System.out.println("RESULT:"+res.substring(0,500));
		Document doc = Jsoup.parse(res);

		if(jsonOutput) {
			jsonOut("taskRunning",url);
		}else{
			System.out.println("ERC running running for - "+url+"\r\n***************************************************");			
		}
		Checker.checkForExternals(cleanURL, doc, "link", "href", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "script", "src", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "iframe", "src", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "object", "data", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "style", "inv", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "img", "src", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "embed", "src", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "source", "src", jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "track", "src", jsonOutput, strictVal, quietVal);
	}

	private static void jsonOut(String key, String value) {
		jsonOutputB.append("\""+key+"\":"+"\""+value+"\",");
	}
}
