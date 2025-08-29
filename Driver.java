import java.util.Scanner;  //may be necessary for input


import kareltherobot.*;

public class Driver implements Directions {
// declared here so it is visible in all the methods!! 
// It will be assigned a value in the getInfo method
	private static Robot roomba; 
	public static void main(String[] args) {
		// LEAVE THIS ALONE!!!!!!
		Driver d = new Driver();
		Scanner scn = new Scanner(System.in);
		String wrldName = "basicRoom.wld";

		World.readWorld(wrldName);
		World.setVisible(true);
		int cornerX = 0;
		int cornerY = 0;
		int length = 1;
		int width = 1;

		outer:
		for (int i = 2; i < World.numberOfStreets(); i++) {
			for (int j = 2; j < World.numberOfAvenues(); j++) {
				inner:
				if (World.checkEWWall(i, j) == true && World.checkNSWall(i, j) == true) {
					cornerY = i;
					cornerX = j;
					// get width
					for (int k = 0; k < World.numberOfAvenues() - j; k++) {
						if (World.checkNSWall(i + k, j) == true) {
							if (World.checkEWWall (i + k, j) == true) {
								width = k;
								break;
							}
						} else {
							break inner;
						}
					}
					// get length
					for (int k = 0; k < World.numberOfStreets() - i; k++) {
						if (World.checkEWWall(i, j + k) == true) {
							if (World.checkNSWall(i,j + k) == true) {
								length = k;
								break;
							}
						} else {
							break inner;
						}
					}
					// confirm corners
					for (int k = 0; k < length; k++) {
						if (World.checkEWWall(i + width, j + k) == false) {
							break inner;
						}
					}
					for (int k = 0; k < width; k++) {
						if (World.checkNSWall(i + k, j + length) == false) {
							break inner;
						}
					}
					// box confirmed; exit code
					break outer;
				}
			}
		}
		System.out.println(length);
		System.out.println(width);

		
		Robot roomba = new Robot(cornerY, cornerX, North, 0);

		//// Check box length & width
		//for (int i = 0; i < World.numberOfAvenues() - cornerX; i++) {
		//	if (World.checkNSWall(cornerY, cornerX + i) == true) {
		//		length = i;
		//		break;
		//	}
		//}
		//for (int i = 0; i < World.numberOfStreets() - cornerY; i++) {
		//	if (World.checkEWWall(cornerY + i, cornerX) == true) {
		//		width = i;
		//		break;
		//	}
		//}
		//
		//// Scanning for beepers
		//for (int i = 0; i < length; i++) {
		//	for (int j = 0; j < width; j++) {
		//		break;
		//	}
		//}

		System.out.println("The biggest pile was ");
	}

	public static void turnRight (Robot r) {
		for (int i = 0; i < 3; i++) {
			r.turnLeft();
		}
	}

	public static void findTheBox (int length, int width) {
		// find corners
	}
		
}
