/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

//==============================================================================
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author mahendra
 */
public class aid02 {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //======================================================================
        BufferedReader brDB = new BufferedReader(new FileReader(args[0]));
        BufferedReader brREF = new BufferedReader(new FileReader(args[1]));
        String fp = args[2];
        BufferedWriter bw = new BufferedWriter(new FileWriter("CpdRankAsperFP.txt"));
        //======================================================================

        String str;

        ArrayList<String> alDB = new ArrayList();
        HashMap<String, String> refs = new HashMap();

        //READ WHOLE DB=========================================================
        //EACH LINE IS: SMILES+" "+name+" "+SIMVALUE+" "+FP
        while ((str = brDB.readLine()) != null) {
            alDB.add(str);
        }
        brDB.close();

        //READ REFERENCES=======================================================
        //EACH LINE IS: SMILES+" "+name+" "+SIMVALUE+" "+FP
        while ((str = brREF.readLine()) != null) {
            refs.put(str, str);
        }
        brREF.close();
        //======================================================================

        //GET THE QUERY MOLECULE FP (1st compound in database)
        String qstring[] = alDB.get(0).split(" ");
        double qfp[] = getStringToDoubleArray(qstring[3].split(";"));

        //Now calculate the distances for all compounds
        ArrayList<molContainer> cpdsWithDist = new ArrayList();
        for (int a = 0; a < alDB.size(); a++) {

            String dbstring[] = alDB.get(a).split(" ");
            double dbfp[] = getStringToDoubleArray(dbstring[3].split(";"));

            //If the figerprint is not MHFP, then do normal CBD calculation
            if (fp.equals("MHFP")) {
                
                double jaccard = getMHPFPjaccard(qfp, dbfp);
                cpdsWithDist.add(new molContainer(alDB.get(a), jaccard));

            } else {
                double cbdDistances = getCBDDistances(qfp, dbfp);
                cpdsWithDist.add(new molContainer(alDB.get(a), cbdDistances));
            }
        }

        Collections.sort(cpdsWithDist);
        //======================================================================
        double min = 1;
        double max = cpdsWithDist.size();

        if (max > min && max != 0) {

            for (int a = 0; a < cpdsWithDist.size(); a++) {

                double rank = a + 1;
                double scaleScore = 1 - ((rank - min) / (max - min));
                cpdsWithDist.get(a).simScore = scaleScore;
            }
        }
        //======================================================================
        //REWRITE THE INPUT FILES AGAIN
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(args[0]));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(args[1]));

        for (int a = 0; a < cpdsWithDist.size(); a++) {

            String data[] = cpdsWithDist.get(a).data.split(" ");
            double simVal = cpdsWithDist.get(a).simScore;
            bw1.write(data[0] + " " + data[1] + " " + data[2] + ";" + simVal + " " + data[3] + "\n");

            if (refs.containsKey(cpdsWithDist.get(a).data)) {
                bw2.write(data[0] + " " + data[1] + " " + data[2] + ";" + simVal + " " + data[3] + "\n");
            }

            bw.write(data[0] + " " + data[1] + " " + data[2] + ";" + simVal + " " + cpdsWithDist.get(a).dist + "\n");
        }
        bw1.close();
        bw2.close();
        bw.close();
        //======================================================================
    }

//==============================================================================
    static double getCBDDistances(double fp1[], double fp2[]) {
        double distance = 0;
        for (int a = 0; a < fp1.length; a++) {
            distance = distance + Math.abs(fp1[a] - fp2[a]);
        }
        return distance;
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
//==============================================================================

    static double getMHPFPjaccard(double fp1[], double fp2[]) {

        HashMap<String, Double> hm1 = new HashMap();
        HashMap<String, Double> hm2 = new HashMap();

        for (int a = 0; a < fp1.length; a++) {
            hm1.put(fp1[a] + "", fp1[a]);
        }

        for (int a = 0; a < fp2.length; a++) {
            hm2.put(fp2[a] + "", fp2[a]);
        }

        double nc = 0;
        double na = hm1.size();
        double nb = hm2.size();
        
        for (String key : hm1.keySet()) {

            if (hm2.containsKey(key)) {
                nc = nc + 1;
            }
        }

        double tanimoto = nc / ((na + nb) - nc);
        return 1 - tanimoto;
    }

//==============================================================================    
}

//==============================================================================
class molContainer implements Comparable<molContainer> {

    public String data;
    public double dist;
    public double simScore = 0;

    public molContainer(String x, double d) {
        data = x;
        dist = d;
    }

    //defining this function makes it a real "Comparable", therefore sort() functional.
    @Override
    public int compareTo(molContainer arg) {
        if (dist < arg.dist) {
            return -1;
        }
        if (dist > arg.dist) {
            return 1;
        }
        return 0;
    }
}
//==============================================================================
