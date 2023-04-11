import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.github.sh0nk.matplotlib4j.Plot;

public class Graph {
    private Map<GraphNode, List<Edge>> adjacencyList;

    public static void main(String[] args) {
        Graph graph = Graph.importFromFile("random.dot");
        if (graph == null) {
            System.out.println("Error importing graph.");
            return;
        }
        System.out.println("Graph imported successfully.");

        graph.timeExecution();
    }

 
    public void slowSP(GraphNode g) {
        
        Set<GraphNode> unvisited = new HashSet<>(adjacencyList.keySet());

       
        Map<GraphNode, Integer> distances = new HashMap<>();
 
        for (GraphNode node : adjacencyList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
    
        distances.put(g, 0);

     
        GraphNode current = g;
        int currentDistance = 0;
        while (!unvisited.isEmpty()) {
           
            for (Edge edge : adjacencyList.get(current)) {
                GraphNode other = edge.getOtherEndpoint(current);
        
                if (unvisited.contains(other)) {
                    int weight = edge.getWeight() + currentDistance;
                    if (weight < distances.get(other)) {
                        distances.put(other, weight);
                    }
                }
            }
         
            unvisited.remove(current);

           
            currentDistance = Integer.MAX_VALUE; 
            for (GraphNode node : unvisited) {
                int distance = distances.get(node);
                if (distance < currentDistance) {
                    current = node;
                    currentDistance = distance;
                }
            }

            if (currentDistance == Integer.MAX_VALUE) {
               
                break;
            }
        }
    }


    public void fastSP(GraphNode g) {
     
        Map<GraphNode, Integer> distances = new HashMap<>();

        for (GraphNode node : adjacencyList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        distances.put(g, 0);

       
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(new Comparator<GraphNode>() {
            @Override
            public int compare(GraphNode o1, GraphNode o2) {
                return distances.get(o1) - distances.get(o2);
            }
        });
        queue.add(g); 

        while (!queue.isEmpty()) {
            GraphNode current = queue.poll(); 

            if (current == null || distances.get(current) == Integer.MAX_VALUE) {
         
                break;
            }

            
            for (Edge edge : adjacencyList.get(current)) {
                GraphNode other = edge.getOtherEndpoint(current);
                int weight = edge.getWeight() + distances.get(current);
                if (weight < distances.get(other)) {
                    distances.put(other, weight);
                    queue.add(other);
                }
            }
        }
    }

    public Map<GraphNode, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

   
    public void timeExecution() {
        Iterator<GraphNode> it = adjacencyList.keySet().iterator();
        List<Double> slowTimes = new ArrayList<>(adjacencyList.size());
        List<Double> fastTimes = new ArrayList<>(adjacencyList.size());
        while (it.hasNext()) {
            GraphNode node = it.next();

            long start = System.nanoTime();
            slowSP(node);
            long end = System.nanoTime();
            slowTimes.add((double) (end - start) / 1_000_000);

            start = System.nanoTime();
            fastSP(node);
            end = System.nanoTime();
            fastTimes.add((double) (end - start) / 1_000_000);
        }

   
        double slowAvg = 0;
        double slowMax = 0;
        double slowMin = Double.MAX_VALUE;
        double fastAvg = 0;
        double fastMax = 0;
        double fastMin = Double.MAX_VALUE;
        for (int i = 0; i < slowTimes.size(); i++) {
            slowAvg += slowTimes.get(i);
            slowMax = Math.max(slowMax, slowTimes.get(i));
            slowMin = Math.min(slowMin, slowTimes.get(i));
            fastAvg += fastTimes.get(i);
            fastMax = Math.max(fastMax, fastTimes.get(i));
            fastMin = Math.min(fastMin, fastTimes.get(i));
        }
        slowAvg /= slowTimes.size();
        fastAvg /= fastTimes.size();

        System.out.println("\nSlow implementation:");
        System.out.println("Average time: " + slowAvg + "ms");
        System.out.println("Max time: " + slowMax + "ms");
        System.out.println("Min time: " + slowMin + "ms");

        System.out.println("\nFast implementation:");
        System.out.println("Average time: " + fastAvg + "ms");
        System.out.println("Max time: " + fastMax + "ms");
        System.out.println("Min time: " + fastMin + "ms");

        plotExecutionTimes(slowTimes, fastTimes);
    }

  
    public void plotExecutionTimes(List<? extends Number> slowTimes, List<? extends Number> fastTimes) {
       
        Plot plt = Plot.create();

      
        plt.hist().add(slowTimes).add(fastTimes).bins(100).log(true);
        plt.legend();
       
        try {
            plt.savefig("plot.png");
            plt.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Graph() {
        adjacencyList = new HashMap<>();
    }

  
    public GraphNode addNode(String data) {
        GraphNode node = new GraphNode(data);

    
        if (adjacencyList.containsKey(node)) {
            return node;
        }

       
        adjacencyList.put(node, new ArrayList<>());
        return node;
    }

    public void removeNode(GraphNode node) {
        adjacencyList.remove(node); // Remove the node from the adjacency list.

        // Remove all edges that contain the node.
        for (GraphNode other : adjacencyList.keySet()) {
            List<Edge> edges = adjacencyList.get(other);
            edges.removeIf(e -> e.hasEndpoint(node));
        }
    }


    public void addEdge(GraphNode n1, GraphNode n2, int weight) {
        Edge edge = new Edge(n1, n2, weight);
        adjacencyList.get(n1).add(edge);
        adjacencyList.get(n2).add(edge);
    }

    public void removeEdge(GraphNode n1, GraphNode n2) {
        List<Edge> edges = adjacencyList.get(n1);
        edges.removeIf(e -> e.hasEndpoint(n2));

        edges = adjacencyList.get(n2);
        edges.removeIf(e -> e.hasEndpoint(n1));
    }


    public static Graph importFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("strict graph")) {
                // GraphViz files must begin with "strict graph" for undirected graphs.
                return null;
            }

            Graph graph = new Graph();
            while ((line = reader.readLine()) != null) {
                if (line.equals("}")) {
                    // End of definition.
                    return graph;
                }

                // Regex to match a line of the form "node1 -- node2 [weight = 1];"
                // or "node1 -- node2;"
                if (!line.matches("\\s*\\w+\\s+--\\s+\\w+(\\s*\\[\\s*weight\\s*=\\s*\\d+\\s*\\])?\\s*;\\s*")) {
                    // Invalid line.
                    return null;
                }

                // Split the line into tokens.
                String[] tokens = line.split("\\s+--\\s+");
                String node1Data = tokens[0].trim();
                String node2Data = tokens[1].trim().replaceAll("\\s*\\[\\s*weight\\s*=\\s*\\d+\\s*\\]\\s*", "");
                node2Data = node2Data.replaceAll(";", "");
                int weight = 1;
                if (line.contains("weight")) {
                
                    weight = Integer.parseInt(line.replaceAll(".weight\\s=\\s*(\\d+).*", "$1"));
                }

           
                GraphNode node1 = graph.addNode(node1Data);
                GraphNode node2 = graph.addNode(node2Data);
                graph.addEdge(node1, node2, weight);
            }
        } catch (IOException | NumberFormatException e) {

            return null;
        }


        return null;
    }
}

class GraphNode {
    private String data;

    public GraphNode(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GraphNode other = (GraphNode) obj;
        return data.equals(other.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}

class Edge {
    private GraphNode endpoint1;
    private GraphNode endpoint2;
    private int weight;

    public Edge(GraphNode endpoint1, GraphNode endpoint2, int weight) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
        this.weight = weight;
    }

    public GraphNode getEndpoint1() {
        return endpoint1;
    }

    public GraphNode getEndpoint2() {
        return endpoint2;
    }

    public int getWeight() {
        return weight;
    }

    public boolean hasEndpoint(GraphNode node) {
        return endpoint1.equals(node) || endpoint2.equals(node);
    }

    public GraphNode getOtherEndpoint(GraphNode node) {
        if (endpoint1.equals(node)) {
            return endpoint2;
        } else if (endpoint2.equals(node)) {
            return endpoint1;
        } else {
            return null;
        }
    }
}