package pisi.unitedmeows.yystal.web;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.hook.YString;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.web.events.WCDownloadFinished;
import pisi.unitedmeows.yystal.web.events.WCDownloadProgress;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class YWebClient {

	private int timeout = 10000;
	private Map<String, String> headers = createDefaultHeaders();
	private static final String DEFAULT_USER_AGENT = randomUserAgent();
	private Map<String, List<String>> responseHeaders = new HashMap<>();

	/* Download Progress Event only works when server has set Content-Length header
	* otherwise the only correct value will be currentBytes variable */
	public event<WCDownloadProgress> downloadProgressEvent = new event<>();
	public event<WCDownloadFinished> downloadFinishedEvent = new event<>();


	public String downloadString(String url) {
		try {
			return downloadString(new URL(url));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public byte[] downloadBytes(String url) {
		try {
			return downloadBytes(new URL(url));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public byte[] downloadBytes(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(timeout);
			connection.setConnectTimeout(timeout);

			/* sets default headers */
			headers.forEach(connection::addRequestProperty);

			/* tries to connect the webserver */
			connection.connect();

			out<URL> newUrl = YYStal.out();
			if (redirectCheck(connection, newUrl)) {
				return downloadBytes(newUrl.get());
			}

			final int contentLength = connection.getContentLength();
			int count;
			downloadProgressEvent.fire(0, 0, contentLength);
			byte data[] = new byte[4096];
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
				while ((count = connection.getInputStream().read(data)) != -1) {
					outputStream.write(data, 0, count);
					double percent = (outputStream.size() * 100D) / contentLength;
					downloadProgressEvent.fire(percent, outputStream.size(), contentLength);
				}
				responseHeaders = connection.getHeaderFields();

				byte[] result = outputStream.toByteArray();
				downloadFinishedEvent.fire();
				return result;
			}


		} catch (Exception e) {
			return null;
		}
	}

	public boolean downloadFile(String url, File file) {
		try {
			return downloadFile(new URL(url), file);
		} catch (MalformedURLException e) {
			return false;
		}
	}



	public boolean downloadFile(URL url, File file) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(timeout);
			connection.setConnectTimeout(timeout);

			/* sets default headers */
			headers.forEach(connection::addRequestProperty);

			/* tries to connect the webserver */
			connection.connect();

			out<URL> newUrl = YYStal.out();
			if (redirectCheck(connection, newUrl)) {
				return downloadFile(newUrl.get(), file);
			}

			int count;

			byte data[] = new byte[4096];
			final int contentLength = connection.getContentLength();
			int totalDownloaded = 0;
			downloadProgressEvent.fire(0, 0, contentLength);
			try (FileOutputStream outputStream = new FileOutputStream(file)){
				while ((count = connection.getInputStream().read(data)) != -1) {
					outputStream.write(data, 0, count);
					totalDownloaded += count;
					downloadProgressEvent.fire((totalDownloaded * 100) / contentLength, totalDownloaded, contentLength);
					outputStream.flush();
				}
				outputStream.flush();
				downloadFinishedEvent.fire();
				responseHeaders = connection.getHeaderFields();
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String downloadString(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(timeout);
			connection.setConnectTimeout(timeout);

			/* sets default headers */
			headers.forEach(connection::addRequestProperty);

			/* tries to connect the webserver */
			connection.connect();

			out<URL> newUrl = YYStal.out();
			if (redirectCheck(connection, newUrl)) {
				return downloadString(newUrl.get());
			}
			responseHeaders = connection.getHeaderFields();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
				return reader.lines().collect(Collectors.joining("\n"));
			}

		} catch (Exception e) {
			return null;
		}
	}



	public String postRequest(String url, Map<String, String> args, String contentType) {
		try {
			return postRequest(new URL(url), args, contentType);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public String postRequest(URL url, Map<String, String> args) {
		return postRequest(url, args, "text/html");
	}

	public String postRequest(String url, Map<String, String> args) {
		try {
			return postRequest(new URL(url), args, "text/html");
		} catch (Exception ex) {
			return null;
		}
	}

	public String postRequest(URL url, Map<String, String> args, String contentType) {

		try {
			StringJoiner stringJoiner = new StringJoiner("&");
			for (Map.Entry<String, String> entry : args.entrySet())
				stringJoiner.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));

			return postRequest(url, stringJoiner.toString(), contentType);
		} catch (Exception ex) {
			return null;
		}
	}

	public String
    postRequest(String url, String value, String contentType) {
		try {
			return postRequest(new URL(url), value, contentType);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public String postRequest(String url, String value) {
		try {
			return postRequest(new URL(url), value, "application/json");
		} catch (MalformedURLException e) {
			return null;
		}
	}


	public String postRequest(URL url, String value, String contentType) {
		try {
			URLConnection connection = url.openConnection();

			/* setup http connection */
			HttpURLConnection http = (HttpURLConnection) connection;
			http.setRequestMethod("POST");
			http.setDoOutput(true);

			/* add headers */
			headers.forEach(connection::addRequestProperty);



			byte[] out = value.getBytes(StandardCharsets.UTF_8);
			int length = out.length;
			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", contentType);
			http.setRequestProperty("charset", "utf-8");
			http.setRequestProperty("Content-Length", Integer.toString( length ));
			http.setInstanceFollowRedirects( false );
			http.setUseCaches( false );
			http.connect();



			try(OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			/* check for redirects */
			out<URL> newUrl = YYStal.out();
			if (redirectCheck(http, newUrl)) {
				return postRequest(newUrl.get(), value, contentType);
			}

			responseHeaders = connection.getHeaderFields();
			StringBuilder stringBuilder = new StringBuilder();
			if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line);
					}
				}
			} else if (http.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
				return "";
			}else {
				return null;
			}
			return stringBuilder.toString();

		} catch (Exception ex) {
			return null;
		}
	}

	public String putRequest(String url, String value) {
		return putRequest(url, value, "application/json");
	}

	public String putRequest(String url, String value, String contentType) {
		try {
			return putRequest(new URL(url),  value, contentType);
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public String putRequest(URL url, String value) {
		return putRequest(url, value, "application/json");
	}

	public String putRequest(URL url, String value, String contentType) {
		try {
			URLConnection connection = url.openConnection();

			/* setup http connection */
			HttpURLConnection http = (HttpURLConnection) connection;
			http.setRequestMethod("PUT");
			http.setDoOutput(true);

			/* add headers */
			headers.forEach(connection::addRequestProperty);



			byte[] out = value.getBytes(StandardCharsets.UTF_8);
			int length = out.length;
			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", contentType);
			http.setRequestProperty("charset", "utf-8");
			http.setRequestProperty("Content-Length", Integer.toString( length ));
			http.setInstanceFollowRedirects( false );
			http.setUseCaches( false );
			http.connect();



			try(OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			/* check for redirects */
			out<URL> newUrl = YYStal.out();
			if (redirectCheck(http, newUrl)) {
				return postRequest(newUrl.get(), value, contentType);
			}

			responseHeaders = connection.getHeaderFields();
			StringBuilder stringBuilder = new StringBuilder();
			if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line);
					}
				}
			} else if (http.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
				return "";
			}else {
				return null;
			}
			return stringBuilder.toString();

		} catch (Exception ex) {
			return null;
		}
	}
	public String patchRequest(String url, String value) {
		return patchRequest(url, value, "application/json");
	}

	public String patchRequest(String url, String value, String contentType) {
		try {
			return patchRequest(new URL(url),  value, contentType);
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public String patchRequest(URL url, String value) {
		return patchRequest(url, value, "application/json");
	}

	public String patchRequest(URL url, String value, String contentType) {
		try {
			URLConnection connection = url.openConnection();

			/* setup http connection */
			HttpURLConnection http = (HttpURLConnection) connection;
			http.setRequestMethod("PATCH");
			http.setDoOutput(true);

			/* add headers */
			headers.forEach(connection::addRequestProperty);



			byte[] out = value.getBytes(StandardCharsets.UTF_8);
			int length = out.length;
			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", contentType);
			http.setRequestProperty("charset", "utf-8");
			http.setRequestProperty("Content-Length", Integer.toString( length ));
			http.setInstanceFollowRedirects( false );
			http.setUseCaches( false );
			http.connect();



			try(OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			/* check for redirects */
			out<URL> newUrl = YYStal.out();
			if (redirectCheck(http, newUrl)) {
				return postRequest(newUrl.get(), value, contentType);
			}

			responseHeaders = connection.getHeaderFields();
			StringBuilder stringBuilder = new StringBuilder();
			if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line);
					}
				}
			} else if (http.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
				return "";
			}else {
				return null;
			}
			return stringBuilder.toString();

		} catch (Exception ex) {
			return null;
		}
	}


	public String deleteRequest(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(timeout);
			connection.setConnectTimeout(timeout);
			connection.setRequestMethod("DELETE");

			/* sets default headers */
			headers.forEach(connection::addRequestProperty);

			/* tries to connect the webserver */
			connection.connect();

			out<URL> newUrl = YYStal.out();
			if (redirectCheck(connection, newUrl)) {
				return downloadString(newUrl.get());
			}
			responseHeaders = connection.getHeaderFields();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
				return reader.lines().collect(Collectors.joining("\n"));
			}

		} catch (Exception e) {
			return null;
		}
	}

	public String deleteRequest(String url) {
		try {
			return deleteRequest(new URL(url));
		} catch (MalformedURLException ex) {
			return null;
		}
	}


	public static boolean redirectCheck(HttpURLConnection connection, out<URL> url) {
		try {
			int responseCode = connection.getResponseCode();

			if (responseCode < 400 && responseCode >= 300) {
				String redirectUrl = connection.getHeaderField("Location");
				try {
					url.set(new URL(redirectUrl));
				} catch (Exception ex) {
					URL newUrl = new URL(connection.getURL().getProtocol() + "://" + connection.getURL().getHost() + redirectUrl);
					url.set(newUrl);
				}
				return true;
			}
		} catch (Exception ex) {}

		return false;
	}

	public Map<String, List<String>> responseHeaders() {
		return responseHeaders;
	}

	public void cookie(String cookies) {
		headers.put("Cookie", cookies);
	}

	public void appendCookie(String cookie) {
		headers.put("Cookie", headers.getOrDefault("Cookie", YString.EMPTY_R) + cookie);
	}

	private static Map<String, String> createDefaultHeaders() {
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", DEFAULT_USER_AGENT);

		return headers;
	}

	public YWebClient setUserAgent(String userAgent) {
		headers.put("User-Agent", userAgent);
		return this;
	}

	private static String randomUserAgent() {
		String[] userAgents = new String[]{
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_3; rv:95.0) Gecko/20010101 Firefox/95.0",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4667.82 Safari/537.36",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20110101 Firefox/93.0",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4648.150 Safari/537.36",
				"Mozilla/5.0 (X11; U; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4655.89 Safari/537.36",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4692.110 Safari/537.36",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4661.55 Safari/537.36",
				"Mozilla/5.0 (Windows NT 11.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4641.165 Safari/537.36",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.174 Safari/537.36"
		};
		return userAgents[YRandom.BASIC.nextInt(userAgents.length)];
	}


	public YWebClient setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public YWebClient header(String header, String value) {
		headers.put(header, value);
		return this;
	}

	public Map<String, String> headers() {
		return headers;
	}
}
