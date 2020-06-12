package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import helpers.NW;

class urlTest {
	
	@Test
	void expandComplete1() {
		String expand = NW.expandComplete("www.github.com","resource"); 
		assertEquals("www.github.com/resource", expand);
	}
	
	@Test
	void expandComplete2() {
		String expand = NW.expandComplete("www.github.com/","resource"); 
		assertEquals("www.github.com/resource", expand);
	}
	
	@Test
	void expandComplete3() {
		String expand = NW.expandComplete("www.github.com","/resource"); 
		assertEquals("www.github.com/resource", expand);
	}
	
	@Test
	void expandComplete4() {
		String expand = NW.expandComplete("www.github.com","http://gist.github.com"); 
		assertEquals("http://gist.github.com", expand);
	}
	
	@Test
	void expandComplete5() {
		String expand = NW.expandComplete("www.github.com","https://gist.github.com"); 
		assertEquals("https://gist.github.com", expand);
	}
	
	@Test
	void expandComplete6() {
		String expand = NW.expandComplete("//github.com","https://gist.github.com"); 
		assertEquals("https://gist.github.com", expand);
	}
	
	@Test
	void expandComplete7() {
		String expand = NW.expandComplete("www.github.com","//gist.github.com"); 
		assertEquals("https://gist.github.com", expand);
	}


	@Test
	void removeSubdomain1() {
		String url = "www.github.com";
		String urlNoSubdomain = NW.removeSubdomains(url,new ArrayList<String>());
		assertEquals("github.com", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain2() {
		String url = "github.com";
		String urlNoSubdomain = NW.removeSubdomains(url,new ArrayList<String>());
		assertEquals("github.com", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain3() {
		String url = "example.github.com/path";
		String urlNoSubdomain = NW.removeSubdomains(url,new ArrayList<String>());
		assertEquals("github.com/path", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain4() {
		ArrayList<String> tlds = new ArrayList<String>();
		tlds.add("co.uk");
		String url = "example.github.co.uk/path";
		String urlNoSubdomain = NW.removeSubdomains(url,tlds);
		assertEquals("github.co.uk/path", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain5() {
		ArrayList<String> tlds = new ArrayList<String>();
		tlds.add("co.uk");
		String url = "github.co.uk/path";
		String urlNoSubdomain = NW.removeSubdomains(url,tlds);
		assertEquals("github.co.uk/path", urlNoSubdomain);
	}
	
	@Test
	void removeSubdomain6() {
		ArrayList<String> tlds = new ArrayList<String>();
		tlds.add("foo.bar");
		String url = "github.co.uk/path";
		String urlNoSubdomain = NW.removeSubdomains(url,tlds);
		assertEquals("co.uk/path", urlNoSubdomain);
	}
	
	
	
	@Test
	void addProtocolHTTPS() {
		String url = "github.com";
		String urlWithProtocol = NW.addProtcol(url);
		assertEquals("https://"+url, urlWithProtocol);
	}
	
	@Test
	void addProtocolRelative() {
		String url = "//github.com";
		String urlWithProtocol = NW.addProtcol(url);
		assertEquals("https:"+url, urlWithProtocol);
	}
	

	
	@Test
	void removePath1() {
		String url = "https://subdomain.github.com/resource/index.html?param=value#wow/./";
		String urlNoPath = NW.removePath(url);
		assertEquals("https://subdomain.github.com", urlNoPath);
	}
	
	@Test
	void removePath2() {
		String url = "https://subdomain.github.com";
		String urlNoPath = NW.removePath(url);
		assertEquals("https://subdomain.github.com", urlNoPath);
	}
	
	@Test
	void removePath3() {
		String url = "subdomain.github.com";
		String urlNoPath = NW.removePath(url);
		assertEquals("subdomain.github.com", urlNoPath);
	}
	
	@Test
	void removePath4() {
		String url = "subdomain.github.com/res";
		String urlNoPath = NW.removePath(url);
		assertEquals("subdomain.github.com", urlNoPath);
	}

}
