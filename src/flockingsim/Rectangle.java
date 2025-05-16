package flockingsim;

import geometry.CartesianCoordinate;
import drawing.Canvas;

public class Rectangle {
    private CartesianCoordinate topLeft;
    private int dx;
    private int dy;
    private Canvas canvas;

    public Rectangle(CartesianCoordinate topLeft, int dx, int dy, Canvas canvas) {
        this.topLeft = topLeft;
        this.dx = dx;
        this.dy = dy;
        this.canvas = canvas;
    }

    /**
     * Draws the rectangle on the canvas.
     */
    public void draw() {
        if (this.canvas == null) return;

        CartesianCoordinate topRight = new CartesianCoordinate(this.topLeft.getX() + this.dx, this.topLeft.getY());
        CartesianCoordinate bottomLeft = new CartesianCoordinate(this.topLeft.getX(), this.topLeft.getY() + this.dy);
        CartesianCoordinate bottomRight = new CartesianCoordinate(this.topLeft.getX() + this.dx, this.topLeft.getY() + this.dy);

        // Draw the four sides
        this.canvas.drawLineBetweenPoints(this.topLeft, topRight);
        this.canvas.drawLineBetweenPoints(topRight, bottomRight);
        this.canvas.drawLineBetweenPoints(bottomRight, bottomLeft);
        this.canvas.drawLineBetweenPoints(bottomLeft, this.topLeft);
    }

    /**
     * Returns the top-left position of the rectangle.
     * @return CartesianCoordinate representing the top-left corner.
     */
    public CartesianCoordinate getPosition() {
        return this.topLeft;
    }

    /**
     * Returns the center position of the rectangle.
     * @return CartesianCoordinate representing the center.
     */
    public CartesianCoordinate getCenter() {
        return new CartesianCoordinate(this.topLeft.getX() + this.dx / 2.0, this.topLeft.getY() + this.dy / 2.0);
    }

    /**
     * Calculates an approximate "radius" for coarse collision detection.
     * Uses half of the smaller dimension.
     * @return double representing the approximate radius.
     */
    public double getRadius() {
        // Use half of the smaller dimension as a simple radius approximation
        return Math.min(this.dx, this.dy) / 2.0;
    }

    /**
     * Calculates a more accurate avoidance "radius" based on the distance
     * from the center to the furthest corner. This ensures the detection
     * radius encompasses the whole rectangle.
     * @return double representing the bounding radius.
     */
    public double getBoundingRadius() {
        double halfDx = this.dx / 2.0;
        double halfDy = this.dy / 2.0;
        // Distance from center to a corner
        return Math.sqrt(halfDx * halfDx + halfDy * halfDy);
    }

    /**
     * Calculates a normal vector pointing away from the rectangle's center towards the boid's position.
     * This is a simplified approach for obstacle avoidance steering.
     * @param boidPosition The position of the boid.
     * @return CartesianCoordinate representing the normal vector (normalized).
     */
    public CartesianCoordinate getNormal(CartesianCoordinate boidPosition) {
        CartesianCoordinate center = getCenter();
        CartesianCoordinate directionFromCenter = boidPosition.subtract(center);
        return directionFromCenter.normalize(); // Return normalized vector pointing away from center
    }

    /**
     * Checks if a given point is inside the rectangle's bounds.
     * @param point The CartesianCoordinate to check.
     * @return true if the point is inside or on the boundary, false otherwise.
     */
    public boolean contains(CartesianCoordinate point) {
        double px = point.getX();
        double py = point.getY();
        double rx = this.topLeft.getX();
        double ry = this.topLeft.getY();
        return px >= rx && px <= rx + this.dx && py >= ry && py <= ry + this.dy;
    }
}
