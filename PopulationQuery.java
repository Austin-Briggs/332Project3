
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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
			System.out.print("Request a query? (y/n) ");
			while (console.hasNext() && console.nextLine().equalsIgnoreCase("y")) {
				//Get the line for the query rectangle numbers 
				System.out.print("Enter the row and column data for your subrectangle request [west south east north]: ");
				String[] usRectLine = console.nextLine().split(" ");
				if (usRectLine.length != 4) {
					System.err.println("Incorrect number of arguments.");
				} else {
					//Process the line to get the query rectangle numbers
					int west, east, south, north = 0;
					try {
						west = Integer.parseInt(usRectLine[0]);
						south = Integer.parseInt(usRectLine[1]);
						east = Integer.parseInt(usRectLine[2]);
						north = Integer.parseInt(usRectLine[3]);
					} catch (NumberFormatException e) {
						System.out.println("ERROR: You must input valid numbers.");
						System.out.print("Request another query? (y/n) ");
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
						System.out.print("Request another query? (y/n) ");
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
					System.out.println("QUERY RESULTS:");
					System.out.println("Query population = "+queryPop);
					System.out.print("Percent of US population = ");
					System.out.printf("%.2f", percentTotalPop);
					System.out.println("%");
				}
				
				System.out.print("Request another query? (y/n) ");
			}
			console.close();
			
		} else if (version.equals("-v2")) { //version 2, simple and parallel
			
		} else if (version.equals("-v3")) { //version 3, smarter and sequential
			
		} else if (version.equals("-v4")) { //version 4, smarter and parallel
			
		} else if (version.equals("-v5")) { //version 5, smarter and lock-based
			
		} else { //incorrect input
			System.err.println("Incorrect version format. Must use -v1, -v2, -v3, -v4, or -v5.");
			System.exit(1);
		}
	}
}
