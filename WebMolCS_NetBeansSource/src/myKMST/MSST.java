/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myKMST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author mahendra
 */
public class MSST {

    //Get the Minimum spanning tree
    void getMST(int[][] coord) {

        //Load the edges
        ArrayList<edge> edges = edgeLoader(coord);

        //getUniq vertex list
        ArrayList<Integer> unqVertexes = getUniqVertexList(edges);

        //Consider each vertex as a indivisual tree, initially
        ArrayList<tree> trees = createTreePerVertex(unqVertexes);

        for (int a = 0; a < edges.size(); a++) {

            //Get the edhe
            edge edg = edges.get(a);

            //get the vertex
            int a1 = findVertexTree(trees, edg.vert1);

            //get the 
            int a2 = findVertexTree(trees, edg.vert2);

            if (a1 == a2) {
                continue;
            }

            trees = mergeComponenets(trees, a1, a2, edg);

            if (trees.size() <= 1) {
                break;
            }
        }

        tree mst = trees.get(0);
        for (int a = 0; a < mst.edges.size(); a++) {
            edge mste = mst.edges.get(a);
            System.out.println(mste.vert1 + " " + mste.vert2);
        }
    }
    //==========================================================================
    ArrayList<Integer> getUniqVertexList(ArrayList<edge> edgeList) {

        HashMap<Integer, Integer> unqVertxIDX = new HashMap();
        for (int a = 0; a < edgeList.size(); a++) {
            unqVertxIDX.put(edgeList.get(a).vert1, 1);
            unqVertxIDX.put(edgeList.get(a).vert2, 1);
        }

        ArrayList<Integer> vertexList = new ArrayList();
        for (Integer key : unqVertxIDX.keySet()) {
            vertexList.add(key);
        }

        return vertexList;

    }
    //==========================================================================
    ArrayList<tree> createTreePerVertex(ArrayList<Integer> vertexList) {

        ArrayList<tree> mst = new ArrayList();
        for (int a = 0; a < vertexList.size(); a++) {
            tree tmp = new tree();
            tmp.addVertex(vertexList.get(a));
            mst.add(tmp);
        }

        return mst;
    }
    //==========================================================================
    int findVertexTree(ArrayList<tree> trees, int vertexIDx) {
        for (int a = 0; a < trees.size(); a++) {
            tree tr = trees.get(a);
            boolean isPresent = tr.checkVertexPresence(vertexIDx);
            if (isPresent) {
                return a;
            }
        }
        return -1;
    }
    //==========================================================================
  ArrayList<tree> mergeComponenets (ArrayList<tree> trees, int cIDX1, int cIDX2, edge ed) {

        tree componenet1 = trees.get(cIDX1);
        tree componenet2 = trees.get(cIDX2);

        if (componenet1.isAlone() && componenet2.isAlone()) {
            componenet1.addEdge(ed);
            componenet1.addVertex(ed.vert1);
            componenet1.addVertex(ed.vert2);
            trees.set(cIDX1, componenet1);
            trees.remove(cIDX2);
            return trees;
        }

        if (componenet1.isAlone() && !componenet2.isAlone()) {
            componenet2.addEdge(ed);
            componenet2.addVertex(ed.vert1);
            componenet2.addVertex(ed.vert2);
            trees.set(cIDX2, componenet2);
            trees.remove(cIDX1);
            return trees;
        }

        if (!componenet1.isAlone() && componenet2.isAlone()) {
            componenet1.addEdge(ed);
            componenet1.addVertex(ed.vert1);
            componenet1.addVertex(ed.vert2);
            trees.set(cIDX1, componenet1);
            trees.remove(cIDX2);
            return trees;
        }

        if (!componenet1.isAlone() && !componenet2.isAlone()) {

            ArrayList<edge> edges = componenet2.getEdges();
            componenet1.addEdge(ed);
            componenet1.addVertex(ed.vert1);
            componenet1.addVertex(ed.vert2);
            for (int a = 0; a < edges.size(); a++) {
                ed = edges.get(a);
                componenet1.addEdge(ed);
                componenet1.addVertex(ed.vert1);
                componenet1.addVertex(ed.vert2);
            }
            trees.set(cIDX1, componenet1);
            trees.remove(cIDX2);
            return trees;
        }

        return null;
    }
    //==========================================================================
    ArrayList<edge> edgeLoader(int distMatrix[][]) {

        ArrayList<edge> al = new ArrayList();
        for (int a = 0; a < distMatrix.length; a++) {
            for (int b = 0; b < distMatrix.length; b++) {

                if (a == b || distMatrix[a][b] == 0) {
                    continue;
                }
                edge v = new edge(a, b, distMatrix[a][b]);
                al.add(v);
            }
        }

        Collections.sort(al);
        return al;
    }
    //==========================================================================
}
