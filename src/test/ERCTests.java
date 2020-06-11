package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ERC.Finding;
import ERC.Settings;
import ERC.URLHelpers;

class ERCTests {

	@Test
	void e2e() throws Exception {
		StringBuilder jsonOutputB = new StringBuilder().append("{");
		Settings settings = new Settings("-", "", true, true, true, true, false, true);
		
		ArrayList<String> secondLevelDomains = URLHelpers.getPublicSuffixList(false,null, true);
		ERC.ERC.doCheck("https://gist.github.com/ozzi-/eccdc84cb352c6df628bbaef06b83e8c", secondLevelDomains, jsonOutputB, settings);
		
		for (Finding finding : Finding.findings) {
			System.out.println(finding.getFinding());
			assertFalse(finding.getFinding().contains("gist.github.com"));
		}
		
		assertEquals(18, Finding.findings.size());
	}
}