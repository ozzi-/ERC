package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import helpers.NW;
import helpers.Settings;
import model.Finding;

class ERCTest {

	@Test
	void e2e() throws Exception {
		StringBuilder jsonOutputB = new StringBuilder().append("{");
		Settings settings = new Settings("-", "", "testagent",true, true, true, true, false, true,null,null);
		
		ArrayList<String> secondLevelDomains = NW.getPublicSuffixList(false,null, true, "testagent");
		erc.ExternalResourceChecker.check("https://gist.github.com/ozzi-/eccdc84cb352c6df628bbaef06b83e8c", secondLevelDomains, jsonOutputB, settings);
		
		for (Finding finding : Finding.findings) {
			System.out.println(finding.getFinding());
			assertFalse(finding.getFinding().contains("gist.github.com"));
		}
		
		assertEquals(18, Finding.findings.size());
	}
}