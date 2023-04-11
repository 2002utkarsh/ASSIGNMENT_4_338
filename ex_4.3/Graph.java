import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Graph {
    private Map<GraphNode, List<Edge>> adjacencyList;

    public static void main(String[] args) {
   
        Graph graph = Graph.importFromFile("example.dot");
        if (graph == null) {
            System.out.println("Error importing graph.");
            return;
        }

        System.out.println("Graph imported successfully.");

        graph.printGraph();

        Graph mst = mst(graph);
        System.out.println("MST:");
        mst.printGraph();
    }

  
    public static Graph mst(Graph graph) {
      
        PriorityQueue<Edge> edges = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
   
        for (GraphNode node : graph.adjacencyList.keySet()) {
            for (Edge edge : graph.adjacencyList.get(node)) {
                if (!edges.contains(edge)) { 
                    edges.add(edge);
                }
            }
        }

        
        Graph tree = new Graph();
    
        for (GraphNode node : graph.adjacencyList.keySet()) {
            tree.addNode(node.getData());
        }

        while (!edges.isEmpty()) {
            Edge edge = edges.poll();

   
            tree.addEdge(edge);

            if (tree.detectCycles()) {
                tree.removeEdge(edge);
            }
        }

        return tree;
    }

   
    private boolean detectCycles() {
        DisjointSet ds = new DisjointSet();
        ds.makeSet(adjacencyList.keySet());

        Set<Edge> seenEdges = new HashSet<>(); 

        for (GraphNode node : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(node)) {
          
                if (seenEdges.contains(edge)) {
                    continue;
                }
                seenEdges.add(edge);

                GraphNode other = edge.getOtherEndpoint(node);
                GraphNode root1 = ds.find(node);
                GraphNode root2 = ds.find(other);
                if (root1 == root2) {
                    return true;
                }
                ds.union(root1, root2);
            }
        }

        return false;
    }

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void printGraph() {
        for (GraphNode node : adjacencyList.keySet()) {
            System.out.print(node.getData() + " -> ");
            for (Edge edge : adjacencyList.get(node)) {
                GraphNode other = edge.getOtherEndpoint(node);
                System.out.print(other.getData() + " (" + edge.getWeight() + "), ");
            }
            System.out.println();
        }
        System.out.println();
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
        adjacencyList.remove(node); 

   
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

   
    public void addEdge(Edge edge) {
        adjacencyList.get(edge.getEndpoint1()).add(edge);
        adjacencyList.get(edge.getEndpoint2()).add(edge);
    }

 
    public void removeEdge(GraphNode n1, GraphNode n2) {
        List<Edge> edges = adjacencyList.get(n1);
        edges.removeIf(e -> e.hasEndpoint(n2));

        edges = adjacencyList.get(n2);
        edges.removeIf(e -> e.hasEndpoint(n1));
    }

 
    public void removeEdge(Edge edge) {
        adjacencyList.get(edge.getEndpoint1()).remove(edge);
        adjacencyList.get(edge.getEndpoint2()).remove(edge);
    }

  
    public static Graph importFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("strict graph")) {
               
                return null;
            }

            Graph graph = new Graph();
            while ((line = reader.readLine()) != null) {
                if (line.equals("}")) {
                  
                    return graph;
                }

               
                if (!line.matches("\\s*\\w+\\s+--\\s+\\w+(\\s*\\[\\s*weight\\s*=\\s*\\d+\\s*\\])?\\s*;\\s*")) {
                   
                    return null;
                }

         
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Edge other = (Edge) obj;

        boolean endpointsMatch = (endpoint1.equals(other.endpoint1) && endpoint2.equals(other.endpoint2))
                || (endpoint1.equals(other.endpoint2) && endpoint2.equals(other.endpoint1));
        boolean weightsMatch = weight == other.weight;

        return endpointsMatch && weightsMatch;
    }

    @Override
    public int hashCode() {
        return endpoint1.hashCode() + endpoint2.hashCode() + weight;
    }
}

class DisjointSet {
    private Map<GraphNode, GraphNode> parentMap = new HashMap<>();

    public void makeSet(Set<GraphNode> nodes) {
      
        for (GraphNode node : nodes) {
            parentMap.put(node, node);
        }
    }

    public GraphNode find(GraphNode node) {
       
        if (parentMap.get(node) == node) {
            return node;
        }

        return find(parentMap.get(node));
    }

    public void union(GraphNode node1, GraphNode node2) {
      
        GraphNode parent1 = find(node1);
        GraphNode parent2 = find(node2);
        parentMap.put(parent1, parent2);
    }
}