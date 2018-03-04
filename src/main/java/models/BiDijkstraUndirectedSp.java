package models;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.*;
import java.util.Stack;

/**
 * Created by llx on 1/03/2018.
 */
public class BiDijkstraUndirectedSp {
    public static final double EPSILON = 1.5;      // Maximum stretch
    public static final double ALPHA = 0.25;    // Local optimality value
    public static final double GAMA = 0.8;      // Maximum sharing value

    public static final int BETA = 1000000;


    private int s;
    private int t;
    private EdgeWeightedGraph G;
    private int meetVertex;
    private double spLength;
    private double bestPathLength;

    private double[] fwDistTo;
    private double[] bwDistTo;

    private Edge[] fwEdgeTo;
    private Edge[] bwEdgeTo;

    private IndexMinPQ<Double> fwPQ;
    private IndexMinPQ<Double> bwPQ;

    private List<Integer> candidateVertices;

    public BiDijkstraUndirectedSp(EdgeWeightedGraph G, int s, int t, double spLength) {
        this.s = s;
        this.t = t;
        this.G = G;
        this.spLength = spLength;
        bestPathLength = Double.POSITIVE_INFINITY;
        candidateVertices = new ArrayList<Integer>();

        fwDistTo = new double[G.V() + 1];
        bwDistTo = new double[G.V() + 1];

        fwEdgeTo = new Edge[G.V() + 1];
        bwEdgeTo = new Edge[G.V() + 1];

        for (int v = 1; v <= G.V(); v++) {
            fwDistTo[v] = Double.POSITIVE_INFINITY;
            bwDistTo[v] = Double.POSITIVE_INFINITY;
        }
        fwDistTo[s] = 0.0;
        bwDistTo[t] = 0.0;

        fwPQ = new IndexMinPQ<Double>(G.V() + 1);
        bwPQ = new IndexMinPQ<Double>(G.V() + 1);

        fwPQ.insert(s, fwDistTo[s]);
        bwPQ.insert(t, bwDistTo[t]);

        double u = 0.0;
        while (u < spLength * EPSILON) {
            u = fwPQ.minKey() + bwPQ.minKey();

            if (fwPQ.minKey() <= bwPQ.minKey()) {
                int fv = fwPQ.delMin();
                for (Edge e : G.adj(fv)) {
                    fwRelax(e, fv);
                }
            } else {
                int bv = bwPQ.delMin();
                for (Edge e : G.adj(bv)) {
                    bwRelax(e, bv);
                }
            }
        }
    }

    private void fwRelax(Edge e, int v) {
        int w = e.other(v);
        if (fwDistTo[w] > fwDistTo[v] + e.weight()) {
            fwDistTo[w] = fwDistTo[v] + e.weight();

            fwEdgeTo[w] = e;

            if (fwPQ.contains(w)) {
                fwPQ.decreaseKey(w, fwDistTo[w]);
            } else if (fwDistTo[w] < spLength * EPSILON) {
                fwPQ.insert(w, fwDistTo[w]);
                if (bwDistTo[w] < spLength * EPSILON) {
                    candidateVertices.add(w);
                    if (bwDistTo[w] + fwDistTo[w] < bestPathLength) {
                        bestPathLength = bwDistTo[w] + fwDistTo[w];
                        meetVertex = w;
                    }
                }
            }
        }
    }

    private void bwRelax(Edge e, int v) {
        int w = e.other(v);
        if (bwDistTo[w] > bwDistTo[v] + e.weight()) {
            bwDistTo[w] = bwDistTo[v] + e.weight();

            bwEdgeTo[w] = e;

            if (bwPQ.contains(w)) {
                bwPQ.decreaseKey(w, bwDistTo[w]);
            } else if (bwDistTo[w] < spLength * EPSILON) {
                bwPQ.insert(w, bwDistTo[w]);
                if (fwDistTo[w] < spLength * EPSILON) {
                    candidateVertices.add(w);
                    if (bwDistTo[w] + fwDistTo[w] < bestPathLength) {
                        bestPathLength = bwDistTo[w] + fwDistTo[w];
                        meetVertex = w;
                    }
                }
            }
        }
    }

    public double fwDistTo(int v) {
        return fwDistTo[v];
    }

    public double bwDistTo(int v) {
        return bwDistTo[v];
    }

    public int getMeetVertex() {
        return meetVertex;
    }

    public double getSpLength() {
        return fwDistTo[meetVertex] + bwDistTo[meetVertex];
    }

    public int getNumOfViaNode() {
        return candidateVertices.size();
    }

    public Iterable<Edge> fwPathTo(int v) {
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = fwEdgeTo[v]; e != null; e = fwEdgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }

    public Iterable<Edge> bwPathTo(int v) {
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = bwEdgeTo[v]; e != null; e = bwEdgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }


    public List<Edge> fwPathToV2(int v) {
        List<Edge> path = new ArrayList<Edge>();
        int x = v;
        for (Edge e = fwEdgeTo[v]; e != null; e = fwEdgeTo[x]) {
            path.add(e);
            x = e.other(x);
        }
        return path;
    }

    public List<Edge> bwPathToV2(int v) {
        List<Edge> path = new ArrayList<Edge>();
        int x = v;
        for (Edge e = bwEdgeTo[v]; e != null; e = bwEdgeTo[x]) {
            path.add(e);
            x = e.other(x);
        }
        return path;
    }

    public double sharedLength(int base, int check) {
        double fwShared = 0.0;
        double bwShared = 0.0;

        List<Edge> fwBase = fwPathToV2(base);
        List<Edge> bwBase = bwPathToV2(base);

        List<Edge> fwCheck = fwPathToV2(check);
        List<Edge> bwCheck = bwPathToV2(check);

        for (Edge fe : fwBase) {
            for (Edge ce : fwCheck) {
                if (fe == ce) {
                    fwShared += ce.weight();
                }
            }
        }

        for (Edge be : bwBase) {
            for (Edge ce : bwCheck) {
                if (be == ce) {
                    bwShared += ce.weight();
                }
            }
        }

        return fwShared + bwShared;
    }

    public List<Integer> getViaVertices(int k) {
        int topK = k;

        DijkstraUndirectedSP fspt = new DijkstraUndirectedSP(G, s);
        DijkstraUndirectedSP bspt = new DijkstraUndirectedSP(G, t);
        Plateau pl = new Plateau(s, t, G, fspt, bspt);

        Map<Integer, Double> plVertex = pl.getPLVertex(spLength, ALPHA);
        Map<Integer, Double> TTestVertex = new HashMap<Integer, Double>();
        List<Double> plLen = new ArrayList<Double>();

        for (int i : candidateVertices) {
            if (plVertex.containsKey(i)) {
                if (!plLen.contains(plVertex.get(i))) {
                    plLen.add(plVertex.get(i));
                    TTestVertex.put(i, plVertex.get(i));
                    //StdOut.println(i + ", " + plVertex.get(i));
                }
            } /*else {
                TTestVertex.put(i, 0.0);
            }*/
        }


        //StdOut.println(TTestVertex.size());

        IndexMinPQ<Double> pq = new IndexMinPQ<Double>(TTestVertex.size());
        Map<Integer, Double> funcVertex = new HashMap<Integer, Double>();

        int cur = 0;
        for (Map.Entry<Integer, Double> entry : TTestVertex.entrySet()) {
            double objFunc = 2 * (fwDistTo[entry.getKey()] + bwDistTo[entry.getKey()]) + sharedLength(meetVertex, entry.getKey())
                    - entry.getValue();

/*            StdOut.println(entry.getKey() + " : " + 2 * (fwDistTo[entry.getKey()] + bwDistTo[entry.getKey()]) + " | " + sharedLength(meetVertex, entry.getKey())
                          + " | " + entry.getValue() + " | " + objFunc);*/

            pq.insert(cur, objFunc);
            funcVertex.put(entry.getKey(), objFunc);
            cur++;
        }

        /**
         * Print out PQ
         */
/*        while (!pq.isEmpty()) {
            double f = pq.minKey();
            int v = pq.delMin();
            StdOut.println(v + " -- " + f);
        }*/

        /**
         * Return all the k + 1 via-vertex topKViaVertex (include meetVertex)
         */
        List<Integer> topKViaVertex = new ArrayList<Integer>();
        //topKViaVertex.add(meetVertex);

        /**
         * Extract keys of funcVertex
         */
        Set<Integer> keys = funcVertex.keySet();
        Integer[] arr = keys.toArray(new Integer[keys.size()]);

        //StdOut.println(Arrays.toString(arr));

        /**
         * Set up top k value
         */
        topK = topK > pq.size() ? pq.size() : topK;

        //StdOut.println(topK + ", " + pq.size());

        while (topK > 0) {
            double f = pq.minKey();
            int index = pq.delMin();
            boolean flag = true;

            for (int i : topKViaVertex) {
                if (sharedLength(i, arr[index]) / spLength > GAMA) {
                    flag = false;
                }
            }

            if (flag)
                topKViaVertex.add(arr[index]);

            //StdOut.println("Result: " + arr[index]);
            topK--;
        }

        return topKViaVertex;
    }

    public Map<Integer, List<Integer>> getAlternativePaths(int k) {
        List<Integer> topKVertices = getViaVertices(k);
        //List<List<Integer>> topKPath = new ArrayList<List<Integer>>();
        Map<Integer, List<Integer>> topKPath = new HashMap<Integer, List<Integer>>();

        for (int v : topKVertices) {
            List<Integer> finalPath = new ArrayList<Integer>();
            List<Integer> tvList = new ArrayList<Integer>();
            List<Integer> svList = new ArrayList<Integer>();

            int pathLen = (int) (bwDistTo(v) * BETA + fwDistTo(v) * BETA) / 10000;
            //StdOut.println("The length of this path is: " + pathLen);

            //tvList.add(t);
            int tv = v;
            for (Edge e : bwPathToV2(v)) {
                tv = e.other(tv);
                if (tv != -1)
                    tvList.add(tv);
            }
            tvList.add(t);

            int su = v;
            for (Edge e : fwPathToV2(v)) {
                su = e.other(su);
                if (su != -1)
                    svList.add(su);
            }

            List<Integer> reverseTvList = new ArrayList<Integer>();
            reverseTvList = Lists.reverse(tvList);
            finalPath.addAll(reverseTvList);
            finalPath.addAll(svList);

            topKPath.put(pathLen, finalPath);
        }
        return topKPath;
    }


    public static void main(String[] args) {
    }






}
