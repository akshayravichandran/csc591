import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class BetweennessCentrality {
	

	public static void relax(int u, int v, int[][] distance, ArrayList<ArrayList<ArrayList<Integer>>> predecessor, int source) {
		
		ArrayList<ArrayList<Integer>> sourcePredecessor = predecessor.get(source);
		if (distance[source][u] == Integer.MAX_VALUE) {
			return;
		}
		if (distance[source][u] + 1 < distance[source][v]) {
			distance[source][v] = distance[source][u] + 1;
			ArrayList<Integer> vSourcePredecessor = sourcePredecessor.get(v);
			vSourcePredecessor.clear();
			vSourcePredecessor.add(u);
		} else if (distance[source][u] + 1 == distance[source][v]) {
			ArrayList<Integer> vSourcePredecessor = sourcePredecessor.get(v);
			vSourcePredecessor.add(u);
		}
	}
	
	public static void dijkstra(ArrayList<ArrayList<Integer>> adjacencyList, int source, int[][] distance
			, ArrayList<ArrayList<ArrayList<Integer>>> predecessor) {
		HashMap<Integer,Integer> q = new HashMap<Integer, Integer>();
		boolean[] seen = new boolean[adjacencyList.size()];
		for (int i = 0; i<adjacencyList.size(); i++) {
			seen[i] = false;
			q.put(i,distance[source][i]);
		}
		while(q.isEmpty()==false) {
			int min = Integer.MAX_VALUE;
			int u = -1;
			for(int key : q.keySet()) {
				int value = q.get(key);
				if (value<=min) {
					min = value;
					u = key;
				}
			}
			q.remove(u);
			seen[u] = true;
			ArrayList<Integer> uAdjacencyList = adjacencyList.get(u);
			for (int v : uAdjacencyList) {
				if(seen[v]==false) {
					relax(u,v,distance,predecessor,source);
					q.put(v, distance[source][v]);
				}
			}
		}
	}
	
	public static int computeTotalShortestPaths(int totalShortestPaths, int s, int t, ArrayList<ArrayList<Integer>> sPredecessor) {
		ArrayList<Integer> stPredecessor = sPredecessor.get(t);
		if (stPredecessor.isEmpty()) {
			totalShortestPaths++;
		} else {
			for (int v : stPredecessor) {
				totalShortestPaths = computeTotalShortestPaths(totalShortestPaths, s, v, sPredecessor);
			}
		}
		return totalShortestPaths;
	}
	
	public static int computeCount(int count, boolean containsI, int i, int s, int t, ArrayList<ArrayList<Integer>> sPredecessor) {
		ArrayList<Integer> stPredecessor = sPredecessor.get(t);
		if (stPredecessor.isEmpty() && containsI) {
			count++;
		} else {
			for (int v : stPredecessor) {
				if(v==i || containsI==true) {
					count = computeCount(count, true, i, s, v, sPredecessor);
				} else {
					count = computeCount(count, false, i, s, v, sPredecessor);
				}
			}
		}
		return count;
	}
	
	public static double[] betweennessCentrality(ArrayList<ArrayList<Integer>> adjacencyList) {
		double[] betweennessCentrality = new double[adjacencyList.size()];
		int[][] distance = new int[adjacencyList.size()][adjacencyList.size()];
		ArrayList<ArrayList<ArrayList<Integer>>> predecessor = new ArrayList<ArrayList<ArrayList<Integer>>> ();
		for (int i=0; i<adjacencyList.size(); i++) {
			ArrayList<ArrayList<Integer>> iPredecessor = new ArrayList<ArrayList<Integer>> ();
			for (int j=0; j<adjacencyList.size(); j++) {
				if (i==j) {
					distance[i][j] = 0;
				} else {
					distance[i][j] = Integer.MAX_VALUE;
				}
				ArrayList<Integer> ijPredecessor = new ArrayList<Integer> ();
				iPredecessor.add(ijPredecessor);
			}
			predecessor.add(iPredecessor);
			dijkstra(adjacencyList, i, distance, predecessor);
		}
		/*System.out.println("Shortest Path Lengths: ");
		for(int i = 0; i<adjacencyList.size(); i++) {
			System.out.print("\n Source " + i + ": ");
			for(int j=0; j<adjacencyList.size(); j++) {
				System.out.print(distance[i][j] + ",");
			}
		}
		System.out.println("\n Predecessors: " + predecessor);*/
		for(int i=0; i<adjacencyList.size(); i++) {
			betweennessCentrality[i] = 0.0;
			for(int s=0; s<adjacencyList.size(); s++) {
				for(int t=0; t<adjacencyList.size(); t++) {
					if(s!=t && s!=i && t!=i) {
						if(distance[s][t]<Integer.MAX_VALUE) {	//there exists a path
							ArrayList<ArrayList<Integer>> sPredecessor = predecessor.get(s);
							int totalShortestPaths = computeTotalShortestPaths(0,s,t,sPredecessor);
							int count = computeCount(0,false,i,s,t,sPredecessor);
							betweennessCentrality[i]+= (double)count/totalShortestPaths;
						}
					}
				}
			}
		}
		
		int sum = 0;
		int pathCount = 0;
		for(int i=0;i<adjacencyList.size();i++) {
			for(int j=0;j<adjacencyList.size();j++) { 
				if(distance[i][j]<Integer.MAX_VALUE && i!=j) {
					sum+=distance[i][j];
					pathCount++;
				}
			}
		}
		System.out.println("Average Path Length: " + (double)sum/pathCount);
		
		double max = 0.0;
		int count = 0;
		for(int i=0; i<adjacencyList.size(); i++) {
			if (betweennessCentrality[i]>max) {
				max = betweennessCentrality[i];
			}
		}
		for(int i=0; i<adjacencyList.size(); i++) {
			betweennessCentrality[i]/=max;
			//System.out.print("\n Betweenness " + i + ": " + betweennessCentrality[i]);
		}
		return betweennessCentrality;
	}
	
	public static String getCol(int row) {
		String col;
		if(row<=26) {
			col = "" + (char)(65+row-1);
		} else {
			col = "" + (char)(65+row%26-1);
			row = row/26;
			col = (char)(65+row%26-1) + col;
		}
		return col;
	}
	
	
	public static void computeMetrics(String csvPath, String namePath, String fileName, String metricsPath) {
		String csvFile = csvPath + fileName + ".csv";
		String nameFile = namePath + fileName + ".txt";
		String metricsFile = metricsPath + fileName + ".txt";
        String line = "";
        String cvsSplitBy = ",";
        
        
       //Read and store species names from this food web
        ArrayList<String> speciesNames = new ArrayList<String> ();
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
        	while((line = br.readLine()) != null) {
        		speciesNames.add(line);
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
       
        //List of species that eat this species
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] splits = line.split(cvsSplitBy);
                ArrayList<Integer> species = new ArrayList<Integer>();
                int index = 0;
                for(int colIndex = 0; colIndex<splits.length; colIndex++) {
                	if(splits[colIndex].equals("1")) {
                		species.add(index);
                	}
                	index++;
                }
                adjacencyList.add(species);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Compute betweenness centrality metrics for all species
        double[] betweennessCentrality = betweennessCentrality(adjacencyList);
        
        //Store speciesName:betweenCentrality for all species
        double max = 0.0;
        int maxIndex = -1;
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(metricsFile))) {
        	for (int i=0; i<adjacencyList.size(); i++) {
        		line = speciesNames.get(i) + ":" + betweennessCentrality[i]+":"+i+"\n";
        		if(betweennessCentrality[i]>max) {
        			max = betweennessCentrality[i];
        			maxIndex = i;
        		}
        		bw.write(line);
        	}
        	int row = maxIndex + 1;
        	String col = getCol(row);
        	System.out.println(fileName + ": " + speciesNames.get(maxIndex) + ": " + row + "," + col);
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	
	public static void main(String[] args) {
		
		/*String csvPath = "C:\\\\Users\\\\aksha\\\\Desktop\\\\CSC 591 Network Science\\\\Final Project\\\\source\\";
		String namePath = "C:\\Users\\aksha\\Desktop\\CSC 591 Network Science\\Final Project\\source\\Species Names\\";
		String metricsPath = "C:\\Users\\aksha\\Desktop\\CSC 591 Network Science\\Final Project\\source\\Metrics\\";
		String[] fileNames = {"AkatoreA","AkatoreB","Berwick","Blackrock","Broad","Canton","Catlins","Coweeta1","Coweeta17"
							,"DempstersAu","DempstersSp","DempstersSu","German","Healy","Kyeburn","LilKyeburn","Martins"
							,"Narrowdale","NorthCol","Powder","Stony","SuttonAu","SuttonSp","SuttonSu","Troy","Venlaw"};
		System.out.println(fileNames.length);
		for(String file : fileNames) {
			computeMetrics(csvPath,namePath,file,metricsPath);
		}*/
		
	}
}
