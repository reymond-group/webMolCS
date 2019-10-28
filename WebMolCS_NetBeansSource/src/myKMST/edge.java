/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myKMST;

public class edge implements Comparable<edge> {

    //edge 1 index
    public int vert1;
    
    //edge 2 index
    public int vert2;
    
    //Wts
    public double wt;

    public edge(int v1, int v2, double w) {

        vert1 = v1;
        vert2 = v2;
        wt = w;
    }

    //defining this function makes it a real "Comparable", therefore sort() functional.
    @Override
    public int compareTo(edge arg) {
        if (wt < arg.wt) {
            return -1;
        }
        if (wt > arg.wt) {
            return 1;
        }
        return 0;
    }
}