package dkeep.logic.layout;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dkeep.logic.engine.Game;
import dkeep.logic.entities.*;
import dkeep.logic.entities.Hero.key_t;
import dkeep.logic.objects.*;

/** Level from a game. */
public class Level implements Serializable {
	
	/** Personality of the guard. */
	static public String 	guardPersonality;
	
	/** Number of ogres. */
	static public int 		nrOgres;
	
	/** Level's possible status. */
	public enum status_t { 
		/** Level is ongoing. */ 	ONGOING,
		/** Cleared the area. */ 	PROCEED,
		/** Killed by an enemy. */ 	KILLED,
		/** Won the game. */ 		WON
		};
		
	/** Map's ID. */
	private int 			_id;
	
	/** Level's map. */
	private char[][] 		_map;
	
	/** Level's hero. */
	private Hero 			_hero;
	
	/** Level's enemies. */
	private List<Entity> 	_enemies = new ArrayList<Entity>();
	
	/** Level's doors. */
	private List<Door> 		_doors = new ArrayList<Door>();
	
	/** Level's key. */
	private DKObject 		_key;
	
	/** Level's status. */
	private status_t 		_status;
	
	/** Creates an instance of Level.
	 * @param id ID of the level. */
	public Level(int id) {
		
		guardPersonality = "Rookie";
		nrOgres = 1;
		
		_status = status_t.ONGOING;
		_loadMap(id);
		_loadEntities();
		DKObject.level = this;
	}
	
	/** Creates an instance of Level.
	 * @param id ID of the level.
	 * @param gP Personality of the guard.
	 * @param nO Number of ogres. */
	public Level(int id, String gP, int nO) {
		
		guardPersonality = gP;
		nrOgres = nO;
		
		_status = status_t.ONGOING;
		_loadMap(id);
		_loadEntities();
		DKObject.level = this;
	}

	/** @return Level's map. */
	public char[][] getMap() {
		return _map;
	}
	
	/** @return Map's ID. */
	public int getID() {
		return _id;
	}
	
	/** @return Level's hero. */
	public Hero getHero() {
		return _hero;
	}
	
	/** @return Level's enemies. */
	public List<Entity> getEnemies() {
		return _enemies;
	}
	
	/** @return Level's doors. */
	public List<Door> getDoors() {
		return _doors;
	}
	
	/** @return Level's key. */
	public DKObject getKey() {
		return _key;
	}
	
	/** @return Level's status. */
	public status_t getStatus() {
		return _status;
	}
	
	/** Updates level's status. 
	 * @param s New status. */
	public void setStatus(status_t s) {
		_status = s;
	}
	
	/** Updates level's key.
	 * @param key New key. */
	public void setKey(DKObject key) {
		_key = key;
	}
	
	/** Loads Level's map accordingly.
	 * @param mapID ID of the wanted map.
	 * @return Returns a char[][] with the map. */
	private void _loadMap(int mapID) {

		char[][] test = null;
		boolean found = false;

		// Tries reading the file
		try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/miscellaneous/maps.txt"))) {

			// Searches for the correct map in maps.txt
			for (String mapSearch; (mapSearch = br.readLine()) != null;) {
				if (mapSearch.equals("Map" + mapID)) {
					found = true;
					break;
				}
			}

			// Checks if the map was found
			if (!found)
				return;

			// If it was found starts retrieving it
			int i = 0;
			String firstLine = br.readLine();

			// Checks how many lines and columns the map is composed of
			String[] parts = firstLine.split("-");
			String lines = parts[0];
			String columns = parts[1];

			int l = Integer.parseInt(lines);
			int c = Integer.parseInt(columns);

			test = new char[l][c];

			// Creates an array representing the map
			for (String line; (line = br.readLine()) != null; i++) {

				if (line.equals(""))
					break;

				char[] tmp = line.toCharArray();
				test[i] = tmp;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		if(test != null ) {
			_id = mapID;
			_map = test;
		}
	}
	
	/** Loads the entities from the loaded map. */
	private void _loadEntities() {
		
		for(int y = 0; y < _map.length; y++) {
			
			for(int x = 0; x < _map[y].length; x++) {
				
				char icon = _map[y][x];
				
				if(icon == 'H' || icon == 'A')
					_hero = new Hero(x, y, icon);
				else if(icon == 'E' || icon == 'e' || icon == 'I' || icon == 'S')
					_doors.add(new Door(x, y, icon));
				else if(icon == 'G')
					_loadGuards(x, y);
				else if(icon == 'O')
					_loadOgres(x, y);
				else if(icon == 'k' || icon == 'l')
					_key = new DKObject(x, y, icon);
			}
		}
	}
	
	/** Loads the guards, accordingly. */
	private void _loadGuards(int x, int y) {
		
		switch(Level.guardPersonality) {
		case "Rookie":
			_enemies.add(new Rookie(x, y));
			break;
		case "Drunken":
			_enemies.add(new Drunken(x, y));
			break;
		case "Suspicious":
			_enemies.add(new Suspicious(x, y));
			break;		
		}
	}
	
	/** Loads the ogres, accordingly. */
	private void _loadOgres(int x, int y) {
		
		for(int i = 0; i < Level.nrOgres; i++)
			_enemies.add(new Ogre(x, y));		
	}
	
	/** Updates the level and its entities.
	 * @param d Direction for the hero. */
	public void updateLevel(char d) {
		
		_clearEntities();
		
		_updateEntities(d);
		
		_updateDoors();
		
		_drawEntities();
		
		_updateLevelStatus();
	}
	
	/** Clears the all entities from the level. */
	private void _clearEntities() {
		
		for(Entity enemy : _enemies)
			enemy.eraseEntity();
		
		_hero.eraseEntity();
	}
	
	/** Updates the entities from the level.
	 * @param d Direction for hero. */
	private void _updateEntities(char d) {
		
		for(Entity enemy : _enemies) {	
			if(enemy instanceof Guard)
				((Guard) enemy).patrol();
			else if(enemy instanceof Ogre)
				((Ogre) enemy).move();
		}
		
		_hero.move(d);
	}
	
	/** Updates the doors from the level. */
	private void _updateDoors() {
		
		if(_hero.getKey() == key_t.NULL)
			return;
		else if(_hero.getKey() == key_t.LEVER) {
			
			for(Door door : _doors) {
				if(door.isExit() && !door.isOpen())
					door.unlockDoor();
			}
			
			_hero.updateKey(key_t.NULL);
		}		
	}
	
	/** Draws the entities from the level. */
	private void _drawEntities() {
		
		for(Entity enemy : _enemies)
			enemy.drawEntity();
		
		_hero.drawEntity();
	}
	
	/** Updates level status. */
	private void _updateLevelStatus() {
		
		if(_foundExit())
			return;
				
		for(Entity enemy : _enemies) {
			
			if(enemy instanceof Guard && _foundByGuard((Guard) enemy))
				return;
			else if(enemy instanceof Ogre && _foundByOgre((Ogre) enemy))
				return;
		}
	}
	
	/** Checks if the hero found the exit.
	 *  @return True if he found it, false otherwise. */
	private boolean _foundExit() {
		
		for(Door door : _doors) {
			
			if(door.isExit() && door.isOpen() && door.equalPosition(_hero.getCoords())) {
				_status = status_t.PROCEED;
				return true;
			}			
		}
		
		return false;
	}
	
	/** Checks if the hero was found by a guard.
	 *  @return True if he  was found, false otherwise. */
	private boolean _foundByGuard(Guard guard) {
		
		if(guard.isHarmless())
			return false;
		else if(guard.checkHit(_hero.getX(), _hero.getY())) {
			_status = status_t.KILLED;
			return true;
		}
		
		return false;
	}
	
	/** Checks if the hero was found by a ogre.
	 *  @return True if he  was found, false otherwise. */
	private boolean _foundByOgre(Ogre ogre) {

		if(ogre.isStunned())
			return false;
		else if(ogre.checkHit(_hero.getX(), _hero.getY())) {
			_status = status_t.KILLED;
			return true;
		}
		
		return false;		
	}
	
	/** Display the level. */
	public void display() {
		
		Game.io.clear();
		
		Game.io.write(_map);
	}
	
	/** @return Summary of the game. */
	public String endgameSummary() {
		
		switch(_status) {
		case ONGOING:
			return "Moving ";
		case PROCEED:
			return "You cleared the area!";
		case KILLED:
			return "You got killed by the enemy!";
		case WON:
			return "You won!";
		default:
			return "";
		}
	}
}
