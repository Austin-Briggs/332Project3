import java.util.concurrent.RecursiveTask;


public class SimpleQuery extends RecursiveTask<Pair<Integer,Integer>>{
	private CensusGroup[] array;
	private int low;
	private int high;
	private Rectangle rect;
	private static final int SEQUENTIAL_CUTOFF = 100;
	
	@Override
	protected Pair<Integer, Integer> compute() {
		if(high - low <= SEQUENTIAL_CUTOFF){
			Integer queryPop = 0;
			Integer totalPop = 0;
			int elements = high - low;
			for(int i = 0; i < elements; i++){
				CensusGroup current = array[low+i];
				if (current.latitude >= rect.bottom && current.latitude <= rect.top && current.longitude >= rect.left && current.longitude <= rect.right) {
					queryPop += current.population;
				}else{
					//System.out.println(low+i);
				}
				totalPop += current.population;
			}
			return new Pair<Integer,Integer>(queryPop,totalPop);
		}
		// TODO Auto-generated method stub
		SimpleQuery left = new SimpleQuery(array, low, (high+low)/2, rect);
		SimpleQuery right = new SimpleQuery(array, (low+high)/2, high, rect);
		left.fork();
		Pair<Integer, Integer> rightResult = right.compute();
		Pair<Integer, Integer> leftResult = left.join();
		Integer resultA = rightResult.getElementA()+leftResult.getElementA();
		Integer resultB = rightResult.getElementB()+leftResult.getElementB();
		return new Pair<Integer,Integer>(resultA, resultB); //Need to split and create a new pair.
	}
	
	public SimpleQuery(CensusGroup[] array, int low, int high, Rectangle rect){
		this.array = array;
		this.low = low;
		this.high = high;
		this.rect = rect;
	}

}
