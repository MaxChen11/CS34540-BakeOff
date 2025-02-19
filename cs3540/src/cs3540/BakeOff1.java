package cs3540;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.SwingUtilities;

import processing.core.PApplet;
import java.awt.*;

/**
 * This application measures the accuracy and efficiency of users clicking on
 * randomly appearing targets in a 4x4 grid using a simulated mouse cursor.
 * 
 * This application uses the Processing library for graphics rendering.
 * 
 * @author Charles Strauss, Max Sone and Moon Jang(Group 15)
 * @version February 19, 2025
 */
public class BakeOff1 extends PApplet {

	/**
	 * Entry point for the application. Initializes and runs the Processing sketch.
	 * 
	 * https://processing.org/reference/
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		// Tell processing what class we want to run.
		PApplet.main("cs3540.BakeOff1");
	}

	// Layout parameters
	int margin = 200; // set the margin around the squares
	final int padding = 50; // padding between buttons and also their width/height
	final int buttonSize = 40; // padding between buttons and also their width/height

	// Experiment tracking variables
	ArrayList<Integer> trials = new ArrayList<Integer>(); // contains the order of buttons that activate in the test
	int trialNum = 0; // the current trial number (indexes into trials array above)
	int startTime = 0; // time starts when the first click is captured
	int finishTime = 0; // records the time of the final click
	int hits = 0; // number of successful clicks
	int misses = 0; // number of missed clicks

	// Java Robot for automated cursor movement
	Robot robot;

	// Experiment configuration
	int numRepeats = 1; // sets the number of times each button repeats in the test

	// Key press tracking for selecting a target using coordinates
	boolean firstNumSelect = true; // Tracks whether the user is selecting X or Y coordinate
	int xCoord = 0; // Selected X coordinate
	int yCoord = 0; // Selected Y coordinate
	int currButtonSelected = 0; // Index of the currently selected button

	/**
	 * The virtual Mouse object simulates a user-controlled cursor. This overrides
	 * the onMouseButtonPressed() method to attempt pressing the currently targeted
	 * button.
	 */
	Mouse mouse = new Mouse(600, 100, 600, 0) {
		@Override
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

	/**
	 * Processes a mouse click or a simulated click to determine if it was within
	 * the target button.
	 * 
	 * https://processing.org/reference/millis_.html
	 * 
	 * @param pressMouseX The X-coordinate of the mouse click or cursor position.
	 * @param pressMouseY The Y-coordinate of the mouse click or cursor position.
	 */
	public void attemptPress(float pressMouseX, float pressMouseY) {
		// Check if all trials are completed
		if (trialNum >= trials.size())
			return; // Exit early if there are no more trials

		// Record the start time if this is the first click of the test
		if (trialNum == 0)
			startTime = millis();

		// If this is the last click in the test, record the finish time
		if (trialNum == trials.size() - 1) {
			finishTime = millis();
			// write to terminal some output:
			System.out.println("we're all done!");
		}

		// Get the location and dimensions of the target button
		Rectangle bounds = getButtonLocation(trials.get(trialNum));

		// Check if the click was inside the button's bounds
		if ((pressMouseX > bounds.x && pressMouseX < bounds.x + bounds.width)
				&& (pressMouseY > bounds.y && pressMouseY < bounds.y + bounds.height)) {
			System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
			hits++; // Increment hit counter
		} else {
			System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
			misses++; // Increment miss counter
		}

		trialNum++; // Move to the next trial

		// Reset the selected coordinates
		xCoord = 0;
		yCoord = 0;
	}

	/**
	 * //
	 */

	/**
	 * Sets up the environment and initializes variables for the experiment.
	 *
	 * https://processing.org/reference/setup_.html
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

		// Initialize the virtual mouse position to the center of the screen
		mouse.setMouseX(width / 2);
		mouse.setMouseY(height / 2);
	}

	/**
	 * Continuously updates and renders the experiment interface.
	 *
	 * https://processing.org/reference/text_.html
	 * https://processing.org/reference/ellipse_.html
	 */
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

		// Convert selected coordinates to string format
		String xString = (xCoord == 0) ? "_" : str(xCoord);
		String yString = (yCoord == 0) ? "_" : str(yCoord);

		int verticalOffset = 150;

		// Display the currently selected coordinate
		text("Currently Selected: ( " + xString + " , " + yString + " )", width / 2, verticalOffset);

		// Determine the current target coordinates
		int targetX = (trials.get(trialNum) % 4) + 1;
		int targetY = (trials.get(trialNum) / 4) + 1;

		// Change text color based on selection status (green if selected, orange
		// otherwise)
		if (xCoord == targetX && yCoord == targetY) {
			fill(0, 255, 0); // Green if correct
		} else {
			fill(255, 165, 0); // Orange if incorrect
		}

		// Display the current target
		text("Target: (" + targetX + ", " + targetY + ")", width / 2, verticalOffset + 20);

		// Display the next target, if applicable
		fill(150); // Set text color to gray
		if (trialNum + 1 < trials.size()) {
			int nextTargetX = (trials.get(trialNum + 1) % 4) + 1;
			int nextTargetY = (trials.get(trialNum + 1) / 4) + 1;
			text("Next Target: (" + nextTargetX + ", " + nextTargetY + ")", width / 2, verticalOffset + 40);
		} else {
			text("No more Trials", width / 2, verticalOffset + 40);
		}

		// Draw all buttons in the grid
		for (int i = 0; i < 16; i++) {
			drawButton(i);
		}

		// Draw the user cursor (red translucent circle)
		fill(255, 0, 0, 200);
		ellipse(mouseX, mouseY, 20, 20);

		// Update and display the virtual mouse
		mouse.update(this);
		mouse.display(this);
	}

	/**
	 * Handles mouse click events.
	 * 
	 * https://processing.org/reference/mousePressed_.html
	 */
	public void mousePressed() {
		// Check if the virtual mouse is being clicked
		if (!mouse.pressed(this)) {
			// If not dragging, attempt to register a button press at the cursor position
			attemptPress(mouseX, mouseY);
		}
	}

	/**
	 * Handles mouse release events.
	 * 
	 * https://processing.org/reference/mouseReleased_.html
	 */
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

	/**
	 * Draws a button at the specified index in the 4x4 grid.
	 * 
	 * @param i The index of the button in the 4x4 grid (0 to 15).
	 */
	public void drawButton(int i) {
		Rectangle bounds = getButtonLocation(i);

		if (trials.get(trialNum) == i) // see if current button is the target
			fill(0, 255, 255); // if so, fill cyan
		else if (trialNum < 15 && trials.get(trialNum + 1) == i) {
			int boundaryBuffer = 4;
			fill(0, 255, 0);
			rect(bounds.x - boundaryBuffer / 2, bounds.y - boundaryBuffer / 2, bounds.width + boundaryBuffer,
					bounds.height + boundaryBuffer);
			fill(0, 0, 0);
			rect(bounds.x, bounds.y, bounds.width, bounds.height);
			return;
		} else
			fill(200); // if not, fill gray

		rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * Handles keyboard input to select grid coordinates and move the cursor.
	 * 
	 * https://processing.org/reference/keyTyped_.html
	 * https://processing.org/reference/keyCode.html
	 */
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
				xCoord = key - '0'; // Convert char to integer
				firstNumSelect = false; // Now waiting for the second coordinate input
			} else {
				yCoord = key - '0'; // Convert char to integer
				firstNumSelect = true; // Reset for the next input

				// Calculate the correct button index for the 4x4 grid
				int buttonIndex = (yCoord - 1) * 4 + (xCoord - 1);
				Rectangle selectedButton = getButtonLocation(buttonIndex);

				// Calculate the exact center of the button
				int targetX = selectedButton.x + selectedButton.width * 3 / 4;
				int targetY = selectedButton.y + selectedButton.height * 4 / 3;

				// Move the mouse to the exact center of the selected button
				robot.mouseMove(targetX + windowPos.x, targetY + windowPos.y);
			}
		}
	}
}

/**
 * Abstract class representing a virtual mouse.
 */
abstract class Mouse {
	// Position variables
	float x, y; // Current position of the mouse
	float offsetX, offsetY; // Offset for dragging behavior

	// Initial position variables
	float initialX = 0, initialY = 0; // Starting position
	float initialCursorX = 0, initialCursorY = 0; // Initial cursor coordinates

	// Cable attachment point
	float cableX, cableY;

	// Mouse dimensions
	float w = 50, h = 70; // Width and height of the mouse

	// State variables
	boolean isDragging = false; // Tracks whether the mouse is being dragged
	float sensitivity = 3; // Sensitivity multiplier for cursor movement

	/**
	 * Constructs a new Mouse object with a given position and cable attachment.
	 * 
	 * @param x      Initial x-coordinate of the mouse
	 * @param y      Initial y-coordinate of the mouse
	 * @param cableX x-coordinate of the cable attachment point
	 * @param cableY y-coordinate of the cable attachment point
	 */
	Mouse(float x, float y, float cableX, float cableY) {
		this.x = x;
		this.y = y;
		this.initialX = x;
		this.initialY = y;
		this.cableX = cableX;
		this.cableY = cableY;
	}

	/**
	 * Updates the position of the virtual mouse when being dragged.
	 * 
	 * @param p The Processing PApplet instance, providing access to mouse
	 *          coordinates.
	 */
	void update(PApplet p) {
		// If the mouse is being dragged, update its position
		if (isDragging) {
			x = p.mouseX + offsetX;
			y = p.mouseY + offsetY;
		}
	}

	/**
	 * Renders the virtual mouse and its components on the screen.
	 * 
	 * https://processing.org/reference/stroke_.html
	 * https://processing.org/reference/strokeWeight_.html
	 * https://processing.org/reference/bezier_.html
	 * 
	 * @param p The Processing PApplet instance used for drawing.
	 */
	void display(PApplet p) {
		// Set stroke properties for the cable
		p.stroke(50);
		p.strokeWeight(4);
		p.noFill();

		// Draw the cable as a bezier curve
		p.bezier(cableX, cableY, cableX, y - 50, x, y - 50, x, y - h / 2);

		// Draw the mouse body
		p.fill(180);
		p.rect(x - (w / 2), y - (h / 2), w, h, 30);

		// Draw the mouse button
		p.fill(150);
		p.ellipse(x, y - 10, 20, 25); // Button shape

		// Disable stroke for cursor drawing
		p.noStroke();

		// Draw the cursor as a red circle
		p.fill(255, 0, 0);
		p.ellipse(getMouseX(), getMouseY(), 10, 10);

	}

	/**
	 * Handles mouse press events for the virtual mouse.
	 * 
	 * @param p The Processing PApplet instance, providing access to mouse
	 *          coordinates.
	 * @return {@code true} if dragging starts, otherwise {@code false}.
	 */
	boolean pressed(PApplet p) {
		// Check if the mouse button (clickable area) is pressed
		float db = p.dist(p.mouseX, p.mouseY, x, y - 10);
		if (db < 20) { // Check if clicking inside the mouse body
			this.onMouseButtonPressed();
		}

		// Check if the main body of the mouse is pressed
		float d = p.dist(p.mouseX, p.mouseY, x, y);
		if (d < w / 2 + 20) { // Check if clicking inside the mouse body
			isDragging = true;
			offsetX = x - p.mouseX;
			offsetY = y - p.mouseY;
			return true; // Indicate that dragging has started
		}
		return false; // No dragging started
	}

	/**
	 * Abstract method to be implemented by subclasses for handling button clicks.
	 */
	abstract void onMouseButtonPressed();

	/**
	 * Calculates the virtual cursor's X-coordinate based on the mouse's movement.
	 *
	 * @return The adjusted X-coordinate of the virtual cursor.
	 */
	float getMouseX() {
		return (x - initialX) * sensitivity + initialCursorX;
	}

	/**
	 * Calculates the virtual cursor's Y-coordinate based on the mouse's movement.
	 * 
	 * @return The adjusted Y-coordinate of the virtual cursor.
	 */
	float getMouseY() {
		return (y - initialY) * sensitivity + initialCursorY;
	}

	/**
	 * Sets the initial X-coordinate of the virtual cursor.
	 * 
	 * @param mx The initial X-coordinate.
	 */
	void setMouseX(float mx) {
		initialCursorX = mx;
	}

	/**
	 * Sets the initial Y-coordinate of the virtual cursor.
	 * 
	 * @param my The initial Y-coordinate.
	 */
	void setMouseY(float my) {
		initialCursorY = my;
	}

	/**
	 * Handles mouse release events.
	 * 
	 * @param p The Processing PApplet instance.
	 */
	void released(PApplet p) {
		isDragging = false;
	}
}
