package cs3540;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.SwingUtilities;

import processing.core.PApplet;
import java.awt.*;

abstract class Mouse {
	float x, y;
	float offsetX, offsetY;
	float initialX = 0, initialY = 0;
	float initialCursorX = 0, initialCursorY = 0;
	float cableX, cableY;
	float w = 50, h = 70;
	boolean isDragging = false;
	float sensitivity = 3;

	Mouse(float x, float y, float cableX, float cableY) {
		this.x = x;
		this.y = y;
		this.initialX = x;
		this.initialY = y;
		this.cableX = cableX;
		this.cableY = cableY;
	}

	void update(PApplet p) {
		if (isDragging) {
			x = p.mouseX + offsetX;
			y = p.mouseY + offsetY;
		}
	}

	void display(PApplet p) {

		p.stroke(50);
		p.strokeWeight(4);
		p.noFill();

		// cable
		p.bezier(cableX, cableY, cableX, y - 50, x, y - 50, x, y - h / 2);

		p.fill(180);
		p.rect(x-(w/2), y-(h/2), w, h, 30); // body
		p.fill(150);
		p.ellipse(x, y - 10, 20, 25); // button


		p.noStroke();

		// cursor
		p.fill(255, 0, 0);
		p.ellipse(getMouseX(), getMouseY(), 10, 10);

	}

	boolean pressed(PApplet p) {
		// pressed button
		float db = p.dist(p.mouseX, p.mouseY, x, y-10);
		if (db < 20) { // Check if clicking inside the mouse body
			this.onMouseButtonPressed();
		}

		// pressed body
		float d = p.dist(p.mouseX, p.mouseY, x, y);
		if (d < w / 2 + 20) { // Check if clicking inside the mouse body
			isDragging = true;
			offsetX = x - p.mouseX;
			offsetY = y - p.mouseY;
			return true;
		}

		return false;

	}

	abstract void onMouseButtonPressed();

	float getMouseX() {
		return (x - initialX) * sensitivity + initialCursorX;
	}

	float getMouseY() {
		return (y - initialY) * sensitivity + initialCursorY;
	}

	void setMouseX(float mx) {
		initialCursorX = mx;
	}

	void setMouseY(float my) {
		initialCursorY = my;
	}

	void released(PApplet p) {
		isDragging = false;
	}
}

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

	Mouse mouse = new Mouse(600,100, 600, 0) {

		void onMouseButtonPressed() {
			attemptPress(mouse.getMouseX(), mouse.getMouseY());
		}

	};

	/**
	 * https://processing.org/reference/settings_.html#:~:text=The%20settings()%20method%20runs,commands%20in%20the%20Processing%20API.
	 */
	public void settings() {
		size(700, 700);
	}

	public void attemptPress(float pressMouseX, float pressMouseY) {
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
		if ((pressMouseX > bounds.x && pressMouseX < bounds.x + bounds.width) &&
			    (pressMouseY > bounds.y && pressMouseY < bounds.y + bounds.height)) // test to see if hit was within bounds
		{
			System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
			hits++;
		} else {
			System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
			misses++;
		}

		trialNum++; // Increment trial number

		// reset to the default square
		xCoord = 0;
		yCoord = 0;

		// in this example design, I move the cursor back to the middle after each click
		// Note. When running from eclipse the robot class affects the whole screen not
		// just the GUI, so the mouse may move outside of the GUI.
		// robot.mouseMove(width/2, (height)/2); //on click, move cursor to roughly
		// center of window!
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

		mouse.setMouseX(width/2);
		mouse.setMouseY(height/2);
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

		mouse.update(this);
		mouse.display(this);
	}

	public void mousePressed() // test to see if hit was in target!
	{
		if (!mouse.pressed(this)) { // the mouse is not being clicked
			attemptPress(mouseX, mouseY); // see if the cursor is over a tile; register the click
		}
	}

	public void mouseReleased() {
		mouse.released(this);
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

		if (trials.get(trialNum) == 	i) // see if current button is the target
			fill(0, 255, 255); // if so, fill cyan
		else if (trialNum < 15 && trials.get(trialNum + 1) == i) {
			int boundaryBuffer = 4;
			fill(0, 255, 0);
			rect(bounds.x - boundaryBuffer/2, bounds.y - boundaryBuffer/2, bounds.width + boundaryBuffer, bounds.height + boundaryBuffer);
			fill(0, 0, 0);
			rect(bounds.x, bounds.y, bounds.width, bounds.height);
			return;
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

	// can use the keyboard if you wish
	// https://processing.org/reference/keyTyped_.html
	// https://processing.org/reference/keyCode.html
	public void keyPressed() {
	    // Get the position of the application window on the screen
	    Point windowPos = new Point(0, 0);
	    Window window = SwingUtilities.getWindowAncestor((Component) surface.getNative());
	    if (window != null) {
	        windowPos = window.getLocationOnScreen();
	    }

	    // Check if the pressed key is a number between '1' and '4'
	    if (key >= '1' && key <= '4') {
	        if (firstNumSelect) {
	            xCoord = key - '0';  // Convert char to integer
	            firstNumSelect = false;  // Now waiting for the second coordinate input
	        } else {
	            yCoord = key - '0';  // Convert char to integer
	            firstNumSelect = true;  // Reset for the next input

	            // Calculate the correct button index for the 4x4 grid
	            int buttonIndex = (yCoord - 1) * 4 + (xCoord - 1);
	            Rectangle selectedButton = getButtonLocation(buttonIndex);

	            // Calculate the exact center of the button
	            int targetX = selectedButton.x + selectedButton.width*3 / 4;
	            int targetY = selectedButton.y + selectedButton.height*4 / 3;

	            // Move the mouse to the exact center of the selected button
	            robot.mouseMove(targetX + windowPos.x, targetY + windowPos.y);
	        }
	    }
	}
}
