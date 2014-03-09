/**@author Nickolas Evans & Austin Briggs
 * 
 * */
public class OverAllInput {
	public int gridX;				// The width of the grid ie max x coordinate
	public int gridY;				// The height of the grid ie max y coordinate
	public Rectangle usCorners;		// 
	public CensusGroup[] census;	// Census array
	public float dLong;
	public float dLat;
	
	public OverAllInput(int horizontal, int vertical, Rectangle corners, CensusGroup[] census){
		this.gridX 	= horizontal;
		this.gridY 	= vertical;
		this.usCorners = corners;
		this.census = census;
		this.dLong 	= (corners.right - corners.left) / horizontal;
		this.dLat 	= (corners.top - corners.bottom) / vertical;
	}
}
