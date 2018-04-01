package dkeep.ui.cli;

import dkeep.io.ConsoleIO;

import dkeep.io.IO;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.Random;

import dkeep.logic.layout.Level;
import dkeep.logic.layout.Level.status_t;

public class Game {
	
	/** Loaded level. */
	private Level _level;
	
	static public String guardPersonality;
	static public int nrOgres;
	
	/**
	 * Input scanner.
	 */
	static public IO io;
	
	public static void main(String[] args) throws IOException, FileNotFoundException {
		
		Random rn = new Random();
		int random_guard = rn.nextInt(3);
		int random_ogres = rn.nextInt(5) + 1;
		
		String guard_type = "";
		
		switch(random_guard)
		{
		case 0:
			guard_type = "Rookie";
			break;
		case 1:
			guard_type = "Drunken";
			break;
		case 2:
			guard_type = "Suspicious";
			break;
		}
		
		Game g = new Game(new ConsoleIO(), guard_type, random_ogres);
		
		g.startGame();
	}
	
	/**
	 * Creates an object Game.
	 */
	public Game(IO io, String gP, int nO) throws IOException, FileNotFoundException {
		
		Game.guardPersonality = gP;
		Game.nrOgres = nO;
		Game.io = io;
		
		loadLevel(5);
	}
	
	/**
	 * Creates an object Game with custom Level.
	 */
	public Game(IO io, String gP, int nO, int id) throws IOException, FileNotFoundException {

		Game.guardPersonality = gP;
		Game.nrOgres = nO;
		Game.io = io;

		loadLevel(id);
	}
	
	/**
	 * Creates an object Game.
	 * @param id Level to start the game with.
	 */
	public Game(int id) throws IOException, FileNotFoundException {
		loadLevel(id);
	}
	
	/**
	 * @return Loaded level.
	 */
	public Level getCurrentLevel() {
		return _level;
	}

	/**
	 * Loads a level into the game.
	 * @param id Level to load.
	 */
	private void loadLevel(int id) throws IOException, FileNotFoundException {
		
		_level = new Level(id);
		
		//Display the initial level
		_level.display();
	}
	
	/**
	 * Starts the game (for console).
	 */
	public void startGame() throws IOException, FileNotFoundException {
		
		boolean over =  false;
		do {
			over = tickGame();
		}
		while(!over);
	}
	
	/**
	 * Ticks the game.
	 * @return True if won/lost, false if ongoing.
	 */
	public boolean tickGame() throws IOException, FileNotFoundException {
		
		if(_level.getStatus() != status_t.ONGOING)
			return true;
		
		//Read input
		char input = io.read();
		
		if(input == 'n')
			return false;
		
		//Move entities
		_level.updateLevel(input);
		
		//Display the current level
		_level.display();
		
		//Check level's status
		switch(_level.getStatus()) {
		case ONGOING:
			break;
		case PROCEED:
			if(_level.getID() < 2)
				loadLevel(_level.getID() + 1);
			else {
				_level.setStatus(status_t.WON);
				return true;
			}
			break;
		case KILLED:
			return true;
		case WON:
			return true;
		}
		
		return false;
	}
}
