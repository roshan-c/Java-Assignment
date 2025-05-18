package flockingsim;

import java.awt.Color;
import java.util.List;

import geometry.CartesianCoordinate;
import drawing.Canvas;

public class Predator extends AbstractSimulatedEntity {
    // Inherited fields: position, velocity, acceleration, canvas, penDown, maxSpeed, maxForce, perceptionRadius

    // Predator-specific constants for drawing
    private static final double PREDATOR_LENGTH = 12; // Slightly larger than boid
    private static final double PREDATOR_WIDTH = 12;
    private static final double PREDATOR_BACK_OFFSET = 8;
    private double predatorTurnRate = 20.0; // Predators might turn a bit slower or faster

    public Predator(Canvas canvas, CartesianCoordinate position, CartesianCoordinate velocity, 
                    double maxSpeed, double maxForce, double perceptionRadius) {
        super(canvas, position, velocity, maxSpeed, maxForce, perceptionRadius);
        // this.acceleration is initialized in super, so no need here unless different default.
    }

    @Override
    public void update(List<SimulatedEntity> allEntities, List<Rectangle> obstacles, CartesianCoordinate currentMousePosition) {
        this.acceleration = new CartesianCoordinate(0, 0); // Reset acceleration each frame

        // Predator behavior
        hunt(allEntities);
        // Obstacle avoidance for predators (can be simpler than boids or similar)
        // CartesianCoordinate obstacleAvoidanceForce = calculateSimpleObstacleAvoidance(obstacles);
        // this.acceleration = this.acceleration.add(obstacleAvoidanceForce.multiply(someWeight));

        // Basic physics: update velocity from acceleration
        this.velocity = this.velocity.add(this.acceleration);
        
        // Speed limit
        double currentSpeed = this.velocity.magnitude();
        if (this.maxSpeed > 0 && currentSpeed > this.maxSpeed) { // Use inherited maxSpeed
            this.velocity = this.velocity.normalize().multiply(this.maxSpeed);
        } else if (this.maxSpeed <= 0) { // Handle case where maxSpeed is zero or negative
            this.velocity = new CartesianCoordinate(0,0);
        }

        // Movement: use inherited turn and move
        int moveDistance = (int) this.velocity.magnitude();
        if (moveDistance > 0) {
            double currentHeading = Math.toDegrees(Math.atan2(this.velocity.getY(), this.velocity.getX()));
            CartesianCoordinate desiredDirection = this.velocity.normalize();
            double desiredHeading = Math.toDegrees(Math.atan2(desiredDirection.getY(), desiredDirection.getX()));
            double turnNeeded = desiredHeading - currentHeading;
            while (turnNeeded > 180) turnNeeded -= 360;
            while (turnNeeded < -180) turnNeeded += 360;
            turnNeeded = Math.max(-this.predatorTurnRate, Math.min(this.predatorTurnRate, turnNeeded));
            
            super.turn((int) turnNeeded); // Call inherited turn method
            super.move(moveDistance, obstacles); // Call inherited move method
        }

        // Apply screen wrapping (inherited)
        if (this.canvas != null) {
             super.wrapPosition(this.canvas.getWidth(), this.canvas.getHeight());
        }
    }

    @Override
    public void draw() {
        if (!this.penDown || this.canvas == null) return; // penDown is inherited
        Color predatorColor = Color.RED; 
        CartesianCoordinate currentPos = this.position; // position is inherited
        CartesianCoordinate vel = this.velocity; // velocity is inherited
        if (vel.magnitude() < 0.0001) {
            vel = new CartesianCoordinate(1, 0); 
        }
        CartesianCoordinate normVelocity = vel.normalize();
        CartesianCoordinate perpVelocity = normVelocity.perpendicular();
        CartesianCoordinate frontPosition = currentPos.add(normVelocity.multiply(PREDATOR_LENGTH));
        CartesianCoordinate baseCenter = currentPos.add(normVelocity.multiply(-PREDATOR_BACK_OFFSET));
        CartesianCoordinate leftPosition = baseCenter.add(perpVelocity.multiply(PREDATOR_WIDTH / 2.0));
        CartesianCoordinate rightPosition = baseCenter.add(perpVelocity.multiply(-PREDATOR_WIDTH / 2.0));
        
        this.canvas.drawLineBetweenPoints(frontPosition, leftPosition, predatorColor);
        this.canvas.drawLineBetweenPoints(leftPosition, rightPosition, predatorColor);
        this.canvas.drawLineBetweenPoints(rightPosition, frontPosition, predatorColor);
    }
    
    private void hunt(List<SimulatedEntity> allEntities) {
        Boid closestBoid = null;
        double closestDistance = Double.MAX_VALUE;

        for (SimulatedEntity entity : allEntities) {
            if (entity instanceof Boid) {
                Boid boid = (Boid) entity;
                double distanceToBoid = this.position.distance(boid.getPosition()).magnitude(); // position is inherited
                
                // Use inherited perceptionRadius
                if (distanceToBoid < this.perceptionRadius && distanceToBoid < closestDistance) {
                    closestBoid = boid;
                    closestDistance = distanceToBoid;
                }
            }
        }

        if (closestBoid != null) {
            // Steer towards the closest boid
            CartesianCoordinate steeringForce = seek(closestBoid.getPosition()); 
            this.acceleration = this.acceleration.add(steeringForce);
        } else {
            // Optional: If no boid is close, predator could wander, slow down, or stop.
            // For now, it will just continue with its current velocity if no boid is targeted.
            // this.velocity = this.velocity.multiply(0.95); // Example: slow down slightly
        }
    }

    // Seek method to steer towards a target
    private CartesianCoordinate seek(CartesianCoordinate target) {
        CartesianCoordinate desired = target.subtract(this.position); // position is inherited
        desired = desired.normalize().multiply(this.maxSpeed); // maxSpeed is inherited
        CartesianCoordinate steer = desired.subtract(this.velocity); // velocity is inherited
        return steer.limit(this.maxForce); // maxForce is inherited
    }
}
