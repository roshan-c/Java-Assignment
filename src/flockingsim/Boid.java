package flockingsim;

import java.util.ArrayList;
import java.util.List; // Use List interface
import java.awt.Color;

import drawing.Canvas;
import geometry.CartesianCoordinate;

/**
 * A Boid is a simple object that can move around the canvas.   
 * It has a position, velocity, and acceleration.
 * It can move, turn, and draw itself.
 * It can also put its pen up or down to control whether its movement is drawn on the canvas. */

public class Boid implements SimulatedEntity {
    private CartesianCoordinate position;
    private CartesianCoordinate velocity;
    private CartesianCoordinate acceleration;
    private boolean penDown;
    private Canvas canvas;
    private double maxSpeed;
    private double maxForce;
    private double perceptionRadius;
    private double mousePerceptionRadius = 100.0;
    private double separationWeight = 1.5;    
    private double alignmentWeight = 1.1;    
    private double cohesionWeight = 1.1; 
    private double mouseAvoidanceWeight = 0.0;     
    private double desiredSeparation = 30.0;   
    private double obstacleAvoidanceWeight = 4.0;  
    private double obstacleSafetyRadius = 120.0;
    private double lookAheadDistance = 150.0;
    private double minSpeed = 2.0;
    private double maxTurnRate = 30.0; 
    private static final double BOID_LENGTH = 7; // Length of the boid
    private static final double BOID_WIDTH = 9; // Width of the boid
    private static final double BOID_BACK_OFFSET = 5; // Offset from the back of the boid to the tip of the tail


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
    public void update(List<Boid> allBoids, List<Rectangle> obstacles, CartesianCoordinate currentMousePosition) {
        // Reset acceleration
        this.acceleration = new CartesianCoordinate(0, 0);

        // Apply the three core flocking rules
        CartesianCoordinate separation = calculateSeparationForce(allBoids);
        CartesianCoordinate alignment = calculateAlignmentForce(allBoids);
        CartesianCoordinate cohesion = calculateCohesionForce(allBoids);
        CartesianCoordinate avoidance = calculateObstacleAvoidanceForce(obstacles);

        CartesianCoordinate mouseAvoidance = calculateMouseAvoidanceForce(currentMousePosition);


        // Apply weights
        this.acceleration = this.acceleration.add(separation.multiply(separationWeight));
        this.acceleration = this.acceleration.add(alignment.multiply(alignmentWeight));
        this.acceleration = this.acceleration.add(cohesion.multiply(cohesionWeight));
        this.acceleration = this.acceleration.add(avoidance.multiply(obstacleAvoidanceWeight));
        this.acceleration = this.acceleration.add(mouseAvoidance.multiply(mouseAvoidanceWeight));

        // Update velocity
        this.velocity = this.velocity.add(this.acceleration);
        
        // Speed control
        double currentSpeed = this.velocity.magnitude();

        if (this.maxSpeed <= 0.001) { // Using a small epsilon for "effectively zero"
            this.velocity = new CartesianCoordinate(0, 0);
        } else {
            if (currentSpeed > this.maxSpeed) {
                this.velocity = this.velocity.normalize().multiply(this.maxSpeed);
            } else if (currentSpeed < this.minSpeed && currentSpeed > 0.001) { // currentSpeed > 0.001 to avoid normalizing zero vector and for meaningful check
                // Only apply minSpeed if it's not trying to exceed maxSpeed
                if (this.minSpeed <= this.maxSpeed) {
                    this.velocity = this.velocity.normalize().multiply(this.minSpeed);
                }
                // If minSpeed > maxSpeed, currentSpeed is already capped by maxSpeed if it was higher,
                // or it's naturally between 0 and maxSpeed. In this state, it will keep its current speed.
                // (Implicitly, if currentSpeed is 0, and maxSpeed > 0, it stays 0 until next acceleration)
            }
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
            
            // Limit turn rate more gradually
            turnNeeded = Math.max(-this.maxTurnRate, Math.min(this.maxTurnRate, turnNeeded));
            
            // Apply turn and move
            this.turn((int)turnNeeded);
            this.move(moveDistance, obstacles);
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
     * @param obstacles The list of obstacles to check against
     */
    public void move(int distance, List<Rectangle> obstacles) {
        if (distance <= 0) return;
        
        CartesianCoordinate direction = this.velocity.normalize();
        CartesianCoordinate displacement = direction.multiply(distance);
        CartesianCoordinate intendedPosition = this.position.add(displacement);

        CartesianCoordinate finalProposedPosition = intendedPosition; // Start with the direct intended position

        if (this.canvas != null) {
            double x = intendedPosition.getX();
            double y = intendedPosition.getY();
            int canvasWidth = this.canvas.getWidth();
            int canvasHeight = this.canvas.getHeight();
            boolean changedByWrapping = false;

            // Only attempt to wrap if canvas dimensions are valid (greater than 0)
            if (canvasWidth > 0 && canvasHeight > 0) {
                double newX = x;
                double newY = y;

                // Perform wrapping logic similar to Boid.wrapPosition
                while (newX < 0) {
                    newX += canvasWidth;
                    changedByWrapping = true;
                }
                while (newX >= canvasWidth) {
                    newX -= canvasWidth;
                    changedByWrapping = true;
                }
                while (newY < 0) {
                    newY += canvasHeight;
                    changedByWrapping = true;
                }
                while (newY >= canvasHeight) {
                    newY -= canvasHeight;
                    changedByWrapping = true;
                }

                if (changedByWrapping) {
                    finalProposedPosition = new CartesianCoordinate(newX, newY);
                }
            }
            // If not changedByWrapping or canvas dimensions invalid, finalProposedPosition remains intendedPosition
        }

        // Check if the (potentially wrapped) new position is safe
        if (isPositionSafe(finalProposedPosition, obstacles)) {
            this.position = finalProposedPosition; // Update to the safe, and correctly wrapped, position
        } else {
            // If position is not safe (i.e., inside an obstacle), do not move.
            // Make the boid "bounce" back slightly by reversing its velocity at reduced speed.
            this.velocity = this.velocity.multiply(-0.5); 
        }
    }

    private boolean isPositionSafe(CartesianCoordinate newPosition, List<Rectangle> obstacles) {
        for (Rectangle obstacle : obstacles) {
            if (obstacle.contains(newPosition)) {
                return false; // Unsafe: inside an obstacle
            }
        }
        return true; // Safe
    }

    /**
     * Draws the boid on the canvas.
     * For debugging, this draws a small cross at the boid's position.
     */
    public void draw() {
        if (!this.penDown) return;
        if (this.canvas == null) return; 

        Color boidColor = Color.BLACK; // All boids will be black

        CartesianCoordinate currentPosition = this.position;
        CartesianCoordinate vel = this.velocity;
        if (vel.magnitude() < 0.0001) {
            vel = new CartesianCoordinate(1, 0);
        }
        CartesianCoordinate normVelocity = vel.normalize();
        CartesianCoordinate perpVelocity = normVelocity.perpendicular();
        CartesianCoordinate frontPosition = currentPosition.add(normVelocity.multiply(BOID_LENGTH));
        CartesianCoordinate baseCenter = currentPosition.add(normVelocity.multiply(-BOID_BACK_OFFSET));
        CartesianCoordinate leftPosition = baseCenter.add(perpVelocity.multiply(BOID_WIDTH / 2.0));
        CartesianCoordinate rightPosition = baseCenter.add(perpVelocity.multiply(-BOID_WIDTH / 2.0));
        
        this.canvas.drawLineBetweenPoints(frontPosition, leftPosition, boidColor);
        this.canvas.drawLineBetweenPoints(leftPosition, rightPosition, boidColor);
        this.canvas.drawLineBetweenPoints(rightPosition, frontPosition, boidColor);
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
                // Make the force stronger when closer
                double strength = Math.pow(1.0 - (distance / this.desiredSeparation), 2);
                diff = diff.multiply(strength * this.maxSpeed);
                steer = steer.add(diff);
                count++;
            }
        }
        
        if (count > 0) {
            steer = steer.divide(count);
            if (steer.magnitude() > 0) {
                steer = steer.normalize().multiply(this.maxSpeed);
                steer = steer.subtract(this.velocity);
                steer = steer.limit(this.maxForce * 1.5); // Increased force limit for separation
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
        int count = 0;
        
        // Calculate future position based on current velocity
        CartesianCoordinate futurePosition = this.position.add(
            this.velocity.normalize().multiply(this.lookAheadDistance)
        );
        
        for (Rectangle obstacle : obstacles) {
            // Check both current and future position
            double currentDistance = this.position.distance(obstacle.getCenter()).magnitude();
            double futureDistance = futurePosition.distance(obstacle.getCenter()).magnitude();
            
            // Use the closer of the two distances
            double distance = Math.min(currentDistance, futureDistance);
            
            // If within safety radius, avoid the obstacle
            if (distance > 0 && distance < this.obstacleSafetyRadius) {
                // Calculate vector away from obstacle
                CartesianCoordinate diff = this.position.subtract(obstacle.getCenter());
                diff = diff.normalize();
                
                // Make the force more gradual
                double strength = Math.pow(1.0 - (distance / this.obstacleSafetyRadius), 2);
                diff = diff.multiply(this.maxSpeed * strength * 3.0); // Increased multiplier from 2.0 to 3.0
                
                steer = steer.add(diff);
                count++;
            }
        }
        
        if (count > 0) {
            // Average the avoidance forces
            steer = steer.divide(count);
            
            // Scale to desired speed and make the force smoother
            if (steer.magnitude() > 0) {
                steer = steer.normalize().multiply(this.maxSpeed * 2.5); // Increased multiplier from 1.5 to 2.5
                steer = steer.subtract(this.velocity);
                steer = steer.limit(this.maxForce * 3.0); // Increased force limit multiplier from 2.0 to 3.0
            }
        }
        
        return steer;
    }

    private CartesianCoordinate calculateMouseAvoidanceForce(CartesianCoordinate currentMousePos) {
        if (currentMousePos == null) { // If mouse position is not available
            return new CartesianCoordinate(0, 0);
        }
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        double distance = this.position.distance(currentMousePos).magnitude();
        if (distance > 0 && distance < this.mousePerceptionRadius) {
            CartesianCoordinate diff = this.position.subtract(currentMousePos);
            diff = diff.normalize();
            double strength = Math.pow(1.0 - (distance / this.mousePerceptionRadius), 2);
            diff = diff.multiply(this.maxSpeed * strength * 3.0);
            steer = steer.add(diff);
        }
        return steer.limit(this.maxForce * 2.0); // Limit the force like other flocking forces
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
     * Sets the maximum speed for this boid.
     * @param maxSpeed The new maximum speed value
     */
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Sets the separation weight for this boid.
     * @param weight The new separation weight
     */
    public void setSeparationWeight(double weight) {
        this.separationWeight = weight;
    }

    /**
     * Sets the alignment weight for this boid.
     * @param weight The new alignment weight
     */
    public void setAlignmentWeight(double weight) {
        this.alignmentWeight = weight;
    }

    /**
     * Sets the cohesion weight for this boid.
     * @param weight The new cohesion weight
     */
    public void setCohesionWeight(double weight) {
        this.cohesionWeight = weight;
    }

    /**
     * Sets the obstacle avoidance weight for this boid.
     * @param weight The new obstacle avoidance weight
     */
    public void setObstacleAvoidanceWeight(double weight) {
        this.obstacleAvoidanceWeight = weight;
    }

    /**
     * Sets the mouse weight for this boid.
     * @param weight The new mouse weight
     */
    public void setMouseAvoidanceWeight(double weight) {
        this.mouseAvoidanceWeight = weight;
    }

    /**
     * Reduces the boid's speed to help with obstacle avoidance.
     * Ensures speed doesn't go below minimum threshold.
     */
    public void reduceSpeed() {
        double currentSpeedVal = this.velocity.magnitude();
        if (currentSpeedVal > this.minSpeed) {
            this.velocity = this.velocity.multiply(0.7);
        }
    }
}