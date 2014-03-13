import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


public class TestInputSimple {
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
		System.out.println("Test Input Simple"); 
		PrintStream dummyStream    = new PrintStream(dummyOutput);
		//InputStream exitStream = new InputStream();
		System.setOut(dummyStream);
		String[] versions= {"-v1","-v3"};
		double[] runtimes = new double[2];
		int i = 0;
		for(String version: versions){
			String[] arr = {"CenPop2010.txt", "100", "500", version};
			runtimes[i] = getAverageRuntime(arr);
			i++;
		}
		System.setOut(prime);
		System.out.println("v1: " + runtimes[0]);
		System.out.println("v3: " + runtimes[1]);
	}
	

	private static double getAverageRuntime(String[] args) {
		double totalTime = 0;

		for(int i=0; i<NUM_TEST; i++) {
			//ByteArrayInputStream in = new ByteArrayInputStream("exit".getBytes());
			try {
				System.setIn(new FileInputStream("inputTest.txt"));
			} catch (FileNotFoundException e) {
			
				e.printStackTrace();
			}
			//System.setIn(in);
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
