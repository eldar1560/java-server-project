package model;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import controller.Controller;

/**
 * Interface Model - contains the function that we use them to give information to the client
 * @author Eldar, Ofek
 */
public interface Model {

	/**
	 * create the solution of the maze and send him to the controller
	 * @param name - the name of the maze
	 */
	Solution<Position> solve(String name);
	/**
	 * create the solution of the maze and send him to the controller
	 * @param maze - the maze
	 */
	Solution<Position> solve(Maze3d maze);
	
	/**
	 * generate new maze with specific name, and size of x, y and z
	 * @param name - the name of the new maze
	 * @param x - the size of x
	 * @param y - the size of y
	 * @param z - the size of z
	 */
	Maze3d generate3dMaze(String str);
	
	/**
	 * Exit from the side of the model in the server
	 */
	void exit();

	/**
	 * set the controller that the model work with him
	 * @param controller - get object of type controller
	 */
	void setController(Controller controller);

	/**
	 * save data to zip
	 */
	void save();

	/**
	 * load data from zip
	 */
	void load();

	/**
	 * Pause the running of threads in the server
	 */
	boolean pause();

	/**
	 * Resume the running of threads in the server
	 */
	boolean resume();
}
