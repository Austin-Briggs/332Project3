/**
 * Austin Briggs and Nick Evans
 * CSE 332 AB
 * Project 3B
 * 
 * PopulateGridThread allows us to populate our grid of the US in parallel.
 */
public class PopulateGridThread extends java.lang.Thread {
	private int low;			//the lower bound of grid to populate
	private int high;			//the upper bound of grid to populate
	private OverAllInput oai;	//the overall input from the user - see constructor for more info
	private int[][] grid;		//the grid of the US to populate
	private Lock[][] locks;		//used to determine if elements of grid are locked
	
	/**
	 * Constructor. 
	 * @param low the lower bound of grid to populate
	 * @param high the upper bound of grid to populate
	 * @param oai the overall input from the user (x/y dimensions of the grid, census data, 
	 * 				Rectangle of the US, and dLat/dLong values 
	 * @param grid the grid of the US to populate
	 * @param locks used to determine if elements of grid are locked
	 */
	public PopulateGridThread(int low, int high, OverAllInput oai,int[][] grid, Lock[][] locks) {
		this.low = low;
		this.high = high;
		this.grid = grid;
		this.locks = locks;
		this.oai = oai;
	}
	
	/**
	 * run populates grid. It locks whatever element it is currently working on so that no
	 * other thread will interfere and cause a race condition.
	 */
	public void run() {
		for(int i = low; i < high; i++){
			// Get coordinates of element in CG.
			Pair<Integer,Integer> coordinates = getGridCoordinates(oai.census[i]);
			// Then acquire lock at that grid location
			locks[coordinates.getElementA()][coordinates.getElementB()].lock();
			// Add population at coordinates
			grid[coordinates.getElementA()][coordinates.getElementB()] += oai.census[i].population;
			// release lock
			locks[coordinates.getElementA()][coordinates.getElementB()].unlock();
		}
	}
	
	/**
	 * getGridCoordinates returns the grid coordinates of a given CensusGroup
	 * @param cen the CensusGroup to find the grid coordinates of
	 * @return a Pair<x, y> of the x and y coordinates
	 */
	public Pair<Integer,Integer> getGridCoordinates(CensusGroup cen){
		float lat = cen.latitude;
		float lon = cen.longitude;
		//if the latitude or longitude lies on the northernmost or easternmost border set the
		//xPos and/or yPos to be the easternmost and/or northernmost rectangle
		int xPos = (lon == oai.usCorners.right) ? oai.gridX - 1 : (int) ((lon - oai.usCorners.left) / oai.dLong); 
		int yPos = (lat == oai.usCorners.top) ? oai.gridY - 1 : (int) ((lat - oai.usCorners.bottom) / oai.dLat); 
		//another check on xPos and yPos to make sure they're not on the border
		return new Pair<Integer,Integer>(xPos,yPos);
	}
}
