import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Really simple servlet that just dumps specific parameters it's sent to stderr
 * 
 * @author twhume
 *
 */

public class LocationReceiverServlet extends HttpServlet {

	/* These are the names of the parameters we're interested in */
	private static String[] PARAMS = {"id","latitude","longitude","time"};

	private static final long serialVersionUID = -5772869374550462861L;

	/**
	 * Make GET pass things onto POST. The only reason we do this is to make it easier
	 * to debug; you can pass parameters into a GET by appending them onto the URL
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * Ultra-simple POST method. Just print out all the relevant parameters we receive.
	 */
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

			for (String param: PARAMS) {
				System.err.println(param + " => " + req.getParameter(param));
			}
			System.err.println();
	}

}
