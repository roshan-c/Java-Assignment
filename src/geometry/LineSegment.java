package geometry;

public class LineSegment {
	// final variables because we are required to not let the line move.
	private final CartesianCoordinate startPoint;
	private final CartesianCoordinate endPoint;

	public LineSegment(CartesianCoordinate startPoint, CartesianCoordinate endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public CartesianCoordinate getStartPoint() {
		return startPoint;
	}

	public CartesianCoordinate getEndPoint() {
		return endPoint;
	}

	@Override
	public String toString() {
		return "LineSegment [startPoint=" + startPoint + ", endPoint=" + endPoint + "]";
	}

	/**
	 * Calculates the length of the line segment.
	 * 
	 * @return the length.
	 */
	public double length() {
		double dx = endPoint.getX() - startPoint.getX();
		double dy = endPoint.getY() - startPoint.getY();
		double length = Math.hypot(dx, dy);
		return length;
	}

}
