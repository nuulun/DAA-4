package algo.io;

import algo.graph.model.Graph;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonLoader {

    public static class GraphData {
        public final String name;
        public final Graph graph;
        public final int source;
        public final String desc;

        public GraphData(String name, Graph graph, int source, String desc) {
            this.name = name;
            this.graph = graph;
            this.source = source;
            this.desc = desc;
        }
    }

    public static List<GraphData> loadGraphs(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));
        List<GraphData> list = new ArrayList<>();

        Iterator<JsonNode> it = root.get("graphs").elements();
        while (it.hasNext()) {
            JsonNode gNode = it.next();
            String name = gNode.get("name").asText();
            int n = gNode.get("n").asInt();
            boolean directed = gNode.get("directed").asBoolean();
            Graph g = new Graph(n, directed);

            for (JsonNode e : gNode.get("edges")) {
                g.addEdge(e.get("u").asInt(), e.get("v").asInt(), e.get("w").asInt());
            }

            int source = gNode.has("source") ? gNode.get("source").asInt() : 0;
            String desc = gNode.has("desc") ? gNode.get("desc").asText() : "";
            list.add(new GraphData(name, g, source, desc));
        }

        return list;
    }
}
