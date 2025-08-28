import java.util.Scanner;  //may be necessary for input

import javax.swing.JOptionPane;  //may be necessary for input

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

		for (int i = 2; i < World.numberOfStreets(); i++) {
			for (int j = 2; j < World.numberOfAvenues(); j++) {
				if (World.checkEWWall(i, j) == true) {
					if (World.checkNSWall(i, j) == true) {
						cornerX = i;
						cornerY = j;
						break;
					}
				}
			}
		}
		
		Robot roomba = new Robot(cornerX, cornerY, North, 0);
		

    System.out.println("The biggest pile was ");
  }

  public static void turnRight (Robot r) {
	for (int i = 0; i < 3; i++) {
		r.turnLeft();
	}
  }

  public static void scanBeepers (Robot r) {
	World.checkBeeper(NorthVal, EastVal);
  }

}
