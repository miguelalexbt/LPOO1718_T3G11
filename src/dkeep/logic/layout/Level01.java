package dkeep.logic.layout;

import java.util.Random;

import dkeep.logic.characters.*;
import dkeep.logic.characters.Hero.key_t;

public class Level01 extends Level {
	
	private Guard guard;
	
	public Level01() {
		
		map = Maps.dungeon;
		mapID =  1;
		hero = new Hero(1, 1, this);
		
		loadEnemies();
	}
	
	protected void clearEntities() {
		guard.erasePosition();
		hero.erasePosition();
	}
	
	protected void updateEntities(char d) {
		guard.patrol();
		hero.move(d);
	}
	
	protected void updateDoors() {
		
		if(hero.getKey() == key_t.NULL || hero.getKey() == key_t.UNLOCKED)
			return;
		
		if(hero.getKey() == key_t.LEVER) {
			map[5][0] = 'S';
			map[6][0] = 'S';
			
			hero.updateKey(key_t.UNLOCKED);
		}
	}
	
	protected void drawEntities() {
		guard.drawPosition();
		hero.drawPosition();
	}
	
	protected void updateLevelStatus() {
		
		//Check if hero found the exit
		if((hero.getY() == 5 || hero.getY() == 6) && hero.getX() == 0) {
			levelStatus = status_t.WON;
			return;
		}
		
		//If the guard is harmless, skip the rest
		if(guard.isHarmless())
			return;
		
		//Check if the hero is in the same spot as the guard 
		else if(hero.getX() == guard.getX() && hero.getY() == guard.getY()) {
			levelStatus = status_t.LOST;
			return;
		}
		
		//Check if the guard caught the hero
		int[][] adjacent = new int[][] {
			{ guard.getY() + 1, guard.getX()},
			{ guard.getY() - 1, guard.getX()},
			{ guard.getY(), guard.getX() + 1},
			{ guard.getY(), guard.getX() - 1}
			};

		for (int[] spot : adjacent) {
			if (map[spot[0]][spot[1]] == hero.getIcon()) {
				levelStatus = status_t.LOST;
				return;
			}
		}
	}
	
	protected void loadEnemies() {
		
		int guard_type = new Random().nextInt(3);
		
		switch(guard_type) {
		case 0:
			guard = new Rookie(8, 1, this);
			break;
		case 1:
			guard = new Drunken(8, 1, this);
			break;
		case 2:
			guard = new Suspicious(8, 1, this);
			break;
			
		default:
			guard = new Rookie(0, 0, null);				
		}
	}
}
