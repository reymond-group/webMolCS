/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mahendra
 */
public class clusterPairs {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //Cluster types 1
        File folder1 = new File(args[0]);
        String inFiles1[] = folder1.list();

        //Cluster types 2
        File folder2 = new File(args[1]);
        String inFiles2[] = folder2.list();

        //Writer
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[2], true));

        //Store pairs from cluster types 1
        HashMap<String, String> hm1 = new HashMap();

        //Store pairs from cluster types 2
        HashMap<String, String> hm2 = new HashMap();

        //Read all pairs and store them
        for (int a = 0; a < inFiles1.length; a++) {

            BufferedReader br1 = new BufferedReader(new FileReader(args[0] + "/" + inFiles1[a]));
            BufferedReader br2 = new BufferedReader(new FileReader(args[1] + "/" + inFiles1[a]));
            String str1 = "";
            String str2 = "";

            //==================================================================
            //Create the pairs in cluster
            ArrayList<String> al1 = new ArrayList();
            while ((str1 = br1.readLine()) != null) {
                String sarray[] = str1.split(" ");
                al1.add(sarray[1]);
            }
            br1.close();

            for (int b = 0; b < al1.size(); b++) {

                String q1 = al1.get(b);
                for (int c = 0; c < al1.size(); c++) {

                    if (b == c) {
                        continue;
                    }

                    String q2 = al1.get(c);
                    hm1.put(q1 + " " + q2, "");
                }
            }
            //==================================================================

            ArrayList<String> al2 = new ArrayList();
            while ((str2 = br2.readLine()) != null) {
                String sarray[] = str2.split(" ");
                al2.add(sarray[1]);
            }
            br2.close();

            for (int b = 0; b < al2.size(); b++) {

                String q1 = al2.get(b);
                for (int c = 0; c < al2.size(); c++) {

                    if (b == c) {
                        continue;
                    }

                    String q2 = al2.get(c);
                    hm2.put(q1 + " " + q2, "");
                }
            }
        }
        //======================================================================

        double totalPairs = hm1.size();
        double totalCluster = inFiles1.length;
        double totalPairfound = 0;
        double perct = 0;

        for (String key1 : hm1.keySet()) {

            String s[] = key1.split(" ");
            String key2 = s[1] + " " + s[0];

            if (hm2.containsKey(key1) || hm2.containsKey(key2)) {
                totalPairfound++;
            }
        }

        perct = totalPairfound / totalPairs;

        bw.write(totalCluster + " " + totalPairs + " " + totalPairfound + " " + perct + "\n");
        bw.close();
        System.out.println("END");
    }
}
