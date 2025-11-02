package algo.graph.topo;

import algo.metrics.Metrics;
import algo.graph.model.Graph;

import java.util.*;

public class TopologicalSort {

    private final Graph graph;
    private final Metrics metrics;

    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }


    public List<Integer> kahnSort() {
        metrics.startTimer();

        int n = graph.size();
        int[] indegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (var e : graph.getEdges(u)) indegree[e.v]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                metrics.kahnPushes++;
            }
        }

        List<Integer> topoOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.kahnPops++;
            topoOrder.add(u);

            for (var e : graph.getEdges(u)) {
                indegree[e.v]--;
                if (indegree[e.v] == 0) {
                    queue.add(e.v);
                    metrics.kahnPushes++;
                }
            }
        }

        metrics.stopTimer();

        if (topoOrder.size() != n) {
            throw new RuntimeException("Graph is not a DAG (cycle detected)");
        }

        return topoOrder;
    }
}
