package boot;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import controller.Controller;
import controller.MyController;
import controller.Properties;
import model.MyModel;
import server.Maze3dClientHandler;
import server.MyServer;

public class Run {

	public static void main(String[] args) {
		XMLDecoder d;
		Properties properties = new Properties();
		
		try {
			d = new XMLDecoder(new BufferedInputStream(new FileInputStream("ServerProperties.xml")));
			properties = (Properties) d.readObject();
			d.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		MyModel model = new MyModel(properties);
		MyServer server=new MyServer(properties.getPort(),new Maze3dClientHandler(),properties.getNumberOfThreads());
		Controller controller = new MyController(model,server);
		
		model.setController(controller);
		server.setController(controller);
		
		server.run();

	}

}
