/**
 * Austin Briggs and Nick Evans
 * CSE 332 AB
 * Project 3B
 * 
 * MergeGrid is a recursive action that computes the population of two grids combined
 */
import java.util.concurrent.RecursiveAction;

public class MergeGrid extends RecursiveAction{
	private GridSection left;								// left is the "left" grid that is being merged by mergeGrid
	private GridSection right;								// right is the "right" grid that is being merged by mergeGrid
	private GridCoordinates focus;							// Is the area of the masterGrid section that mergeGrid is focusing on merging.
	private GridSection masterGrid;							// masterGrid is the gridSection that is being populated by mergeGrid
	public static final int SEQUENTIAL_CUTOFF = 5000;		// The max amount of elements needed before solved sequentially.
	
	
	/** Constructs a MergeGrid
	 * @param left is the left GridSection that will provide population info for grid
	 * @param right is the right GridSection that will provide population info for grid
	 * @param focus is the area in which MergeGrid will be focusing on in grid. 
	 * 			focus.bottom lowest y, focus.up highest y, focus.left lowest x, focus.right highest x  
	 * @param grid is the matrix that will be given the population from both left.grid and right.grid
	 * */
	public MergeGrid(GridSection left, GridSection right, GridCoordinates focus ,GridSection grid){
		this.masterGrid = grid;
		this.left = left;
		this.right = right;
		this.focus = focus;
	}
	
	/** Computes if the area is to large splits into quadrants otherwise focuses on quadrant given and populates masterGrid.
	 * */
	public void compute(){
		if((focus.maxX-focus.minX) * (focus.maxY-focus.minY) <= SEQUENTIAL_CUTOFF ){
			for(int i = focus.minX; i<=focus.maxX; i++){
				for(int j = focus.minY; j <= focus.maxY; j++){
					masterGrid.grid[i-masterGrid.gridCoordinates.minX][j-masterGrid.gridCoordinates.minY] = left.getPopAtLocation(i, j)+right.getPopAtLocation(i, j);
				}
			}
			return;
		}
		
		// Can split into fourths
		int maxY = focus.maxY;
		int minumumY = focus.minY;
		int maxX = focus.maxX;
		int minumumX = focus.minX;
		int yMidPoint = (maxY+minumumY)/2;
		int xMidPoint = (minumumX+maxX)/2;
		
		//Four quadrants
		GridCoordinates UpperLeftQuadrant = new GridCoordinates(minumumX,xMidPoint,maxY,yMidPoint);
		GridCoordinates UpperRightQuadrant = new GridCoordinates(xMidPoint, maxX, maxY, yMidPoint);
		GridCoordinates LowerLeftQuadrant = new GridCoordinates(minumumX, xMidPoint, yMidPoint, minumumY);
		GridCoordinates LowerRightQuadrant = new GridCoordinates(xMidPoint, maxX, yMidPoint, minumumY);
		
		//Merge quadrants
		MergeGrid ULQMerge = new MergeGrid(this.left,this.right, UpperLeftQuadrant ,masterGrid);
		MergeGrid URQMerge = new MergeGrid(this.left,this.right, UpperRightQuadrant,masterGrid);
		MergeGrid LLQMerge = new MergeGrid(this.left,this.right, LowerLeftQuadrant ,masterGrid);
		MergeGrid LRQMerge = new MergeGrid(this.left,this.right, LowerRightQuadrant,masterGrid);
		
		//Parallel operations
		ULQMerge.fork();
		URQMerge.fork();
		LLQMerge.fork();
		LRQMerge.compute();
		ULQMerge.join();
		URQMerge.join();
		LLQMerge.join();
	}
}
