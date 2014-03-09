/**@author Nickolas Evans & Austin Briggs
 * 
 * */
import java.util.concurrent.RecursiveTask;

/**
 * Goes through and divides the CensusGroup array into smaller parts.
 * Then makes a grid based on the area of the CensusGroup array that it is looking at
 * Then uses merge grid to add the subdivided grids together.
 * 
 * */
public class Version4Part1 extends RecursiveTask<GridSection>{
	public OverAllInput oai;
	public int low;
	public int high;
	private static final int SEQUENTIAL_CUTOFF = 100;
	
	public Version4Part1(OverAllInput oai, int low, int high){
		this.oai = oai;
		this.low = low;
		this.high = high;
	}
	
	@Override
	protected GridSection compute() {
		if(high - low <= SEQUENTIAL_CUTOFF){
			// Get locations of each element of array uses oai.gridX and oai.gridY oai.usCorners has the lat and long
			// oai.census is the census group array
			//Pair<Integer,Integer>[] coordinates = (Pair<Integer, Integer>[]) new Object[high-low];
			Pair<Integer,Integer> primeCoordinates = getCensusCoordinates(oai.census[low]);
			
			GridCoordinates gridArea = new GridCoordinates(primeCoordinates.getElementA(),primeCoordinates.getElementA(),primeCoordinates.getElementB(),primeCoordinates.getElementB()); //Location algorithm. 
			for(int i = low+1; i < high; i++){
				Pair<Integer,Integer> tempCoordinates = getCensusCoordinates(oai.census[i]);
				GridCoordinates tempGrid = new GridCoordinates(tempCoordinates.getElementA(),tempCoordinates.getElementA(),tempCoordinates.getElementB(),tempCoordinates.getElementB()); //Location algorithm.  
				//Possibly make an array of pairs that holds coordinates of each CG so I just need to calculate gridlocation once for each CG.
				
				gridArea = gridArea.encompass(tempGrid);
			}
			int[][] masterGrid = new int[gridArea.maxX-gridArea.minX+1][gridArea.maxY-gridArea.minY+1]; 

			int totalPopulation = 0;
			for(int i = low; i < high; i++){
				// populate grid in here
				Pair<Integer,Integer> tempCoordinates = getCensusCoordinates(oai.census[i]);
				masterGrid[tempCoordinates.getElementA()-gridArea.minX][tempCoordinates.getElementB()-gridArea.minY] += oai.census[i].population;
				// Also add population to
				totalPopulation += oai.census[i].population;
			}
			return new GridSection(masterGrid, gridArea);
		}
		/*if(high - low == 1){ // Adjust this part to have a SCONE.
			int[][] grid = new int[1][1];
			int population = oai.census[low].population;
			grid[0][0] = population;
			GridCoordinates plot =  new GridCoordinates(1,1,1,1);	//Get location in matrix then place here
			return new GridSection(grid, plot, population);
		}*/
		Version4Part1 left = new Version4Part1(oai, low, (low+high)/2);
		Version4Part1 right = new Version4Part1(oai, (low+high)/2, high);
		left.fork();
		GridSection right_result = right.compute();
		GridSection left_result = left.join();
		GridCoordinates newCoordinates = right_result.gridCoordinates.encompass(left_result.gridCoordinates);
		int[][] grid = new int[newCoordinates.maxX-newCoordinates.minX+1][newCoordinates.maxY-newCoordinates.minY+1];
		GridSection masterGrid = new GridSection(grid,newCoordinates);
		MergeGrid mg = new MergeGrid(left_result,right_result,newCoordinates,masterGrid);
		mg.compute();
		//int totalPopulation = right_result.population+left_result.population;
		return masterGrid;
	}
	
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
