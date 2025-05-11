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

	public CartesianCoordinate subtract(CartesianCoordinate dist) {
		return new CartesianCoordinate(this.getX() - dist.getX(), this.getY() - dist.getY());
	}

	public CartesianCoordinate multiply(double scalar) {
		return new CartesianCoordinate(this.xPosition * scalar, this.yPosition * scalar);
	}

	public CartesianCoordinate divide(double scalar) {
		return new CartesianCoordinate(this.xPosition / scalar, this.yPosition / scalar);
	}	

	// Used to normalize the vector to a unit vector (represents direction)
	public CartesianCoordinate normalize() {
		double magnitude = Math.sqrt(this.xPosition * this.xPosition + this.yPosition * this.yPosition);
		if (magnitude == 0) {
			return new CartesianCoordinate(0, 0);
		}
		return new CartesianCoordinate(this.xPosition / magnitude, this.yPosition / magnitude);
	}

	// Used to get the perpendicular vector (90 degree rotation)
	public CartesianCoordinate perpendicular() {
		return new CartesianCoordinate(-this.yPosition, this.xPosition);
	}

	public double magnitude() {
		return Math.sqrt(this.xPosition * this.xPosition + this.yPosition * this.yPosition);
	}

	public CartesianCoordinate limit(double maxMagnitude) {
		double currentMagnitude = this.magnitude();
		if (currentMagnitude > maxMagnitude) {
			return this.normalize().multiply(maxMagnitude);
		}
		return this;
	}

	public CartesianCoordinate distance(CartesianCoordinate other) {
		return new CartesianCoordinate(this.xPosition - other.xPosition, this.yPosition - other.yPosition);
	}

}
