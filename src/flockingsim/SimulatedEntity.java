package flockingsim;

import java.util.List;
import geometry.CartesianCoordinate;

/**
 * Interface for all simulated entities in the flocking simulation.
 * Defines the common contract for behaviors like updating state and drawing.
 */
public interface SimulatedEntity {

    /**
     * Updates the entity's state based on its environment and interactions.
     * @param allEntities A list of all other entities in the simulation.
     * @param obstacles A list of obstacles in the simulation.
     * @param mousePosition The current position of the mouse on the canvas.
     */
    void update(List<SimulatedEntity> allEntities, List<Rectangle> obstacles, CartesianCoordinate mousePosition);

    /**
     * Draws the entity on the canvas.
     */
    void draw();

    /**
     * Gets the current position of the entity.
     * @return The CartesianCoordinate representing the entity's position.
     */
    CartesianCoordinate getPosition();

    /**
     * Gets the current velocity of the entity.
     * @return The CartesianCoordinate representing the entity's velocity.
     */
    CartesianCoordinate getVelocity();

    /**
     * Sets the maximum speed for this entity.
     * @param maxSpeed The new maximum speed value.
     */
    void setMaxSpeed(double maxSpeed);
    
    // Potentially other common methods if they emerge, e.g.:
    // boolean isAlive();
    // double getPerceptionRadius(); // If common and needed by others polymorphically
}
