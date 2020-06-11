package ERC;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class ERC {
	public static void main(String[] args) throws MalformedURLException {				
		StringBuilder jsonOutputB = new StringBuilder().append("{");
		Settings settings = Settings.parseArgs(args);
		ArrayList<String> secondLevelDomains = URLHelpers.getPublicSuffixList(settings.isSecondlevel(),settings.getProxyObj());
		
		try {
			doCheck(URLHelpers.addProtcol(settings.getUrl()),secondLevelDomains, jsonOutputB,settings);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ERCHelper.processResults(jsonOutputB, Finding.findings, Error.errors, settings);
		ERCHelper.handleExitCode(Finding.findings, settings);
	}
		
	private static void doCheck(String url, ArrayList<String> sld, StringBuilder jsonOutputB, Settings settings) throws Exception {
		String cleanURL = GR.clean(url).getSt();
		String res = URLHelpers.getHTTP(url, false, true, settings.getProxyObj());
		ERCHelper.runInfo(url, jsonOutputB, settings, res);
		Document doc = Jsoup.parse(res);

		Checker.checkForExternals(cleanURL, doc, "link", "href", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "script", "src", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "iframe", "src", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "object", "data", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "style", "inv", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "img", "src", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "embed", "src", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "source", "src", sld, settings);
		Checker.checkForExternals(cleanURL, doc, "track", "src", sld, settings);
	}
}
