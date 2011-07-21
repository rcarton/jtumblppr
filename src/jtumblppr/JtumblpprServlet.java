package jtumblppr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class JtumblpprServlet extends HttpServlet {

	public final class Const {
		public static final String TUMBLR_API_PATH = "/api/read/json";
		public static final String TUMBLR_API_VAR = "var tumblr_api_read";
		public static final String URL_HTTP = "http://";
	}

	private static final Logger log = Logger.getLogger(JtumblpprServlet.class
			.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		
		String tumblrUrl = req.getParameter("tumblr_url");
		
		// Add "http://" if needed
		if (!tumblrUrl.startsWith(Const.URL_HTTP)) { tumblrUrl = Const.URL_HTTP + tumblrUrl; }
		// Remove trailing slash
		if (tumblrUrl.endsWith("/")) { tumblrUrl = tumblrUrl.subSequence(0, tumblrUrl.length()-1).toString(); }
		
		String content = getTumblrApiPage(tumblrUrl);
		out.println(content);

	}

	/**
	 * Returns the content of the api/read/json page
	 * 
	 * @param url
	 * @return
	 */
	private String getTumblrApiPage(String tumblr_url) {

		StringBuilder sb = new StringBuilder("");
		try {
			URL url = new URL(tumblr_url + Const.TUMBLR_API_PATH);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {

				log.log(Level.FINEST, line);
				if (i == 0 && !line.startsWith(Const.TUMBLR_API_VAR)) {
					log.log(Level.WARNING, "Tumblr outage");

					throw new TumblrErrorException(
							"Error when retrieving data from Tumblr");
				}

				sb.append(line);
				++i;
			}
			reader.close();

		} catch (Exception e) {
			// TODO handle tumblr outages with a proper JSON message
			return "var tumblr_api_read = {error: \"Error retrieving images\"};";
		}

		return sb.toString();
	}

}
