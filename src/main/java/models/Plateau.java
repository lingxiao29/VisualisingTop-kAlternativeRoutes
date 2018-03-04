package models;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class Plateau {
    public static final int EPSILON = 1000000;

    private int source;
    private int target;
    private EdgeWeightedGraph ewg;
    private DijkstraUndirectedSP fspt;
    private DijkstraUndirectedSP bspt;


    public Plateau(int source, int target, EdgeWeightedGraph ewg, DijkstraUndirectedSP fspt, DijkstraUndirectedSP bspt) {
        this.source = source;
        this.target = target;
        this.ewg = ewg;
        this.fspt = fspt;
        this.bspt = bspt;
    }

    public Map<Integer, Double> getPLVertex(double spLength, double ratio) {
        Map<Integer, List<Edge>> plSegments = new HashMap<Integer, List<Edge>>();

        int key_u = 0;
        int key_v = 0;
        for (Edge e : ewg.edges()) {
            key_u = (int) (fspt.distTo(e.either()) * EPSILON + bspt.distTo(e.either()) * EPSILON);
            key_v = (int) (fspt.distTo(e.other(e.either())) * EPSILON + bspt.distTo(e.other(e.either())) * EPSILON);
            if (key_u == key_v && key_u <= (int)(fspt.distTo(target) * EPSILON * 1.5)) {
                //if (key_u == key_v) {
                if ((e.equals(fspt.getEdge(e.either())) || e.equals(fspt.getEdge(e.other(e.either())))) && (e.equals(bspt.getEdge(e.either())) || e.equals(bspt.getEdge(e.other(e.either()))))) {
                    if (plSegments.containsKey(key_u)) {
                        plSegments.get(key_u).add(e);
                    } else {
                        List<Edge> pp = new ArrayList<Edge>();
                        pp.add(e);
                        plSegments.put(key_u, pp);
                    }
                }
            }
        }

        Map<Integer, Double> plVertex = new HashMap<Integer, Double>();
        for (Map.Entry<Integer, List<Edge>> entry : plSegments.entrySet()) {
            if (entry.getValue().size() > 2) {
                List<Edge> el = entry.getValue();
                CC cc = new CC(entry.getValue());
                for (Map.Entry<Integer, List<Integer>> me : cc.connectedComponents().entrySet()) {
                    if (me.getKey() >= (int)(spLength * ratio * EPSILON)) {
                        for (int i : me.getValue()) {
                            plVertex.put(i, me.getKey() * 1.0 / EPSILON);
                        }
                    }
                }
            }
        }
        return plVertex;
    }

    public List<List<Integer>> getPlateaus(int k) {
        int topK = k;
        Map<Integer, List<Edge>> plSegments = new HashMap<Integer, List<Edge>>();

        int key_u = 0;
        int key_v = 0;
        for (Edge e : ewg.edges()) {
            key_u = (int) (fspt.distTo(e.either()) * EPSILON + bspt.distTo(e.either()) * EPSILON);
            key_v = (int) (fspt.distTo(e.other(e.either())) * EPSILON + bspt.distTo(e.other(e.either())) * EPSILON);
            if (key_u == key_v && key_u <= (int)(fspt.distTo(target) * EPSILON * 1.5)) {
            //if (key_u == key_v) {
                if ((e.equals(fspt.getEdge(e.either())) || e.equals(fspt.getEdge(e.other(e.either())))) && (e.equals(bspt.getEdge(e.either())) || e.equals(bspt.getEdge(e.other(e.either()))))) {
                    if (plSegments.containsKey(key_u)) {
                        plSegments.get(key_u).add(e);
                    } else {
                        List<Edge> pp = new ArrayList<Edge>();
                        pp.add(e);
                        plSegments.put(key_u, pp);
                    }
                }
            }
        }

        // Using Google Guava
        Multimap<Integer, List<Integer>> multiMaps = ArrayListMultimap.create();
        IndexMaxPQ<Integer> maxIndexPQ = new IndexMaxPQ<Integer>();
        for (Map.Entry<Integer, List<Edge>> entry : plSegments.entrySet()) {
            if (entry.getValue().size() > 2) {
                List<Edge> el = entry.getValue();
                CC cc = new CC(entry.getValue());
                for (Map.Entry<Integer, List<Integer>> me : cc.connectedComponents().entrySet()) {
                    maxIndexPQ.insert(me.getKey());
                    multiMaps.put(me.getKey(), me.getValue());
                }
            }
        }

        topK = topK > maxIndexPQ.size() ? maxIndexPQ.size() : topK;
        List<List<Integer>> topKPLs = new ArrayList<List<Integer>>();
        while (topK > 0) {
            int key = maxIndexPQ.delMax();
            int mSize = multiMaps.get(key).size();
            for (List<Integer> el : multiMaps.get(key)) {
/*                if (el.size() <= THRESHOLD) {
                    topKPLs.add(el);
                } else {
                    List<Integer> plHeadTail = new ArrayList<Integer>();
                    // Insert head of plateau
                    plHeadTail.add(findNearestNode(el, "SU"));
                    // Insert middle part of plateau
                    List<Integer> partPl = modPickItem(el);
                    plHeadTail.addAll(partPl);
                    // Insert tail of this plateau
                    plHeadTail.add(findNearestNode(el, "TV"));

                    topKPLs.add(plHeadTail);
                }*/
                topKPLs.add(el);
            }
            topK = topK - mSize;
        }

        return topKPLs;
    }


    public Map<Integer, List<Integer>> getPaths(int k) {
        List<List<Integer>> topKPLs = getPlateaus(k);
        Map<Integer, List<Integer>> topKPath = new HashMap<Integer, List<Integer>>();

        for (int i = 0; i < topKPLs.size(); i++) {
            int u = findNearestNode(topKPLs.get(i), "SU");
            int v = findNearestNode(topKPLs.get(i), "TV");

            List<Integer> finalPath = new ArrayList<Integer>();
            int pathLen = (int) (fspt.distTo(v) * EPSILON + bspt.distTo(v) * EPSILON) / 10000;

            if (fspt.distTo(u) < bspt.distTo(u)) {
                List<Integer> sv = new ArrayList<Integer>();
                sv.add(source);
                for (Edge e : fspt.pathTo(v)) {
                    v = e.other(e.either());
                    if (v != -1)
                        sv.add(v);
                }

                List<Integer> tv = new ArrayList<Integer>();
                for (Edge e : bspt.pathToV2(v)) {
                    v = e.other(v);
                    if (v != -1)
                        tv.add(v);
                }

                finalPath.addAll(sv);
                finalPath.addAll(tv);

            } else {
                //StdOut.println("BBB");
                List<Integer> tv = new ArrayList<Integer>();
                tv.add(target);
                for (Edge e : bspt.pathTo(v)) {
                    v = e.other(e.either());
                    if (v != -1)
                        tv.add(v);
                }

                List<Integer> sv = new ArrayList<Integer>();
                for (Edge e : fspt.pathToV2(v)) {
                    v = e.other(v);
                    if (v != -1)
                        sv.add(v);
                }

                finalPath.addAll(tv);
                finalPath.addAll(sv);
            }
            //topKPath.add(finalPath);
            topKPath.put(pathLen, finalPath);
        }
        return topKPath;
    }

    private int findNearestNode(List<Integer> pl, String flag) {
        int vertex = pl.get(0);
        double distance;
        if (flag == "SU") {
            distance = fspt.distTo(vertex);
            for (int i = 1; i < pl.size(); i++) {
                if (distance > fspt.distTo(pl.get(i))) {
                    distance = fspt.distTo(pl.get(i));
                    vertex = pl.get(i);
                }
            }
        } else if (flag == "TV") {
            distance = bspt.distTo(vertex);
            for (int i = 1; i < pl.size(); i++) {
                if (distance > bspt.distTo(pl.get(i))) {
                    distance = bspt.distTo(pl.get(i));
                    vertex = pl.get(i);
                }
            }
        }
        return vertex;
    }
}