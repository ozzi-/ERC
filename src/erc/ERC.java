package erc;

import java.net.MalformedURLException;
import java.util.ArrayList;

import helpers.NW;
import helpers.Settings;
import model.Error;
import model.Finding;

public class ERC {
	public static void main(String[] args) throws MalformedURLException {
		StringBuilder jsonOutputB = new StringBuilder().append("{");
		Settings settings = Settings.parseArgs(args);
		ArrayList<String> secondLevelDomains = NW.getPublicSuffixList(settings.isSecondlevel(), settings.getProxyObj(), settings.isDebug() && !settings.isJsonOutput(), settings.getUserAgent());

		try {
			ExternalResourceChecker.check(NW.addProtcol(settings.getUrl()), secondLevelDomains, jsonOutputB, settings);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ERCHelper.processResults(jsonOutputB, Finding.findings, Error.errors, settings);
		ERCHelper.handleExitCode(Finding.findings, settings);
	}
}