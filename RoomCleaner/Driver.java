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
		for (int i = 2; i < World.numberOfStreets(); i++) {
			in:
			for (int j = 2; j < World.numberOfAvenues(); j++) {
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

		// last resort
		if (cornerX == 0 || cornerY == 0) {
			System.err.println("The code doesn't work. :(");
			System.exit(1);
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
			// turn
			if (distanceY > 0) {
				while (!roomba.facingNorth()) {
					roomba.turnLeft();
				}
			} else if (distanceY < 0) {
				while (!roomba.facingSouth()) {
					roomba.turnLeft();
				}
			}
			// move y-direction
			for (int i = 0; i < Math.abs(distanceY); i++) {
				roomba.move();
			}
			// turn
			if (distanceX > 0) {
				while (!roomba.facingEast()) {
					roomba.turnLeft();
				}
			} else if (distanceX < 0) {
				while (!roomba.facingWest()) {
					roomba.turnLeft();
				}
			}
			// move x-direction
			for (int i = 0; i < Math.abs(distanceX); i++) {
				roomba.move();
			}
			// check if picked up beepers are greater than maxBeeper
			while (World.checkBeeper(roomba.street(), roomba.avenue())) {
				roomba.pickBeeper();
				temp++;
			}
			if (temp > maxBeeper) {
				maxBeeper = temp;
				largeX = roomba.avenue();
				largeY = roomba.street();
			}
			allBeeper += temp;
			// update values
			temp = 0;
			distance = Integer.MAX_VALUE;
			totalBeepers--;
		}
		
		System.out.println("Area: " + (length + 1) * (width + 1));
		System.out.println("Total piles: " + numBeeper);
		System.out.println("Total number: " + allBeeper);
    	System.out.println("Largest pile:  " + maxBeeper);
		System.out.println("Largest pile location: (" + largeX + ", " + largeY + ")");
		System.out.println("Average pile: " + allBeeper / numBeeper);
		System.out.println("Percent dirty: " + ((double) numBeeper / ((length + 1) * (width + 1))) * 100 + "%");
  	}	
}

