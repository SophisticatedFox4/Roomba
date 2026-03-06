import java.util.ArrayList;

import kareltherobot.*;

public class Driver implements Directions {
	public static void main(String[] args) {
		// World stuff
		String name = "basicRoom.wld";
		World.readWorld(name);
		World.setVisible(true);
		World.setDelay(20);

		// AUTONOMY
		int distance, distanceX, distanceY, tempX, tempY, direction;
		boolean[][][] turns = {
				// Facing north
				{
						{ false, false }, // positive
						{ false, true } // negative
				},
				// Facing south
				{
						{ true, false }, // positive
						{ false, false } // negative
				},
				// Facing east
				{
						{ true, true }, // positive
						{ false, true } // negative
				},
				// Facing west
				{
						{ true, false }, // positive
						{ true, true } // negative
				}
		};

		// data collection
		int maxBeeper = 0;
		int allBeeper = 0;
		int largeX = 0;
		int largeY = 0;
		int numBeeper, temp;

		intPair[] corner = findCorners();
		int width = corner[1].getX();
		int length = corner[1].getY();

		Robot roomba = new Robot(corner[0].getY(), corner[0].getX(), North, 0);

		ArrayList<intPair> beepers = scanBeepers(width, length, corner[0]);

		numBeeper = beepers.size();

		// get distance
		while (beepers.size() != 0) {

			temp = 0;
			distanceX = 0;
			distanceY = 0;
			distance = length + width;
			direction = roomba.facingNorth() ? 0 : roomba.facingSouth() ? 1 : roomba.facingEast() ? 2 : 3;

			for (intPair beeper : beepers) {
				tempX = beeper.getX() - roomba.avenue();
				tempY = beeper.getY() - roomba.street();
				if (Math.abs(tempX) + Math.abs(tempY) < distance) {
					distance = Math.abs(tempX) + Math.abs(tempY);
					distanceX = tempX;
					distanceY = tempY;
				}
			}

			if (distanceX == 0)
				moveY(roomba, distanceY);
			else if (distanceY == 0)
				moveX(roomba, distanceX);
			else if (turns[direction][distanceX > 0 ? 0 : 1][distanceY > 0 ? 0 : 1]) {
				moveX(roomba, distanceX);
				moveY(roomba, distanceY);
			} else {
				moveY(roomba, distanceY);
				moveX(roomba, distanceX);
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
			beepers.removeIf(c -> c.getX() == roomba.avenue() && c.getY() == roomba.street());
			allBeeper += temp;
		}

		// final output
		System.out.println("Area: " + (length + 1) * (width + 1));
		System.out.println("Total piles: " + numBeeper);
		System.out.println("Total number: " + allBeeper);
		System.out.println("Largest pile:  " + maxBeeper);
		System.out.println("Largest pile location relative to top left corner: <" + (largeX - corner[0].getX()) + ", "
				+ (largeY - (corner[0].getY() + width)) + ">");
		System.out.println("Average pile: " + (double) allBeeper / numBeeper);
		System.out.println("Percent dirty: " + ((double) numBeeper / ((length + 1) * (width + 1))) * 100 + "%");
	}

	private static intPair[] findCorners() {
		int width, length;
		intPair[] result = new intPair[2];

		for (int i = 1; i < World.numberOfStreets(); i++) {
			loop: for (int j = 1; j < World.numberOfAvenues(); j++) {
				width = 0;
				length = 0;
				// scan corners
				if (World.checkEWWall(i - 1, j) && World.checkNSWall(i, j - 1)) {
					// get width
					while (width + i < World.numberOfStreets()) {
						if (World.checkNSWall(width + i, j - 1)) {
							if (World.checkEWWall(width + i, j))
								break;
						} else
							continue loop;
						width++;
					}
					// get length
					while (length + j < World.numberOfAvenues()) {
						if (World.checkEWWall(i - 1, length + j)) {
							if (World.checkNSWall(i, length + j))
								break;
						} else
							continue loop;
						length++;
					}
					// check opposite length
					for (int k = 0; k <= length; k++) {
						if (!World.checkEWWall(i + width, j + k))
							continue loop;
					}
					// check opposite width
					for (int k = 0; k <= width; k++) {
						if (!World.checkNSWall(i + k, j + length))
							continue loop;
					}
					result[0] = new intPair(j, i);
					result[1] = new intPair(width, length);
				}
			}
		}
		return result;
	}

	private static ArrayList<intPair> scanBeepers(int width, int length, intPair corner) {
		ArrayList<intPair> result = new ArrayList<intPair>();
		for (int i = 0; i <= width; i++) {
			for (int j = 0; j <= length; j++) {
				if (World.checkBeeper(corner.getY() + i, corner.getX() + j))
					result.add(new intPair(corner.getX() + j, corner.getY() + i));
			}
		}
		return result;
	}

	private static void moveX(Robot r, int distanceX) {
		if (distanceX > 0) {
			while (!r.facingEast())
				r.turnLeft();
		} else {
			while (!r.facingWest())
				r.turnLeft();
		}
		for (int i = 0; i < Math.abs(distanceX); i++)
			r.move();
	}

	private static void moveY(Robot r, int distanceY) {
		if (distanceY > 0) {
			while (!r.facingNorth())
				r.turnLeft();
		} else {
			while (!r.facingSouth())
				r.turnLeft();
		}
		for (int i = 0; i < Math.abs(distanceY); i++)
			r.move();
	}
}
