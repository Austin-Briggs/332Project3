/**@author Nickolas Evans & Austin Briggs
 * 
 * */
import java.util.concurrent.RecursiveAction;


public class MergeGrid extends RecursiveAction{
	private GridSection left;
	private GridSection right;
	private GridCoordinates focus;
	private GridSection masterGrid;
	public static final int SEQUENTIAL_CUTOFF = 100;
	
	
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
	
	
	public void compute(){
		if((focus.maxX-focus.minX) * (focus.maxY-focus.minY) <= SEQUENTIAL_CUTOFF ){
			for(int i = focus.minX; i<=focus.maxX; i++){
				for(int j = focus.minY; j <= focus.maxY; j++){
					masterGrid.grid[i-masterGrid.gridCoordinates.minX][j-masterGrid.gridCoordinates.minY] = left.getPopAtLocation(i, j)+right.getPopAtLocation(i, j);
				}
			}
			return;
		}
		/*if((int)focus.maxY-(int)focus.minY == 1){
			// focus is a straight line across a horizontal line
			// split in half
			int right = focus.maxX;
			int left = focus.minX;
			int middle = (right+left)/2;
			GridCoordinates leftRect = new GridCoordinates(left,middle,focus.maxY,focus.minY);
			GridCoordinates rightRect = new GridCoordinates(middle,right,focus.maxY,focus.minY);
			MergeGrid leftMerge = new MergeGrid(this.left,this.right,leftRect,grid);
			MergeGrid rightMerge = new MergeGrid(this.left,this.right,rightRect,grid);
			leftMerge.fork();
			rightMerge.compute();
			leftMerge.join();
		}else if((int)focus.maxX-(int)focus.minX == 1){
			// focus is a straight line across a vertical line
			// split in half
			int top = focus.maxY;
			int bottom = focus.minY;
			int middle = (top+bottom)/2;
			GridCoordinates topRect = new GridCoordinates(focus.minX,focus.maxX,top,middle);
			GridCoordinates bottomRect = new GridCoordinates(focus.minX,focus.maxX,middle,bottom);
			MergeGrid topMerge = new MergeGrid(this.left,this.right,topRect,grid);
			MergeGrid bottomMerge = new MergeGrid(this.left,this.right,bottomRect,grid);
			topMerge.fork();
			bottomMerge.compute();
			topMerge.join();
		}else{ */
			// Can split into fourths
			int maxY = focus.maxY;
			int minumumY = focus.minY;
			int maxX = focus.maxX;
			int minumumX = focus.minX;
			int yMidPoint = (maxY+minumumY)/2;
			int xMidPoint = (minumumX+maxX)/2;
			GridCoordinates UpperLeftQuadrant = new GridCoordinates(minumumX,xMidPoint,maxY,yMidPoint);
			GridCoordinates UpperRightQuadrant = new GridCoordinates(xMidPoint, maxX, maxY, yMidPoint);
			GridCoordinates LowerLeftQuadrant = new GridCoordinates(minumumX, xMidPoint, yMidPoint, minumumY);
			GridCoordinates LowerRightQuadrant = new GridCoordinates(xMidPoint, maxX, yMidPoint, minumumY);
			//System.out.println("ULQ"+UpperLeftQuadrant);
			//System.out.println("URQ"+UpperRightQuadrant);
			//System.out.println("LLQ"+LowerLeftQuadrant);
			//System.out.println("LRQ"+LowerRightQuadrant);
			MergeGrid ULQMerge = new MergeGrid(this.left,this.right, UpperLeftQuadrant ,masterGrid);
			MergeGrid URQMerge = new MergeGrid(this.left,this.right, UpperRightQuadrant,masterGrid);
			MergeGrid LLQMerge = new MergeGrid(this.left,this.right, LowerLeftQuadrant ,masterGrid);
			MergeGrid LRQMerge = new MergeGrid(this.left,this.right, LowerRightQuadrant,masterGrid);
			ULQMerge.fork();
			URQMerge.fork();
			LLQMerge.fork();
			LRQMerge.compute();
			ULQMerge.join();
			URQMerge.join();
			LLQMerge.join();
		//}
	}
}
