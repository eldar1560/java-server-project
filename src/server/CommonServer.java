package server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import controller.Controller;

/**
 * CommonServer abstract class that implements Server
 * @author Eldar ,Ofek
 */

public abstract class CommonServer implements Server {
	
	Controller controller;
	int port;
	ServerSocket server;	
	ClientHandler clinetHandler;
	int numOfClients;
	ExecutorService threadpool;	
	volatile boolean stop;	
	Thread mainServerThread;	
	int clientsHandled=0;
	
	/**
	 * CommonServer constructor
	 * @param port - get the port of the server
	 * @param numOfClients - get the number of Clients to the server
	 */
	public CommonServer(int port, int numOfClients){
		this.port = port;
		this.numOfClients = numOfClients;
		
	}

	@Override
	public abstract void start();

	@Override
	public abstract void close();

	@Override
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public abstract void setMessage(String str);

	@Override
	public abstract boolean isAlive();

	@Override
	public abstract ArrayList<String> getIps();
}