package flockingsim;

import java.util.ArrayList;
import java.util.List; // Use List interface

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
    private double maxSpeed;
    private double maxForce;
    private double perceptionRadius;
    private double separationWeight = 1.0;
    private double alignmentWeight = 1.0;
    private double cohesionWeight = 1.0;
    private double desiredSeparation = 25.0;
    private static final double BOID_LENGTH = 7; // Length of the boid
    private static final double BOID_WIDTH = 9; // Width of the boid
    private static final double BOID_BACK_OFFSET = 5; // Offset from the back of the boid to the tip of the tail
    private double accumulatedMovement = 0.0; // field for fractional movement (removes precision issues with integer movement)


    /**
     * Constructor for the Boid class.
     * @param canvas The canvas on which the boid will be drawn.
     * @param position The initial position of the boid.
     * @param velocity The initial velocity of the boid.
     * @param maxSpeed The maximum speed the boid can travel.
     * @param maxForce The maximum steering force that can be applied.
     * @param perceptionRadius The distance within which the boid considers others.
     */
    public Boid(Canvas canvas, CartesianCoordinate position, CartesianCoordinate velocity, double maxSpeed, double maxForce, double perceptionRadius) {
        this.canvas = canvas;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new CartesianCoordinate(0, 0); // Start with zero acceleration
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.perceptionRadius = perceptionRadius;
        this.penDown = true; // Or false if you don't want initial trails
    }
    
    /**
     * Calculates the required turn angle (degrees) and move distance based on 
     * current velocity, acceleration, and time delta (dt).
     * @param dt The time delta for this step.
     * @return A double array containing [angleToTurnDegrees, distanceToMove].
     */
    public double[] calculateTurnAndMove(double dt) {
        // Calculate target velocity based on current velocity and acceleration
        CartesianCoordinate targetVelocity = this.velocity.add(this.acceleration.multiply(dt));

        // Limit target velocity to maxSpeed
        if (this.maxSpeed > 0 && targetVelocity.magnitude() > this.maxSpeed) {
            targetVelocity = targetVelocity.normalize().multiply(this.maxSpeed);
        }

        // Calculate the angle difference between current and target velocity
        double currentAngleRad = Math.atan2(this.velocity.getY(), this.velocity.getX());
        double targetAngleRad = Math.atan2(targetVelocity.getY(), targetVelocity.getX());
        
        double angleDeltaRad = targetAngleRad - currentAngleRad;

        // Normalize angle difference to be between -PI and PI
        while (angleDeltaRad > Math.PI) angleDeltaRad -= 2 * Math.PI;
        while (angleDeltaRad <= -Math.PI) angleDeltaRad += 2 * Math.PI;
        
        double angleDeltaDegrees = Math.toDegrees(angleDeltaRad);

        // Calculate distance to move based on the magnitude of the target velocity
        double distanceToMove = targetVelocity.magnitude() * dt;
        
        // If velocity was zero, the angle calculation might be unstable.
        // In this case, don't turn, just move according to the new velocity magnitude.
        if (this.velocity.magnitude() < 0.0001) {
             angleDeltaDegrees = 0; 
        }


        return new double[]{angleDeltaDegrees, distanceToMove};
    }

    /**
     * Accumulates the ideal movement distance and calls the internal move(int)
     * method when the accumulated value reaches whole numbers.
     * @param idealDistance The calculated ideal distance (double) for this step.
     */
    public void accumulateAndMove(double idealDistance) {
        this.accumulatedMovement += idealDistance; // Add ideal distance to accumulator
        int distanceToInt = (int) this.accumulatedMovement; // Get the whole number part

        if (distanceToInt > 0) {
            // Call the original move method with the integer part
            this.move(distanceToInt);
            // Subtract the integer part we just moved from the accumulator
            this.accumulatedMovement -= distanceToInt;
        }
        // The remaining fractional part stays in accumulatedMovement for the next step
    }

    /**
     * Moves the boid a certain distance in the direction of its current velocity.
     * @param distanceToMove The integer distance to move the boid.
     */
    public void move(int distanceToMove) {
        // If distance is negative, don't move
        if (distanceToMove <= 0) {
           return;
        }
        // If velocity is zero, don't move
        if (this.velocity.magnitude() < 0.0001) {
            return;
        }
        
        CartesianCoordinate direction = this.velocity.normalize();
        CartesianCoordinate displacement = direction.multiply(distanceToMove); // Use integer distance

        // Update position
        this.position = this.position.add(displacement); 

        // Apply boundary conditions
        if (this.canvas != null) { // Ensure canvas is available
            wrapPosition(this.canvas.getWidth(), this.canvas.getHeight());
        }
    }

    /**
     * Turns the boid by rotating its velocity vector by a certain angle (in degrees).
     * Results in slight inaccuracies in movement due to the use of integers, accumulateAndMove() mostly corrects this.
     * @param angleToTurn The integer angle in degrees to turn the boid by.
     */
    public void turn(int angleToTurn) {
        // Convert degrees to radians
        double angleRad = Math.toRadians(angleToTurn);

        // Get current velocity components
        double vx = this.velocity.getX();
        double vy = this.velocity.getY();

        // Apply 2D rotation matrix
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);

        double newVx = vx * cosTheta - vy * sinTheta;
        double newVy = vx * sinTheta + vy * cosTheta;

        // Update the velocity vector
        this.velocity = new CartesianCoordinate(newVx, newVy);
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
            this.canvas.drawLineBetweenPoints(frontPosition, leftPosition);
            this.canvas.drawLineBetweenPoints(leftPosition, rightPosition);
            this.canvas.drawLineBetweenPoints(rightPosition, frontPosition);
        }
    }

    /**
     * Removes the boid from the canvas.
     * This method should be called every time the boid moves.
     * It will remove the boid from the canvas so it can be redrawn. */
    public void undraw() {
        this.canvas.removeMostRecentLine();
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

    /**
     * Calculates and applies flocking forces (separation, alignment, cohesion)
     * to the boid's acceleration.
     * This method should be called once per simulation step BEFORE update().
     * @param allBoids List of all boids in the simulation.
     */
    public void applyFlockingRules(List<Boid> allBoids) {
        // It's often better to clear acceleration each frame before applying forces
        this.acceleration = new CartesianCoordinate(0, 0); 

        ArrayList<Boid> neighbors = getNeighbors(allBoids);

        CartesianCoordinate separationForce = calculateSeparationForce(neighbors);
        CartesianCoordinate alignmentForce = calculateAlignmentForce(neighbors);
        CartesianCoordinate cohesionForce = calculateCohesionForce(neighbors);

        // Apply weights
        separationForce = separationForce.multiply(separationWeight);
        alignmentForce = alignmentForce.multiply(alignmentWeight);
        cohesionForce = cohesionForce.multiply(cohesionWeight);

        // Add forces to acceleration
        this.acceleration = this.acceleration.add(separationForce);
        this.acceleration = this.acceleration.add(alignmentForce);
        this.acceleration = this.acceleration.add(cohesionForce);

        // Optional: Limit total acceleration if needed, though limiting individual
        // steering forces (as done in calculate methods) is more common.
        // if (this.acceleration.magnitude() > someOverallMaxForce) {
        //    this.acceleration = this.acceleration.normalize().multiply(someOverallMaxForce);
        // }
    }

    /**
     * Finds boids within the perception radius.
     * @param allBoids List of all boids.
     * @return List of neighboring boids (excluding self).
     */
    private ArrayList<Boid> getNeighbors(List<Boid> allBoids) {
        ArrayList<Boid> neighbors = new ArrayList<>();
        for (Boid other : allBoids) {
            if (other != this) { // Don't compare with self
                double distance = this.position.distance(other.position).magnitude();
                // Check if within perception radius
                if (distance > 0 && distance < this.perceptionRadius) { // distance > 0 avoids self comparison if positions are identical
                    neighbors.add(other);
                }
            }
        }
        return neighbors;
    }

    /**
     * Calculates the separation steering force.
     * Steers away from the average position of very close neighbors.
     * @param neighbors List of neighboring boids.
     * @return Separation force vector.
     */
    private CartesianCoordinate calculateSeparationForce(ArrayList<Boid> neighbors) {
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        int count = 0;
        for (Boid other : neighbors) {
            double distance = this.position.distance(other.position).magnitude();
            if (distance > 0 && distance < this.desiredSeparation) {
                // Calculate vector pointing away from neighbor, weighted by distance
                CartesianCoordinate diff = this.position.subtract(other.position);
                diff = diff.normalize();
                diff = diff.divide(distance); // Weight by distance (closer = stronger)
                steer = steer.add(diff);
                count++;
            }
        }

        if (count > 0) {
            steer = steer.divide(count); // Average the force
        }

        // If the steering force is significant, scale it to maxSpeed and apply maxForce limit
        if (steer.magnitude() > 0) {
            steer = steer.normalize();
            steer = steer.multiply(this.maxSpeed);
            steer = steer.subtract(this.velocity); // Steering force = desired velocity - current velocity
            steer = steer.limit(this.maxForce);    // Limit the magnitude of the steering force
        }
        return steer;
    }

    /**
     * Calculates the alignment steering force.
     * Steers towards the average heading (velocity) of neighbors.
     * @param neighbors List of neighboring boids.
     * @return Alignment force vector.
     */
    private CartesianCoordinate calculateAlignmentForce(ArrayList<Boid> neighbors) {
        CartesianCoordinate sumVelocities = new CartesianCoordinate(0, 0);
        int count = 0;
        for (Boid other : neighbors) {
             // Check distance again or assume neighbors list is already filtered by perceptionRadius
             double distance = this.position.distance(other.position).magnitude();
             if (distance > 0 && distance < this.perceptionRadius) { // Ensure within radius
                sumVelocities = sumVelocities.add(other.velocity);
                count++;
             }
        }

        if (count > 0) {
            sumVelocities = sumVelocities.divide(count); // Average velocity
            // Steer towards the average velocity
            sumVelocities = sumVelocities.normalize();
            sumVelocities = sumVelocities.multiply(this.maxSpeed);
            CartesianCoordinate steer = sumVelocities.subtract(this.velocity); // Steering force = desired - current
            steer = steer.limit(this.maxForce); // Limit force
            return steer;
        } else {
            return new CartesianCoordinate(0, 0); // No neighbors, no alignment force
        }
    }

     /**
     * Calculates the cohesion steering force.
     * Steers towards the average position (center of mass) of neighbors.
     * @param neighbors List of neighboring boids.
     * @return Cohesion force vector.
     */
    private CartesianCoordinate calculateCohesionForce(ArrayList<Boid> neighbors) {
        CartesianCoordinate sumPositions = new CartesianCoordinate(0, 0);
        int count = 0;
        for (Boid other : neighbors) {
            // Check distance again or assume neighbors list is already filtered by perceptionRadius
            double distance = this.position.distance(other.position).magnitude();
             if (distance > 0 && distance < this.perceptionRadius) { // Ensure within radius
                sumPositions = sumPositions.add(other.position);
                count++;
             }
        }

        if (count > 0) {
            sumPositions = sumPositions.divide(count); // Average position (center of mass)
            return seek(sumPositions); // Calculate steering force towards the target position
        } else {
            return new CartesianCoordinate(0, 0); // No neighbors, no cohesion force
        }
    }

    /**
     * Helper method to calculate a steering force towards a target coordinate.
     * @param target The target CartesianCoordinate.
     * @return The steering force vector.
     */
    private CartesianCoordinate seek(CartesianCoordinate target) {
        CartesianCoordinate desired = target.subtract(this.position); // Vector from current position to target
        desired = desired.normalize();
        desired = desired.multiply(this.maxSpeed);

        // Steering force = Desired Velocity - Current Velocity
        CartesianCoordinate steer = desired.subtract(this.velocity);
        steer = steer.limit(this.maxForce); // Limit the steering force
        return steer;
    }

    // --- Getters for external access if needed (e.g., for GUI tuning) ---
    // public CartesianCoordinate getPosition() { return position; }
    // public CartesianCoordinate getVelocity() { return velocity; }
    // Add setters for weights if you implement GUI controls later
}