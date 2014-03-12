
// A class to represent a GridCoordinates
// Clearly based off of Rectangle
// You do not have to use this, but it's quite convenient
public class GridCoordinates {
        // invariant: right >= left and top >= bottom (i.e., numbers get bigger as you move up/right)
        // note in our census data longitude "West" is a negative number which nicely matches bigger-to-the-right
	public int minX;
	public int maxX;
	public int maxY;
	public int minY;
	
	public GridCoordinates(int smallX, int bigX, int bigY, int smallY) {
		minX   = smallX;
		maxX  = bigX;
		maxY    = bigY;
		minY = smallY;
	}
	
	// a functional operation: returns a new Rectangle that is the smallest rectangle
	// containing this and that
	public GridCoordinates encompass(GridCoordinates that) {
		return new GridCoordinates(Math.min(this.minX,   that.minX),
						     Math.max(this.maxX,  that.maxX),             
						     Math.max(this.maxY,    that.maxY),
				             Math.min(this.minY, that.minY));
	}
	
	public String toString() {
		return "[minX=" + minX + " maxX=" + maxX + " maxY=" + maxY + " minY=" + minY + "]";
	}
}
