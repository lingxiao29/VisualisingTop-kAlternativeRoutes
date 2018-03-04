package models;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.*;

public class EdgeWeightedGraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int V;
    private int E;
    private Bag<Edge>[] adj;

    public EdgeWeightedGraph() {
        //File file = new File("web/WEB-INF/content/col_gr.txt");
        String line = null;

        Resource res = new ClassPathResource("conf/col_gr.txt");
        //FileSystemResource resource = new FileSystemResource("D:\\IJ_project\\routeDemo\\src\\main\\resources\\col_gr");
        try {
            File file = res.getFile();
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            this.V = Integer.parseInt(bufferedReader.readLine().trim());
            this.E = Integer.parseInt(bufferedReader.readLine().trim());
            //this.V = 250;

            adj = (Bag<Edge>[]) new Bag[V + 1];
            for (int v = 1; v <= V; v++) {
                adj[v] = new Bag<Edge>();
            }


            while ((line = bufferedReader.readLine()) != null) {
                String[] fields = line.split(" ");
                int v = Integer.parseInt(fields[1].trim());
                int w = Integer.parseInt(fields[2].trim());
                double weight = Integer.parseInt(fields[3].trim()) * 1.0 / 1000000;
                Edge e = new Edge(v, w, weight);
                addEdge(e);
                //System.out.println(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
    }

    public EdgeWeightedGraph(File file) {
        String line = null;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            this.V = Integer.parseInt(bufferedReader.readLine().trim());
            this.E = Integer.parseInt(bufferedReader.readLine().trim());

            adj = (Bag<Edge>[]) new Bag[V + 1];
            for (int v = 1; v <= V; v++) {
                adj[v] = new Bag<Edge>();
            }


            while ((line = bufferedReader.readLine()) != null) {
                String[] fields = line.split(" ");
                int v = Integer.parseInt(fields[1].trim());
                int w = Integer.parseInt(fields[2].trim());
                double weight = Integer.parseInt(fields[3].trim()) * 1.0 / 1000000;
                Edge e = new Edge(v, w, weight);
                addEdge(e);
                //System.out.println(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);
        adj[v].add(e);
        //adj[w].add(e);
        E++;
    }

    public Iterable<Edge> adj(int v) {
        return adj[v];
    }

    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 1; v <= V; v++) {     // For real data, first node is 1
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                } else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0)
                        list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }

    public void getCoordinate(String[] arr) {
        Resource res = new ClassPathResource("conf/col_co.txt");
        String line = null;

        arr[0] = "lat_lng";
        try {
            File file = res.getFile();
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int i = 1;
            while ((line = bufferedReader.readLine()) != null) {
                String[] fields = line.split(" ");
                String lat = Integer.parseInt(fields[3].trim()) * 1.0 / 1000000 + "";
                String lng = Integer.parseInt(fields[2].trim()) * 1.0 / 1000000 + "";
                arr[i] = lat + "_" + lng;
                i++;
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
    }

    public int getNearestNode(String[] arr, String latLng) {
        String[] fields = latLng.trim().split("_");
        double distance = Double.POSITIVE_INFINITY;
        double currentDis = 0.0;
        int nodeId = 0;
        for (int i = 1; i < arr.length; i++) {
            String[] items = arr[i].split("_");
            currentDis = DistanceCalculator.distance(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]),
                                                     Double.parseDouble(items[0]), Double.parseDouble(items[1]));
            if (currentDis < distance) {
                distance = currentDis;
                nodeId = i;
            }
        }
        StdOut.println(arr[nodeId] + ", " + currentDis);
        return nodeId;
    }


    public static void main(String[] args) {
    }
}
