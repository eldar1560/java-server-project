package controller;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.Model;
import server.Server;

public interface Controller {
	/**
	 * get the model that the controller work with him
	 * @return - object from type Model
	 */
	Model getModel();
	
	/**
	 * get the view that the controller work with him
	 * @return - object from type View
	 */
	Server getServer();
	
	/**
	 * set a Message that will be send to the view
	 * @param massage - the massage to send
	 */
	void setMessage(String massage);
	
	/**
	 * Get the maze by name
	 * @param parm - the name of the maze
	 * @return Maze3d
	 */
	Maze3d getMaze(String parm);
	
	/**
	 * get solution of maze
	 * @param parm - maze name
	 * @return solution of the maze
	 */
	Solution<Position> getSolution(String parm);

	/**
	 * exit from the server side
	 */
	void exit();

	/**
	 * close the server
	 */
	void close();

	/**
	 * pause the server
	 * @return true if success and false if not
	 */
	boolean pause();

	/**
	 * resume the server
	 * @return true if success and false if not
	 */
	boolean resume();
}
