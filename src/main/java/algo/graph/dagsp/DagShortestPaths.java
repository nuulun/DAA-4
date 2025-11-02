package algo.graph.dagsp;

import algo.graph.model.Graph;
import algo.metrics.Metrics;

import java.util.*;

public class DagShortestPaths {
    private final Graph dag;
    private final Metrics metrics;

    public DagShortestPaths(Graph dag, Metrics metrics) {
        this.dag = dag;
        this.metrics = metrics;
    }

    public static class PathResult {
        public final int length;
        public final List<Integer> path;

        public PathResult(int length, List<Integer> path) {
            this.length = length;
            this.path = path;
        }
    }

    public PathResult shortestFrom(int source) {
        metrics.startTimer();

        int n = dag.size();
        int[] dist = new int[n];
        int[] pred = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(pred, -1);

        dist[source] = 0;

        List<Integer> topo = new ArrayList<>();
        try {
            topo = new algo.graph.topo.TopologicalSort(dag, metrics).kahnSort();
        } catch (Exception e) {
            throw new RuntimeException("Graph is not a DAG for shortestFrom");
        }

        for (int u : topo) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (var e : dag.getEdges(u)) {
                    metrics.relaxations++;
                    if (dist[e.v] > dist[u] + e.w) {
                        dist[e.v] = dist[u] + e.w;
                        pred[e.v] = u;
                    }
                }
            }
        }
        metrics.stopTimer();

        int target = 0;
        int maxReach = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] != Integer.MAX_VALUE && dist[i] > maxReach) {
                maxReach = dist[i];
                target = i;
            }
        }
        List<Integer> path = reconstructPath(pred, source, target);

        return new PathResult(maxReach, path);
    }

    public PathResult longestFrom(int source) {
        metrics.startTimer();

        int n = dag.size();
        int[] dist = new int[n];
        int[] pred = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(pred, -1);

        dist[source] = 0;

        List<Integer> topo = new ArrayList<>();
        try {
            topo = new algo.graph.topo.TopologicalSort(dag, metrics).kahnSort();
        } catch (Exception e) {
            throw new RuntimeException("Graph is not a DAG for longestFrom");
        }

        for (int u : topo) {
            if (dist[u] != Integer.MIN_VALUE) {
                for (var e : dag.getEdges(u)) {
                    metrics.relaxations++;
                    if (dist[e.v] < dist[u] + e.w) {
                        dist[e.v] = dist[u] + e.w;
                        pred[e.v] = u;
                    }
                }
            }
        }
        metrics.stopTimer();

        int target = 0;
        int maxReach = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (dist[i] != Integer.MIN_VALUE && dist[i] > maxReach) {
                maxReach = dist[i];
                target = i;
            }
        }
        List<Integer> path = reconstructPath(pred, source, target);

        return new PathResult(maxReach, path);
    }

    private List<Integer> reconstructPath(int[] pred, int source, int target) {
        List<Integer> path = new ArrayList<>();
        if (source == target) { path.add(source); return path; }
        int cur = target;
        while (cur != -1) {
            path.add(cur);
            cur = pred[cur];
        }
        Collections.reverse(path);
        if (path.get(0) != source) return Collections.emptyList();
        return path;
    }
}
