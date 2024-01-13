import java.util.PriorityQueue;
import java.util.Queue;

public class GraphAdjacencyMatrix {
    int[][] adjacencyMatrix;
    int[] incidents;
    Queue<Integer> schedule;

    /**
     * Constructs a graph with the specified number of vertices using an adjacency matrix representation.
     *
     * @param vertices The number of vertices in the graph.
     */
    public GraphAdjacencyMatrix(int vertices){
        adjacencyMatrix = new int[vertices][vertices];
        incidents = new int[vertices];
        schedule = new PriorityQueue<>();
    }
    /**
     * Adds an edge between two vertices in the graph.
     *
     * @param v1 The first vertex of the edge.
     * @param v2 The second vertex of the edge.
     */
    public void addEdge(int v1, int v2){
        if(adjacencyMatrix.length > v1 && adjacencyMatrix.length > v2 && v1!=v2){
            adjacencyMatrix[v1][v2] = 1;
            incidents[v2]++;
        }
    }

    /**
     * Removes the edge between two vertices in the graph.
     *
     * @param v1 The first vertex of the edge.
     * @param v2 The second vertex of the edge.
     */
    public void removeEdge(int v1, int v2){
        if(adjacencyMatrix.length > v1 && adjacencyMatrix.length > v2 && v1!=v2){
            adjacencyMatrix[v1][v2] = 0;
            incidents[v2]--;
        }
    }

    /**
     * Finds a vertex with no incident edges.
     *
     * @return The index of a vertex with no incident edges, or -1 if none is found.
     */
    public int noIncidentVertex(){
        for(int i = 0; i < incidents.length; i++){
            if(incidents[i] == 0){
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes outgoing edges from a vertex.
     *
     * @param v1 The vertex from which outgoing edges are to be removed.
     */
    public void removeOutgoingVertices(int v1){
        for(int i = 0; i < adjacencyMatrix.length; i++){
            if(adjacencyMatrix[v1][i] == 1){
                incidents[i]--;
            }
        }
    }

    /**
     * Performs a topological sort on the graph.
     */
    public void topologicalSort(){
        for(int i = 0; i < adjacencyMatrix.length; i++){
            int noIncidentVert = noIncidentVertex();
            incidents[noIncidentVert] = -1;
            schedule.add(noIncidentVert);
            removeOutgoingVertices(noIncidentVert);
        }
    }

    public static void main(String[] args){
        GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix(10);

        graph.addEdge(0, 2);
        graph.addEdge(0, 3);
        graph.addEdge(0, 6);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);

        graph.topologicalSort();
    }
}
