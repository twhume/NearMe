import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;


public class ExampleServer {

	public static void main(String[] argv) throws Exception {
		Server server = new Server(8080);
		Context root = new Context(server,"/",Context.SESSIONS);
		root.addServlet(new ServletHolder(new LocationReceiverServlet("Receiver")), "/location");
		server.start();
	}
}
