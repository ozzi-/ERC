import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
	
	static boolean isExternal(String baseURL, GR gr, boolean strictVal) {
		baseURL = URLHelpers.removePath(baseURL);
		String checkURL = gr.getSt();
		if (checkURL.startsWith("/")) {
			return false;
		}
		if (gr.isChanged()) {
			boolean sameURL;
			if(strictVal) {
				sameURL = checkURL.startsWith(baseURL);				
			}else {
				sameURL = checkURL.contains(baseURL);
				if(!sameURL) {
					sameURL = baseURL.contains(URLHelpers.removePath(checkURL));
				}
			}
			return !sameURL;
		}
		return false;
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
