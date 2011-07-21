package jtumblppr.cron;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jtumblppr.model.PMF;
import jtumblppr.model.TumblrImage;

@SuppressWarnings("serial")
public class CleanCacheServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		
		if (!req.getHeader("X-AppEngine-Cron").equals("true")) {
			resp.getOutputStream().println(":(");
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -1);
		Date yesterday = new Date(today.getTimeInMillis()); 
		
		Query query = pm.newQuery(TumblrImage.class, "date < dateParam");
	    query.declareImports("import java.util.Date");
	    query.declareParameters("Date dateParam");
	    query.deletePersistentAll(yesterday);
	    
	    resp.setContentType("text/plain");
	    resp.getOutputStream().println("ok");
	}

}
