package flockingsim;

import java.util.List;

import drawing.Canvas;
import geometry.CartesianCoordinate;

public abstract class  AbstractSimulatedEntitity implements SimulatedEntity {

    private CartesianCoordinate position;
    private CartesianCoordinate velocity;
    private CartesianCoordinate acceleration;
    private Canvas canvas;
    private double maxSpeed;
    private double maxForce;
    private double perceptionRadius;
    
    public AbstractSimulatedEntitity(CartesianCoordinate position, CartesianCoordinate velocity, double maxSpeed, double maxForce, double perceptionRadius) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new CartesianCoordinate(0, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.perceptionRadius = perceptionRadius;
    }

    public abstract void move(int distance, List<Rectangle> obstacles);
    public abstract void turn(int angle);
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

}
