package jtumblppr;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.apphosting.api.DeadlineExceededException;

import jtumblppr.model.PMF;
import jtumblppr.model.TumblrImage;

/**
 * Serves images from the datastore
 * 
 * @author Remi
 * 
 */
@SuppressWarnings("serial")
public class GetImageServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(GetImageServlet.class
			.getName());
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String id = request.getParameter("id");
		String url = request.getParameter("url");
		TumblrImage image = getImage(id, url);

		if (image != null && image.getImageType() != null
				&& image.getImage() != null) {
			// Set the appropriate Content-Type header and write the raw bytes
			// to the response's output stream
			response.setContentType(image.getImageType());
			response.getOutputStream().write(image.getImage());
		} else {
			// If no image is found with the given title, redirect the user to
			// a static image
			response.sendRedirect("/img/noimage.jpg");
		}
	}

	@SuppressWarnings("unchecked")
	private TumblrImage getImage(String id, String url) {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(TumblrImage.class);
		query.setFilter("id == idParam");
		query.declareParameters("String idParam");
		query.setRange(0, 1);

		try {
			List<TumblrImage> results = (List<TumblrImage>) query.execute(id);
			if (results.iterator().hasNext()) {
				// If the results list is non-empty, return the first (and only)
				// result
				return results.get(0);
			} else {
				// Make sure the url is correct
				//ex: http://30.media.tumblr.com/tumblr_lnmat5gJUF1qhdh4ko1_500.jpg
				if (!url.matches("^http://\\d+\\.media\\.tumblr\\.com/tumblr_\\w+_\\d+\\.\\w+")) {
					log.log(Level.WARNING, "Rejected URL: " + url);
					return null;
				}
				
				// Download and store
				log.log(Level.FINE, "Image with id=" + id + " and url=" + url + " not found");
				return storeImageInDataStore(id, url);
			}

		} finally {
			query.closeAll();
		}
	}

	private TumblrImage storeImageInDataStore(String imageId, String imageUrl) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		TumblrImage tImage = null;
		
		try {
		
			// Download the image
			URL url = new URL(imageUrl);

			URLFetchService fetchService = URLFetchServiceFactory
					.getURLFetchService();

			HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.GET,
					FetchOptions.Builder.withDeadline(10));
			// Fetch the image at the location given by the url query string
			// parameter
			HTTPResponse fetchResponse = fetchService.fetch(httpRequest);

			String fetchResponseContentType = null;
			for (HTTPHeader header : fetchResponse.getHeaders()) {
				// For each request header, check whether the name equals
				// "Content-Type"; if so, store the value of this header
				// in a member variable
				if (header.getName().equalsIgnoreCase("content-type")) {
					fetchResponseContentType = header.getValue();
					break;
				}
			}

			if (fetchResponseContentType != null) {
				// Create an image instance
				tImage = new TumblrImage(imageId,
						fetchResponse.getContent(),
						fetchResponseContentType);

				// Store the image in App Engine's datastore
				pm.makePersistent(tImage);
			}
		} catch (IOException e) {
			// Pass, the image is not saved
			log.warning("IOException for: " + imageUrl);
		} catch (DeadlineExceededException e) {
			// Pass, the image is not saved
			log.warning("DeadlineExceededException for: " + imageUrl);
		} finally {
			pm.close();
		}
		
		return tImage;
	}

	@SuppressWarnings("unchecked")
	private Boolean inDatabase(String imageId) {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(TumblrImage.class);
		query.setFilter("id == idParam");
		query.declareParameters("String idParam");
		query.setRange(0, 1);

		try {
			List<TumblrImage> results = (List<TumblrImage>) query
					.execute(imageId);
			return !results.isEmpty();

		} finally {
			query.closeAll();
		}
	}
}
