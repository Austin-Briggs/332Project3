import java.util.concurrent.RecursiveTask;


public class ParallelSquare extends RecursiveTask<Rectangle>{
	private int low;
	private int high;
	private CensusGroup[] array;
	public static final int SEQUENTIAL_CUTOFF = 100;
	
	public ParallelSquare(CensusGroup[] array, int low, int high){
		this.low = low;
		this.high = high;
		this.array = array;
	}
	
	@Override
	protected Rectangle compute() {
		if(high - low <= SEQUENTIAL_CUTOFF){
			Rectangle val = new Rectangle(array[low].longitude, array[low].longitude, array[low].latitude, array[low].latitude);
			int elements = high - low;
			for(int i = 0; i < elements; i++){
				Rectangle r = new Rectangle(array[low+i].longitude, array[low+i].longitude, array[low+i].latitude, array[low+i].latitude);
				val = val.encompass(r);
			}
			return val;
		}
		ParallelSquare left = new ParallelSquare(array, low, (low+high)/2);
		ParallelSquare right = new ParallelSquare(array, (low+high)/2, high);
		left.fork();
		Rectangle rightResult = right.compute();
		Rectangle leftResult = left.join();
		Rectangle result = rightResult.encompass(leftResult);
		return result;
	}
	
}
