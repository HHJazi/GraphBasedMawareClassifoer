import java.util.HashMap;


public class Graph {
	
	//the structure for storing graph information
	public HashMap<Integer, String> titleToLabel = new HashMap<Integer, String>();
	public HashMap<Integer, Integer> NodeType = new HashMap<Integer, Integer>();
	public int numberOfNodes;
	public int numberOfEdge;
	public int numberOfDummyNodes;
	public int numberOfExternalFunctions;
	public BitMatrix AdjMatrix;
	public int clusterId;
	public boolean referenceSample; 
	public Graph()
	{
		numberOfNodes = 0;
		numberOfDummyNodes = 0;
		numberOfExternalFunctions = 0;
	}

}
