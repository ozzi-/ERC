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
	private static boolean secondlevelVal;
	private static boolean debugVal;

	public static void main(String[] args) throws MalformedURLException {				
		jsonOutputB.append("{");
		
		CommandLine cmd = parseArgs(args);
		String url = cmd.getOptionValue("url");
		jsonOutput = cmd.hasOption("json");
		quietVal = cmd.hasOption("quiet");
		strictVal = cmd.hasOption("strict");
		exitCodeVal = cmd.hasOption("exitcode");
		secondlevelVal = cmd.hasOption("secondlevel");
		debugVal = cmd.hasOption("debug");
		
		ArrayList<String>sLD = URLHelpers.getPublicSuffixList(secondlevelVal);			
		// TODO UNIT TESTS
		try {
			doCheck(URLHelpers.addProtcol(url),sLD,debugVal);
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
			for (String error : errors) {
				System.out.println("!- "+error);
			}
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
		
		Option secondLevel = new Option("l", "secondlevel", false, "download second level domain list");
		options.addOption(secondLevel);
		
		Option debug = new Option("d", "debug", false, "output first 1000 characters of the response received");
		options.addOption(debug);
	
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("java -jar ERC.jar", options);
			System.exit(1);
		}
		return cmd;
	}
		
	private static void doCheck(String url, ArrayList<String> sld, boolean debugVal) throws Exception {
		String cleanURL = GR.clean(url).getSt();
		String res = URLHelpers.getHTTP(url, false, true);
		if(jsonOutput) {
			jsonOut("taskRunning",url);
		}else{
			String header ="ERC running running for - "+url+" ("+res.length()+")";
			System.out.print(header+"\r\n");
			System.out.print(Strng.repeat("*", header.length())+"\r\n");			
			if(debugVal) {
				int maxLength = res.length()<1000?res.length():1000;
				System.out.println("Debug Output:"+res.substring(0,maxLength));
			}
		}
		Document doc = Jsoup.parse(res);

		Checker.checkForExternals(cleanURL, doc, "link", "href", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "script", "src", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "iframe", "src", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "object", "data", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "style", "inv", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "img", "src", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "embed", "src", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "source", "src", sld, jsonOutput, strictVal, quietVal);
		Checker.checkForExternals(cleanURL, doc, "track", "src", sld, jsonOutput, strictVal, quietVal);
	}

	private static void jsonOut(String key, String value) {
		jsonOutputB.append("\""+key+"\":"+"\""+value+"\",");
	}
}
