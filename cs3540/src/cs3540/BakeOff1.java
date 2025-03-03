package cs3540;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class BakeOff1 extends PApplet {

    public static void main(String[] args) {
        PApplet.main("cs3540.BakeOff1");
    }

    // Layout parameters
    int margin = 200;
    final int padding = 50;
    int buttonSize = 40; // Dynamic size

    // Experiment tracking variables
    ArrayList<Integer> trials = new ArrayList<>();
    int trialNum = 0;
    int startTime = 0;
    int prevClickTime = 0;
    int finishTime = 0;
    int hits = 0;
    int misses = 0;
    int startX = 0, startY = 0;

    // Java Robot for automated cursor movement
    Robot robot;

    // Experiment configuration
    int numRepeats = 10;
    int participantID = 1; // Change for each team member

    // Log storage
    StringBuilder logData = new StringBuilder("Trial,Participant,X_Start,Y_Start,X_Target,Y_Target,Width,Time,Hit\n");

    public void settings() {
        size(700, 700);
    }

    public void setup() {
        noStroke();
        textFont(createFont("Arial", 16));
        textAlign(CENTER);
        frameRate(60);
        ellipseMode(CENTER);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Generate randomized order of trials
        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < numRepeats; k++)
                trials.add(i);
        }
        Collections.shuffle(trials);
    }

    public void draw() {
        background(0);

        if (trialNum >= trials.size()) {
            displayResults();
            return;
        }

        fill(255);
        text((trialNum + 1) + " of " + trials.size(), 40, 20);

        // Draw all buttons
        for (int i = 0; i < 16; i++) {
            drawButton(i);
        }
    }

    public void attemptPress(float pressMouseX, float pressMouseY) {
    	if (trialNum >= trials.size()) return;

        int currentTime = millis();

        if (trialNum == 0) {
            // Store the first cursor position at the start of the experiment
            startX = mouseX;
            startY = mouseY;
            startTime = currentTime;
            prevClickTime = currentTime;
        }

        if (trialNum == trials.size() - 1) finishTime = currentTime;

        Rectangle bounds = getButtonLocation(trials.get(trialNum));

        boolean hit = (pressMouseX > bounds.x && pressMouseX < bounds.x + bounds.width) &&
                      (pressMouseY > bounds.y && pressMouseY < bounds.y + bounds.height);

        float clickTime = (currentTime - prevClickTime) / 1000.0f;
        prevClickTime = currentTime;

        // Log the correct starting position
        String logEntry = trialNum + "," + participantID + "," +
                startX + "," + startY + "," +  // Corrected starting position
                (bounds.x + bounds.width / 2) + "," + (bounds.y + bounds.height / 2) + "," +
                buttonSize + "," + clickTime + "," + (hit ? "1" : "0");

        logData.append(logEntry).append("\n");
        System.out.println(logEntry);

        if (hit) hits++;
        else misses++;

        // Store new start position after clicking
        startX = mouseX;
        startY = mouseY;

        trialNum++;
        adjustTargetSize();
    }

    public void displayResults() {
        float totalTime = (finishTime - startTime) / 1000f;
        float accuracy = (float) hits * 100f / (hits + misses);

        fill(255);
        text("Finished!", width / 2, height / 2);
        text("Hits: " + hits, width / 2, height / 2 + 20);
        text("Misses: " + misses, width / 2, height / 2 + 40);
        text("Accuracy: " + accuracy + "%", width / 2, height / 2 + 60);
        text("Total time: " + totalTime + " sec", width / 2, height / 2 + 80);
        text("Avg. time per button: " + nf((totalTime) / (hits + misses), 0, 3) + " sec", width / 2, height / 2 + 100);

        System.out.println("Experiment Complete.\n" + logData);
    }

    public void mousePressed() {
        attemptPress(mouseX, mouseY);
    }

    public Rectangle getButtonLocation(int i) {
        int x = (i % 4) * (padding + buttonSize) + margin;
        int y = (i / 4) * (padding + buttonSize) + margin;
        return new Rectangle(x, y, buttonSize, buttonSize);
    }

    public void drawButton(int i) {
        Rectangle bounds = getButtonLocation(i);
        fill((trials.get(trialNum) == i) ? color(0, 255, 255) : 200);
        rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void adjustTargetSize() {
        if (trialNum % 5 == 0) {
            buttonSize = 35 + (int) (Math.random() * 30);
        }
    }
}
