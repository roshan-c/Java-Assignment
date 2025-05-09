package geometry;


public class CartesianCoordinate {
	// final variables because we are required to not let the point move.
	private final double xPosition;
	private final double yPosition;

	public CartesianCoordinate(double xPosition, double yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	public double getX() {
		return xPosition;
	}

	public double getY() {
		return yPosition;
	}

	@Override
	public String toString() {
		return "CartesianCoordinate [x=" + xPosition + ", y=" + yPosition + "]";
	}

	public CartesianCoordinate add(CartesianCoordinate dist) {
		return new CartesianCoordinate(this.getX() + dist.getX(), this.getY() + dist.getY());
	}

	public CartesianCoordinate multiply(double scalar) {
		return new CartesianCoordinate(this.xPosition * scalar, this.yPosition * scalar);
	}

}
