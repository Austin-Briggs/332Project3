/**
 * Austin Briggs and Nick Evans
 * CSE 332 AB
 * Project 3B
 * 
 * SimpleQuery is a recursive task that calculates population that is contained 
 * within the provided boundary.
 */
import java.util.concurrent.RecursiveTask;

public class SimpleQuery extends RecursiveTask<Integer>{
	private CensusGroup[] array;						//The CensusGroup[] that will be queried by SimpleQuery
	private int low;									// The starting index within the CensusGroup[] array
	private int high;									// The ending value of the query
	private Rectangle queryRect;						// The rectangle that the census groups will be determined if they are contained in.
	private static final int SEQUENTIAL_CUTOFF = 100;	// The max amount of elements needed before solved sequentially. 
	
	/** Returns an Integer which represents the population in the Queried Rectangle
	 * @return Returns an Integer that is the population within the queried rectangle.
	 * */
	@Override
	protected Integer compute() {
		if(high - low <= SEQUENTIAL_CUTOFF){
			Integer queryPop = 0;
			Integer totalPop = 0;
			int elements = high - low;
			for(int i = 0; i < elements; i++){
				CensusGroup current = array[low+i];
				if (current.latitude >= queryRect.bottom && current.latitude <= queryRect.top && current.longitude >= queryRect.left && current.longitude <= queryRect.right) {
					queryPop += current.population;
				}
				totalPop += current.population;
			}
			return queryPop;
		}
		//Need to split and create a new pair.
		SimpleQuery left = new SimpleQuery(array, low, (high+low)/2, queryRect);
		SimpleQuery right = new SimpleQuery(array, (low+high)/2, high, queryRect);
		left.fork();
		Integer rightResult = right.compute();
		Integer leftResult = left.join();
		Integer result = rightResult+leftResult;
		return result; 
	}
	
	/** Constructor initializes SimpleQuery
	 * @param array the CensusGroup[] that will be queried by.
	 * @param low the starting index within the CensusGroup[] array.
	 * @param high the ending value of the query.
	 * @param rect the rectangle that the census groups will be determined if they are contained in.
	 * */
	public SimpleQuery(CensusGroup[] array, int low, int high, Rectangle rect){
		this.array = array;
		this.low = low;
		this.high = high;
		this.queryRect = rect;
	}

}
