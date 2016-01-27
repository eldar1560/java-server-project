package controller;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.Model;
import server.Server;

public abstract class CommonController implements Controller{
	
	Model model;
	Server server;
	
	/**
	 * CommonController Constructor - initialize the Model and View that
	 * the controller work with them
	 * @param model - get object from type Model
	 * @param view - get object from type View
	 */
	public CommonController(Model model, Server server){
		this.model = model;
		this.server = server;
	}
	
	@Override
	public Model getModel() { return model; }
	
	@Override
	public Server getServer() {return server; }
	
	@Override
	public abstract void setMessage(String massage);
	
	@Override
	public abstract Maze3d getMaze(String parm);
	
	@Override
	public abstract Solution<Position> getSolution(String parm);

	@Override 
	public abstract Solution<Position> getSolution(Maze3d maze);
	@Override
	public abstract void exit();

	@Override
	public abstract boolean pause();
	
	@Override
	public abstract boolean resume();
}
