package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ERC.URLHelpers;

class urlTests {

	@Test
	void removeSubdomain1() {
		String url = "www.github.com";
		String urlNoSubdomain = URLHelpers.removeSubdomains(url,new ArrayList<String>());
		assertEquals("github.com", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain2() {
		String url = "github.com";
		String urlNoSubdomain = URLHelpers.removeSubdomains(url,new ArrayList<String>());
		assertEquals("github.com", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain3() {
		String url = "example.github.com/path";
		String urlNoSubdomain = URLHelpers.removeSubdomains(url,new ArrayList<String>());
		assertEquals("github.com/path", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain4() {
		ArrayList<String> tlds = new ArrayList<String>();
		tlds.add("co.uk");
		String url = "example.github.co.uk/path";
		String urlNoSubdomain = URLHelpers.removeSubdomains(url,tlds);
		assertEquals("github.co.uk/path", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain5() {
		ArrayList<String> tlds = new ArrayList<String>();
		tlds.add("co.uk");
		String url = "github.co.uk/path";
		String urlNoSubdomain = URLHelpers.removeSubdomains(url,tlds);
		assertEquals("github.co.uk/path", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain6() {
		ArrayList<String> tlds = new ArrayList<String>();
		tlds.add("foo.bar");
		String url = "github.co.uk/path";
		String urlNoSubdomain = URLHelpers.removeSubdomains(url,tlds);
		assertEquals("co.uk/path", urlNoSubdomain);
	}
	
	
	
	@Test
	void addProtocolHTTPS() {
		String url = "github.com";
		String urlWithProtocol = URLHelpers.addProtcol(url);
		assertEquals("https://"+url, urlWithProtocol);
	}
	
	@Test
	void addProtocolRelative() {
		String url = "//github.com";
		String urlWithProtocol = URLHelpers.addProtcol(url);
		assertEquals("https:"+url, urlWithProtocol);
	}
	
	@Test
	void makeURLComplete1() {
		String url = "//github.com";
		String path = "index.html?par=val";
		String res = URLHelpers.makeURLComplete(url,path);
		assertEquals("//github.com/index.html?par=val", res);
	}
	
	@Test
	void makeURLComplete2() {
		String url = "github.com";
		String path = "index.html?par=val";
		String res = URLHelpers.makeURLComplete(url,path);
		assertEquals("https://github.com/index.html?par=val", res);
	}
	
	@Test
	void makeURLComplete3() {
		String url = "http://github.com";
		String path = "index.html?par=val";
		String res = URLHelpers.makeURLComplete(url,path);
		assertEquals("http://github.com/index.html?par=val", res);
	}
	
	@Test
	void makeURLComplete4() {
		String url = "http://github.com/";
		String path = "";
		String res = URLHelpers.makeURLComplete(url,path);
		assertEquals("http://github.com/", res);
	}
	
	@Test
	void makeURLComplete5() {
		String url = "http://github.com";
		String path = "";
		String res = URLHelpers.makeURLComplete(url,path);
		assertEquals("http://github.com", res);
	}
	
	@Test
	void removePath1() {
		String url = "https://subdomain.github.com/resource/index.html?param=value#wow/./";
		String urlNoPath = URLHelpers.removePath(url);
		assertEquals("https://subdomain.github.com", urlNoPath);
	}
	
	@Test
	void removePath2() {
		String url = "https://subdomain.github.com";
		String urlNoPath = URLHelpers.removePath(url);
		assertEquals("https://subdomain.github.com", urlNoPath);
	}
	
	@Test
	void removePath3() {
		String url = "subdomain.github.com";
		String urlNoPath = URLHelpers.removePath(url);
		assertEquals("subdomain.github.com", urlNoPath);
	}
	
	@Test
	void removePath4() {
		String url = "subdomain.github.com/res";
		String urlNoPath = URLHelpers.removePath(url);
		assertEquals("subdomain.github.com", urlNoPath);
	}

}
