/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeDOrganizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 *
 * @author mahendra
 *
 */
public class K_specialASFForOccupancy {

    static DecimalFormat df = new DecimalFormat("#.##");

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //======================================================================
        if (args.length == 0) {
            System.out.println("args: \n"
                    + "0) ASF file (from step H)\n"
                    + "1) Points from ASF file for which to calculate new STDev (from step H)\n"
                    + "2) Radius\n"
                    + "3) Output file name");
            System.exit(0);
        }
        //======================================================================
        //Read all average points 
        System.out.println("1) Loading All Points..");
        BufferedReader br_AllAvgPoints = new BufferedReader(new FileReader(args[0]));
        HashMap<String, String> hm_AllAvgPoints = new HashMap<String, String>();
        String str;
        while ((str = br_AllAvgPoints.readLine()) != null) {

            String s[] = str.split(" ");
            hm_AllAvgPoints.put(s[0] + " " + s[1] + " " + s[2], s[3] + " " + s[4] + " " + s[5]);
        }
        br_AllAvgPoints.close();
        //======================================================================        
        BufferedReader br_QPoints = new BufferedReader(new FileReader(args[1]));
        //======================================================================
        int radius = Integer.valueOf(args[2]);
        //======================================================================

        System.out.println("2) Calculating standard deviation with radius " + radius + "..");
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[3]));
        while ((str = br_QPoints.readLine()) != null) {

            String s[] = str.split(" ");
            int x = Integer.valueOf(s[0]);
            int y = Integer.valueOf(s[1]);
            int z = Integer.valueOf(s[2]);

            int startX = x - radius;
            int startY = y - radius;
            int startZ = z - radius;

            int endX = x + radius;
            int endY = y + radius;
            int endZ = z + radius;

            double countMol = 0;

            for (int a = startX; a <= endX; a++) {
                for (int b = startY; b <= endY; b++) {
                    for (int c = startZ; c <= endZ; c++) {

                        String id = a + " " + b + " " + c;
                        String get = hm_AllAvgPoints.get(id);
                        if (get == null) {
                            continue;
                        }

                        countMol = countMol + Double.valueOf(get.split(" ")[0]);
                    }
                }
            }

            bw.write(s[0] + " " + s[1] + " " + s[2] + " " + df.format(countMol) + " " + 0 + " " + s[5] + "\n");
        }
        br_QPoints.close();
        bw.close();
        System.out.println("END");
    }
    //======================================================================
}
