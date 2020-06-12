package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import helpers.Strng;

public class StrngTest {

	
	@Test
	void repeatTest() throws Exception {
		String in = "Example";
		String res = Strng.repeat(in, 3);
		assertEquals("ExampleExampleExample", res);
	}
	
	@Test
	void nthLastIndexOfTest() throws Exception {
		String in = "Example.SomeThing.MoreThing.F00.Bar!";
		int res = Strng.nthLastIndexOf(3,".",in);
		assertEquals(17, res);
	}
	
	@Test
	void countOccurrencesTest() throws Exception {
		String in = "Example.SomeThing.MoreThing.F00.Bar!";
		int res = Strng.countOccurrences(in,".");
		assertEquals(4, res);
	}
	
}
