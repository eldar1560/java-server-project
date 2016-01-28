package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * MyServer class - manage the side of the server
 * @author Eldar,Ofek
 */

public class MyServer extends CommonServer implements Runnable{
	ClientHandler clientHandler;
	ArrayList<String> ips;
	HashMap<String, Socket> ipToSocket;
	protected Display display;
	protected Shell shell;
	Table table;
	Label statusText;
	int numOfClients;
	
	/**
	 * Constructor to MyServer 
	 * @param port the port of the server
	 * @param clientHandler the client handler that we use to manage the client
	 * @param numOfClients number of the clients that connect to the server
	 */
	public MyServer(int port,ClientHandler clientHandler,int numOfClients) {
		super(port, numOfClients);
		this.clientHandler = clientHandler;
		this.numOfClients = numOfClients;
		this.threadpool=Executors.newFixedThreadPool(numOfClients);
		ips = new ArrayList<String>();
		ipToSocket = new HashMap<String,Socket>();
		this.display = new Display();
		shell = new Shell(display);
		shell.setSize(550, 400);
		shell.setText("Server");
	}
	
	
	public void start(){
		try {
			server=new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			server.setSoTimeout(10*1000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		mainServerThread=new Thread(new Runnable() {			
			@Override
			public void run() {
				while(!stop){
					try {
						final Socket someClient=server.accept();
						if(someClient!=null){
							threadpool.execute(new Runnable() {									
								@Override
								public void run() {
									try{	
										int i = 0;
										boolean isContains;
										do{
											i++;
											isContains = ips.contains(someClient.getInetAddress().toString().split("/")[1] + "_" + i);
										}while(isContains);
										ips.add(someClient.getInetAddress().toString().split("/")[1] + "_" + i);
										ipToSocket.put(someClient.getInetAddress().toString().split("/")[1] + "_" + i, someClient);

										clientsHandled++;
									    
										clientHandler.setController(controller);
										clientHandler.handleClient(someClient.getInputStream(), someClient.getOutputStream());
										ips.remove(someClient.getInetAddress().toString().split("/")[1] + "_" + i);
										ipToSocket.remove(someClient.getInetAddress().toString().split("/")[1] + "_" + i);
										someClient.close();
										clientsHandled--;
									}catch(IOException e){
										System.out.println(e.getMessage());
									}									
								}
							});								
						}
					}
					catch (SocketTimeoutException e){} 
					catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			} // end of the mainServerThread task
		});
		
		mainServerThread.start();
		
	}
	
	public void close(){
		pause();
	}
	/**
	 * pause the server
	 */
	public void pause(){
		stop=true;
		if(!statusText.isDisposed()){
			statusText.setText("shutting down");
		    statusText.update();
		}
		
	    
		threadpool.shutdown();
		if(!controller.pause()){
			if(!statusText.isDisposed()){
				statusText.setText("Problem with close server");
				statusText.update();
			}
			return;
		}
		
		for(int i = 0; i <ips.size();i++){
			Socket soc = ipToSocket.get(ips.get(i));
			ipToSocket.remove(ips.get(i));
			ips.remove(i);
			try {
				soc.close();
			} catch (IOException e) {
				if(!statusText.isDisposed()){
					statusText.setText(e.getMessage());
					statusText.update();
				}
			}
		}
		
		threadpool.shutdownNow();
		
		try {
			mainServerThread.join();
		} catch (InterruptedException e) {
			if(!statusText.isDisposed()){
				statusText.setText(e.getMessage());
				statusText.update();
			}
		}
		
		try {
			server.close();
		} catch (IOException e) {
			if(!statusText.isDisposed()){
				statusText.setText(e.getMessage());
				statusText.update();
			}
		}
	}
	
	public void resume(){
		stop=false;	
		if(!controller.resume()){
			if(!statusText.isDisposed()){
				statusText.setText("Problem with up server");
				statusText.update();
			}
			return;
		}
		if(!statusText.isDisposed()){
			statusText.setText("Up serever");
			statusText.update();
		}
		threadpool = Executors.newFixedThreadPool(numOfClients);
		start();
	}
	
	@Override
	public boolean isAlive(){
		return !this.stop;
	}
	
	@Override
	public void setMessage(String str){
		statusText.setText(str);
		statusText.update();
	}

	@Override
	public ArrayList<String> getIps() {
		return ips;
	}
	
	/**
	 * Configure the window widgets
	 */
	public void initWidgets(){
		shell.setLayout(new GridLayout(3, false));
		
		Button mode = new Button(shell, SWT.PUSH);
		mode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		mode.setText("Change Server Mode");
		
		Label label = new Label(shell, SWT.NONE);
		if(isAlive()){
			label.setText("Online");
			shell.setBackground(new Color (display,0,255,0));
		}
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button closeServer = new Button(shell, SWT.PUSH);
		closeServer.setText("Close the server");
		closeServer.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
		
		Button closeSession = new Button(shell, SWT.PUSH);
		closeSession.setText("Stop the connection with specific client");

		
		table = new Table(shell, SWT.PUSH | SWT.MULTI | SWT.BORDER
		        | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
	    table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,3));
		
		TableColumn column = new TableColumn(table, SWT.NONE);
	    column.setText("IP Address");
	    column.setData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
	    column.setWidth(shell.getSize().x);
	    
	    statusText = new Label(shell,SWT.NONE);
	    statusText.setText("Server is UP");
	    statusText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,3,1));
	    
	    getIps();
	    table.clearAll();
	    for(int i = 0; i < ips.size();i++){
	    	TableItem item = new TableItem(table, SWT.Selection, i);
	    	item.setText(0,ips.get(i));
	    	table.setItemCount(ips.size());
	    }
	    table.update();

	    
	    closeServer.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				close();
				shell.dispose();
				
			}
		});
	    
	    closeSession.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event arg0)
	    	{
	    		TableItem[] tableItems = table.getSelection();
	    		if(tableItems.length > 0){
	    			for(int i = 0; i<tableItems.length;i++){
	    				Socket soc = null;
	    				String str = null;
	    				str = tableItems[i].getText();
	    				if(str!=null)
					    	 soc=ipToSocket.get(str);
	    				try {
					    	 if(soc!=null)
					    		 soc.close();
					          }
	    				catch (IOException e) {
	    					e.printStackTrace();
					    }
	    				ips.remove(str);
	    				ipToSocket.remove(str);	
		    		    }
	    		}
	    	}
	    			

		});
	    
	    mode.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				if(isAlive()){
					label.setText("shutting down");
					pause();
					label.setBackground(new Color(display,255,255,255));
					label.setText("Offline");
					shell.setBackground(new Color (display,255,0,0));
					if(!statusText.isDisposed()){
						statusText.setText("Offline");
						statusText.update();
					}
				}
				else{
					resume();
					label.setText("Online");
					shell.setBackground(new Color (display,0,255,0));
					if(!statusText.isDisposed()){
						statusText.setText("Online");
						statusText.update();
					}
				}

			}
		});
	    
	}
	
	/**
	 * start the thread of the window
	 */
	@Override
	public void run() {
		initWidgets();
		shell.open();
		start();
		long a,b,c;
		while(!shell.isDisposed()){
			a = System.currentTimeMillis();
	        c=System.currentTimeMillis();
			if(!display.readAndDispatch())
				display.sleep();
			c=System.currentTimeMillis()-c;
			if(!table.isDisposed())
			{
				b=System.currentTimeMillis();
				b+=c;
				if(b-a>=1500)
					 updateTable();
			}
		}
		close();
		display.dispose();
		
	}
	/**
	 * update the table that contains the ip addresses
	 */
	private void updateTable(){
		table.clearAll();				
	    for(int i = 0; i < ips.size();i++){
	    	TableItem item = new TableItem(table, SWT.Selection, i);
	    	item.setText(0,ips.get(i));
	    	table.setItemCount(ips.size());
	    }
	    table.update();
	    if(!stop){
	    	Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        if(!statusText.isDisposed()){
	        	statusText.setText("Table is update to: " + sdf.format(cal.getTime()));
	        	statusText.update();
	        }
	    }
	}
}