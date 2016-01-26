package controller;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.Model;
import server.Server;

public class MyController extends CommonController{
	/**
	 * MyController constructor - get Model and View
	 * initialize the model and view in the CommonController
	 * create the HashMap from String to Command
	 * @param model - get object from type Model
	 * @param view - get object from type View
	 */
	
	public MyController(Model model, Server server) {
		super(model, server);
	}

	@Override
	public void setMessage(String message) {
		this.server.setMessage(message);

	}

	@Override
	public Maze3d getMaze(String parm) {
		Maze3d maze = model.generate3dMaze(parm);
		return maze;
	}

	@Override
	public Solution<Position> getSolution(String parm) {
		Solution<Position> solution = model.solve(parm);
		return solution;
	}
	@Override
	public void exit(){
		model.exit();
	}
	
	@Override
	public void close(){
		model.exit();
	}
	
	@Override
	public boolean pause(){
		return model.pause();
	}
	
	@Override
	public boolean resume(){
		return model.resume();
	}
}
