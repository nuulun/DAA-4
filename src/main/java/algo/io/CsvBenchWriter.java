package algo.io;

import java.io.FileWriter;
import java.io.IOException;

public class CsvBenchWriter {
    private static final String HEADER = String.join(",",
            "Graph", "Nodes", "Edges", "SCCs", "TopoOrderLen",
            "ShortestDist", "LongestDist",
            "SCC_Time(ms)", "Condensation_Time(ms)", "Topo_Time(ms)",
            "Shortest_Time(ms)", "Longest_Time(ms)", "Total_Time(ms)",
            "DFS_Visits", "DFS_Edges", "Kahn_Pushes", "Kahn_Pops", "Relaxations");

    private final FileWriter writer;

    public CsvBenchWriter(String path) throws IOException {
        writer = new FileWriter(path);
        writer.write(HEADER + "\n");
    }

    public void append(String graphName,
                       int nodes, int edges,
                       int topoSize,
                       double shortestDist, double longestDist,
                       double sccTime, double condTime, double topoTime,
                       double shortTime, double longTime, double totalTime,
                       long dfsVisits, long dfsEdges, long kahnPushes, long kahnPops, long relaxations) throws IOException {

        String row = String.join(",",
                graphName,
                String.valueOf(nodes),
                String.valueOf(edges),
                String.valueOf(topoSize),
                String.valueOf(shortestDist),
                String.valueOf(longestDist),
                String.format("%.3f", sccTime),
                String.format("%.3f", condTime),
                String.format("%.3f", topoTime),
                String.format("%.3f", shortTime),
                String.format("%.3f", longTime),
                String.format("%.3f", totalTime),
                String.valueOf(dfsVisits),
                String.valueOf(dfsEdges),
                String.valueOf(kahnPushes),
                String.valueOf(kahnPops),
                String.valueOf(relaxations)
        );

        writer.write(row + "\n");
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }
}
