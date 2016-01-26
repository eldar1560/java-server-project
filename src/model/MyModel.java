package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import algorithms.demo.SearchableMaze;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.MyMaze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.PrimMaze3dGenerator;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.AStar;
import algorithms.search.BFS;
import algorithms.search.CostComparator;
import algorithms.search.MazeAirDistance;
import algorithms.search.MazeManhattenDistance;
import algorithms.search.Solution;
import algorithms.search.State;
import controller.Controller;
import controller.Properties;

public class MyModel extends CommonModel{
	Controller controller;
	ExecutorService threadpool;
	int numberOfThreads;
	String algorithemForSolution;
	String algorithemForGenerate;
	Properties properties;
	
	/**
	 * Default Constructor of MyModel
	 */
	public MyModel(Properties properties) {
		super();
		this.properties = properties;
		setProperties(properties);
		threadpool = Executors.newFixedThreadPool(numberOfThreads);
		load();
	}
	
	/**
	 * set the properties of the server
	 * @param properties - get properties to set them
	 */
	private void setProperties(Properties properties) {
		this.numberOfThreads = properties.getNumberOfThreads();
		algorithemForGenerate = properties.getAlgorithemForGenerate();
		algorithemForSolution = properties.getAlgorithemForSolution();
	}

	@Override
	public Maze3d generate3dMaze(String str) {
		String[] parm = str.split(" ");
		String name = parm[2];
		int y = Integer.parseInt(parm[3]);
		int z = Integer.parseInt(parm[4]);
		int x = Integer.parseInt(parm[5]);
		if(hm.get(name)!=null)
			return hm.get(name);
		Callable<Maze3d> callable = new Callable<Maze3d>() {

			@Override
			public Maze3d call() throws Exception {
				Maze3d maze;
				if(algorithemForGenerate.equals("MyMaze3dGenerator"))
					maze = new MyMaze3dGenerator().generate(y, z, x);
				else if(algorithemForGenerate.equals("PrimMaze3dGenerator"))
					maze = new PrimMaze3dGenerator().generate(y, z, x);
				else
					maze = new SimpleMaze3dGenerator().generate(y, z, x);
				return maze;
			}
		};
		
		
		
		Future<Maze3d> maze3d = threadpool.submit(callable);
		try {
			hm.put(name, maze3d.get());
			return maze3d.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return null;		
	}

	@Override
	public Solution<Position> solve(String str) {
		String[] parm=str.split(" ");
		String algorithm;
		String name = parm[0];
		Solution<Position> solution = hashSolution.get(name);

		if(solution != null){
			return solution;
		}
		
		if(parm.length == 2)
			algorithm = parm[1];
		else
			algorithm = algorithemForSolution;
		Maze3d maze = hm.get(name);
		Callable<Solution<Position>> callable = new Callable<Solution<Position>>() {
			@Override
			public Solution<Position> call() throws Exception {
				if(algorithm.equalsIgnoreCase("bfs")){
					Maze3d maze = hm.get(name);
					if(maze != null){
						CostComparator<Position> c = new CostComparator<Position>();
						BFS<Position> bfs = new BFS<Position>(c);
						Solution<Position> bfsSolution = bfs.search(new SearchableMaze(maze));
						hashSolution.put(maze, bfsSolution);
						return bfsSolution;
					}
				}
				else if(algorithm.equalsIgnoreCase("MazeManhattanDistance")){
					Maze3d maze = hm.get(name);
					if(maze != null){
						CostComparator<Position> c = new CostComparator<Position>();
						AStar<Position> astarManhattanDistance = new AStar<Position>(new MazeManhattenDistance(new State<Position>(maze.getGoalPosition())),c);
						Solution<Position> astarManhattan = astarManhattanDistance.search(new SearchableMaze(maze));
						hashSolution.put(maze, astarManhattan);
						return astarManhattan;
					}
				}
				else if(algorithm.equalsIgnoreCase("MazeAirDistance")){
					Maze3d maze = hm.get(name);
					if(maze != null){
						CostComparator<Position> c = new CostComparator<Position>();
						AStar<Position> astarAirDistance = new AStar<Position>(new MazeAirDistance(new State<Position>(maze.getGoalPosition())),c);
						Solution<Position> astarAir = astarAirDistance.search(new SearchableMaze(maze));
						hashSolution.put(maze, astarAir);
						return astarAir;
					}
				}
				return hashSolution.get(hm.get(name));
			}
		
		};
			
		Future<Solution<Position>> solutionCreate = threadpool.submit(callable);
		try {
			hashSolution.put(maze, solutionCreate.get());
			return solutionCreate.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;	
	}

	@Override
	public void exit(){
		threadpool.shutdown();
		try {
			while(!(threadpool.awaitTermination(10, TimeUnit.SECONDS)));
		} catch (InterruptedException e) {
			controller.setMessage(e.getMessage());
		}
		save();
	}
	
	@Override
	public void setController(Controller controller){
		this.controller = controller;
	}
	
	@Override
	public void save() {
		ObjectOutputStream out = null;
		try{
			out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("ClientMap.zip")));
			out.writeObject(hm);
			out.flush();
		} catch (FileNotFoundException e) {
			controller.setMessage(e.getMessage());
		}
		catch(IOException e){
			controller.setMessage(e.getMessage());
		} finally{
			try{
				out.close();
			}catch(IOException e)
			{
				controller.setMessage(e.getMessage());
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void load() {
		ObjectInputStream in = null;
		try{
			in = new ObjectInputStream(new GZIPInputStream(new FileInputStream("ServerMap.zip")));
			hm = (HashMap<String, Maze3d>) in.readObject();
		} catch (FileNotFoundException e) {
			controller.setMessage(e.getMessage());
		}
		catch(IOException e){
			controller.setMessage(e.getMessage());
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
			finally{
		
			try{
				if(in!= null)
					in.close();
			}catch(IOException e)
			{
				controller.setMessage(e.getMessage());
			}
		}

	}
	
	@Override
	public boolean pause(){
		threadpool.shutdown();
		try {
			while(!(threadpool.awaitTermination(10, TimeUnit.SECONDS)));
		} catch (InterruptedException e) {
			controller.setMessage(e.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean resume(){
		threadpool = Executors.newFixedThreadPool(numberOfThreads);
		return true;
	}
}
