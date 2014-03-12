/**
 * Austin Briggs and Nick Evans
 * CSE 332 AB
 * Project 3B
 * 
 * ParallelGridSection is a recursiveTask that:
 * Goes through and divides the CensusGroup array into smaller parts.
 * Then makes a grid based on the area of the CensusGroup array that it is looking at
 * Then uses merge grid to add the subdivided grids together.
 */
import java.util.concurrent.RecursiveTask;

public class ParallelGridSection extends RecursiveTask<GridSection>{
	private OverAllInput oai;							// Holding class containing (X and Y values of grid, US Corners, census data, etc.)
	private int low;									// The starting index within OverAllInput's CensusGroup array
	private int high;									// The ending value of the query
	private static final int SEQUENTIAL_CUTOFF = 100;	// The max amount of elements needed before solved sequentially.  
	
	/** Constructs (Name of class)
	 * @param oai is a holder class containing (X and Y values of grid, US Corners, census data, etc.)
	 * @param low The starting index within OverAllInput's CensusGroup array
	 * @param high The ending value of the query
	 * */
	public ParallelGridSection(OverAllInput oai, int low, int high){
		this.oai = oai;
		this.low = low;
		this.high = high;
	}
	
	/** Goes through and divides the CensusGroup array into smaller parts.
	 * Then makes a grid based on the area of the CensusGroup array that it is looking at
	 * Then uses merge grid to add the subdivided grids together.
	 * @return GridSection that represents the GridSection of the array contained within the census low and high.
	 * */
	@Override
	protected GridSection compute() {
		if(high - low <= SEQUENTIAL_CUTOFF){
			// Get locations of each element of array uses oai.gridX and oai.gridY oai.usCorners has the lat and long
			// oai.census is the census group array
			Pair<Integer,Integer> primeCoordinates = getCensusCoordinates(oai.census[low]);
			
			GridCoordinates gridArea = new GridCoordinates(primeCoordinates.getElementA(),primeCoordinates.getElementA(),primeCoordinates.getElementB(),primeCoordinates.getElementB()); //Location algorithm. 
			for(int i = low+1; i < high; i++){
				Pair<Integer,Integer> tempCoordinates = getCensusCoordinates(oai.census[i]);
				GridCoordinates tempGrid = new GridCoordinates(tempCoordinates.getElementA(),tempCoordinates.getElementA(),tempCoordinates.getElementB(),tempCoordinates.getElementB()); //Location algorithm.  
				//Possibly make an array of pairs that holds coordinates of each CG so I just need to calculate gridlocation once for each CG.
				gridArea = gridArea.encompass(tempGrid);
			}
			int[][] masterGrid = new int[gridArea.maxX-gridArea.minX+1][gridArea.maxY-gridArea.minY+1]; 

			for(int i = low; i < high; i++){
				// populate grid in here
				Pair<Integer,Integer> tempCoordinates = getCensusCoordinates(oai.census[i]);
				masterGrid[tempCoordinates.getElementA()-gridArea.minX][tempCoordinates.getElementB()-gridArea.minY] += oai.census[i].population;
			}
			return new GridSection(masterGrid, gridArea);
		}
		//Splits 
		ParallelGridSection left = new ParallelGridSection(oai, low, (low+high)/2);
		ParallelGridSection right = new ParallelGridSection(oai, (low+high)/2, high);
		left.fork();
		GridSection right_result = right.compute();
		GridSection left_result = left.join();
		GridCoordinates newCoordinates = right_result.gridCoordinates.encompass(left_result.gridCoordinates);
		int[][] grid = new int[newCoordinates.maxX-newCoordinates.minX+1][newCoordinates.maxY-newCoordinates.minY+1];
		GridSection masterGrid = new GridSection(grid,newCoordinates);
		MergeGrid mg = new MergeGrid(left_result,right_result,newCoordinates,masterGrid);
		mg.compute();
		return masterGrid;
	}
	
	/** Calculates the x y coordinate of the passed in CensusGroup.
	 * @return Pair<Integer,Integer> where elementA represents the x coordinate and elementB represents the y coordinate.
	 * */
	public Pair<Integer,Integer> getCensusCoordinates(CensusGroup cen){
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
