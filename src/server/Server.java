package server;

import java.util.ArrayList;

import controller.Controller;
/**
 * Server Interface - set the functional of the server
 * 
 */
public interface Server {
	/**
	 * start the server
	 */
	void start();
	/**
	 * close the server
	 */
	void close();
	/**
	 * set the controller to work with him
	 * @param controller - the controller
	 */
	void setController(Controller controller);
	/**
	 * display message to the mannage of the server
	 * @param message
	 */
	void setMessage(String message);
	
	/**
	 * return if the server is up or down
	 * @return true if the server is up and down if not
	 */
	boolean isAlive();
	
	/**
	 * get the ip that connected to the server
	 * @return ArrayList of ips
	 */
	ArrayList<String> getIps();
}
