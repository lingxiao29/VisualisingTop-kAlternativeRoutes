package models;

import java.util.ArrayList;
import java.util.List;

public class DijkstraUndirectedSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;


    public DijkstraUndirectedSP(EdgeWeightedGraph G, int s) {
        distTo = new double[G.V() + 1];
        edgeTo = new Edge[G.V() + 1];
        for (int v = 0; v <= G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        pq = new IndexMinPQ<Double>(G.V() + 1);
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v))
                relax(e, v);
        }
    }

    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Edge getEdge(int v) {
        return edgeTo[v];
    }

    public Iterable<Edge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }

    public List<Edge> pathToV2(int v) {
        if (!hasPathTo(v)) return null;
        List<Edge> path = new ArrayList<Edge>();
        int x = v;
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
            path.add(e);
            x = e.other(x);
        }
        return path;
    }

    public static void main(String[] args) {

    }
}