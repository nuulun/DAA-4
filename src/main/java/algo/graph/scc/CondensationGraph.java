package algo.graph.scc;

import algo.graph.model.Graph;
import algo.graph.model.Edge;
import java.util.*;

public class CondensationGraph {
    public static Graph build(List<List<Integer>> sccs, Graph original) {
        Map<Integer, Integer> nodeToSCC = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++)
            for (int u : sccs.get(i)) nodeToSCC.put(u, i);

        Graph dag = new Graph(sccs.size(), true);
        Set<Long> seen = new HashSet<>();

        for (int u = 0; u < original.size(); u++) {
            int cu = nodeToSCC.get(u);
            for (Edge e : original.getEdges(u)) {
                int cv = nodeToSCC.get(e.v);
                if (cu != cv) {
                    long key = ((long)cu << 32) | (cv & 0xffffffffL);
                    if (!seen.contains(key)) {
                        seen.add(key);
                        dag.addEdge(cu, cv, e.w);
                    }
                }
            }
        }
        return dag;
    }
}
