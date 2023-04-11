
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Graph {
    private Map<GraphNode, List<Edge>> adjacencyList;

    public Graph() {
        adjacencyList = new LinkedHashMap<>();
    }

    public GraphNode createNode(String data) {
        GraphNode node = new GraphNode(data);
        if (adjacencyList.containsKey(node)) {
            return node;
        }
        adjacencyList.put(node, new ArrayList<>());
        return node;
    }

    public void deleteNode(GraphNode node) {
        if (!adjacencyList.containsKey(node)) {
            return;
        }
        adjacencyList.remove(node);
        for (GraphNode other : adjacencyList.keySet()) {
            List<Edge> edges = adjacencyList.get(other);
            edges.removeIf(e -> e.isConnectedTo(node));
        }
    }

    public void insertEdge(GraphNode n1, GraphNode n2, int weight) {
        Edge newEdge = new Edge(n1, n2, weight);
        List<Edge> edges1 = adjacencyList.get(n1);
        for (Edge edge : edges1) {
            if (edge.isConnectedTo(n2)) {
                edge.setWeight(weight);
                return;
            }
        }
        edges1.add(newEdge);
        adjacencyList.get(n2).add(newEdge);
    }

    public void deleteEdge(GraphNode n1, GraphNode n2) {
        if (!adjacencyList.containsKey(n1) || !adjacencyList.containsKey(n2)) {
            System.out.println("At least one of the nodes is not present in the graph.");
            return;
        }

        List<Edge> edges = adjacencyList.get(n1);
        edges.removeIf(e -> e.isConnectedTo(n2));

        edges = adjacencyList.get(n2);
        edges.removeIf(e -> e.isConnectedTo(n1));
    }

    public static Graph loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("strict graph")) {
                return null;
            }

            Graph graph = new Graph();
            String regex = "\\s*(\\w+)\\s+--\\s+(\\w+)(\\s*\\[\\s*weight\\s*=\\s*(\\d+)\\s*\\])?\\s*;\\s*";
            while ((line = reader.readLine()) != null) {
                if (line.equals("}")) {
                    return graph;
                }

                if (!line.matches(regex)) {
                    return null;
                }

                Matcher matcher = Pattern.compile(regex).matcher(line);
                if (matcher.matches()) {
                    String node1Data = matcher.group(1);
                    String node2Data = matcher.group(2);
                    int weight = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 1;
                    GraphNode node1 = graph.createNode(node1Data);
                    GraphNode node2 = graph.createNode(node2Data);
                    graph.insertEdge(node1, node2, weight);
                }
            }
        } catch (IOException | NumberFormatException e) {
            return null;
        }

        return null;
    }
 public static void main(String[] args) {
        Graph graph = Graph.loadFromFile("example.dot");
        if (graph == null) {
            System.out.println("Error importing graph.");
            return;
        }

        System.out.println("Graph imported successfully.");
         for (GraphNode node : graph.adjacencyList.keySet()) {
            System.out.print(node.getData() + " -> ");
            for (Edge edge : graph.adjacencyList.get(node)) {
                GraphNode other = edge.getOppositeEndpoint(node);
                System.out.print(other.getData() + " (" + edge.getWeight() + "), ");
            }
            System.out.println();
        }
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

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isConnectedTo(GraphNode node) {
        return endpoint1.equals(node) || endpoint2.equals(node);
    }

    public GraphNode getOppositeEndpoint(GraphNode node) {
        if (endpoint1.equals(node)) {
            return endpoint2;
        } else if (endpoint2.equals(node)) {
            return endpoint1;
        } else {
            return null;
        }
    }
}