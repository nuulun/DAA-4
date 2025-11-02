package algo;

import algo.graph.model.Graph;
import algo.graph.scc.Kosaraju;
import algo.graph.scc.CondensationGraph;
import algo.graph.dagsp.DagShortestPaths;
import algo.graph.topo.TopologicalSort;
import algo.io.JsonLoader;
import algo.io.CsvBenchWriter;
import algo.metrics.Metrics;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "data/input.json";
        String csvPath = "data/bench.csv";

        List<JsonLoader.GraphData> graphs = JsonLoader.loadGraphs(path);
        CsvBenchWriter csv = new CsvBenchWriter(csvPath);

        for (var gd : graphs) {
            System.out.println("=== Graph: " + gd.name + " (" + gd.desc + ") ===");
            System.out.println("Nodes: " + gd.graph.size() + " Source: " + gd.source + " Directed: " + gd.graph.isDirected());

            Metrics metrics = new Metrics();
            long totalStart = System.nanoTime();

            long t0 = System.nanoTime();
            Kosaraju sccAlgo = new Kosaraju(gd.graph, metrics);
            var sccs = sccAlgo.computeSCCs();
            double sccTime = (System.nanoTime() - t0) / 1_000_000.0;
            System.out.println("SCCs: " + sccs);

            Map<Integer, Integer> nodeToSCC = new HashMap<>();
            for (int i = 0; i < sccs.size(); i++)
                for (int u : sccs.get(i)) nodeToSCC.put(u, i);
            int sccSource = nodeToSCC.get(gd.source);

            t0 = System.nanoTime();
            Graph dag = CondensationGraph.build(sccs, gd.graph);
            double condTime = (System.nanoTime() - t0) / 1_000_000.0;

            t0 = System.nanoTime();
            TopologicalSort topo = new TopologicalSort(dag, metrics);
            List<Integer> topoOrder = topo.kahnSort();
            double topoTime = (System.nanoTime() - t0) / 1_000_000.0;
            System.out.println("Topological order of components (SCC indices): " + topoOrder);

            t0 = System.nanoTime();
            DagShortestPaths sp = new DagShortestPaths(dag, metrics);
            DagShortestPaths.PathResult shortest = sp.shortestFrom(sccSource);
            double shortestTime = (System.nanoTime() - t0) / 1_000_000.0;

            t0 = System.nanoTime();
            DagShortestPaths.PathResult longest = sp.longestFrom(sccSource);
            double longestTime = (System.nanoTime() - t0) / 1_000_000.0;

            double totalTime = (System.nanoTime() - totalStart) / 1_000_000.0;

            List<Integer> shortestOriginal = new ArrayList<>();
            for (int sccIdx : shortest.path)
                shortestOriginal.addAll(sccs.get(sccIdx));

            List<Integer> longestOriginal = new ArrayList<>();
            for (int sccIdx : longest.path)
                longestOriginal.addAll(sccs.get(sccIdx));

            System.out.println("Shortest distance (sum of weights): " + shortest.length + " Path: " + shortestOriginal);
            System.out.println("Longest distance (sum of weights): " + longest.length + " Path: " + longestOriginal);

            System.out.println("Metrics: dfsVisits=" + metrics.dfsVisits +
                    ", dfsEdges=" + metrics.dfsEdges +
                    ", kahnPushes=" + metrics.kahnPushes +
                    ", kahnPops=" + metrics.kahnPops +
                    ", relaxations=" + metrics.relaxations +
                    ", times(ms): Kosaraju=" + sccTime +
                    ", Condensation=" + condTime +
                    ", TopoSort=" + topoTime +
                    ", Shortest=" + shortestTime +
                    ", Longest=" + longestTime +
                    ", Total=" + totalTime);
            System.out.println();

            csv.append(
                    gd.name,
                    gd.graph.size(),
                    sccs.size(),
                    topoOrder.size(),
                    shortest.length,
                    longest.length,
                    sccTime,
                    condTime,
                    topoTime,
                    shortestTime,
                    longestTime,
                    totalTime,
                    metrics.dfsVisits,
                    metrics.dfsEdges,
                    metrics.kahnPushes,
                    metrics.kahnPops,
                    metrics.relaxations
            );
        }

        csv.close();
    }
}
