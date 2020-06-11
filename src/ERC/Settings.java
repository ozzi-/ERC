package ERC;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Settings {
	
	private String url;
	private String proxyString;
	private boolean quiet;
	private boolean strict;
	private boolean jsonOutput;
	private boolean exitCode;
	private boolean secondlevel;
	private boolean debug;
	private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36 - https://github.com/ozzi-/ERC";
	
	public Settings(String url, String proxyString, String userAgent, boolean jsonOutput, boolean quiet, boolean strict, boolean exitCode, boolean secondlevel, boolean debug) {
		this.setUrl(url);
		if(userAgent!=null) {
			this.setUserAgent(userAgent);
		}
		this.setProxyString("");
		if(proxyString!=null) {
			this.setProxyString(proxyString);			
		}
		this.setJsonOutput(jsonOutput);
		this.setQuiet(quiet);
		this.setStrict(strict);
		this.setExitCode(exitCode);
		this.setSecondlevel(secondlevel);
		this.setDebug(debug);
	}
	
	public static Settings parseArgs(String[] args) {
		Options options = new Options();
		
		Option input = new Option("u", "url", true, "URL to scan for external content");
		input.setRequired(true);
		options.addOption(input);
		
		Option agent = new Option("a", "useragent", true, "Send custom user agent");
		options.addOption(agent);

		Option suffix = new Option("j", "json", false, "output results as JSON");
		options.addOption(suffix);
		
		Option exitcode = new Option("e", "exitcode", false, "amount of findings will be returned as exit code");
		options.addOption(exitcode);
		
		Option quiet = new Option("q", "quiet", false, "don't output further HTTP call notices");
		options.addOption(quiet);
		
		Option proxy = new Option("p", "proxy", true, "use proxy (i.E. 127.0.0.1:8080)");
		options.addOption(proxy);
		
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
		
		String url = cmd.getOptionValue("url");
		String proxyString = cmd.getOptionValue("proxy");
		String agentVal = cmd.getOptionValue("useragent");
		boolean jsonOutput = cmd.hasOption("json");
		boolean quietVal = cmd.hasOption("quiet");
		boolean strictVal = cmd.hasOption("strict");
		boolean exitCodeVal = cmd.hasOption("exitcode");
		boolean secondlevelVal = cmd.hasOption("secondlevel");
		boolean debugVal = cmd.hasOption("debug");
		return new Settings(url,proxyString,agentVal,jsonOutput,quietVal,strictVal,exitCodeVal,secondlevelVal,debugVal);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProxyString() {
		return proxyString;
	}

	public Proxy getProxyObj() {
		if(proxySet()) {
			String[] proxyArr = proxyString.split(":",2);
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyArr[0], Integer.valueOf(proxyArr[1])));			
		}
		return null;
	}

	public boolean proxySet() {
		return proxyString.contains(":");
	}

	
	public void setProxyString(String proxyString) {
		this.proxyString = proxyString;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public boolean isJsonOutput() {
		return jsonOutput;
	}

	public void setJsonOutput(boolean jsonOutput) {
		this.jsonOutput = jsonOutput;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public boolean isExitCode() {
		return exitCode;
	}

	public void setExitCode(boolean exitCode) {
		this.exitCode = exitCode;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isSecondlevel() {
		return secondlevel;
	}

	public void setSecondlevel(boolean secondlevel) {
		this.secondlevel = secondlevel;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
