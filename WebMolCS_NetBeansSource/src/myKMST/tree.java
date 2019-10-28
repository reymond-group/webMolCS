/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myKMST;

import java.util.ArrayList;
import java.util.HashMap;

//==============================================================================
/**
 * @author mahendra
 */
//==============================================================================
public class tree {

    ArrayList<edge> edges = new ArrayList();
    HashMap<Integer, Integer> vertex = new HashMap();

    void addEdge(edge ed) {
        edges.add(ed);
    }

    ArrayList<edge> getEdges() {
        return edges;
    }

    void addVertex(int v) {
        vertex.put(v, v);
    }

    boolean checkVertexPresence(int v) {
        if (vertex.containsKey(v)) {
            return true;
        } else {
            return false;
        }
    }

    boolean isAlone() {
        if (edges.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
//==============================================================================