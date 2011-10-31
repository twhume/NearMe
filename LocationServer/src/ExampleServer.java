import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * The simplest web server I could construct, just for test purposes.
 * Uses the Jetty library to listen on a port and pass hits to /location
 * through to a simple servlet.
 * 
 * @author twhume
 *
 */

public class ExampleServer {

	public static void main(String[] argv) throws Exception {
		Server server = new Server(8080);
		Context root = new Context(server,"/",Context.SESSIONS);
		root.addServlet(new ServletHolder(new LocationReceiverServlet()), "/location");
		server.start();
	}
}
