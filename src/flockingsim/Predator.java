package flockingsim;

import java.util.ArrayList;

import geometry.CartesianCoordinate;
import drawing.Canvas;



public class Predator implements SimulatedEntity {
    private CartesianCoordinate position;
    private CartesianCoordinate velocity;
    private CartesianCoordinate acceleration;
    private double maxSpeed;
    private double maxForce;
    private double perceptionRadius;

    public Predator(CartesianCoordinate position, CartesianCoordinate velocity, double maxSpeed, double maxForce, double perceptionRadius) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new CartesianCoordinate(0, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.perceptionRadius = perceptionRadius;
    }

    public void update(List<SimulatedEntity> allEntities, List<Rectangle> obstacles, CartesianCoordinate currentMousePosition) {

        // Reset acceleration
        this.acceleration = new CartesianCoordinate(0, 0);
        this.velocity = this.velocity.add(this.acceleration);

        double currentSpeed = this.velocity.magnitude();
        if (currentSpeed > this.maxSpeed) {
            this.velocity = this.velocity.normalize().multiply(this.maxSpeed);
        }

        






    }


}
