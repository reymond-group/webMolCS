/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculateSimFP2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * ALL CBD
 *
 * @author mahendra
 */
public class screen031APfp {

    static DecimalFormat df = new DecimalFormat("#.####");

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //==================================================================================
        BufferedReader brRef = new BufferedReader(new FileReader(args[0]));
        BufferedReader brAllActives = new BufferedReader(new FileReader(args[1]));
        BufferedReader brDB = new BufferedReader(new FileReader(args[2]));
        //==================================================================================
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[3]));
        String str;
        //==================================================================================
//        ArrayList<double[]> refFPs = new ArrayList();
//        while ((str = brRef.readLine()) != null) {
//            refFPs.add(getFPs(str));
//        }
//        brRef.close();
//
//        ArrayList<double[]> allActivesFPs = new ArrayList();
//        while ((str = brAllActives.readLine()) != null) {
//            allActivesFPs.add(getFPs(str));
//        }
//        brAllActives.close();
        //======================================================================
        while ((str = brDB.readLine()) != null) {

            //==================================================================
//            double[] fPs = getFPs(str);
            String sarray[] = str.split(" ");
//
//            String dist = "";
//            for (int a = 0; a < refFPs.size(); a++) {
//
//                double d = getCBDDistances(fPs, refFPs.get(a));
//
//                d = 200.0 / (d + 200.0);
//                dist = dist + df.format(d) + ";";
//            }
//            //==============================================================
//            double maxTani = Double.MIN_VALUE;
//            for (int a = 0; a < allActivesFPs.size(); a++) {
//                double d = getCBDDistances(fPs, allActivesFPs.get(a));
//                d = 200.0 / (d + 200.0);
//
//                if (d > maxTani) {
//                    maxTani = d;
//                }
//            }

            double maxTani = 0;
            bw.write(sarray[0] + " " + sarray[1] + " " + sarray[3] + " " + sarray[2] + ";" + maxTani + "\n");
            //==================================================================
        }

        brDB.close();
        bw.close();
        System.out.println("END");
    }

//==============================================================================    
    static double[] getFPs(String line) {

        String sarray[] = line.split(" ");
        String StringA_fp[] = sarray[3].split(";");
        double[] fp = getStringToDoubleArray(StringA_fp);
        return fp;
    }
//==============================================================================

    static double[] getStringToDoubleArray(String s[]) {

        double[] out = new double[s.length];
        for (int a = 0; a < s.length; a++) {
            out[a] = Double.valueOf(s[a]);
            int tmp = (int) (out[a]);
            out[a] = tmp;
        }

        return out;
    }
    //==========================================================================

    static double getCBDDistances(double fp1[], double fp2[]) {
        double distance = 0;
        for (int a = 0; a < fp1.length; a++) {
            distance = distance + Math.abs(fp1[a] - fp2[a]);
        }
        return distance;
    }
    //==========================================================================

    static float getEcludianDistance(double vectorA[], double vectorB[]) {

        double distance = 0;
        for (int a = 0; a < vectorA.length; a++) {
            distance = distance + (vectorA[a] - vectorB[a]) * (vectorA[a] - vectorB[a]);
        }

        return (float) Math.sqrt(distance);
    }
    //==========================================================================

    static double TaniScalarDistTo(double[] refMQNarr, double[] tgtMQNarr) {

        double SUMab = 0;
        double SUMa2 = 0; // query/tgt
        double SUMb2 = 0; // ref
        int Longest = Math.max(refMQNarr.length, tgtMQNarr.length);

        for (int i = 0; i < Longest; i++) {
            SUMab += refMQNarr[i] * tgtMQNarr[i];
            SUMa2 += tgtMQNarr[i] * tgtMQNarr[i];
            SUMb2 += refMQNarr[i] * refMQNarr[i];
        }

        double Tani = (double) SUMab / ((double) SUMa2 + (double) SUMb2 - (double) SUMab);
        return Tani;
    }
    //==========================================================================
}
