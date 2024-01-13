import java.util.*;

public class Graph<T> {

    private final LinkedList<Edge>[] adjacencyList;

    /**
     * Constructs a graph with the specified number of vertices.
     *
     * @param vertices The number of vertices in the graph.
     */
    public Graph(int vertices){
        adjacencyList = new LinkedList[vertices];
        for(int i = 0; i< vertices; i++){
            adjacencyList[i] = new LinkedList<Edge>();
        }
    }

    /**
     * Adds an edge between two vertices in the graph with the specified weight.
     *
     * @param v1     The first vertex of the edge.
     * @param v2     The second vertex of the edge.
     * @param weight The weight of the edge.
     */
    public void addEdge(int v1, int v2, int weight){
        Edge e = new Edge(v1, v2, weight);
        adjacencyList[v1].add(e);
    }

    /**
     * Removes the edge between two vertices in the graph.
     *
     * @param v1 The first vertex of the edge.
     * @param v2 The second vertex of the edge.
     */
    public void removeEdge(int v1, int v2){
        LinkedList<Edge> ll = adjacencyList[v1];
        int index = 0;
        boolean found = false;
        Iterator<Edge> iterator = ll.iterator();
        while(iterator.hasNext() && !found){
           Edge current = iterator.next();
           if(current.getV2() == v2){
               found = true;
           } else {
               index++;
           }
        }
        ll.remove(index);
    }
}
