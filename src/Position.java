
public class Position {
	
	protected int x;
	protected int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	public boolean equals(Object obj) {
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public int distanceTo(Position pos) {
		return Math.abs(x - pos.x) + Math.abs(y - pos.y);
	}
	
}
