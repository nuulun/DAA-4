package algo.graph.scc;

import algo.graph.model.Graph;
import algo.metrics.Metrics;

import java.util.*;

public class Kosaraju {
    private final Graph graph;
    private final Metrics metrics;

    public Kosaraju(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<List<Integer>> computeSCCs() {
        metrics.startTimer();

        int n = graph.size();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        // 1st pass DFS
        for (int i = 0; i < n; i++) if (!visited[i]) dfs1(i, visited, stack);
        // transpose graph
        Graph gT = transposeGraph();
        // 2nd pass DFS
        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                dfs2(u, visited, component, gT);
                sccs.add(component);
            }
        }
        metrics.stopTimer();
        return sccs;
    }

    private void dfs1(int u, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;
        metrics.dfsVisits++;
        for (var e : graph.getEdges(u)) {
            metrics.dfsEdges++;
            if (!visited[e.v]) dfs1(e.v, visited, stack);
        }
        stack.push(u);
    }

    private void dfs2(int u, boolean[] visited, List<Integer> component, Graph gT) {
        visited[u] = true;
        metrics.dfsVisits++;
        component.add(u);
        for (var e : gT.getEdges(u)) {
            metrics.dfsEdges++;
            if (!visited[e.v]) dfs2(e.v, visited, component, gT);
        }
    }

    private Graph transposeGraph() {
        Graph gT = new Graph(graph.size(), true);
        for (int u = 0; u < graph.size(); u++) {
            for (var e : graph.getEdges(u)) gT.addEdge(e.v, e.u, e.w);
        }
        return gT;
    }
}
