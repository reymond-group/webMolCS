package KMST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author daenu
 */
public class KNearestNeighbourMinimumSpanningTree {

    private static ArrayList<Edge> mst;
    private static ArrayList<Edge> tmp_graph;
    private static ArrayList<Edge> knn_graph;

    private static double getDistance(double[] a, double[] b) {
        double total = 0.0;

        if (a.length != b.length) {
            throw new Error("Arrays a and b must be of the same length.");
        }

        for (int i = 0; i < a.length; i++) {
            total += Math.abs(b[i] - a[i]);
        }

        return total;
    }

    private static ArrayList<Edge> getKDistancesFrom(int node, int k) {
        ArrayList<Edge> k_nearest_neighbours = new ArrayList();

        for (int i = 0; i < tmp_graph.size() && k_nearest_neighbours.size() < k; i++) {
            Edge edge = tmp_graph.get(i);
            if (edge.getFrom() == node || edge.getTo() == node) {
                k_nearest_neighbours.add(edge);
            }
        }

        return k_nearest_neighbours;
    }

    public static ArrayList<Edge> create(double[][] coordinates, int k) {
        mst = new ArrayList();
        knn_graph = new ArrayList();

        // Ugly code due to Java memory restrictions ...
        for (int i = 0; i < coordinates.length; i++) {
            tmp_graph = new ArrayList();
            for (int j = 0; j < coordinates.length; j++) {
                if (j != i) {
                    tmp_graph.add(new Edge(i, j, getDistance(coordinates[i], coordinates[j])));
                }
            }

            Collections.sort(tmp_graph, new Comparator<Edge>() {
                @Override
                public int compare(Edge a, Edge b) {
                    double diff = a.getWeight() - b.getWeight();
                    if (diff < 0) {
                        return -1;
                    }
                    if (diff > 0) {
                        return 1;
                    }
                    return 0;
                }
            });

            ArrayList<Edge> tmp = getKDistancesFrom(i, k);
            for (int j = 0; j < tmp.size(); j++) {
                Edge edge = tmp.get(j);
                if (!knn_graph.contains(edge)) {
                    knn_graph.add(edge);
                }
            }
        }

        // Sort it again
        Collections.sort(knn_graph, new Comparator<Edge>() {
            @Override
            public int compare(Edge a, Edge b) {
                double diff = a.getWeight() - b.getWeight();
                if (diff < 0) {
                    return -1;
                }
                if (diff > 0) {
                    return 1;
                }
                return 0;
            }
        });

        // Vertices
        Set<Integer> vertices = new HashSet();
        for (int i = 0; i < coordinates.length; i++) {
            vertices.add(i);
        }

        // Kruskal
        UnionFind<Integer> forest = new UnionFind<Integer>(vertices);

        for (int i = 0; i < knn_graph.size(); i++) {
            Edge edge = knn_graph.get(i);
            int source = edge.getFrom();
            int target = edge.getTo();

            if (forest.find(source).equals(forest.find(target))) {
                continue;
            }

            forest.union(source, target);
            mst.add(edge);
        }

        return mst;
    }
}
