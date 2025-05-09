package flockingsim;

import drawing.Canvas;
import geometry.CartesianCoordinate;

/**
 * A Boid is a simple object that can move around the canvas.   
 * It has a position, velocity, and acceleration.
 * It can move, turn, and draw itself.
 * It can also put its pen up or down to control whether its movement is drawn on the canvas. */

public class Boid {
    private CartesianCoordinate position;
    private CartesianCoordinate velocity;
    private CartesianCoordinate acceleration;
    private boolean penDown;
    private Canvas canvas;
    private int angle; // Represents the boid's orientation, if used for drawing or turning logic

    /**
     * Constructor for the Boid class.
     * @param canvas The canvas on which the boid will be drawn.
     * @param position The initial position of the boid.
     * @param velocity The initial velocity of the boid. */
    public Boid(Canvas canvas,CartesianCoordinate position, CartesianCoordinate velocity) {
        this.canvas = canvas;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new CartesianCoordinate(0, 0); // Initialize acceleration
        this.angle = 0; // Initialize angle
        this.penDown = true;
    }
    
    /**
     * Updates the boid's state (velocity and position) based on its acceleration.
     * This method should be called once per simulation step for each boid.
     */
    public void update(double dt) { // dt passed in or calculated
        // Scale acceleration by dt before adding to velocity
        this.velocity = this.velocity.add(this.acceleration.multiply(dt)); // Needs multiply method

        // Scale velocity by dt before adding to position
        this.position = this.position.add(this.velocity.multiply(dt)); // Needs multiply method

        this.acceleration = new CartesianCoordinate(0, 0);
    }

    /*
     * Moves the boid a certain distance in the direction of its velocity.
     * @param distanceToMove The distance to move the boid. */
    public void move(int distanceToMove) {
        double newX = this.position.getX() + this.velocity.getX() * distanceToMove;
        double newY = this.position.getY() + this.velocity.getY() * distanceToMove;

        CartesianCoordinate newPosition = new CartesianCoordinate(newX, newY); // creating a new position for the boid
        if (this.penDown) {
            this.canvas.drawLineBetweenPoints(this.position, newPosition);
        }

        this.position = newPosition; // update the position of the boid
        canvas.repaint();
    }

    /**
     * Turns the boid by a certain angle (in degrees).
     * Note: This method currently only updates an internal 'angle' field.
     * To make the boid visually turn, its velocity vector needs to be updated
     * based on this angle (e.g., by rotating the velocity vector).
     * @param angleToTurn The angle to turn the boid by. */
    public void turn(int angleToTurn) {
        this.angle += angleToTurn; // update the angle of the boid
        // Normalise the angle to be between 0 and 360 degrees
        this.angle = this.angle % 360;
        if (this.angle < 0) {
            this.angle += 360;
        }   
    }

    /**
     * Puts the pen up to stop the boid from drawing trails (if trail drawing is implemented). */
    public void putPenUp() {
        this.penDown = false;
    }

    /**
     * Puts the pen down to start drawing trails (if trail drawing is implemented). */
    public void putPenDown() {
        this.penDown = true;
    }

    /**
     * Checks if the pen is down.
     * @return true if the pen is down, false otherwise. */
    public boolean isPenDown() {
        return this.penDown;
    }

    /**
     * Draws the boid on the canvas.
     * For debugging, this draws a small cross at the boid's position.
     */
    public void draw() {
        // Draw the boid in its current position
        canvas.drawLineBetweenPoints(this.position, this.position.add(this.velocity));
    }

    /**
     * Removes the boid from the canvas.
     * This method should be called every time the boid moves.
     * It will remove the boid from the canvas so it can be redrawn. */
    public void undraw() {
        canvas.removeMostRecentLine();
    }
}