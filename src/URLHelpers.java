import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLHelpers {
	
	// adds protocol if missing and appends path to domain name
	public static String makeURLComplete(String domainName, String path) {
		if (path.toLowerCase().startsWith("//")) {
			path = addProtcol(path);
		} else if (!path.toLowerCase().startsWith("http")) {
			path = domainName + (domainName.endsWith("/") ? "" : "/") + path;
		}
		return path;
	}
	
	// adds protocol to URL if missing
	public static String addProtcol(String url) {
		if (url.toLowerCase().startsWith("//")) {
			url = "https:" + url;
		}
		if (!url.toLowerCase().startsWith("http")) {
			url = "https://" + url;
		}
		return url;
	}

	public static String removePath(String baseURL) {
		baseURL = baseURL.contains("/") ? baseURL.substring(0, baseURL.indexOf("/")) : baseURL;
		return baseURL;
	}
	
	static boolean isExternal(String baseURL, GR gr, boolean strict) {
		baseURL = URLHelpers.removePath(baseURL);
		String checkURL = gr.getSt();
		if (checkURL.startsWith("/")) {
			return false;
		}
		if (gr.isChanged()) {
			boolean sameURL;
			if(strict) {
				sameURL = checkURL.startsWith(baseURL);				
			}else {
				// TODO baseURL a.hin.ch doesn't match with c.hin.ch
				String checkURLNoSub = removeSubdomains(checkURL);
				String baseURLNoSub = removeSubdomains(baseURL);
				
				sameURL = checkURLNoSub.contains(baseURLNoSub);
				if(!sameURL) {
					sameURL = baseURLNoSub.contains(URLHelpers.removePath(checkURLNoSub));
				}
			}
			return !sameURL;
		}
		return false;
	}
	
	public static String removeSubdomains(String url) {
		String protocol="";
		int cut = 0;
		
		String containsProtocolPattern = "^([a-zA-Z]*:\\/\\/)|^(\\/\\/)";
		Pattern pattern = Pattern.compile(containsProtocolPattern);
		Matcher m = pattern.matcher(url);
	    if (m.find( )) {	      
	    	protocol=m.group();
	    	cut = protocol.length();
	    }
		url = url.substring(cut);
					
		String urlDomain=url;
		String path="";
		if(urlDomain.contains("/")) {
			int slashPos = urlDomain.indexOf("/");
			path=urlDomain.substring(slashPos);
			urlDomain=urlDomain.substring(0, slashPos);
		}
		
		int dotCount = Strng.countOccurrences(urlDomain, ".");
		if(dotCount==1){
			return protocol+url;
		}
		int pos = Strng.nthLastIndexOf(2, ".", urlDomain)+1;
		urlDomain = urlDomain.substring(pos);
					
		return protocol+urlDomain+path;
	}

	public static String getHTTP(String url, boolean jsonOutput, boolean quietVal) throws Exception {
		if (!quietVal) {
			if (!jsonOutput) {
				System.out.print("-- Making HTTP GET for " + url);
			}
		}
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setReadTimeout(5000);
		int status = 0;
		try {
			status = conn.getResponseCode();
			if (!quietVal) {
				if (!jsonOutput) {
					System.out.print(" -> got response code = " + status + "\r\n");
				}
			}
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
					String newUrl = conn.getHeaderField("Location");
					conn = (HttpURLConnection) new URL(newUrl).openConnection();
				}
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer html = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			return html.toString();
		} catch (Exception e) {
		}
		return "";
	}
}
