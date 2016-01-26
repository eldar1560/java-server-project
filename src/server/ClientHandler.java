package server;

import java.io.InputStream;
import java.io.OutputStream;

import controller.Controller;
/**
 * ClientHandler interface - set the functional of the client handler
 */
public interface ClientHandler {
	/**
	 * handle the connection with the customer
	 * @param inFromClient - get the socket from the client
	 * @param outToClient - get the socket to the client
	 */
	void handleClient(InputStream inFromClient, OutputStream outToClient);
	
	/**
	 * set the controller to work with him
	 * @param controller - the controller
	 */
	void setController(Controller controller);
}