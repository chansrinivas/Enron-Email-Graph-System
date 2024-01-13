public class Edge {

    int v1;
    int v2;
    int weight;

    /**
     * Constructs an edge between two vertices with the specified weight.
     *
     * @param vertex1 The first vertex of the edge.
     * @param vertex2 The second vertex of the edge.
     * @param w       The weight of the edge.
     */
    public Edge(int vertex1, int vertex2, int w) {
        v1 = vertex1;
        v2 = vertex2;
        weight = w;
    }

    /**
     * Returns the first vertex of the edge.
     *
     * @return The first vertex.
     */
    public int getV1() {
        return v1;
    }

    /**
     * Sets the first vertex of the edge.
     *
     * @param v1 The first vertex to set.
     */
    public void setV1(int v1) {
        this.v1 = v1;
    }

    /**
     * Returns the second vertex of the edge.
     *
     * @return The second vertex.
     */
    public int getV2() {
        return v2;
    }

    /**
     * Sets the second vertex of the edge.
     *
     * @param v2 The second vertex to set.
     */
    public void setV2(int v2) {
        this.v2 = v2;
    }

    /**
     * Returns the weight of the edge.
     *
     * @return The weight.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the edge.
     *
     * @param weight The weight to set.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
}
