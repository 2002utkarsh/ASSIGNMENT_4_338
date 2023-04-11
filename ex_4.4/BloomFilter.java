import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BloomFilter {
    private Map<Vertex, Set<Connection>> adjacencyMap;

    public BloomFilter() {
        adjacencyMap = new HashMap<>();
    }

    public void addEdge(Vertex vertex1, Vertex vertex2, int weight) {
        Connection edge = new Connection(vertex1, vertex2, weight);
        adjacencyMap.putIfAbsent(vertex1, new HashSet<>());
        adjacencyMap.putIfAbsent(vertex2, new HashSet<>());
        adjacencyMap.get(vertex1).add(edge);
        adjacencyMap.get(vertex2).add(edge);
    }

    public static void main(String[] args) {
        BloomFilter graph = new BloomFilter();
        Vertex A = new Vertex("A");
        Vertex B = new Vertex("B");
        Vertex C = new Vertex("C");

        graph.addEdge(A, B, 3);
        graph.addEdge(B, C, 4);
        graph.addEdge(A, C, 5);

        for (Vertex vertex : graph.adjacencyMap.keySet()) {
            System.out.print(vertex.getLabel() + " -> ");
            for (Connection connection : graph.adjacencyMap.get(vertex)) {
                Vertex other = connection.getOtherEndpoint(vertex);
                System.out.print(other.getLabel() + " (" + connection.getWeight() + "), ");
            }
            System.out.println();
        }
    }

    public int hash(String s, int i) {
        return 0;
    }
}

class Vertex {
    private String label;

    public Vertex(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vertex other = (Vertex) obj;
        return label.equals(other.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}

class Connection {
    private Vertex endpointA;
    private Vertex endpointB;
    private int weight;

    public Connection(Vertex endpointA, Vertex endpointB, int weight) {
        this.endpointA = endpointA;
        this.endpointB = endpointB;
        this.weight = weight;
    }

    public Vertex getEndpointA() {
        return endpointA;
    }

    public Vertex getEndpointB() {
        return endpointB;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isConnectedTo(Vertex vertex) {
        return endpointA.equals(vertex) || endpointB.equals(vertex);
    }

    public Vertex getOtherEndpoint(Vertex vertex) {
        if (endpointA.equals(vertex)) {
            return endpointB;
        } else if (endpointB.equals(vertex)) {
            return endpointA;
        } else {
            return null;
        }
    }
}