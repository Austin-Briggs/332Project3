/**@author Nickolas Evans & Austin Briggs
 * 
 * GridSection represents a grid and the area it occupies in the grid described in coordinates.
 * */
public class GridSection {
	public int[][] grid;					//Population grid of the GridSection
	public GridCoordinates gridCoordinates;	//Coordinates of the main grid  this GridSection contains

	/** Constructs GridSection
	 *  @param grid is the grid that contains 
	 *  @param coordinates is the location that this represents in the main grid. 
	 * */
	public GridSection(int[][] grid, GridCoordinates coordinates){
		this.grid = grid;
		this.gridCoordinates = coordinates;
	}
	
	
	/** Returns the population at the given location. Returns zero if outside of grid location.
	 * @param x the locations horizontal coordinate being queried
	 * @param y the locations vertical coordinate being queried
	 * */
	public int getPopAtLocation(int x, int y){
		if(x> gridCoordinates.maxX || x < gridCoordinates.minX 
				|| y > gridCoordinates.maxY || y < gridCoordinates.minY){
			return 0;
		}
		return grid[x-gridCoordinates.minX][y - gridCoordinates.minY];
	}
}
