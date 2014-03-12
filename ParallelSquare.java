/**
 * Austin Briggs and Nick Evans
 * CSE 332 AB
 * Project 3B
 * 
 * ParallelSquare is a Recursive Task that calculates the boundary of the country
 * and its total population.
 */
import java.util.concurrent.RecursiveTask;

public class ParallelSquare extends RecursiveTask<Pair<Rectangle, Integer> >{
	private CensusGroup[] array;						//The CensusGroup[] that will be queried by ParallelSquare
	private int low;									// The starting index within the CensusGroup[] array
	private int high;									// The ending value of the query
	private static final int SEQUENTIAL_CUTOFF = 100;	// The max amount of elements needed before solved sequentially. 
	
	
	/** Initializes ParallelSquare
	 * @param array //The CensusGroup[] that will be queried by ParallelSquare
	 * @param low // The starting index within the CensusGroup[] array
	 * @param high // The ending value of the query
	 * */
	public ParallelSquare(CensusGroup[] array, int low, int high){
		this.low = low;
		this.high = high;
		this.array = array;
	}
	
	/**	Calculates the boundary of the country and its total population. 
	 * 	@return Returns a Pair<Rectangle, Integer> elementA representing the United States boundary and elementB representing the total population in the country.
	 * */
	@Override
	protected Pair<Rectangle,Integer> compute() {
		if(high - low <= SEQUENTIAL_CUTOFF){
			Rectangle val = new Rectangle(array[low].longitude, array[low].longitude, array[low].latitude, array[low].latitude);
			int elements = high - low;
			Integer totalPop = 0;
			for(int i = 0; i < elements; i++){
				CensusGroup current = array[low+i];
				Rectangle r = new Rectangle(current.longitude, current.longitude, current.latitude, current.latitude);
				val = val.encompass(r);
				totalPop += current.population;
			}
			return new Pair<Rectangle, Integer>(val,totalPop);
		}
		ParallelSquare left = new ParallelSquare(array, low, (low+high)/2);
		ParallelSquare right = new ParallelSquare(array, (low+high)/2, high);
		left.fork();
		Pair<Rectangle, Integer> rightResult = right.compute();
		Pair<Rectangle, Integer>  leftResult = left.join();
		Pair<Rectangle, Integer>  result = new Pair<Rectangle, Integer>(leftResult.getElementA().encompass(rightResult.getElementA()),rightResult.getElementB()+leftResult.getElementB());
		return result;
	}
	
}
