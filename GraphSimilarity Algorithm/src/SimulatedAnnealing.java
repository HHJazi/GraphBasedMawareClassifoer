import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class SimulatedAnnealing {
	
	private Graph graphH, graphG;
	private int threshold = 100;
	double beta, beta_0 =4.0, beta_final = 10.00, cooler_threshold = 0.9, lambda_i, lambda_i1, delta;
	int m_iterator = 10;
	public HashMap<Integer, Integer> bijectiveFunction_i = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_i1 = new HashMap<Integer, Integer>();

	//modified algorithm
	public HashMap<Integer, Integer> bijectiveFunction_k1 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_k2 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_k3 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n1k1 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n2k1 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n3k1 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n1k2 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n2k2 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n3k2 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n1k3 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n2k3 = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bijectiveFunction_n3k3 = new HashMap<Integer, Integer>();
	double lambda_k1, lambda_k2, lambda_k3, lambda_n1k1, lambda_n2k1, lambda_n3k1, lambda_n1k2, lambda_n2k2,
	lambda_n3k2, lambda_n1k3, lambda_n2k3, lambda_n3k3;

	public SimulatedAnnealing(Graph G, Graph H) {
		// TODO Auto-generated constructor stub
		graphH= H;
		graphG= G;
	}
	//calculating simulated annealing for two graph structure
	/*
		Algorithm 1 Simulated Annealing for computing GED
		Require: Graphs G, H, Annealed parameter values β0,β f inal , Cooling rate ε, 
		Iterations per relaxation m, λφ(G, H) = VertexCost + EdgeCost + RelabelCost
		start:
		φi = random_φ()
		β = β0
		while β<β_final do
		    for m iterations do
		        φi+1 = neighbor_solution(φi)
		        ∆(λφi , λφi+1 ) = λφi+1 − λφi
		        if ∆(λφi , λφi+1 ) < 0 then
		            φi+1 = φi    
		        else with Pr(e−β∆(λφi,λφi+1))
		            φi+1 = φi
		    end if
		    if (minφ λφ == λφi ) OR no_progress() then return best φ
		    end if
		    end for
		    β = β/ε
		end while
	 */
	public double algorithom() {
		// TODO Auto-generated method stub
		Graph graphGPrime, graphHPrime;
		int costBeforeNeighboring=0, costAfterNeighboring =0;
		if(graphG.numberOfNodes < graphH.numberOfNodes)
		{
			graphGPrime = madeSameSizeGraph(graphG, graphH.numberOfNodes);
			graphHPrime = graphH;
		}
		else if(graphG.numberOfNodes > graphH.numberOfNodes)
		{
			graphHPrime = madeSameSizeGraph(graphH, graphG.numberOfNodes);
			graphGPrime = graphG;
		}
			
		else
		{
			graphGPrime = graphG;
			graphHPrime = graphH;
		}
			
		bijectiveFunction_i = generateRandomBijectiveFunction(graphGPrime, graphHPrime);
		beta = beta_0;
		while(beta < beta_final)
		{
			for(int i=0; i< m_iterator; i++)
			{
				Random r = new Random();
				int low = 0;
				int high = graphGPrime.numberOfNodes;
				int rand1 = r.nextInt(high-low) + low;
				int rand2 = r.nextInt(high - low) + low;
				bijectiveFunction_i1 = findNeighborSolution(bijectiveFunction_i, rand1, rand2);
				lambda_i = costFunction(bijectiveFunction_i, graphGPrime, graphHPrime);
				costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_i, graphGPrime, graphHPrime, rand1, rand2);
				costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_i1, graphGPrime, graphHPrime, rand1, rand2);
				
				lambda_i1 = lambda_i - (costBeforeNeighboring - costAfterNeighboring);
				delta = lambda_i - lambda_i1;
				if(delta < 0)
				{
					bijectiveFunction_i1 = new HashMap<Integer, Integer>(bijectiveFunction_i);
				}
				else
				{
					Random random = new Random();
					double rand = random.nextDouble();
					if(rand < Math.exp(-(beta*delta)))
					{
						bijectiveFunction_i1 = new HashMap<Integer, Integer>(bijectiveFunction_i);
					}	
				}	
				if(lowerBound(graphGPrime, graphHPrime) == lambda_i || no_progress(lambda_i, lambda_i1))
					return lambda_i;
			}
			beta = beta / cooler_threshold;
		}
		return lambda_i;
	}
	//calculate updating cost after swaping two nodes
	private int calculateUpdatedCost(
			HashMap<Integer, Integer> bijectiveFunction_i2, Graph graphG, Graph graphH, int rand1, int rand2) 
	{
		int edgeCost = 0;
		int relabelingCost = 0;
		//edge cost for two node
		edgeCost = edgeCostForNode(bijectiveFunction_i2, graphG, graphH, rand1) +
		edgeCostForNode(bijectiveFunction_i2, graphG, graphH, rand2);
		
		relabelingCost = relabelingCostForNode(bijectiveFunction_i2,graphG, graphH, rand1, rand2);
		
		return edgeCost + relabelingCost;
	}
	
	//calculate relabeling cost for two selected nodes
	private int relabelingCostForNode(
			HashMap<Integer, Integer> bijectiveFunction_i2, Graph graphG2,
			Graph graphH2, int rand1, int rand2) {
		int relabelingCost =0;
		int value1 = bijectiveFunction_i2.get(rand1);
		int value2 = bijectiveFunction_i2.get(rand2);

		try
		{
			if (graphG2.NodeType.get(rand1) != graphH2.NodeType.get(value1))
					relabelingCost++;
			if (graphG2.NodeType.get(rand2) != graphH2.NodeType.get(value2))
					relabelingCost++;			
		}
		catch(Exception E)
		{
			
		}
		
		return relabelingCost;
	}
	
	//calculate edge cost of a node
	private int edgeCostForNode(HashMap<Integer, Integer> bijectiveFunction_i2,
			Graph graphG2, Graph graphH2, int rand1) {
		// TODO Auto-generated method stub
		int cost =0;
		if(graphG2.numberOfEdge > 0)
		{
			for(int i = 0; i < graphG2.AdjMatrix.colSize(); i++)
			{
				if(graphG2.AdjMatrix.get(rand1,i))
				{
					try
					{
						int bijective_rand1 = bijectiveFunction_i2.get(rand1);
						int bijective_i = bijectiveFunction_i2.get(i);
						//if(graphH.AdjMatrix.get(bijective_rand1, bijective_i != 1)
						if(!graphH2.AdjMatrix.get(bijective_rand1, bijective_i))
							cost++;
					}
					catch(Exception e)
					{
						cost++;
					}
				}
				//else if(graphG.AdjMatrix[rand1][i] == 0)
				else if(! graphG2.AdjMatrix.get(rand1,i))
				{
					try
					{
						int bijective_rand1 = bijectiveFunction_i2.get(rand1);
						int bijective_i = bijectiveFunction_i2.get(i);
						//if(graphH.AdjMatrix[bijective_rand1][bijective_i] != 0)
						if(graphH2.AdjMatrix.get(bijective_rand1,bijective_i))
							cost++;
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		if(graphG2.numberOfEdge > 0)
		{
			for(int i = 0; i < graphG2.AdjMatrix.colSize(); i++)
			{
				if(graphG2.AdjMatrix.get(i, rand1))
				{
					try
					{
						int bijective_rand1 = bijectiveFunction_i2.get(rand1);
						int bijective_i = bijectiveFunction_i2.get(i);
						if(!graphH2.AdjMatrix.get(bijective_i, bijective_rand1))
							cost++;
					}
					catch(Exception e)
					{
						cost++;
					}
				}
				else if(!graphG2.AdjMatrix.get(i, rand1))
				{
					try
					{
						int bijective_rand1 = bijectiveFunction_i2.get(rand1);
						int bijective_i = bijectiveFunction_i2.get(i);
						if(graphH2.AdjMatrix.get(bijective_i, bijective_rand1))
							cost++;
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		return cost;
	}
	
	//made a same size graph
	private Graph madeSameSizeGraph(Graph graphG2, int numberOfNodes) {
		
		Graph newGraph = new Graph();
		int dummyNodesNumber = numberOfNodes - graphG2.numberOfNodes;
		newGraph.numberOfDummyNodes = dummyNodesNumber;
		newGraph.numberOfNodes = numberOfNodes;
		newGraph.numberOfExternalFunctions = graphG2.numberOfExternalFunctions;
		newGraph.numberOfEdge = graphG2.numberOfEdge;
		newGraph.NodeType = new HashMap<Integer, Integer>(graphG2.NodeType);
		newGraph.AdjMatrix = new BitMatrix(numberOfNodes, numberOfNodes);
		for(int i =0 ; i< numberOfNodes; i++)
		{
			if(i < graphG2.numberOfNodes)
			{
				newGraph.titleToLabel.put(i, graphG2.titleToLabel.get(i));
			}
			else
				newGraph.titleToLabel.put(i, "dummyNode");
		}
		if(graphG2.numberOfEdge > 0)
		{
			for(int i= 0; i< numberOfNodes; i++)
			{
				for(int j= 0; j< numberOfNodes; j++)
				{
					if(i < graphG2.numberOfNodes && j < graphG2.numberOfNodes)
						if(graphG2.AdjMatrix.get(i,j))
						{
						   newGraph.AdjMatrix.set(i,j);
						}
						
						//newGraph.AdjMatrix[i][j]= graphG2.AdjMatrix[i][j];
					else
						newGraph.AdjMatrix.clear(i, j);
				}
			}
		}
		// TODO Auto-generated method stub
		return newGraph;
	}
	//generate random bijective function
	public HashMap<Integer, Integer> generateRandomBijectiveFunction(Graph graphG, Graph graphH)
	{
 		HashMap<Integer, Integer> bijective = new HashMap<Integer, Integer>();
		//for(int i= 0 ; i < graphG.numberOfNodes; i++)
 		int i =0;
		while(bijective.size() < graphG.numberOfNodes)
		{
				Random r = new Random();
				int Low = 0;
				int High = graphG.numberOfNodes;
				int rand = r.nextInt(High-Low) + Low;
				if(bijective.containsValue(rand))
					continue;
				else
				{
					bijective.put(i, rand);
					i++;
				}
		}
		

		return bijective;
	}
	
	public HashMap<Integer, Integer> findNeighborSolution(HashMap<Integer, Integer> bijectiveFunction_i, int rand1, int rand2)
	{ 
		HashMap<Integer, Integer> bijective = new HashMap<Integer, Integer>();
		bijective = new HashMap<Integer, Integer>(bijectiveFunction_i);
		int temp1 = bijective.get(rand1);
		int temp2 = bijective.get(rand2);
		bijective.put(rand2, temp1);
		bijective.put(rand1, temp2);
		return bijective;
	}
	
	public double costFunction(HashMap<Integer, Integer> bijectiveFunction_i, Graph graphG, Graph graphH)
	{
		return vertexCost(graphG, graphH)+ relabelingCost(bijectiveFunction_i, graphG, graphH) 
				+  edgeCost(bijectiveFunction_i, graphG, graphH);
	}
	private int relabelingCost(HashMap<Integer, Integer> bijectiveFunction_i2,
			Graph graphG, Graph graphH) {
		// TODO Auto-generated method stub
		int relabelCost =0;
		for(int i=0; i< graphG.numberOfNodes; i++)
			{
				int graphHvalue = bijectiveFunction_i2.get(i);
				try
				{
					if (graphG.NodeType.get(i) != graphH.NodeType.get(graphHvalue))
							relabelCost++;
				}
				catch(Exception E)
				{
					
				}
			}
		return relabelCost;
	}
	
	private int edgeCost(HashMap<Integer, Integer> bijectiveFunction_i2,
			Graph graphG, Graph graphH) {
		// TODO Auto-generated method stub
		int edgeCost = 0;
		if(graphG.numberOfEdge <= 0 && graphH.numberOfEdge <=0)
			return edgeCost;
		if(graphG.numberOfEdge <= 0)
		{
			edgeCost = graphH.numberOfEdge;
			return edgeCost;
		}
		if(graphH.numberOfEdge <= 0)
		{
			edgeCost = graphG.numberOfEdge;
			return edgeCost;
		}
		for(int  i= 0; i< graphG.AdjMatrix.size(); i++)
			{
				for(int j= 0; j< graphG.AdjMatrix.colSize(); j++ )
				{
					if(i != j)
					{
						if(graphG.AdjMatrix.get(i,j))
						{
								int bijective_i = bijectiveFunction_i2.get(i);
								int bijective_j = bijectiveFunction_i2.get(j);
								if(graphH.numberOfEdge <= 0)
								{
									edgeCost++;
								}
								else if(!graphH.AdjMatrix.get(bijective_i, bijective_j))
									edgeCost++;
						}
						else if(!graphG.AdjMatrix.get(i,j))
						{
								int bijective_i = bijectiveFunction_i2.get(i);
								int bijective_j = bijectiveFunction_i2.get(j);
								if(graphH.numberOfEdge <= 0)
								{
									continue;
								}
								else if(graphH.AdjMatrix.get(bijective_i, bijective_j))
									edgeCost++;
						}
					}
				}
			}
		return edgeCost;
	}
	
	private int vertexCost(Graph graphG, Graph graphH) {
		// TODO Auto-generated method stub
		int vertexCost = 0;
		vertexCost = Math.abs((graphG.numberOfNodes - graphG.numberOfDummyNodes) - (graphH.numberOfNodes - graphH.numberOfDummyNodes));
		return vertexCost;
	}
	public double lowerBound(Graph graphGPrime, Graph graphHPrime)
	{
		int sizeG = graphGPrime.numberOfNodes - graphGPrime.numberOfDummyNodes;
		int sizeH = graphHPrime.numberOfNodes - graphHPrime.numberOfDummyNodes;
		int MaxExternalFunctions = Math.max(graphGPrime.numberOfExternalFunctions, graphHPrime.numberOfExternalFunctions);
		int meetOfTwoGraphs = meetOfTwoGraphs(graphGPrime, graphHPrime);
		int sigma = calculateSigma(graphGPrime, graphHPrime);
		return (Math.abs(sizeG - sizeH) + 
				Math.max(0, (MaxExternalFunctions - meetOfTwoGraphs - vertexCost(graphGPrime, graphHPrime))) + sigma)
				/ (sizeG + sizeH + graphGPrime.numberOfEdge + graphHPrime.numberOfEdge);
	}
	private int calculateSigma(Graph graphGPrime, Graph graphHPrime) {
		// TODO Auto-generated method stub
		int sum =0;
		ArrayList<Integer> gOutDegreeList = new ArrayList<Integer>();
		ArrayList<Integer> hOutDegreeList = new ArrayList<Integer>();
		int temp =0;
		if(graphGPrime.numberOfEdge > 0)
		{
			for(int i= 0 ; i < graphGPrime.AdjMatrix.size(); i++)
			{
				for(int j = 0; j< graphGPrime.AdjMatrix.colSize(); j++)
				{
					if(graphGPrime.numberOfEdge <= 0)
						temp = 0;
					else if(graphGPrime.AdjMatrix.get(i,j))
						temp++;		
				}
				gOutDegreeList.add(temp);
			}
		}
		else
		{
			if(graphHPrime.numberOfEdge > 0)
			{
				for(int k= 0; k < graphHPrime.AdjMatrix.size(); k++)
				{
					gOutDegreeList.add(0);
				}
			}
		}
		if(graphHPrime.numberOfEdge > 0)
		{
			for(int i= 0 ; i < graphHPrime.AdjMatrix.size(); i++)
			{
				for(int j = 0; j< graphHPrime.AdjMatrix.colSize(); j++)
				{
					if(graphHPrime.numberOfEdge <= 0)
						temp =0;
					else if(graphHPrime.AdjMatrix.get(i,j))
						temp++;		
				}
				hOutDegreeList.add(temp);
			}
		}
		else
		{
			if(graphGPrime.numberOfEdge > 0)
			{
				for(int k= 0; k < graphGPrime.AdjMatrix.size(); k++)
				{
					hOutDegreeList.add(0);
				}
			}
		}
		Collections.sort(gOutDegreeList);
		Collections.sort(hOutDegreeList);
		for(int i= 0; i< graphGPrime.numberOfNodes; i++)
		{
			try
			{
			 sum+= Math.abs(gOutDegreeList.get(i) - hOutDegreeList.get(i));
			}
			catch(Exception e)
			{
				sum+= 0;
			}
		}
		
		return sum;
	}
	private int meetOfTwoGraphs(Graph graphGPrime, Graph graphHPrime) {
		// TODO Auto-generated method stub
		int meetNumbers = 0;
		Iterator it = graphGPrime.NodeType.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        if(graphHPrime.NodeType.containsValue(pair.getValue()))
		        	meetNumbers++;
		        it.remove(); // avoids a ConcurrentModificationException

		}
		return meetNumbers;
	}
	public boolean no_progress(double lambda_i2, double lambda_i12)
	{
		if(Math.abs(lambda_i12 -lambda_i2) >= threshold)
			return false;
		else
			return true; 
	}

	public double modifiedAlgorithom() {
		// TODO Auto-generated method stub
		int k = 3;
		Graph graphGPrime, graphHPrime;
		int costBeforeNeighboring=0, costAfterNeighboring =0;
		if(graphG.numberOfNodes < graphH.numberOfNodes)
		{
			graphGPrime = madeSameSizeGraph(graphG, graphH.numberOfNodes);
			graphHPrime = graphH;
		}
		else if(graphG.numberOfNodes > graphH.numberOfNodes)
		{
			graphHPrime = madeSameSizeGraph(graphH, graphG.numberOfNodes);
			graphGPrime = graphG;
		}
			
		else
		{
			graphGPrime = graphG;
			graphHPrime = graphH;
		}
			
		bijectiveFunction_k1 = generateRandomBijectiveFunction(graphGPrime, graphHPrime);
		lambda_k1 = costFunction(bijectiveFunction_k1, graphGPrime, graphHPrime);

		bijectiveFunction_k2 = generateRandomBijectiveFunction(graphGPrime, graphHPrime);
		lambda_k2 = costFunction(bijectiveFunction_k2, graphGPrime, graphHPrime);
		
		bijectiveFunction_k3 = generateRandomBijectiveFunction(graphGPrime, graphHPrime);
		lambda_k3 = costFunction(bijectiveFunction_k3, graphGPrime, graphHPrime);
		//negibor 1 for k1
		Random r = new Random();
		int low = 0;
		int high = graphGPrime.numberOfNodes;
		int rand1 = r.nextInt(high-low) + low;
		int rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n1k1 = findNeighborSolution(bijectiveFunction_k1, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k1, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n1k1, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n1k1 = lambda_k1 - (costBeforeNeighboring - costAfterNeighboring);
		//delta = lambda_i - lambda_i1;
		
		//neighbor 2 for k1
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n2k1 = findNeighborSolution(bijectiveFunction_k1, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k1, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n2k1, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n2k1 = lambda_k1 - (costBeforeNeighboring - costAfterNeighboring);
		//delta = lambda_i - lambda_i1;
		
		
		//neighbor 3 for k1
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n3k1 = findNeighborSolution(bijectiveFunction_k1, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k1, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n3k1, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n3k1 = lambda_k1 - (costBeforeNeighboring - costAfterNeighboring);
		
		//neighbor 1 for k2
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n1k2 = findNeighborSolution(bijectiveFunction_k2, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k2, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n1k2, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n1k2 = lambda_k2 - (costBeforeNeighboring - costAfterNeighboring);
		
		
		//neighbor 2 for k2
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n2k2 = findNeighborSolution(bijectiveFunction_k2, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k2, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n2k2, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n2k2 = lambda_k2 - (costBeforeNeighboring - costAfterNeighboring);
		
		//neighbor 3 for k2
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n3k2 = findNeighborSolution(bijectiveFunction_k2, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k2, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n3k2, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n3k2 = lambda_k2 - (costBeforeNeighboring - costAfterNeighboring);
		
		//neighbor 1 for k3
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n1k3 = findNeighborSolution(bijectiveFunction_k3, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k3, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n1k3, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n1k3 = lambda_k3 - (costBeforeNeighboring - costAfterNeighboring);
		//neighbor 2 for k3
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n2k3 = findNeighborSolution(bijectiveFunction_k3, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k3, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n2k3, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n2k3 = lambda_k3 - (costBeforeNeighboring - costAfterNeighboring);
		
		//neighbor 3 for k3
		r = new Random();
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		bijectiveFunction_n3k3 = findNeighborSolution(bijectiveFunction_k3, rand1, rand2);
		costBeforeNeighboring = calculateUpdatedCost(bijectiveFunction_k3, graphGPrime, graphHPrime, rand1, rand2);
		costAfterNeighboring = calculateUpdatedCost(bijectiveFunction_n3k3, graphGPrime, graphHPrime, rand1, rand2);
		
		lambda_n3k3 = lambda_k3 - (costBeforeNeighboring - costAfterNeighboring);

		////
		double lambdaArr []= {0,0,0,0,0,0,0,0,0};

		lambdaArr[0] = lambda_n1k1;
		lambdaArr[1] = lambda_n2k1;
		lambdaArr[2] = lambda_n3k1;
		lambdaArr[3] = lambda_n1k2;
		lambdaArr[4] = lambda_n2k2;
		lambdaArr[5] = lambda_n3k2;
		lambdaArr[6] = lambda_n1k3;
		lambdaArr[7] = lambda_n2k3;
		lambdaArr[8] = lambda_n3k3;
		
		Arrays.sort(lambdaArr);
		
		double lambdaNew[]= {0,0,0,0,0,0};
		lambdaNew[0] = lambdaArr[0];
		lambdaNew[1] = lambdaArr[1];
		lambdaNew[2] = lambdaArr[2];

		r = new Random();
		low = 3;
		high = 8;
		rand1 = r.nextInt(high-low) + low;
		rand2 = r.nextInt(high - low) + low;
		int rand3 = r.nextInt(high - low) + low;
		lambdaNew[3] = lambdaArr[rand1];
		lambdaNew[4] = lambdaArr[rand2];
		lambdaNew[5] = lambdaArr[rand3];


		
		for( int i =0; i < 6; i ++)
		{
				if(lowerBound(graphGPrime, graphHPrime) <= lambdaNew[i])
					return lambdaNew[i];
				if(lowerBound(graphGPrime, graphHPrime) == lambda_k1 || no_progress(lambda_k1, lambdaNew[i]) )
					return lambda_k1;
				if(lowerBound(graphGPrime, graphHPrime) == lambda_k2 || no_progress(lambda_k2, lambdaNew[i]) )
					return lambda_k2;
				if(lowerBound(graphGPrime, graphHPrime) == lambda_k3 || no_progress(lambda_k3, lambdaNew[i]) )
					return lambda_k3;
		}
				
		return lambdaArr[0];
	}

}
