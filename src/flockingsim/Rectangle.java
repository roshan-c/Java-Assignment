package flockingsim;

import geometry.CartesianCoordinate;
import drawing.Canvas;

public class Rectangle {
    private CartesianCoordinate topLeft;
    private int dx;
    private int dy;
    private Canvas canvas;

    public Rectangle(CartesianCoordinate topLeft, int dx, int dy) {
        this.topLeft = topLeft;
        this.dx = dx;
        this.dy = dy;
    }
    public void draw() {
        CartesianCoordinate bottomRight = new CartesianCoordinate(this.topLeft.getX() + this.dx, this.topLeft.getY() + this.dy);
        this.canvas.drawLineBetweenPoints(this.topLeft, bottomRight);
        }
}
