import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class handler {
	private static int nodeLimit = 20000;
	//read a directoy of graphs
	//pre process them and store in a graph structure
	//calling simulated annealing for two storing graphs
	public static void main(String [ ] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter distanceWriter = new PrintWriter("Dynamic_distance_matrix.txt", "UTF-8");
		PrintWriter MapSamples = new PrintWriter("MapSamples.txt", "UTF-8");
		PrintWriter timeWriter = new PrintWriter("timesCalculation.txt", "UTF-8");
		PrintWriter statsWriter = new PrintWriter("stats.txt", "UTF-8");

		int directorySize= 0;
		int count =0;	
		//String directoryPath = "/home/hossein/Documents/Hossein-Thesis/static_gdl_graph";
		String directoryPath = "/home/hossein/Documents/Hossein-Thesis/Final-Data/StaticGraph/days/day1";
		//String directoryPath = "/home/hossein/Documents/Hossein-Thesis/test";
		File dir = new File(directoryPath);
		File[] directoryListing = dir.listFiles();
		directorySize = directoryListing.length;
		String[] sampleNameList = new String[directorySize];
		double[][] distanceMatrixe = new double[directorySize][directorySize];
		Graph [] callGraphList = new Graph[directorySize];
		if (directoryListing != null) 
		try
		{
			for (File child : directoryListing) 
			{
				PreProcess preProcess = new PreProcess(child);
				callGraphList[count] =preProcess.tokenize();
				String extensionRemoved = child.getName().toString().split("\\.")[0];
				String temp = extensionRemoved.substring(3);
				sampleNameList[count] = extensionRemoved;
				System.out.println(extensionRemoved);
				statsWriter.print(extensionRemoved);
				MapSamples.print(extensionRemoved);
				statsWriter.print("\t");
				MapSamples.print("\t");
				MapSamples.println(String.valueOf(count));			
				statsWriter.print(callGraphList[count].numberOfNodes);
				statsWriter.print("\t");
				statsWriter.println(callGraphList[count].numberOfEdge);
				count++;
		      // Do something with child
			}
			MapSamples.close();
			statsWriter.close();
			System.out.println("End of processing of the first part stats and map samples");
			//System.exit(0);
		} 
		catch(Exception e) 
		{
			System.err.println("ERROR: CANNOT READ FILES");
		 }
		
		//calling simulated annealing
		for (int i = 0; i < callGraphList.length; i++) 
		{
		 System.out.println("Starting point");
		 long millis = System.currentTimeMillis() % 1000;
         System.out.println("The time is:" + millis);
         long startTime = System.currentTimeMillis();
         System.out.println("Start Time is" +startTime);	
         long endTime, totalTime; 

		    for (int k = 0; k < callGraphList.length; k++) 
		    {
		        if (i != k) 
		        {
		        	if(callGraphList[i].numberOfNodes <= nodeLimit || callGraphList[k].numberOfNodes <= nodeLimit)
		        	{
			            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(callGraphList[i], callGraphList[k]);
			           
			            System.out.println("number of edges" + i + ":" + callGraphList[i].numberOfEdge);
			            System.out.println("number of edges" + k + ":" + callGraphList[k].numberOfEdge);
			           // distanceMatrixe[i][k]= simulatedAnnealing.algorithom();	
			            distanceMatrixe[i][k]= simulatedAnnealing.modifiedAlgorithom();	
			            System.out.print(callGraphList[i].numberOfNodes);
			            System.out.print("\t");
			            System.out.print(callGraphList[i].numberOfEdge);
			            System.out.println();
			            
			            System.out.print(callGraphList[k].numberOfNodes);
			            System.out.print("\t");
			            System.out.print(callGraphList[k].numberOfEdge);
			            System.out.println();
			            endTime   = System.currentTimeMillis();
			            totalTime = endTime - startTime;
			            System.out.println("Time is: " +totalTime);	
			            timeWriter.print(totalTime);
			            timeWriter.print("\t");
			            startTime = endTime;
			            distanceWriter.print(String.valueOf(distanceMatrixe[i][k]));
			            distanceWriter.print("\t");
			            System.out.println( String.valueOf(i) + "\t" + String.valueOf(k));
		        	}
		        	else
		        	{
		        		System.out.println("The size of graphs are too large");
		        		System.exit(0);
		        	}
		            
		        }
		        else
		        {
		        	distanceMatrixe[i][k] =0;
		            distanceWriter.print(String.valueOf(distanceMatrixe[i][k]));
		            distanceWriter.print("\t");
		            timeWriter.print("0");
		            timeWriter.print("\t");
		        	
		        }
		    }
		    distanceWriter.println();
		    timeWriter.println();
		}
		distanceWriter.close();
		timeWriter.close();
		System.out.println("End of the processing graphs");
		System.exit(0);
		
	}

}
