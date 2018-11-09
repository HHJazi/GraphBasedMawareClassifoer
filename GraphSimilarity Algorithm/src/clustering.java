import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clustering {

	public static void main(String [ ] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		int pruningThereshold = 150;
		int directorySize= 0;
		int count =0;	
		String directoryPath = "/home/hossein/Documents/Hossein-Thesis/static_gdl_graph";
		File dir = new File(directoryPath);
		File[] directoryListing = dir.listFiles();
		directorySize = directoryListing.length;
		HashMap<Integer, String> names = new HashMap<Integer, String>();
		Graph [] callGraphList = new Graph[directorySize];
//		Vector<Vector<Integer>> clusterMembers = new Vector<Vector<Integer>>();


		//process the labels

		//end of label processing
		int graphOrder = 0;
		try
		{
			for (File child : directoryListing) 
			{
				PreProcess preProcess = new PreProcess(child);
				callGraphList[count] =preProcess.tokenize();
				String name = child.getName();
				String extensionRemoved = name.split("\\.")[0];
				names.put(count, extensionRemoved);
				count++;
			}
		} 
		catch(Exception e) 
		{
			System.err.println("ERROR: CANNOT READ FILES");
		 }
		
		for(int l = 0; l < callGraphList.length; l++)
		{
			callGraphList[l].clusterId = -1;
			
		}
		//calling clustering
		for (int i = 0; i < callGraphList.length; i++) 
		{
			//// Get candidate clusters Cq and their reference samples rCi Get Cq from DB.
			Vector<Integer> candidateClusters = new Vector<>();
			Vector<Integer> prunedCandidateClusters = new Vector<>();
			Vector<Integer> suitableClusters = new Vector<>();

			graphOrder = callGraphList[i].numberOfNodes;
				//first time you need to get candidate clusters
			for(int k = 0; k < callGraphList.length; k++)
				{
					if( k != i )
					{
						if(callGraphList[k].numberOfNodes == graphOrder)
						{
							if(callGraphList[k].clusterId == -1)
							{
								callGraphList[k].clusterId = k;
								callGraphList[k].referenceSample = true;
							}
							if(callGraphList[k].referenceSample == true)
								candidateClusters.add(k);
						}
					}
				}

			if(candidateClusters.size() == 0)
			{
				//return new cluster q
				callGraphList[i].clusterId = i;
				callGraphList[i].referenceSample = true;
				continue;
			}
			//prune using GEDLowerBound
			for(int j =0; j < candidateClusters.size(); j ++)
			{
	            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(callGraphList[i], callGraphList[j]);
	            double distance = simulatedAnnealing.lowerBound(callGraphList[i], callGraphList[j]);
	            if(distance < pruningThereshold)
	            {
	            	prunedCandidateClusters.add(candidateClusters.get(j));
	            }
			}
			
			if(prunedCandidateClusters.size() == 0)
			{
				callGraphList[i].clusterId = i;
				callGraphList[i].referenceSample = true;
				continue;
			}
			//find suitable clusters
			for(int k= 0 ; k < prunedCandidateClusters.size(); k++)
			{
	            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(callGraphList[i], callGraphList[k]);
	            if( simulatedAnnealing.algorithom() < pruningThereshold )
	            {
	            	suitableClusters.add(prunedCandidateClusters.get(k));
	            }
			}
			if(suitableClusters.size() == 0)
			{
				callGraphList[i].clusterId = i;
				callGraphList[i].referenceSample = true;
				continue;

			}
			else if(suitableClusters.size() == 1)
			{
				//add to existing cluster
				callGraphList[i].clusterId = suitableClusters.get(0);
				
			}
			else if(suitableClusters.size() > 1 )
			{
				//merge clusters
				int defaultId = suitableClusters.get(0);
				callGraphList[i].clusterId = defaultId;
				for(int j = 1; j < suitableClusters.size(); j++)
				{
					callGraphList[suitableClusters.get(j)].clusterId = defaultId; 
					callGraphList[suitableClusters.get(j)].referenceSample= false;	
				}
			}
		}
		evaluate(callGraphList, names);
		System.exit(0);
	}
	public static void evaluate(Graph [] callGraphList, HashMap<Integer, String> names)
	{
		HashMap<Integer, Integer> clusters = new HashMap<Integer, Integer>();
		HashMap<String, String> realLabels = new HashMap<String, String>();
		HashMap<String, String> labels = new HashMap<String, String>();

		String labelDirectory = "/home/hossein/Documents/Hossein-Thesis/staticLabels/labelMapMicrosoft";

		try (BufferedReader reader =  new BufferedReader(new FileReader(labelDirectory))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) 
		    {
		    	String [] tokens= line.split("\t");
		    	String [] temps = tokens[1].split("/");
		    	if(temps.length ==1)
		    		realLabels.put(tokens[0], temps[0]);
		    	else if(temps[1].contains("."))
		    	{
		    		String [] temp2 = temps[1].split("\\.");
		    		realLabels.put(tokens[0], temp2[0]);
		    	}
		    	else
		    		realLabels.put(tokens[0], temps[1]);
		    	//processing nodes
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
		for(int i=0; i < callGraphList.length; i++)
		{
			labels.put(names.get(i), realLabels.get(names.get(callGraphList[i].clusterId)));
		}
		double tp = 0.0;
		double fp = 0.0;
		double fn = 0.0;
		double P, R, F;

	    Iterator it = labels.entrySet().iterator();
	    while (it.hasNext())
	    {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(realLabels.get(pair.getKey()) == pair.getValue())
	        	tp++;
	        else
	        	fp++;
	    }
	    System.out.println("true positive " + (tp/labels.size())*100 );
	    System.out.println("false positive " + (fp/labels.size())*100 );

	    R = (tp/labels.size());
	    int max = 0;
	    for(int i = 0; i < callGraphList.length; i++)
	    {
	    	try
	    	{
	    		int temp = clusters.get(callGraphList[i].clusterId);
	    		clusters.put(callGraphList[i].clusterId, temp++);
	    	}
	    	catch(Exception e)
	    	{
	    		clusters.put(callGraphList[i].clusterId, 1);
	    	}
	    }
	    Map.Entry<Integer, Integer> maxEntry = null;

	    for (Map.Entry<Integer, Integer> entry : clusters.entrySet())
	    {
	        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
	        {
	            maxEntry = entry;
	        }
	    }
	    P = ((double) maxEntry.getValue()) / labels.size();
	    System.out.println("precision is: " + P * 100);
	    System.out.println("recall is: " + R * 100);
	    System.out.println("F-measure is: " + ((2*P*R)/(P+R))*100);
	    System.out.println("number of clusters is : "+ clusters.size());
	    
	}
	
		
}
