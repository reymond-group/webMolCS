/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KMST;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author daenu
 */
public class KMST {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {


        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        //Read the data
        String str = br.readLine();
        br.close();
        if (str == null || str.isEmpty()) {
            System.exit(0);
        }

        String sarray[] = str.split(";");
        if (sarray.length < 2) {
            System.exit(0);
        }

        double[][] coord = new double[sarray.length][3];

        for (int a = 0; a < sarray.length; a++) {
            String xyz[] = sarray[a].split("_");
            coord[a][0] = Double.valueOf(xyz[0]);
            coord[a][1] = Double.valueOf(xyz[1]);
            coord[a][2] = Double.valueOf(xyz[2]);
        }

        ArrayList<Edge> mst = KNearestNeighbourMinimumSpanningTree.create(coord, 5);
        for (int a = 0; a < mst.size(); a++) {
            Edge ed = mst.get(a);
            int from = ed.getFrom();
            int to = ed.getTo();
            double wt = ed.getWeight();
            bw.write(from + " " + to + " " + wt + "\n");
        }
        bw.close();
        System.out.println("DONE");
    }
}
