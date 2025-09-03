import kareltherobot.*;

public class Driver implements Directions {
	private static Robot roomba; 
	public static void main(String[] args) {
		Driver d = new Driver();
		
		// World stuff
		String wrldName = "basicRoom.wld";
		World.readWorld(wrldName);
    	World.setVisible(true);
		World.setDelay(10);
		
		// AUTONOMY
		int cornerX = 0;
		int cornerY = 0;
		int width = 0;
		int length = 0;

		// AUTONOMY PT. 2
		int totalBeepers = 0;
		int distance = Integer.MAX_VALUE;
		int distanceX = 0;
		int distanceY = 0;

		// data collection
		int maxBeeper = 0;
		int numBeeper = 0;
		int allBeeper = 0;
		int largeX = 0;
		int largeY = 0;
		int temp = 0;

		out:
		for (int i = 1; i < World.numberOfStreets(); i++) {
			in:
			for (int j = 1; j < World.numberOfAvenues(); j++) {
				// scan corners
				if (World.checkEWWall(i - 1, j) && World.checkNSWall(i, j - 1)) {
					// get width
					while (width + i < World.numberOfStreets()) {
						if (World.checkNSWall(width + i, j - 1)) {
							if (World.checkEWWall(width + i, j)) {
								break;
							}
						} else {
							width = 0;
							length = 0;
							continue in;
						}
						width++;
					}
					// get length
					while (length + j < World.numberOfAvenues()) {
						if (World.checkEWWall(i - 1, length + j)) {
							if (World.checkNSWall(i, length + j)) {
								break;
							}
						} else {
							width = 0;
							length = 0;
							continue in;
						}
						length++;
					}
					// check opposite length
					for (int k = 0; k <= length; k++) {
						if (!World.checkEWWall(i + width, j + k)) {
							width = 0;
							length = 0;
							continue in;
						}
					}
					// check opposite width
					for (int k = 0; k <= width; k++) {
						if (!World.checkNSWall(i + k, j + length)) {
							width = 0;
							length = 0;
							continue in;
						}
					}
					cornerX = j;
					cornerY = i;
					break out;
				}
			}
		}
		
		Robot roomba = new Robot(cornerY, cornerX, North, 0);
		
		// scan total beepers
		for (int i = 0; i <= width; i++) {
			for (int j = 0; j <= length; j++) {
				if (World.checkBeeper(cornerY + i, cornerX + j)) {
					totalBeepers++;
				}
			}
		}

		numBeeper = totalBeepers;

		// get distance
		while (totalBeepers != 0) {
			for (int i = 0; i <= width; i++) {
				for (int j = 0; j <= length; j++) {
					if (World.checkBeeper(cornerY + i, cornerX + j)) {
						if (Math.abs(cornerY + i - roomba.street()) + Math.abs(cornerX + j - roomba.avenue()) <= distance) {
							distance = Math.abs(cornerY + i - roomba.street()) + Math.abs(cornerX + j - roomba.avenue());
							distanceX = cornerX + j - roomba.avenue();
							distanceY = cornerY + i - roomba.street();
						}
					}
				}
			}
			if (Math.signum(distanceX) == 0) {
				moveY(roomba, distanceY);
			} else if (Math.signum(distanceY) == 0) {
				moveX(roomba, distanceX);
			} else if (Math.signum(distanceX) == Math.signum(distanceY)) {
				if (distanceX > 0) {
					if (roomba.facingNorth()) {
						moveY(roomba, distanceY);
						moveX(roomba, distanceX);
					} else {
						moveX(roomba, distanceX);
						moveY(roomba, distanceY);
					}
				} else {
					if (roomba.facingSouth()) {
						moveY(roomba, distanceY);
						moveX(roomba, distanceX);
					} else {
						moveX(roomba, distanceX);
						moveY(roomba, distanceY);
					}
				}
			} else {
				if (distanceX > 0) {
					if (roomba.facingEast()) {
						moveX(roomba, distanceX);
						moveY(roomba, distanceY);
					} else {
						moveY(roomba, distanceY);
						moveX(roomba, distanceX);
					}
				} else {
					if (roomba.facingWest()) {
						moveX(roomba, distanceX);
						moveY(roomba, distanceY);
					} else {
						moveY(roomba, distanceY);
						moveX(roomba, distanceX);
					}
				}
			}
			// pick up beepers
			while (World.checkBeeper(roomba.street(), roomba.avenue())) {
				roomba.pickBeeper();
				temp++;
			}
			// check if picked up beepers are greater than maxBeeper
			if (temp > maxBeeper) {
				maxBeeper = temp;
				largeX = roomba.avenue();
				largeY = roomba.street();
			}
			// update values
			allBeeper += temp;
			temp = 0;
			distance = Integer.MAX_VALUE;
			totalBeepers--;
		}
		
		// final output
		System.out.println("Area: " + (length + 1) * (width + 1));
		System.out.println("Total piles: " + numBeeper);
		System.out.println("Total number: " + allBeeper);
    	System.out.println("Largest pile:  " + maxBeeper);
		System.out.println("Largest pile location: (" + largeX + ", " + largeY + ")");
		System.out.println("Average pile: " + allBeeper / numBeeper);
		System.out.println("Percent dirty: " + ((double) numBeeper / ((length + 1) * (width + 1))) * 100 + "%");
  	}
	
	public static void moveX (Robot r, int distanceX) {
		// turn
		if (distanceX > 0) {
			while (!r.facingEast()) {
				r.turnLeft();
			}
		} else if (distanceX < 0) {
			while (!r.facingWest()) {
				r.turnLeft();
			}
		}
		// move
		for (int i = 0; i < Math.abs(distanceX); i++) {
			r.move();
		}
	}

	public static void moveY (Robot r, int distanceY) {
		// turn
		if (distanceY > 0) {
			while (!r.facingNorth()) {
				r.turnLeft();
			}
		} else if (distanceY < 0) {
			while (!r.facingSouth()) {
				r.turnLeft();
			}
		}
		// move
		for (int i = 0; i < Math.abs(distanceY); i++) {
			r.move();
		}
	}
}


