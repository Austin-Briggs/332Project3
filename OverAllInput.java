/**@author Nickolas Evans & Austin Briggs
 * 
 * OverAllInput
 * Is a holder class that contains relevant data for calculations that doesn't change with queries or parallelism
 * 
 * */
public class OverAllInput {
	public int gridX;				// The width of the grid ie max x coordinate
	public int gridY;				// The height of the grid ie max y coordinate
	public Rectangle usCorners;		// The locations of the four corners of the united states
	public CensusGroup[] census;	// Census array
	public float dLong;				// The change in longitude for every x.
	public float dLat;				// The change in latitude for every y.
	
	
	/**	Constructs OverAllInput object
	 * @param horizontal is the number of columns
	 * @param vertical is the number of rows
	 * @param corners is the locations of the four corners of the united states
	 * @param census is the CensusGroup[] that represents the united states
	 * */
	public OverAllInput(int horizontal, int vertical, Rectangle corners, CensusGroup[] census){
		this.gridX 	= horizontal;
		this.gridY 	= vertical;
		this.usCorners = corners;
		this.census = census;
		this.dLong 	= (corners.right - corners.left) / horizontal;
		this.dLat 	= (corners.top - corners.bottom) / vertical;
	}
}
