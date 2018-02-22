package characters;

import java.util.Random;

public abstract class Entity {
	
	private int x;
	private int y;
	private char icon;
	
	public Entity(int x, int y, char i) {
		this.x = x;
		this.y = y;
		icon = i;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public char getIcon() {
		return icon;
	}
	
	public void updateCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void updateIcon(char i) {
		icon = i;
	}
	
	protected void generatePosition(char direction, int x, int y) {
		
		switch(direction) {
		case 'w':
			y--;
			break;
		case 'a':
			x--;
			break;
		case 's':
			y++;
			break;
		case 'd':
			x++;
			break;
		}
		
		if(checkCollision(x, y))
			updateCoord(x, y);
	}

	protected void generateRandomPosition(int x, int y) {
		
		char direction;
		
		while(true) {
			
			direction = generateDirection();

			switch (direction) {
			case 'w':
				y--;
				break;
			case 'a':
				x--;
				break;
			case 's':
				y++;
				break;
			case 'd':
				x++;
				break;
			}

			if (checkCollision(x, y)) {
				updateCoord(x, y);
				return;
			}
			
			x = getX();
			y = getY();
		}
	}

	protected abstract boolean checkCollision(int x, int y);
	
	protected abstract void eraseLastPosition();
	
	protected char generateDirection() {

		// Random number between 0 and 3;
		int nextMove = new Random().nextInt(4);

		switch (nextMove) {
		case 0:
			return 'w';
		case 1:
			return 'a';
		case 2:
			return 's';
		case 3:
			return 'd';
		default:
			return 'n';
		}
	}
}
