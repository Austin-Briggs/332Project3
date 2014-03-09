
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;

	// parse the input file into a large array held in a CensusData object
	public static CensusData parse(String filename) {
		CensusData result = new CensusData();

		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(filename));
			
			// Skip the first line of the file
			// After that each line has 7 comma-separated numbers (see constants above)
			// We want to skip the first 4, the 5th is the population (an int)
			// and the 6th and 7th are latitude and longitude (floats)
			// If the population is 0, then the line has latitude and longitude of +.,-.
			// which cannot be parsed as floats, so that's a special case
			//   (we could fix this, but noisy data is a fact of life, more fun
			//    to process the real data as provided by the government)

			String oneLine = fileIn.readLine(); // skip the first line

			// read each subsequent line and add relevant data to a big array
			while ((oneLine = fileIn.readLine()) != null) {
				String[] tokens = oneLine.split(",");
				if(tokens.length != TOKENS_PER_LINE)
					throw new NumberFormatException();
				int population = Integer.parseInt(tokens[POPULATION_INDEX]);
				if(population != 0)
					result.add(population,
							Float.parseFloat(tokens[LATITUDE_INDEX]),
							Float.parseFloat(tokens[LONGITUDE_INDEX]));
			}

			fileIn.close();
		} catch(IOException ioe) {
			System.err.println("Error opening/reading/writing input or output file.");
			System.exit(1);
		} catch(NumberFormatException nfe) {
			System.err.println(nfe.toString());
			System.err.println("Error in file format");
			System.exit(1);
		}
		return result;
	}

	// argument 1: file name for input data: pass this to parse
	// argument 2: number of x-dimension buckets
	// argument 3: number of y-dimension buckets
	// argument 4: -v1, -v2, -v3, -v4, or -v5
	public static void main(String[] args) {
		// FOR YOU
		if(args.length != 4) {
			System.err.println("Usage: [filename] [num x-dimension buckets]" +
					" [num y-dimension buckets] [-v#]");
			System.exit(1);
		}

		String filename = args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		String version = args[3];
		if (version.equals("-v1")) { 		//version 1, simple and sequential
			executeVersionOne(x, y, filename);
		} else if (version.equals("-v2")) { //version 2, simple and parallel
			executeVersionTwo(x, y, filename);
		} else if (version.equals("-v3")) { //version 3, smarter and sequential
			executeVersionThree(x, y, filename);
		} else if (version.equals("-v4")) { //version 4, smarter and parallel

		} else if (version.equals("-v5")) { //version 5, smarter and lock-based

		} else { //incorrect input
			System.err.println("Incorrect version format. Must use -v1, -v2, -v3, -v4, or -v5.");
			System.exit(1);
		}
	}

	//version 1, simple and sequential
	//x = x dimension of the grid
	//y = y dimension of the grid
	//filename = name of the file to parse data from
	private static void executeVersionOne(int x, int y, String filename) {

		CensusData cData = parse(filename);
		CensusGroup[] data = cData.data;

		//Get the minimum and maximum latitudes and longitudes
		float minLon = data[0].longitude;
		float minLat = data[0].latitude;
		float maxLon = data[0].longitude;
		float maxLat = data[0].latitude;
		int totalUSPop = data[0].population;

		for (int i = 1; i < cData.data_size; i++) {
			if (data[i].latitude < minLat) minLat = data[i].latitude;
			if (data[i].longitude < minLon) minLon = data[i].longitude;
			if (data[i].latitude > maxLat) maxLat = data[i].latitude;
			if (data[i].longitude > maxLon) maxLon = data[i].longitude;

			//Also add to the total US population
			totalUSPop += data[i].population;
		}
		
		Scanner console = new Scanner(System.in);
		//Get the line for the query rectangle numbers 
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		String[] usRectLine = console.nextLine().split(" ");
		while (usRectLine.length == 4) {
			//Process the line to get the query rectangle numbers
			int west, east, south, north = 0;
			try {
				west = Integer.parseInt(usRectLine[0]);
				south = Integer.parseInt(usRectLine[1]);
				east = Integer.parseInt(usRectLine[2]);
				north = Integer.parseInt(usRectLine[3]);
			} catch (NumberFormatException e) {
				System.out.println("ERROR: You must input valid numbers.");
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				usRectLine = console.nextLine().split(" ");
				continue;
			}

			//Validate the query inputs
			try {
				if (west < 1 || west > x) 		throw new IllegalArgumentException("ERROR: west cannot be less than 1 or greater than "+x+".");
				if (south < 1 || south > y)		throw new IllegalArgumentException("ERROR: south cannot be less than 1 or greater than "+y+".");
				if (east < west || east > x) 	throw new IllegalArgumentException("ERROR: east cannot be less than west or greater than "+x+".");
				if (north < south || north > y) throw new IllegalArgumentException("ERROR: north cannot be less than south or greater than "+y+".");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				usRectLine = console.nextLine().split(" ");
				continue;
			}

			//Create the query Rectangle
			float dLong = (maxLon - minLon) / x;
			float dLat 	= (maxLat - minLat) / y;
			Rectangle qRect = new Rectangle(minLon + dLong * (west - 1),
					minLon + dLong * east,
					minLat + dLat * north,
					minLat + dLat * (south - 1));

			//Find the total population within the query rectangle and the % of totalUSPop it is
			int queryPop = 0;
			for (int i = 0; i < cData.data_size; i++) {
				CensusGroup cg = cData.data[i];
				float lat = cg.latitude;
				float lon = cg.longitude;

				//If the current CensusGroup is bounded by the query rectangle, 
				//add its population to queryPop
				if (lat >= qRect.bottom && lat <= qRect.top && lon >= qRect.left && lon <= qRect.right) {
					queryPop += cg.population;
				}
			}
			float percentTotalPop = ((float) queryPop * 100) / totalUSPop;

			//Print the results
			System.out.println("population of rectangle: "+queryPop);
			System.out.print("percent of total population: ");
			System.out.printf("%.2f", percentTotalPop);
			System.out.println();

			//Prompt again
			System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
			usRectLine = console.nextLine().split(" ");				
		}
		console.close();
		
	}

	//version 2, simple and parallel
	//x = x dimension of the grid
	//y = y dimension of the grid
	//filename = name of the file to parse data from
	private static void executeVersionTwo(int x, int y, String filename) {
		CensusData cData = parse(filename);
		CensusGroup[] data = cData.data;

		//Makes the United States Rectangle
		ForkJoinPool fjPool = new ForkJoinPool();
		ParallelSquare ps = new ParallelSquare(data,0,cData.data_size);
		Rectangle usRectangle = fjPool.invoke(ps);

		Scanner console = new Scanner(System.in);
		//Get the line for the query rectangle numbers 
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		String[] usRectLine = console.nextLine().split(" ");
		while (usRectLine.length == 4) {
			//Process the line to get the query rectangle numbers
			int west, east, south, north = 0;
			try {
				west = Integer.parseInt(usRectLine[0]);
				south = Integer.parseInt(usRectLine[1]);
				east = Integer.parseInt(usRectLine[2]);
				north = Integer.parseInt(usRectLine[3]);
			} catch (NumberFormatException e) {
				System.out.println("ERROR: You must input valid numbers.");
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				usRectLine = console.nextLine().split(" ");
				continue;
			}

			//Validate the query inputs
			try {
				if (west < 1 || west > x) 		throw new IllegalArgumentException("ERROR: west cannot be less than 1 or greater than "+x+".");
				if (south < 1 || south > y)		throw new IllegalArgumentException("ERROR: south cannot be less than 1 or greater than "+y+".");
				if (east < west || east > x) 	throw new IllegalArgumentException("ERROR: east cannot be less than west or greater than "+x+".");
				if (north < south || north > y) throw new IllegalArgumentException("ERROR: north cannot be less than south or greater than "+y+".");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				usRectLine = console.nextLine().split(" ");
				continue;
			}

			float dLong = (usRectangle.right - usRectangle.left) / x;
			float dLat 	= (usRectangle.top - usRectangle.bottom) / y;
			Rectangle qRect = new Rectangle(usRectangle.left + dLong * (west - 1),
					usRectangle.left + dLong * east,
					usRectangle.bottom + dLat * north,
					usRectangle.bottom + dLat * (south - 1));
			SimpleQuery sq = new SimpleQuery(data, 0, cData.data_size,qRect);
			Pair<Integer,Integer> qp = fjPool.invoke(sq);
			Integer queryPop = qp.getElementA();
			Integer totalUSPop = qp.getElementB();
			float percentTotalPop = ((float) queryPop * 100) / totalUSPop;

			//Print the results
			System.out.println("population of rectangle: "+queryPop);
			System.out.print("percent of total population: ");
			System.out.printf("%.2f", percentTotalPop);
			System.out.println();
			
			//Prompt again
			System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
			usRectLine = console.nextLine().split(" ");
		}
		console.close();
	}

	//version 3, smarter and sequential
	//x = x dimension of the grid
	//y = y dimension of the grid
	//filename = name of the file to parse data from
	private static void executeVersionThree(int x, int y, String filename) {
		//Parse data
		CensusData cData = parse(filename);
		CensusGroup[] data = cData.data;
		
		//Get the minimum and maximum latitudes and longitudes as above
		float minLon = data[0].longitude;
		float minLat = data[0].latitude;
		float maxLon = data[0].longitude;
		float maxLat = data[0].latitude;
		int totalUSPop = data[0].population;

		for (int i = 1; i < cData.data_size; i++) {
			if (data[i].latitude < minLat) minLat = data[i].latitude;
			if (data[i].longitude < minLon) minLon = data[i].longitude;
			if (data[i].latitude > maxLat) maxLat = data[i].latitude;
			if (data[i].longitude > maxLon) maxLon = data[i].longitude;

			//Also add to the total US population
			totalUSPop += data[i].population;
		}

		//Calculate the dLong and dLat between each x and y grid position
		float dLong = (maxLon - minLon) / x;
		float dLat 	= (maxLat - minLat) / y;
		
		//Create x*y grid where each element is the total population of the (xi, yi) grid position
		int[][] grid = new int[x][y];
		
		//Populate the grid
		for (int i = 0; i < cData.data_size; i++) {
			float lon = data[i].longitude;
			float lat = data[i].latitude;
			
			//if the latitude or longitude lies on the northernmost or easternmost border set the
			//xPos and/or yPos to be the easternmost and/or northernmost rectangle
			int xPos = (lon == maxLon) ? x - 1 : (int) ((lon - minLon) / dLong); 
			int yPos = (lat == maxLat) ? y - 1 : (int) ((lat - minLat) / dLat);
			
			grid[xPos][yPos] += data[i].population;
		}
		
		//Modify grid so that it each element now holds the total for all postitions that are
		//neither farther East nor farther South.
		for (int j = y-1; j >= 0; j--) {
			for (int i = 0; i < x; i++) {
				//get the populations for the corners (i-1, j), (i, j+1), and (i-1, j+1)
				int bottomLeft = (i == 0) ? 0 : grid[i-1][j];
				int topRight   = (j == y - 1) ? 0 : grid[i][j+1]; 
				int topLeft    = (i == 0 || j == y - 1) ? 0 : grid[i-1][j+1];
				
				//modify the population to grid[i][j]+bottomLeft+topRight-topLeft
				grid[i][j] = grid[i][j] + bottomLeft + topRight - topLeft;
			}
		}
		
		Scanner console = new Scanner(System.in);
		//Get the line for the query rectangle numbers 
		System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
		String[] usRectLine = console.nextLine().split(" ");
		while (usRectLine.length == 4) {
			//Process the line to get the query rectangle numbers
			int west, east, south, north = 0;
			try {
				west = Integer.parseInt(usRectLine[0]);
				south = Integer.parseInt(usRectLine[1]);
				east = Integer.parseInt(usRectLine[2]);
				north = Integer.parseInt(usRectLine[3]);
			} catch (NumberFormatException e) {
				System.out.println("ERROR: You must input valid numbers.");
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				usRectLine = console.nextLine().split(" ");
				continue;
			}

			//Validate the query inputs
			try {
				if (west < 1 || west > x) 		throw new IllegalArgumentException("ERROR: west cannot be less than 1 or greater than "+x+".");
				if (south < 1 || south > y)		throw new IllegalArgumentException("ERROR: south cannot be less than 1 or greater than "+y+".");
				if (east < west || east > x) 	throw new IllegalArgumentException("ERROR: east cannot be less than west or greater than "+x+".");
				if (north < south || north > y) throw new IllegalArgumentException("ERROR: north cannot be less than south or greater than "+y+".");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
				usRectLine = console.nextLine().split(" ");
				continue;
			}

			//Create the query Rectangle
			Rectangle qRect = new Rectangle(minLon + dLong * (west - 1),
					minLon + dLong * east,
					minLat + dLat * north,
					minLat + dLat * (south - 1));

			//Find the total population within the query rectangle. Use a similar formula from before:
			//bottomRight - aboveTopRight - leftBottomLeft + aboveAndLeftTopLeft
			int bottomRight = grid[east-1][south-1];
			int aboveTopRight = (north == y) ? 0 : grid[east-1][north];
			int leftBottomLeft = (west == 1) ? 0 : grid[west-2][south-1];
			int aboveAndLeftTopLeft = (north == y || west == 1) ? 0 : grid[west-2][north];
			int queryPop = bottomRight - aboveTopRight - leftBottomLeft + aboveAndLeftTopLeft;

			//% of totalUSPop query is
			float percentTotalPop = ((float) queryPop * 100) / totalUSPop;

			//Print the results
			System.out.println("population of rectangle: "+queryPop);
			System.out.print("percent of total population: ");
			System.out.printf("%.2f", percentTotalPop);
			System.out.println();
			
			//Prompt again
			System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
			usRectLine = console.nextLine().split(" ");
		}
		console.close();
	}
	
	//private helper method used to print the grid
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
