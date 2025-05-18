package flockingsim;

import java.util.List;

import drawing.Canvas;
import geometry.CartesianCoordinate;

public abstract class  AbstractSimulatedEntity implements SimulatedEntity {

    protected CartesianCoordinate position;
    protected CartesianCoordinate velocity;
    protected CartesianCoordinate acceleration;
    protected Canvas canvas;
    protected boolean penDown;
    protected double maxSpeed;
    protected double maxForce;
    protected double perceptionRadius;
    
    public AbstractSimulatedEntity(Canvas canvas, CartesianCoordinate position, CartesianCoordinate velocity, double maxSpeed, double maxForce, double perceptionRadius) {

        this.canvas = canvas;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new CartesianCoordinate(0, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.perceptionRadius = perceptionRadius;
        this.penDown = true;
    }

    public abstract void draw();
    public abstract void update(List<SimulatedEntity> allEntities, List<Rectangle> obstacles, CartesianCoordinate currentMousePosition);
    

    public CartesianCoordinate getPosition() {
        return this.position;
    }

    public CartesianCoordinate getVelocity() {
        return this.velocity;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMaxForce(double maxForce) {
        this.maxForce = maxForce;
    }

    public void wrapPosition(int canvasWidth, int canvasHeight) {
        if (this.canvas == null) return;
        canvasWidth = this.canvas.getWidth();
        canvasHeight = this.canvas.getHeight();

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

    public void turn(int angle) {
        double angleRad = Math.toRadians(angle);

        if (this.velocity.magnitude() == 0) {
            return;
        }

        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);

        double newVx = this.velocity.getX() * cosTheta - this.velocity.getY() * sinTheta;
        double newVy = this.velocity.getX() * sinTheta + this.velocity.getY() * cosTheta;

        this.velocity = new CartesianCoordinate(newVx, newVy);

        double currentSpeed = this.velocity.magnitude();
        if (currentSpeed > 0) {
            this.velocity = this.velocity.normalize().multiply(currentSpeed);
        }
    }

    public void move(int distance, List<Rectangle> obstacles) {
        if (distance <= 0) return;
        
        CartesianCoordinate direction;
        if (this.velocity.magnitude() == 0) {
            return; 
        }
        direction = this.velocity.normalize();
        
        CartesianCoordinate displacement = direction.multiply(distance);
        CartesianCoordinate intendedPosition = this.position.add(displacement);
        CartesianCoordinate finalProposedPosition = intendedPosition; 

        if (this.canvas != null) {
            double x = intendedPosition.getX();
            double y = intendedPosition.getY();
            int canvasWidth = this.canvas.getWidth();
            int canvasHeight = this.canvas.getHeight();
            boolean changedByWrapping = false;

            if (canvasWidth > 0 && canvasHeight > 0) {
                double newX = x;
                double newY = y;
                while (newX < 0) { newX += canvasWidth; changedByWrapping = true; }
                while (newX >= canvasWidth) { newX -= canvasWidth; changedByWrapping = true; }
                while (newY < 0) { newY += canvasHeight; changedByWrapping = true; }
                while (newY >= canvasHeight) { newY -= canvasHeight; changedByWrapping = true; }
                if (changedByWrapping) {
                    finalProposedPosition = new CartesianCoordinate(newX, newY);
                }
            }
        }

        if (isPositionSafe(finalProposedPosition, obstacles)) {
            this.position = finalProposedPosition; 
        } else {
            this.velocity = this.velocity.multiply(-0.5); 
        }
    }

    private boolean isPositionSafe(CartesianCoordinate newPosition, List<Rectangle> obstacles) {
        for (Rectangle obstacle : obstacles) {
            if (obstacle.contains(newPosition)) {
                return false; 
            }
        }
        return true; 
    }
}
