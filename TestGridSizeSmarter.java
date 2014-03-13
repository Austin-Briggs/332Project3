import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


public class TestGridSizeSmarter {
	
	private static final int NUM_TEST = 100;
	private static final int NUM_WARMUP = 5;
	
	public static void main(String[] args) {
		PrintStream prime = System.out;
		OutputStream dummyOutput = new OutputStream(){
			@Override
			public void write(int b) throws IOException {
				// DoesNothing
			}
		};
		//
		System.out.println("Test Grid Size");
		PrintStream dummyStream    = new PrintStream(dummyOutput);
		//InputStream exitStream = new InputStream();
		System.setOut(dummyStream);
		String[] versions= {"-v3","-v4"};
		String[][] xYSizes = {{"10","20"},{"40","80"},{"100","40"},{"75","400"},{"50","250"},{"46","74"},
							{"100","500"},{"200","500"},{"400","750"},{"100","50"}};
		double[][] runtimes = new double[2][10];
		int runCounter = 0;
		for(String version: versions){
			for(int j = 0; j < 4;){
				String[] arr = {"CenPop2010.txt", xYSizes[j][0], xYSizes[j][1], version};
				runtimes[runCounter][j] = getAverageRuntime(arr);
				
			}
			runCounter++;
		}
		
		System.setOut(prime);
		System.out.println("Version \t X \t Y \t Time");
		for(int i = 0; i < versions.length; i++){	
			for(int j = 0; j < 4; j++){
				System.out.println(versions[i]+"\t"+xYSizes[j][0]+"\t"+xYSizes[j][1]+"\t"+runtimes[i][j]);
			}
		}
	}
	

	private static double getAverageRuntime(String[] args) {
		double totalTime = 0;
		/*try {
			System.setIn(new FileInputStream("exit.txt"));
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		}*/
		byte[] exit = "exit".getBytes(); 
		for(int i=0; i<NUM_TEST; i++) {
			ByteArrayInputStream in = new ByteArrayInputStream(exit);
			System.setIn(in);
			long startTime = System.currentTimeMillis();
		    PopulationQuery.main(args);
		    long endTime = System.currentTimeMillis();
		    if(NUM_WARMUP <= i) {                    // Throw away first NUM_WARMUP runs to encounter JVM warmup
		    	totalTime += (endTime - startTime);
		    }
		}
		return totalTime / (NUM_TEST-NUM_WARMUP);  // Return average runtime.
	}
}
