import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.Toolkit;

public class RandomGraph {
	
	public static void relax(int u, int v, int[][] dist, int source) {
		if (dist[source][u] == Integer.MAX_VALUE) {
			return;
		} else if (dist[source][u] + 1 < dist[source][v]) {
			dist[source][v] = dist[source][u] + 1;
		}
	}
	
	
	public static void dijkstra(int[][] adj, int[][]dist, int n, int source) {
		boolean[] seen = new boolean[n];
		HashMap<Integer,Integer> q = new HashMap<Integer, Integer>();
		for (int i = 0; i<n; i++) {
			seen[i] = false;
			q.put(i,dist[source][i]);
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
			for(int v=0; v<n; v++) {
				if(adj[u][v]==1) {
					if(seen[v]==false) {
						relax(u,v,dist,source);
						q.put(v, dist[source][v]);
					}
				}
			}
		}
	}
	
	public static double getAveragePathLengthFromAdjacencyMatrix(int[][] adj, int n) {
		int[][] dist = new int[n][n];
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(i==j) { 
					dist[i][j] = 0;
				} else {
					dist[i][j] = Integer.MAX_VALUE;
				}
			}
		}
		for(int i=0;i<n;i++) {
			dijkstra(adj,dist,n,i);
		}
		int sum = 0;
		int pathCount = 0;
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) { 
				if(dist[i][j]<Integer.MAX_VALUE) {
					sum+=dist[i][j];
					pathCount++;
				}
			}
		}
		return (double)sum/pathCount;
	}
	
	public static double getAveragePathLength(int S, int L) {
		/*Graph randomGraph = new SingleGraph("Random Graph");
		randomGraph.setStrict(false);
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");*/
			
		/*for(int i=0;i<S;i++) { 
			randomGraph.addNode(""+i);
		}*/
		
		Random rand = new Random();
		
		int[][]adj = new int[S][S];
		
		for(int i=1;i<S;i++) {
			int first = rand.nextInt(2);
			if(first==1) {
				int j = rand.nextInt(i+1);
				while(adj[i][j]==1 || i==j) {
					j = rand.nextInt(i+1);
				}
				adj[i][j] = 1;
				//randomGraph.addEdge("" + i + "," + j, "" + i, "" + j, true);
			} else {
				int j = rand.nextInt(i+1);
				while(adj[j][i]==1 || i==j) {
					j = rand.nextInt(i+1);
				}
				adj[j][i] = 1;
				//randomGraph.addEdge("" + j + "," + i, "" + j, "" + i, true);
			}
		}
		
		for(int count=S; count<=L; count++) {
			int first = rand.nextInt(S);
			int second = rand.nextInt(S);
			while(adj[first][second]==1) {
				first = rand.nextInt(S);
				second = rand.nextInt(S);
			}
			adj[first][second] = 1;
			//randomGraph.addEdge("" + first + "," + second, "" + first, "" + second, true);
		}
		
		//System.out.println(randomGraph.getEdgeCount());
		double avg = getAveragePathLengthFromAdjacencyMatrix(adj,S);
		//System.out.println("Average Path Length: " + avg);

		//randomGraph.display();
		return avg;
	}
	
	public static double getAverageClusteringCoefficient(int S, int L) { 
		Graph randomGraph = new SingleGraph("Random Graph");
		randomGraph.setStrict(false);
			
		for(int i=0;i<S;i++) { 
			randomGraph.addNode(""+i);
		}
		
		Random rand = new Random();
		
		int[][]adj = new int[S][S];
		
		for(int i=1;i<S;i++) {
			int first = rand.nextInt(2);
			if(first==1) {
				int j = rand.nextInt(i+1);
				while(adj[i][j]==1 || i==j) {
					j = rand.nextInt(i+1);
				}
				adj[i][j] = 1;
				randomGraph.addEdge("" + i + "," + j, "" + i, "" + j, true);
			} else {
				int j = rand.nextInt(i+1);
				while(adj[j][i]==1 || i==j) {
					j = rand.nextInt(i+1);
				}
				adj[j][i] = 1;
				randomGraph.addEdge("" + j + "," + i, "" + j, "" + i, true);
			}
		}
		
		for(int count=S; count<=L; count++) {
			int first = rand.nextInt(S);
			int second = rand.nextInt(S);
			while(adj[first][second]==1) {
				first = rand.nextInt(S);
				second = rand.nextInt(S);
			}
			adj[first][second] = 1;
			randomGraph.addEdge("" + first + "," + second, "" + first, "" + second, true);
		}
		return Toolkit.averageClusteringCoefficient(randomGraph);
	}
	
	public static double getClusteringCoefficient(String csvPath, String namePath, String fileName, String metricsPath) {
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
        int n = adjacencyList.size();
        Graph graph = new SingleGraph("graph");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        for(int i=0; i<n; i++) {
        	ArrayList<Integer> species = adjacencyList.get(i);
        	for(int index : species) {
          			graph.addEdge("" + i + "," + index, "" + i, "" + index, true);
        	}
        }
        return Toolkit.averageClusteringCoefficient(graph);
	}
	
	
	
	public static void main (String[] args) {
		/*int[] S = {85, 58, 79, 87, 95, 109, 49, 58, 71, 86, 97, 107, 86, 96, 98, 78, 105, 71, 78, 78, 113, 83, 79, 92, 78, 69};
		int[] L = {227, 117, 240, 375, 565,	708, 110, 126, 148, 415, 538, 966, 353,	634, 629, 375, 343, 155, 241, 268, 832, 335, 391, 423, 181, 187};
		double sum = 0.0;
		int n = 200;
		for(int c = 0; c<26; c++) { 
			sum = 0.0;
			for(int i=0;i<n;i++) {
				sum+=getAveragePathLength(S[c],L[c]);
			}
			System.out.println("Average of " + c + " over " + n + " samples: " + sum/n);
		}
		for(int c = 0; c<26; c++) { 
			sum = 0.0;
			for(int i=0;i<n;i++) {
				sum+=getAverageClusteringCoefficient(S[c],L[c]);
			}
			System.out.println("Average of " + c + " over " + n + " samples: " + sum/n);
		}*/
		
		/*String csvPath = "C:\\\\Users\\\\aksha\\\\Desktop\\\\CSC 591 Network Science\\\\Final Project\\\\source\\";
		String namePath = "C:\\Users\\aksha\\Desktop\\CSC 591 Network Science\\Final Project\\source\\Species Names\\";
		String metricsPath = "C:\\Users\\aksha\\Desktop\\CSC 591 Network Science\\Final Project\\source\\Metrics\\";
		String[] fileNames = {"AkatoreA","AkatoreB","Berwick","Blackrock","Broad","Canton","Catlins","Coweeta1","Coweeta17"
							,"DempstersAu","DempstersSp","DempstersSu","German","Healy","Kyeburn","LilKyeburn","Martins"
							,"Narrowdale","NorthCol","Powder","Stony","SuttonAu","SuttonSp","SuttonSu","Troy","Venlaw"};
		System.out.println(fileNames.length);
		for(String file : fileNames) {
			System.out.println(file + ": " + getClusteringCoefficient(csvPath,namePath,file,metricsPath));
		}*/
		
		
	}
}
