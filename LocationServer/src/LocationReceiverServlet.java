import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LocationReceiverServlet extends HttpServlet {

	String[] PARAMS = {"id","latitude","longitude","time"};

	private static final long serialVersionUID = -5772869374550462861L;

	public LocationReceiverServlet(String string) {
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


			
			for (String param: PARAMS) {
				System.err.println(param + " => " + req.getParameter(param));
			}
			System.err.println();
	}

}
