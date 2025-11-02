package algo.graph.model;

import java.util.*;

public class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }


    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(u, v, w));
        if (!directed) adj.get(v).add(new Edge(v, u, w));
    }

    public List<Edge> getEdges(int u) { return adj.get(u); }
    public int size() { return n; }
    public boolean isDirected() { return directed; }
}
