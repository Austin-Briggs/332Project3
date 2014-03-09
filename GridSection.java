
public class GridSection {
	public int[][] grid;					//Population grid of the GridSection
	public GridCoordinates gridCoordinates;	//Coordinates of the main grid  this GridSection contains
  				//Total population that this section has 

	public GridSection(int[][] grid, GridCoordinates coordinates){
		this.grid = grid;
		this.gridCoordinates = coordinates;
	}
	
	
	/** Returns the population at the given location. Returns zero if outside of grid location.
	 * @param x 
	 * @param y 
	 * */
	public int getPopAtLocation(int x, int y){
		if(x> gridCoordinates.maxX || x < gridCoordinates.minX 
				|| y > gridCoordinates.maxY || y < gridCoordinates.minY){
			return 0;
		}
		return grid[x-gridCoordinates.minX][y - gridCoordinates.minY];
	}
}
