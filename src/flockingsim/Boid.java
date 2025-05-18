package flockingsim;

import java.util.ArrayList;
import java.util.List; // Use List interface
import java.awt.Color;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import flockingsim.AbstractSimulatedEntity;

/**
 * A Boid is a simple object that can move around the canvas.
 * It has a position, velocity, and acceleration.
 * It can move, turn, and draw itself.
 * It can also put its pen up or down to control whether its movement is drawn
 * on the canvas.
 */

public class Boid extends AbstractSimulatedEntity {
    private double mousePerceptionRadius = 100.0;
    private double separationWeight = 1.5;
    private double alignmentWeight = 1.0;
    private double cohesionWeight = 1.0;
    private double mouseAvoidanceWeight = 0.0;
    private double desiredSeparation = 30.0;
    private double obstacleAvoidanceWeight = 4.0;
    private double obstacleSafetyRadius = 120.0;
    private double lookAheadDistance = 150.0;
    private double minSpeed = 2.0;
    private double maxTurnRate = 30.0;
    private double predatorFleeWeight = 2.5;
    private static final double BOID_LENGTH = 7; // Length of the boid
    private static final double BOID_WIDTH = 9; // Width of the boid
    private static final double BOID_BACK_OFFSET = 5; // Offset from the back of the boid to the tip of the tail

    /**
     * Constructor for the Boid class.
     * 
     * @param canvas           The canvas on which the boid will be drawn.
     * @param position         The initial position of the boid.
     * @param velocity         The initial velocity of the boid.
     * @param maxSpeed         The maximum speed the boid can travel.
     * @param maxForce         The maximum steering force that can be applied.
     * @param perceptionRadius The distance within which the boid considers others.
     */
    public Boid(Canvas canvas, CartesianCoordinate position, CartesianCoordinate velocity, double maxSpeed,
            double maxForce, double perceptionRadius) {
        super(canvas, position, velocity, maxSpeed, maxForce, perceptionRadius);
    }

    @Override
    public void update(List<SimulatedEntity> allEntities, List<Rectangle> obstacles, CartesianCoordinate currentMousePosition) {
        this.acceleration = new CartesianCoordinate(0, 0);

        // Get neighbors once
        ArrayList<Boid> neighbors = getNeighbors(allEntities);

        // Calculate flocking forces using the pre-filtered neighbors list
        CartesianCoordinate separation = calculateSeparationForce(neighbors);
        CartesianCoordinate alignment = calculateAlignmentForce(neighbors);
        CartesianCoordinate cohesion = calculateCohesionForce(neighbors);
        
        // Other forces
        CartesianCoordinate avoidance = calculateObstacleAvoidanceForce(obstacles);
        CartesianCoordinate mouseAvoidance = calculateMouseAvoidanceForce(currentMousePosition);
        CartesianCoordinate predatorFlee = calculatePredatorFleeForce(allEntities); // Predator flee still needs allEntities

        this.acceleration = this.acceleration.add(separation.multiply(separationWeight));
        this.acceleration = this.acceleration.add(alignment.multiply(alignmentWeight));
        this.acceleration = this.acceleration.add(cohesion.multiply(cohesionWeight));
        this.acceleration = this.acceleration.add(avoidance.multiply(obstacleAvoidanceWeight));
        this.acceleration = this.acceleration.add(mouseAvoidance.multiply(mouseAvoidanceWeight));
        this.acceleration = this.acceleration.add(predatorFlee.multiply(predatorFleeWeight));

        this.velocity = this.velocity.add(this.acceleration);
        double currentSpeed = this.velocity.magnitude();
        if (this.maxSpeed <= 0.001) {
            this.velocity = new CartesianCoordinate(0, 0);
        } else {
            if (currentSpeed > this.maxSpeed) {
                this.velocity = this.velocity.normalize().multiply(this.maxSpeed);
            } else if (currentSpeed < this.minSpeed && currentSpeed > 0.001) {
                if (this.minSpeed <= this.maxSpeed) {
                    this.velocity = this.velocity.normalize().multiply(this.minSpeed);
                }
            }
        }
        
        int moveDistance = (int) this.velocity.magnitude();
        if (moveDistance > 0) {
            double currentHeading = Math.toDegrees(Math.atan2(this.velocity.getY(), this.velocity.getX()));
            CartesianCoordinate desiredDirection = this.velocity.normalize();
            double desiredHeading = Math.toDegrees(Math.atan2(desiredDirection.getY(), desiredDirection.getX()));
            double turnNeeded = desiredHeading - currentHeading;
            while (turnNeeded > 180) turnNeeded -= 360;
            while (turnNeeded < -180) turnNeeded += 360;
            turnNeeded = Math.max(-this.maxTurnRate, Math.min(this.maxTurnRate, turnNeeded));
            
            super.turn((int) turnNeeded);
            this.move(moveDistance, obstacles);
        }
        if (this.canvas != null) {
            super.wrapPosition(this.canvas.getWidth(), this.canvas.getHeight());
        }
    }

    @Override
    public void draw() {
        if (!this.penDown || this.canvas == null) return;
        Color boidColor = Color.BLACK; 
        CartesianCoordinate currentPos = this.position;
        CartesianCoordinate vel = this.velocity;
        if (vel.magnitude() < 0.0001) {
            vel = new CartesianCoordinate(1, 0); 
        }
        CartesianCoordinate normVelocity = vel.normalize();
        CartesianCoordinate perpVelocity = normVelocity.perpendicular();
        CartesianCoordinate frontPosition = currentPos.add(normVelocity.multiply(BOID_LENGTH));
        CartesianCoordinate baseCenter = currentPos.add(normVelocity.multiply(-BOID_BACK_OFFSET));
        CartesianCoordinate leftPosition = baseCenter.add(perpVelocity.multiply(BOID_WIDTH / 2.0));
        CartesianCoordinate rightPosition = baseCenter.add(perpVelocity.multiply(-BOID_WIDTH / 2.0));
        
        this.canvas.drawLineBetweenPoints(frontPosition, leftPosition, boidColor);
        this.canvas.drawLineBetweenPoints(leftPosition, rightPosition, boidColor);
        this.canvas.drawLineBetweenPoints(rightPosition, frontPosition, boidColor);
    }

    public void undraw() { 
        if (this.canvas != null) this.canvas.removeMostRecentLine();
    }

    private ArrayList<Boid> getNeighbors(List<SimulatedEntity> allEntities) {
        ArrayList<Boid> neighbors = new ArrayList<>();
        for (SimulatedEntity entity : allEntities) {
            if (entity instanceof Boid && entity != this) {
                Boid otherBoid = (Boid) entity;
                double distance = this.position.distance(otherBoid.position).magnitude();
                if (distance > 0 && distance < this.perceptionRadius) {
                    neighbors.add(otherBoid);
                }
            }
        }
        return neighbors;
    }

    private CartesianCoordinate calculateSeparationForce(List<Boid> neighbors) {
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        int count = 0;
        for (Boid other : neighbors) {
            double distance = this.position.distance(other.position).magnitude();
            if (distance > 0 && distance < this.desiredSeparation) {
                CartesianCoordinate diff = this.position.subtract(other.position);
                diff = diff.normalize();
                double strength = Math.pow(1.0 - (distance / this.desiredSeparation), 2);
                diff = diff.multiply(this.maxSpeed * strength);
                steer = steer.add(diff);
                count++;
            }
        }
        if (count > 0) {
            steer = steer.divide(count);
            if (steer.magnitude() > 0) {
                steer = steer.normalize().multiply(this.maxSpeed);
                steer = steer.subtract(this.velocity);
                steer = steer.limit(this.maxForce * 1.5);
            }
        }
        return steer;
    }

    private CartesianCoordinate calculateAlignmentForce(List<Boid> neighbors) {
        CartesianCoordinate sumVelocities = new CartesianCoordinate(0, 0);
        int count = 0;
        for (Boid other : neighbors) {
            if (this.position.distance(other.position).magnitude() < this.perceptionRadius) {
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

    private CartesianCoordinate calculateCohesionForce(List<Boid> neighbors) {
        CartesianCoordinate sumPositions = new CartesianCoordinate(0, 0);
        int count = 0;
        for (Boid other : neighbors) {
            if (this.position.distance(other.position).magnitude() < this.perceptionRadius) {
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

    private CartesianCoordinate seek(CartesianCoordinate target) {
        CartesianCoordinate desired = target.subtract(this.position);
        desired = desired.normalize();
        desired = desired.multiply(this.maxSpeed);
        CartesianCoordinate steer = desired.subtract(this.velocity);
        return steer.limit(this.maxForce);
    }

    private CartesianCoordinate calculateObstacleAvoidanceForce(List<Rectangle> obstacles) {
        CartesianCoordinate steer = new CartesianCoordinate(0, 0);
        int count = 0;
        CartesianCoordinate futurePosition = this.position.add(
                this.velocity.normalize().multiply(this.lookAheadDistance));
        for (Rectangle obstacle : obstacles) {
            double currentDistance = this.position.distance(obstacle.getCenter()).magnitude();
            double futureDistance = futurePosition.distance(obstacle.getCenter()).magnitude();
            double distance = Math.min(currentDistance, futureDistance);
            if (distance > 0 && distance < this.obstacleSafetyRadius) {
                CartesianCoordinate diff = this.position.subtract(obstacle.getCenter());
                diff = diff.normalize();
                double strength = Math.pow(1.0 - (distance / this.obstacleSafetyRadius), 2);
                diff = diff.multiply(this.maxSpeed * strength * 3.0);
                steer = steer.add(diff);
                count++;
            }
        }
        if (count > 0) {
            steer = steer.divide(count);
            if (steer.magnitude() > 0) {
                steer = steer.normalize().multiply(this.maxSpeed * 2.5);
                steer = steer.subtract(this.velocity);
                steer = steer.limit(this.maxForce * 3.0);
            }
        }
        return steer;
    }

    private CartesianCoordinate calculateMouseAvoidanceForce(CartesianCoordinate currentMousePos) {
        if (currentMousePos == null) { 
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
        return steer.limit(this.maxForce * 2.0); 
    }

    private CartesianCoordinate calculatePredatorFleeForce(List<SimulatedEntity> allEntities) {
        CartesianCoordinate totalFleeForce = new CartesianCoordinate(0, 0);
        int predatorsNearby = 0;

        for (SimulatedEntity entity : allEntities) {
            if (entity instanceof Predator) {
                Predator predator = (Predator) entity;
                CartesianCoordinate predatorPosition = predator.getPosition();
                double distance = this.position.distance(predatorPosition).magnitude();
                
                if (distance > 0 && distance < this.perceptionRadius) {
                    CartesianCoordinate diff = this.position.subtract(predatorPosition);
                    diff = diff.normalize();
                    diff = diff.divide(distance);
                    
                    totalFleeForce = totalFleeForce.add(diff);
                    predatorsNearby++;
                }
            }
        }

        if (predatorsNearby > 0) {
            totalFleeForce = totalFleeForce.divide(predatorsNearby);
            if (totalFleeForce.magnitude() > 0) {
                totalFleeForce = totalFleeForce.normalize().multiply(this.maxSpeed);
                totalFleeForce = totalFleeForce.subtract(this.getVelocity());
                totalFleeForce = totalFleeForce.limit(this.maxForce * 3.5);
            }
        }
        return totalFleeForce;
    }

    public void avoidPredator(List<SimulatedEntity> allEntities) {
        for (SimulatedEntity entity : allEntities) {
            if (entity instanceof Predator) {
                Predator predator = (Predator) entity;
                CartesianCoordinate predatorPosition = predator.getPosition();
                double distance = this.position.distance(predatorPosition).magnitude();
                if (distance > 0 && distance < this.perceptionRadius) {
                    CartesianCoordinate diff = this.position.subtract(predatorPosition);
                    diff = diff.normalize();
                    double strength = Math.pow(1.0 - (distance / this.perceptionRadius), 2);
                    
                }
            }
        }
    }

    public double getObstacleSafetyRadius() {
        return this.obstacleSafetyRadius;
    }

    public void setSeparationWeight(double weight) {
        this.separationWeight = weight;
    }
    public void setAlignmentWeight(double weight) {
        this.alignmentWeight = weight;
    }
    public void setCohesionWeight(double weight) {
        this.cohesionWeight = weight;
    }
    public void setObstacleAvoidanceWeight(double weight) {
        this.obstacleAvoidanceWeight = weight;
    }
    public void setMouseAvoidanceWeight(double weight) {
        this.mouseAvoidanceWeight = weight;
    }
    public void setPredatorFleeWeight(double weight) {
        this.predatorFleeWeight = weight;
    }

    public void reduceSpeed() {
        double currentSpeedVal = this.getVelocity().magnitude();
        if (currentSpeedVal > this.minSpeed) {
            this.velocity = this.getVelocity().multiply(0.7);
        }
    }

    @Override
    public double getVisualRadius() {
        return Math.max(BOID_LENGTH, BOID_WIDTH) / 2.0; 
    }
}