
public class PopulateGridThread extends java.lang.Thread {
	private int low;
	private int high;
	private int[][] grid;
	private Lock[][] locks;
	private OverAllInput oai;
	
	
	public PopulateGridThread(int low, int high, OverAllInput oai,int[][] grid, Lock[][] locks) {
		this.low = low;
		this.high = high;
		this.grid = grid;
		this.locks = locks;
		this.oai = oai;
	}
	
	
	public void run() {
		for(int i = low; i < high; i++){
			// Get coordinates of element in CG.
			Pair<Integer,Integer> coordinates = getCensusCoordinates(oai.census[i]);
			// Then acquire lock at that grid location
			locks[coordinates.getElementA()][coordinates.getElementB()].lock();
			// Add population at coordinates
			grid[coordinates.getElementA()][coordinates.getElementB()] += oai.census[i].population;
			// release lock
			locks[coordinates.getElementA()][coordinates.getElementB()].unlock();
		}
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
