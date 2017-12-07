import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Visualize {
	public static void displayGraph(String csvPath, String namePath, String fileName) {
		String csvFile = csvPath + fileName + ".csv";
		String nameFile = namePath + fileName + ".txt";
        String line = "";
        String cvsSplitBy = ",";

        Graph graph = new SingleGraph(fileName);

       //Read and store species names from this food web
        ArrayList<String> speciesNames = new ArrayList<String> ();
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
        	while((line = br.readLine()) != null) {
        		speciesNames.add(line);
        		graph.addNode(line);
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
       
        //List of species that eat this species
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	int rowIndex = 0;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] splits = line.split(cvsSplitBy);
                ArrayList<Integer> species = new ArrayList<Integer>();
                int index = 0;
                for(int colIndex = 0; colIndex<splits.length; colIndex++) {
                	if(splits[colIndex].equals("1")) {
                		species.add(index);
                		graph.addEdge(speciesNames.get(rowIndex) + "->" + speciesNames.get(colIndex), speciesNames.get(rowIndex), speciesNames.get(colIndex),true);
                	}
                	index++;
                }
                adjacencyList.add(species);
                rowIndex++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        graph.display();
        for(Node n : graph) {
        	System.out.println(n.getId() + ":" + n.getEdgeSet());
        }
	}
	
	public static void main(String[] args) {
		/*System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		String csvPath = "C:\\\\Users\\\\aksha\\\\Desktop\\\\CSC 591 Network Science\\\\Final Project\\\\source\\";
		String namePath = "C:\\Users\\aksha\\Desktop\\CSC 591 Network Science\\Final Project\\source\\Species Names\\";
		String fileName = "Sample";
		
		
		Graph graph = new SingleGraph("Random");
		Generator gen = new RandomGenerator(2);
		gen.addSink(graph);
		gen.begin();
		for(int i=0;i<10;i++) {
			gen.nextEvents();
		}
		gen.end();
		graph.display();*/
	}
}
