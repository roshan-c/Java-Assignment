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
    // Standard flocking weights
    private double separationWeight = 1.5;
    private double alignmentWeight = 1.0;
    private double cohesionWeight = 1.0;
    private double desiredSeparation = 25.0;
    private double obstacleAvoidanceWeight = 2.0;
    private double obstacleSafetyRadius = 80.0;
    private double obstacleLookAhead = 40.0;
    private double obstacleAvoidanceBuffer = 40.0;
    private double minSpeed = 2.0;
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
        this.acceleration = new CartesianCoordinate(0, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.perceptionRadius = perceptionRadius;
        this.penDown = true;
    }

    /**
     * Updates the boid's position and velocity based on flocking rules.
     * @param allBoids List of all boids in the simulation
     * @param obstacles List of obstacles in the simulation
     */
    public void update(List<Boid> allBoids, List<Rectangle> obstacles) {
        // Reset acceleration
        this.acceleration = new CartesianCoordinate(0, 0);

        // Apply the three core flocking rules
        CartesianCoordinate separation = calculateSeparationForce(allBoids);
        CartesianCoordinate alignment = calculateAlignmentForce(allBoids);
        CartesianCoordinate cohesion = calculateCohesionForce(allBoids);
        CartesianCoordinate avoidance = calculateObstacleAvoidanceForce(obstacles);

        // Apply weights
        this.acceleration = this.acceleration.add(separation.multiply(separationWeight));
        this.acceleration = this.acceleration.add(alignment.multiply(alignmentWeight));
        this.acceleration = this.acceleration.add(cohesion.multiply(cohesionWeight));
        this.acceleration = this.acceleration.add(avoidance.multiply(obstacleAvoidanceWeight));

        // Update velocity
        this.velocity = this.velocity.add(this.acceleration);
        
        // Limit speed
        if (this.velocity.magnitude() > this.maxSpeed) {
            this.velocity = this.velocity.normalize().multiply(this.maxSpeed);
        }
        // Ensure minimum speed
        if (this.velocity.magnitude() < 3.0) {
            this.velocity = this.velocity.normalize().multiply(3.0);
        }

        // Calculate movement
        int moveDistance = (int) this.velocity.magnitude();
        if (moveDistance > 0) {
            // Get current heading
            double currentHeading = Math.toDegrees(Math.atan2(this.velocity.getY(), this.velocity.getX()));
            
            // Calculate desired heading
            CartesianCoordinate desiredDirection = this.velocity.normalize();
            double desiredHeading = Math.toDegrees(Math.atan2(desiredDirection.getY(), desiredDirection.getX()));
            
            // Calculate turn needed (shortest path)
            double turnNeeded = desiredHeading - currentHeading;
            
            // Normalize turn to [-180, 180]
            while (turnNeeded > 180) turnNeeded -= 360;
            while (turnNeeded < -180) turnNeeded += 360;
            
            // Limit turn rate
            turnNeeded = Math.max(-30, Math.min(30, turnNeeded));
            
            // Apply turn and move
            this.turn((int)turnNeeded);
            this.move(moveDistance);
        }
    }

    /**
     * Turns the boid by the specified angle in degrees.
     * @param angle The angle to turn in degrees
     */
    public void turn(int angle) {
        // Convert angle to radians
        double angleRad = Math.toRadians(angle);
        
        // Calculate new velocity using rotation matrix
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);
        
        double newVx = this.velocity.getX() * cosTheta - this.velocity.getY() * sinTheta;
        double newVy = this.velocity.getX() * sinTheta + this.velocity.getY() * cosTheta;
        
        // Update velocity
        this.velocity = new CartesianCoordinate(newVx, newVy);
        
        // Ensure velocity maintains its magnitude
        double currentSpeed = this.velocity.magnitude();
        if (currentSpeed > 0) {
            this.velocity = this.velocity.normalize().multiply(currentSpeed);
        }
    }

    /**
     * Moves the boid forward by the specified distance.
     * @param distance The distance to move
     */
    public void move(int distance) {
        if (distance <= 0) return;
        
        // Calculate new position
        CartesianCoordinate direction = this.velocity.normalize();
        CartesianCoordinate displacement = direction.multiply(distance);
        CartesianCoordinate newPosition = this.position.add(displacement);

        // Check if new position is safe
        if (isPositionSafe(newPosition)) {
            this.position = newPosition;
            if (this.canvas != null) {
                wrapPosition(this.canvas.getWidth(), this.canvas.getHeight());
            }
        }
    }

    private boolean isPositionSafe(CartesianCoordinate newPosition) {
        for (Rectangle obstacle : this.getObstacles()) {
            double distance = newPosition.distance(obstacle.getCenter()).magnitude();
            if (distance < this.obstacleSafetyRadius) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draws the boid on the canvas.
     * For debugging, this draws a small cross at the boid's position.
     */
    public void draw() {
        if (!this.penDown) return;

        CartesianCoordinate currentPosition = this.position;
        CartesianCoordinate vel = this.velocity;

        // Handle zero velocity
        if (vel.magnitude() < 0.0001) {
            vel = new CartesianCoordinate(1, 0);
        }

        CartesianCoordinate normVelocity = vel.normalize();
        CartesianCoordinate perpVelocity = normVelocity.perpendicular();

        // Calculate triangle points
        CartesianCoordinate frontPosition = currentPosition.add(normVelocity.multiply(7));
        CartesianCoordinate baseCenter = currentPosition.add(normVelocity.multiply(-5));
        CartesianCoordinate leftPosition = baseCenter.add(perpVelocity.multiply(4.5));
        CartesianCoordinate rightPosition = baseCenter.add(perpVelocity.multiply(-4.5));

        // Draw the triangle
        this.canvas.drawLineBetweenPoints(frontPosition, leftPosition);
        this.canvas.drawLineBetweenPoints(leftPosition, rightPosition);
        this.canvas.drawLineBetweenPoints(rightPosition, frontPosition);
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
     * @param obstacles List of obstacles in the simulation.
     */
    public void applyFlockingRules(List<Boid> allBoids, List<Rectangle> obstacles) {
        // It's often better to clear acceleration each frame before applying forces
        this.acceleration = new CartesianCoordinate(0, 0);

        ArrayList<Boid> neighbors = getNeighbors(allBoids);

        CartesianCoordinate separationForce = calculateSeparationForce(neighbors);
        CartesianCoordinate alignmentForce = calculateAlignmentForce(neighbors);
        CartesianCoordinate cohesionForce = calculateCohesionForce(neighbors);
        CartesianCoordinate avoidanceForce = calculateObstacleAvoidanceForce(obstacles);

        // Apply weights
        separationForce = separationForce.multiply(separationWeight);
        alignmentForce = alignmentForce.multiply(alignmentWeight);
        cohesionForce = cohesionForce.multiply(cohesionWeight);
        avoidanceForce = avoidanceForce.multiply(obstacleAvoidanceWeight);

        // Add forces to acceleration
        this.acceleration = this.acceleration.add(separationForce);
        this.acceleration = this.acceleration.add(alignmentForce);
        this.acceleration = this.acceleration.add(cohesionForce);
        this.acceleration = this.acceleration.add(avoidanceForce);

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
            if (other != this) {
                double distance = this.position.distance(other.position).magnitude();
                if (distance > 0 && distance < this.perceptionRadius) {
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
    private CartesianCoordinate calculateSeparationForce(List<Boid> neighbors) {
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        int count = 0;
        
        for (Boid other : neighbors) {
            double distance = this.position.distance(other.position).magnitude();
            if (distance > 0 && distance < this.desiredSeparation) {
                CartesianCoordinate diff = this.position.subtract(other.position);
                diff = diff.normalize();
                diff = diff.divide(distance); // Stronger when closer
                steer = steer.add(diff);
                count++;
            }
        }
        
        if (count > 0) {
            steer = steer.divide(count);
            if (steer.magnitude() > 0) {
                steer = steer.normalize().multiply(this.maxSpeed);
                steer = steer.subtract(this.velocity);
                steer = steer.limit(this.maxForce);
            }
        }
        return steer;
    }

    /**
     * Calculates the alignment steering force.
     * Steers towards the average heading (velocity) of neighbors.
     * @param neighbors List of neighboring boids.
     * @return Alignment force vector.
     */
    private CartesianCoordinate calculateAlignmentForce(List<Boid> neighbors) {
        CartesianCoordinate sumVelocities = new CartesianCoordinate(0, 0);
        int count = 0;
        
        for (Boid other : neighbors) {
            double distance = this.position.distance(other.position).magnitude();
            if (distance < this.perceptionRadius) {
                sumVelocities = sumVelocities.add(other.velocity);
                count++;
            }
        }
        
        if (count > 0) {
            sumVelocities = sumVelocities.divide(count);
            if (sumVelocities.magnitude() > 0) {
                sumVelocities = sumVelocities.normalize().multiply(this.maxSpeed);
                CartesianCoordinate steer = sumVelocities.subtract(this.velocity);
                return steer.limit(this.maxForce);
            }
        }
        return new CartesianCoordinate(0, 0);
    }

     /**
     * Calculates the cohesion steering force.
     * Steers towards the average position (center of mass) of neighbors.
     * @param neighbors List of neighboring boids.
     * @return Cohesion force vector.
     */
    private CartesianCoordinate calculateCohesionForce(List<Boid> neighbors) {
        CartesianCoordinate sumPositions = new CartesianCoordinate(0, 0);
        int count = 0;
        
        for (Boid other : neighbors) {
            double distance = this.position.distance(other.position).magnitude();
            if (distance < this.perceptionRadius) {
                sumPositions = sumPositions.add(other.position);
                count++;
            }
        }
        
        if (count > 0) {
            CartesianCoordinate centerOfMass = sumPositions.divide(count);
            return seek(centerOfMass);
        }
        return new CartesianCoordinate(0, 0);
    }

    /**
     * Helper method to calculate a steering force towards a target coordinate.
     * @param target The target CartesianCoordinate.
     * @return The steering force vector.
     */
    private CartesianCoordinate seek(CartesianCoordinate target) {
        CartesianCoordinate desired = target.subtract(this.position);
        desired = desired.normalize();
        desired = desired.multiply(this.maxSpeed);
        
        CartesianCoordinate steer = desired.subtract(this.velocity);
        return steer.limit(this.maxForce);
    }

    /**
     * Calculates the obstacle avoidance steering force.
     * Steers away from nearby obstacles.
     * @param obstacles List of obstacles in the simulation.
     * @return Avoidance force vector.
     */
    private CartesianCoordinate calculateObstacleAvoidanceForce(List<Rectangle> obstacles) {
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        
        for (Rectangle obstacle : obstacles) {
            double distance = this.position.distance(obstacle.getCenter()).magnitude();
            if (distance < this.obstacleSafetyRadius) {
                CartesianCoordinate away = this.position.subtract(obstacle.getCenter());
                away = away.normalize();
                double strength = (this.obstacleSafetyRadius - distance) / this.obstacleSafetyRadius;
                away = away.multiply(this.maxSpeed * strength);
                steer = steer.add(away);
            }
        }
        
        if (steer.magnitude() > 0) {
            steer = steer.normalize().multiply(this.maxSpeed);
            steer = steer.subtract(this.velocity);
            steer = steer.limit(this.maxForce * 2.0);
        }
        
        return steer;
    }

    /**
     * Gets the list of obstacles from the simulation.
     * This is a temporary solution - you should modify your architecture to properly pass obstacles to boids.
     */
    private List<Rectangle> getObstacles() {
        // This is a temporary solution. You should modify your architecture to properly pass obstacles to boids.
        // For now, we'll return an empty list to prevent compilation errors.
        return new ArrayList<>();
    }

    // later make another obstacles class that rectangle inherits from
    private CartesianCoordinate avoidObstacles(List<Rectangle> obstacles) {
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        for (Rectangle obstacle : obstacles) {
            if (obstacle.getPosition().distance(this.position).magnitude() < obstacle.getRadius()) {
                steer = steer.add(obstacle.getNormal(this.position));
            }
        }
        return steer;
    }

    // Add getter methods
    public CartesianCoordinate getPosition() {
        return this.position;
    }

    public CartesianCoordinate getVelocity() {
        return this.velocity;
    }

    public double getObstacleSafetyRadius() {
        return this.obstacleSafetyRadius;
    }

    /**
     * Reduces the boid's speed to help with obstacle avoidance.
     * Ensures speed doesn't go below minimum threshold.
     */
    public void reduceSpeed() {
        double currentSpeed = this.velocity.magnitude();
        if (currentSpeed > this.minSpeed) {
            this.velocity = this.velocity.multiply(0.7); // Less aggressive speed reduction
        }
    }

    // --- Getters for external access if needed (e.g., for GUI tuning) ---
    // public CartesianCoordinate getPosition() { return position; }
    // public CartesianCoordinate getVelocity() { return velocity; }
    // Add setters for weights if you implement GUI controls later
}