package flockingsim;

import java.util.ArrayList;

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
    private CartesianCoordinate direction;
    private CartesianCoordinate angularVelocity;
    private boolean penDown;
    private Canvas canvas;
    private int angle; // Represents the boid's orientation, if used for drawing or turning logic
    private double maxSpeed;
    private double maxForce;
    private double perceptionRadius; // the radius within which the boid can see other boids
    private double cohesionAmount;
    private static final double BOID_LENGTH = 7; // Length of the boid
    private static final double BOID_WIDTH = 9; // Width of the boid
    private static final double BOID_BACK_OFFSET = 5; // Offset from the back of the boid to the tip of the tail


    /**
     * Constructor for the Boid class.
     * @param canvas The canvas on which the boid will be drawn.
     * @param position The initial position of the boid.
     * @param velocity The initial velocity of the boid.
     * @param maxSpeed The maximum speed of the boid.
     * @param maxForce The maximum force of the boid. */
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
        if (this.velocity.magnitude() > this.maxSpeed){
            this.velocity = this.velocity.normalize().multiply(this.maxSpeed);
        }

        // Apply boundary conditions
        if (this.canvas != null) { // Ensure canvas is available
            wrapPosition(this.canvas.getWidth(), this.canvas.getHeight());
        }

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
        CartesianCoordinate currentPosition = this.position;
        CartesianCoordinate vel = this.velocity;

        // Handle zero velocity for drawing: either draw a default shape or use a default orientation
        double magnitudeSq = vel.getX() * vel.getX() + vel.getY() * vel.getY();
        CartesianCoordinate normVelocity;

        if (magnitudeSq < 0.00001) { // If velocity is very close to zero make it default to facing right
            normVelocity = new CartesianCoordinate(1, 0); 
        } else {
            normVelocity = vel.normalize();
        }

        CartesianCoordinate perpVelocity = normVelocity.perpendicular();

        CartesianCoordinate frontPosition = currentPosition.add(normVelocity    .multiply(BOID_LENGTH));

        // Calculate the center of the base of the triangle (behind the current position)
        CartesianCoordinate baseCenter = currentPosition.add(normVelocity.multiply(-BOID_BACK_OFFSET));
        
        CartesianCoordinate leftPosition = baseCenter.add(perpVelocity.multiply(BOID_WIDTH / 2.0));
        CartesianCoordinate rightPosition = baseCenter.add(perpVelocity.multiply(-BOID_WIDTH / 2.0)); // Or baseCenter.subtract(perpVelocity.multiply(BOID_WIDTH / 2.0))

        // Draw the triangle outline
        if (this.penDown) { // Only draw if pen is down,
            canvas.drawLineBetweenPoints(frontPosition, leftPosition);
            canvas.drawLineBetweenPoints(leftPosition, rightPosition);
            canvas.drawLineBetweenPoints(rightPosition, frontPosition);
        }
    }

    /**
     * Removes the boid from the canvas.
     * This method should be called every time the boid moves.
     * It will remove the boid from the canvas so it can be redrawn. */
    public void undraw() {
        canvas.removeMostRecentLine();
    }

    /** Keep the boid within the bounds of the canvas
     * The boid will appear on the opposite side of the canvas when it reaches an edge.
     * @param width The width of the canvas
     * @param height The height of the canvas */

    public void wrapPosition(int canvasWidth, int canvasHeight) {
        // Safeguard: Do nothing if canvas dimensions are not valid
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            return;
        }

        double x = this.position.getX();
        double y = this.position.getY();
        boolean changed = false;

        while (x < 0) {
            x += canvasWidth;
            changed = true;
        }
        while (x >= canvasWidth) {
            x -= canvasWidth;
            changed = true;
        }

        while (y < 0) {
            y += canvasHeight;
            changed = true;
        }
        while (y >= canvasHeight) {
            y -= canvasHeight;
            changed = true;
        }

        if (changed) {
            this.position = new CartesianCoordinate(x, y);
        }

    }

    public int getNeighboursInRadius(ArrayList<Boid> boids, double radius) {
        int count = 0;
        for (Boid boid : boids) {
            if (this.position.distance(boid.position).magnitude() <= radius) {
                count++;
            }
        }
        return count;
    }

    public CartesianCoordinate getCentreOfMass(ArrayList<Boid> boids, double radius) {
        int count = 0;
        CartesianCoordinate sum = new CartesianCoordinate(0, 0);
        for (Boid boid : boids) {
            if (this.position.distance(boid.position).magnitude() <= radius) {
                sum = sum.add(boid.position);
                count++;
            }
        }
        return sum.divide(count);
    }

    public double getSteeringAngle(ArrayList<Boid> boids, double radius) {
        CartesianCoordinate centreOfMass = getCentreOfMass(boids, radius);
        CartesianCoordinate directionToCentreOfMass = centreOfMass.subtract(this.position);  // direction to the centre of mass
        double angle = Math.atan2(directionToCentreOfMass.getY(), directionToCentreOfMass.getX());
        return angle;
    }

    public CartesianCoordinate applyCohesion(ArrayList<Boid> boids, double radius, double cohesionAmount) {
        double directionToCentreOfMass = this.position.magnitude() + cohesionAmount*getSteeringAngle(boids, radius);
        this.velocity = this.velocity.add(new CartesianCoordinate(Math.cos(directionToCentreOfMass), Math.sin(directionToCentreOfMass)));
        return this.velocity;
    }


}