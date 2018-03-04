package models;

public class Edge implements Comparable<Edge> {

    private final int v;
    private final int w;
    private final double weight;

    public Edge() {
        this.v = 0;
        this.w = 0;
        this.weight = 0.0;
    }

    public Edge(int v, int w, double weight) {
        if (v < 0) throw new IllegalArgumentException("vertex index must be a nonnegative integer");
        if (w < 0) throw new IllegalArgumentException("vertex index must be a nonnegative integer");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    public double weight() {
        return weight;
    }

    public int either() {
        return v;
    }

    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else return -1;
    }

    /**
     * Compares two edges by weight.
     * Note that {@code compareTo()} is not consistent with {@code equals()},
     * which uses the reference equality implementation inherited from {@code Object}.
     *
     * @param  that the other edge
     * @return a negative integer, zero, or positive integer depending on whether
     *         the weight of this is less than, equal to, or greater than the
     *         argument edge
     */
    @Override
    public int compareTo(Edge that) {
        return Double.compare(this.weight, that.weight);
    }

    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */
//    public String toString() {
//        return String.format("%d-%d %.5f", v, w, weight);
//    }

    public String toString() {
        return String.format("%d-%d", v, w);
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this)
            return true;

        /* Check if o is an instance of Edge or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Edge)) {
            return false;
        }

        // typecast o to Edge so that we can compare data members
        Edge edge = (Edge) o;

        // Compare the data members and return accordingly
        return this.v == edge.either() && this.w == edge.other(edge.either())
                || this.w == edge.either() && this.v == edge.other(edge.either());

    }

    public static void main(String[] args) {
        Edge e1 = new Edge(12, 34, 5.67);
        Edge e2 = new Edge(12, 34, 5.89);
        Edge e3 = new Edge(34, 12, 9.99);
        Edge e4 = null;

        if (e2.equals(e4)) {
            StdOut.println("They are equal!");
        } else {
            StdOut.println("Not equal!");
        }

        StdOut.println(e1);
    }
}