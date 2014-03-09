import java.util.concurrent.ForkJoinPool;


public class TestMergeGrid {
	public static void main(String[] args){
		ForkJoinPool fjPool = new ForkJoinPool();
		//overlap(fjPool);
		//noOverLap(fjPool);
		partialOverLap(fjPool);
	}
	
	public static void overlap(ForkJoinPool fjPool){
		int[][] grid1 = new int[4][4];
		for(int i = 0; i < grid1.length; i++){
			for(int j = 0; j < grid1[1].length; j++){
				grid1[i][j] = j;
			}
		}
		GridCoordinates gc1 = new GridCoordinates(0,3,3,0);
		GridSection g1 = new GridSection(grid1,gc1);
		int[][] testGrid = new int[4][4];
		GridSection g3 = new GridSection(testGrid,gc1);
		MergeGrid mg = new MergeGrid(g1,g1,gc1,g3);
		fjPool.invoke(mg);
		for(int i = 0; i < testGrid.length; i++){
			for(int j = 0; j < testGrid[1].length; j++){
				System.out.print(testGrid[i][j] +"\t");
			}
			System.out.println();
		}
	}
	
	public static void noOverLap(ForkJoinPool fjPool){
		int[][] grid1 = {{1,2},{2,3}};
		int[][] grid2 = {{4,3,2,1},{4,3,2,1}};
		GridCoordinates gc1 = new GridCoordinates(0,1,1,0);
		GridCoordinates gc2 = new GridCoordinates(2,3,3,0);
		GridCoordinates gc3 = gc1.encompass(gc2);
		int[][] testGrid = new int[4][4];
		GridSection g1 = new GridSection(grid1,gc1);
		GridSection g2 = new GridSection(grid2,gc2);
		GridSection g3 = new GridSection(testGrid,gc3);
		MergeGrid mg = new MergeGrid(g1,g2,gc3,g3);
		fjPool.invoke(mg);
		for(int i = 0; i < testGrid.length; i++){
			for(int j = 0; j < testGrid[1].length; j++){
				System.out.print(testGrid[i][j] +"\t");
			}
			System.out.println();
		}
	}
	
	public static void partialOverLap(ForkJoinPool fjPool){
		int grid1Y = 40;
		int grid1X = 40;
		int grid2Y = 40;
		int grid2X = 40;
		int[][] grid1 = new int[grid1X][grid1Y];
		int[][] grid2 = new int[grid2X][grid2Y];
		for(int i = 0; i < grid1.length; i++){
			for(int j = 0; j < grid1[1].length; j++){
				grid1[i][j] = j;
			}
		}
		
		for(int i = 0; i < grid2.length; i++){
			for(int j = 0; j < grid2[1].length; j++){
				grid2[i][j] = j;
			}
		}
		GridCoordinates gc1 = new GridCoordinates(0,grid1X-1,grid1Y-1,0);
		GridCoordinates gc2 = new GridCoordinates(0,grid2X-1,grid2Y-1,0);
		GridCoordinates gc3 = gc1.encompass(gc2);
		int[][] testGrid = new int[gc3.maxX+1][gc3.maxY+1];
		GridSection g1 = new GridSection(grid1,gc1);
		GridSection g2 = new GridSection(grid2, gc2);
		GridSection g3 = new GridSection(testGrid,gc3);
		MergeGrid mg = new MergeGrid(g1,g2,gc3,g3);
		fjPool.invoke(mg);
		//printGrid(39,39,grid1);
		System.out.println();
		//System.out.print("3 maxX"+ gc3.maxX);
		printGrid(39,39, testGrid);
	}
	private static void printGrid(int x, int y, int[][] grid) {
		//Test print of grid
		for (int i = y-1; i >= 0; i--) {
			for (int j = 0; j < x; j++) {
				System.out.print(grid[j][i] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}
}
