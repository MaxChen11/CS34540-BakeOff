package cs3540;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.SwingUtilities;

import processing.core.PApplet;
import java.awt.*;

public class BakeOff1 extends PApplet {
	// when in doubt, consult the Processsing reference:
	// https://processing.org/reference/
	// The argument passed to main must match the class name
	public static void main(String[] args) {
		// Tell processing what class we want to run.
		PApplet.main("cs3540.BakeOff1");
	}

	int margin = 200; // set the margin around the squares
	final int padding = 50; // padding between buttons and also their width/height
	final int buttonSize = 40; // padding between buttons and also their width/height
	ArrayList<Integer> trials = new ArrayList<Integer>(); // contains the order of buttons that activate in the test
	int trialNum = 0; // the current trial number (indexes into trials array above)
	int startTime = 0; // time starts when the first click is captured
	int finishTime = 0; // records the time of the final click
	int hits = 0; // number of successful clicks
	int misses = 0; // number of missed clicks
	Robot robot; // initialized in setup

	int numRepeats = 1; // sets the number of times each button repeats in the test
	
	boolean firstNumSelect = true;
	int xCoord = 0;
	int yCoord = 0;
	int currButtonSelected = 0;

	/**
	 * https://processing.org/reference/settings_.html#:~:text=The%20settings()%20method%20runs,commands%20in%20the%20Processing%20API.
	 */
	public void settings() {
		size(700, 700);
	}

	/**
	 * // https://processing.org/reference/setup_.html
	 */
	public void setup() {
		// noCursor(); // hides the system cursor if you want
		noStroke(); // turn off all strokes, we're just using fills here (can change this if you
					// want)
		textFont(createFont("Arial", 16)); // sets the font to Arial size 16
		textAlign(CENTER);
		frameRate(60); // normally you can't go much higher than 60 FPS.
		ellipseMode(CENTER); // ellipses are drawn from the center (BUT RECTANGLES ARE NOT!)
		// rectMode(CENTER); //enabling will break the scaffold code, but you might find
		// it easier to work with centered rects

		try {
			robot = new Robot(); // create a "Java Robot" class that can move the system cursor
		} catch (AWTException e) {
			e.printStackTrace();
		}

		// ===DON'T MODIFY MY RANDOM ORDERING CODE==
		for (int i = 0; i < 16; i++) // generate list of targets and randomize the order
			// number of buttons in 4x4 grid
			for (int k = 0; k < numRepeats; k++)
				// number of times each button repeats
				trials.add(i);

		Collections.shuffle(trials); // randomize the order of the buttons
		System.out.println("trial order: " + trials); // print out order for reference

		surface.setLocation(0, 0);// put window in top left corner of screen (doesn't always work)
	}

	public void draw() {
		background(0); // set background to black

		if (trialNum >= trials.size()) // check to see if test is over
		{
			float timeTaken = (finishTime - startTime) / 1000f;
			float penalty = constrain(((95f - ((float) hits * 100f / (float) (hits + misses))) * .2f), 0, 100);
			fill(255); // set fill color to white
			// write to screen (not console)
			text("Finished!", width / 2, height / 2);
			text("Hits: " + hits, width / 2, height / 2 + 20);
			text("Misses: " + misses, width / 2, height / 2 + 40);
			text("Accuracy: " + (float) hits * 100f / (float) (hits + misses) + "%", width / 2, height / 2 + 60);
			text("Total time taken: " + timeTaken + " sec", width / 2, height / 2 + 80);
			text("Average time for each button: " + nf((timeTaken) / (float) (hits + misses), 0, 3) + " sec", width / 2,
					height / 2 + 100);
			text("Average time for each button + penalty: "
					+ nf(((timeTaken) / (float) (hits + misses) + penalty), 0, 3) + " sec", width / 2,
					height / 2 + 140);
			return; // return, nothing else to do now test is over
		}

		fill(255); // set fill color to white
		text((trialNum + 1) + " of " + trials.size(), 40, 20); // display what trial the user is on
		
		String xString = "";
		String yString = "";
		
		xString = str(xCoord);
		yString = str(yCoord);
		
		if (xCoord == 0) {
			xString = "_";
		}
		if (yCoord == 0) {
			yString = "_";
		}
		
		int verticalOffset = 150;
		
		// Currently Selected Coordinate Text
		text("Currently Selected: ( " + xString + " , " + yString + " )", width/2, verticalOffset);
		
		// Target Coordinate Text
		// Green if currently selected, Orange otherwise
		if (xCoord == (trials.get(trialNum)%4 + 1) && yCoord == (trials.get(trialNum)/4 + 1)) {
			fill(0, 255, 0);
		} else {
			fill(255, 165, 0);
		}
		text("Target: (" + (trials.get(trialNum)%4 + 1) + ", " + (trials.get(trialNum)/4 + 1) + ")", width/2, verticalOffset + 20); 
		
		fill(150);
		if (trialNum + 1 < 16) {
			text("Next Target: (" + (trials.get(trialNum + 1)%4 + 1) + ", " + (trials.get(trialNum + 1)/4 + 1) + ")", width/2, verticalOffset + 40);
		} else {
			text("No more Trials", width/2, verticalOffset + 40);
		}
		for (int i = 0; i < 16; i++)// for all button
			drawButton(i); // draw button

		fill(255, 0, 0, 200); // set fill color to translucent red
		ellipse(mouseX, mouseY, 20, 20); // draw user cursor as a circle with a diameter of 20

	}

	public void mousePressed() // test to see if hit was in target!
	{
		if (trialNum >= trials.size()) // check if task is done
			return;

		if (trialNum == 0) // check if first click, if so, record start time
			startTime = millis();

		if (trialNum == trials.size() - 1) // check if final click
		{
			finishTime = millis();
			// write to terminal some output:
			System.out.println("we're all done!");
		}

		Rectangle bounds = getButtonLocation(trials.get(trialNum));

		// check to see if cursor was inside button
		if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width)
				&& (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) // test to see if hit was within bounds
		{
			System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
			hits++;
		} else {
			System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
			misses++;
		}

		trialNum++; // Increment trial number
		xCoord = 0;
		yCoord = 0;

		// in this example design, I move the cursor back to the middle after each click
		// Note. When running from eclipse the robot class affects the whole screen not
		// just the GUI, so the mouse may move outside of the GUI.
		// robot.mouseMove(width/2, (height)/2); //on click, move cursor to roughly
		// center of window!
	}

	// probably shouldn't have to edit this method
	public Rectangle getButtonLocation(int i) // for a given button ID, what is its location and size
	{
		int x = (i % 4) * (padding + buttonSize) + margin;
		int y = (i / 4) * (padding + buttonSize) + margin;

		return new Rectangle(x, y, buttonSize, buttonSize);
	}

	// you can edit this method to change how buttons appear
	public void drawButton(int i) {
		Rectangle bounds = getButtonLocation(i);

		if (trials.get(trialNum) == i) // see if current button is the target
			fill(0, 255, 255); // if so, fill cyan
		else if (trials.get(trialNum) == i + 1) {
			int boundaryBuffer = 10;
			fill(0, 255, 0);
			rect(bounds.x - boundaryBuffer/2, bounds.y - boundaryBuffer/2, bounds.width + boundaryBuffer, bounds.height + boundaryBuffer);
			fill(0, 0, 0);
			rect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		else
			fill(200); // if not, fill gray

		rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void mouseMoved() {
		// can do stuff everytime the mouse is moved (i.e., not clicked)
		// https://processing.org/reference/mouseMoved_.html
	}

	public void mouseDragged() {
		// can do stuff everytime the mouse is dragged
		// https://processing.org/reference/mouseDragged_.html
	}

	public void keyPressed() {
		Point windowPos = new Point(0, 0);
		Window window = SwingUtilities.getWindowAncestor((Component) surface.getNative());
		if (window != null) {
			windowPos = window.getLocationOnScreen();
		}
		
		if (key == '1' || key == '2' || key == '3' || key == '4') {
			
		
		
		if (firstNumSelect) 
		{
			yCoord = 0;
			switch(key) {
			case '1':
				currButtonSelected = 0;
				firstNumSelect = !firstNumSelect;
				xCoord = 1;
				break;
			case '2':
				currButtonSelected = 1;
				firstNumSelect = !firstNumSelect;
				xCoord = 2;
				break;
			case '3':
				currButtonSelected = 2;
				firstNumSelect = !firstNumSelect;
				xCoord = 3;
				break;
			case '4':
				currButtonSelected = 3;
				firstNumSelect = !firstNumSelect;
				xCoord = 4;
				break;
			default:
				break;
			}
			
			Rectangle selectedButton = getButtonLocation(currButtonSelected);

			robot.mouseMove(selectedButton.x + selectedButton.width*3/4 + windowPos.x, 
							selectedButton.y + selectedButton.height*4/3 + windowPos.y);
		} 
		else if (!firstNumSelect) 
		{
			switch(key) {
			case '1':
				currButtonSelected += 0;
				firstNumSelect = !firstNumSelect;
				yCoord = 1;
				break;
			case '2':
				currButtonSelected += 4;
				firstNumSelect = !firstNumSelect;
				yCoord = 2;
				break;
			case '3':
				currButtonSelected += 8;
				firstNumSelect = !firstNumSelect;
				yCoord = 3;
				break;
			case '4':
				currButtonSelected += 12;
				firstNumSelect = !firstNumSelect;
				yCoord = 4;
				break;
			default:
				break;
			}
			Rectangle selectedButton = getButtonLocation(currButtonSelected);

			robot.mouseMove(selectedButton.x + selectedButton.width*3/4 + windowPos.x, 
							selectedButton.y + selectedButton.height*4/3 + windowPos.y);
		}
		}
		
		// can use the keyboard if you wish
		// https://processing.org/reference/keyTyped_.html
		// https://processing.org/reference/keyCode.html
		
	}
}
