package algo;

import algo.graph.model.Graph;
import algo.graph.scc.Kosaraju;
import algo.graph.scc.CondensationGraph;
import algo.graph.dagsp.DagShortestPaths;
import algo.graph.topo.TopologicalSort;
import algo.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmsTest {

    private Graph simpleDAG() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(0, 2, 4);
        return g;
    }

    private Graph graphWithCycle() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1); // cycle
        g.addEdge(2, 3, 2);
        return g;
    }

    @Test
    void testKosarajuSimpleDAG() {
        Graph g = simpleDAG();
        Metrics metrics = new Metrics();
        Kosaraju scc = new Kosaraju(g, metrics);
        List<List<Integer>> sccs = scc.computeSCCs();

        assertEquals(3, sccs.size());
        for (List<Integer> comp : sccs) assertEquals(1, comp.size());
    }

    @Test
    void testKosarajuCycle() {
        Graph g = graphWithCycle();
        Metrics metrics = new Metrics();
        Kosaraju scc = new Kosaraju(g, metrics);
        List<List<Integer>> sccs = scc.computeSCCs();

        boolean hasCycleSCC = sccs.stream().anyMatch(c -> c.size() == 3);
        assertTrue(hasCycleSCC);
        boolean hasSingleNode = sccs.stream().anyMatch(c -> c.size() == 1);
        assertTrue(hasSingleNode);
    }

    @Test
    void testCondensationGraph() {
        Graph g = graphWithCycle();
        Metrics metrics = new Metrics();
        Kosaraju scc = new Kosaraju(g, metrics);
        List<List<Integer>> sccs = scc.computeSCCs();
        Graph dag = CondensationGraph.build(sccs, g);

        TopologicalSort topo = new TopologicalSort(dag, metrics);
        List<Integer> order = topo.kahnSort();
        assertEquals(dag.size(), order.size());
    }

    @Test
    void testDagShortestPaths() {
        Graph g = simpleDAG();
        Metrics metrics = new Metrics();
        Kosaraju scc = new Kosaraju(g, metrics);
        List<List<Integer>> sccs = scc.computeSCCs();
        Graph dag = CondensationGraph.build(sccs, g);

        Map<Integer, Integer> nodeToSCC = sccsToMap(sccs);
        int source = nodeToSCC.get(0);

        DagShortestPaths sp = new DagShortestPaths(dag, metrics);
        DagShortestPaths.PathResult shortest = sp.shortestFrom(source);
        DagShortestPaths.PathResult longest = sp.longestFrom(source);

        assertEquals(3, shortest.length);
        assertEquals(4, longest.length);
        assertFalse(shortest.path.isEmpty());
        assertFalse(longest.path.isEmpty());
    }

    @Test
    void testSingleNode() {
        Graph g = new Graph(1, true);
        Metrics metrics = new Metrics();

        Kosaraju scc = new Kosaraju(g, metrics);
        List<List<Integer>> sccs = scc.computeSCCs();
        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());

        Graph dag = CondensationGraph.build(sccs, g);
        assertEquals(1, dag.size());

        TopologicalSort topo = new TopologicalSort(dag, metrics);
        List<Integer> order = topo.kahnSort();
        assertEquals(1, order.size());

        DagShortestPaths sp = new DagShortestPaths(dag, metrics);
        DagShortestPaths.PathResult shortest = sp.shortestFrom(0);
        DagShortestPaths.PathResult longest = sp.longestFrom(0);
        assertEquals(0, shortest.length);
        assertEquals(0, longest.length);
    }

    private Map<Integer, Integer> sccsToMap(List<List<Integer>> sccs) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) map.put(node, i);
        }
        return map;
    }
}
