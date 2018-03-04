package models;

import java.util.*;

/**
 * Created by llx on 10/02/2018.
 */
public class CC {
    private int V;
    private List<Integer> vertices;
    private Bag<Integer>[] adj;
    private Bag<Edge>[] adjacencyList;

    public CC(List<Edge> eg) {
        Set ss = new HashSet();
        for (Edge e : eg) {
            ss.add(e.either());
            ss.add(e.other(e.either()));
        }

        V = ss.size();
        vertices = new ArrayList(ss);

        adj = new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag();
        }

        adjacencyList = (Bag<Edge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adjacencyList[v] = new Bag<Edge>();
        }

        for (Edge e : eg) {
            addEdge(e);
        }
    }

    private void addEdge(Edge e) {
        int pos_u = vertices.indexOf(e.either());
        int pos_v = vertices.indexOf(e.other(e.either()));
        adj[pos_u].add(pos_v);
        adj[pos_v].add(pos_u);

        adjacencyList[pos_u].add(e);
        adjacencyList[pos_v].add(e);
    }

    private void DFSUtil(int v, boolean visited[], List scc) {
        visited[v] = true;
        scc.add(vertices.get(v));

        for (int i : adj[v]) {
            if (!visited[i]) {
                DFSUtil(i, visited, scc);
            }
        }
    }

    public Map<Integer, List<Integer>> connectedComponents() {
        Map<Integer, List<Integer>> ccs = new HashMap<Integer, List<Integer>>();
        boolean[] visited = new boolean[V];
        for (int v = 0; v < V; v++) {
            visited[v] = false;
        }

        for (int v = 0; v < V; v++) {
            if (!visited[v]) {
                List<Integer> singleCC = new ArrayList();
                DFSUtil(v, visited, singleCC);

                if (singleCC.size() >= 2) {
                    ccs.put(getPLLength(singleCC), singleCC);
                    //StdOut.print(getPLLength(singleCC) + ": ");
                    //StdOut.println(singleCC);
                }
            }
        }
        return ccs;
    }

    private int getPLLength(List<Integer> pl) {
        int v = pl.get(0);
        int vIndex = vertices.indexOf(v);
        double length = 0.0;

        for (int i = 1; i < pl.size(); i++) {
            for (Edge e : adjacencyList[vIndex]) {
                if ((e.either() == pl.get(i)) || (e.other(v) == pl.get(i))) {
                    length += e.weight();
                }
            }
            v = pl.get(i);
            vIndex = vertices.indexOf(v);
        }
        return (int) (length * 1000000);
    }

    public static void main(String[] args) {
        Edge e1 = new Edge(1, 0, 0.2);
        Edge e2 = new Edge(2, 3, 0.3);
        Edge e3 = new Edge(3, 4, 0.5);
        Edge e4 = new Edge(9, 10, 0.5);
        List<Edge> le = new ArrayList<Edge>();
        le.add(e1);
        le.add(e3);
        le.add(e2);
        le.add(e4);

        CC cc = new CC(le);
        cc.connectedComponents();
    }

}