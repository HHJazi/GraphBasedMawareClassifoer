import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PreProcess {
	//read the dot files and store them with their info in the graph structure
	File file;
	public PreProcess(File child) {
		// TODO Auto-generated constructor stub
		file = child;
	}
	public Graph tokenize() {
		boolean firstEdge = true;
	    Graph callGraph = new Graph();
		try (BufferedReader reader =  new BufferedReader(new FileReader(file))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) 
		    {
		    	String [] tokens= line.split(":", 2);
		    	//processing nodes
		    	if(tokens[0].equals("node"))
		    	{
		    			callGraph.numberOfNodes ++;
		    	        String regex ="title: \"([^\"]*)\".*label: \"([^\"]*)\"";
		    	        Pattern p = Pattern.compile(regex);
		    	        Matcher m = p.matcher(tokens[1]);
		    	        if(m.find())
		    	        {
		    	            String title = m.group(1);
		    	            String label = m.group(2);
		    	            callGraph.titleToLabel.put(Integer.parseInt(title), label);
		    	            if(label.toLowerCase().contains("sub_".toLowerCase()))
		    	            {
		    	            	//zero means local function 
		    	            	callGraph.NodeType.put(Integer.parseInt(title), 0);
		    	            }
		    	            else
		    	            {
		    	            	//1 means external function 
		    	            	callGraph.NodeType.put(Integer.parseInt(title), 1);
		    	            	callGraph.numberOfExternalFunctions++;
		    	            }
		    	        }
		    		
		    	}
		    	//processing edges
		    	else if(tokens[0].equals("edge"))
		    	{
		    		if(firstEdge == true)
		    		{
		    			//callGraph.AdjMatrix = new int[callGraph.numberOfNodes][callGraph.numberOfNodes];
		    			callGraph.AdjMatrix = new BitMatrix(callGraph.numberOfNodes, callGraph.numberOfNodes);
		    			callGraph.numberOfEdge = 0;
		    			firstEdge = false;
		    		}
		    		String regex ="sourcename: \"([^\"]*)\".*targetname: \"([^\"]*)\"";
		    		Pattern p = Pattern.compile(regex);
		    		Matcher m = p.matcher(tokens[1]);
		    		if(m.find())
		    		{
		    			String sourceEdge = m.group(1);
		    			String targetEdge = m.group(2);
		    			//callGraph.AdjMatrix[Integer.parseInt(sourceEdge)][Integer.parseInt(targetEdge)] = 1;
		    			callGraph.AdjMatrix.set(Integer.parseInt(sourceEdge), Integer.parseInt(targetEdge));
		    			callGraph.numberOfEdge++;
		    		}
		    	}
		    			    		
		    	//	System.out.println(line);
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		// TODO Auto-generated method stub
		return callGraph;
	}


}
