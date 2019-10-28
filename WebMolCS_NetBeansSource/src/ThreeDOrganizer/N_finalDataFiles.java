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
import java.util.HashMap;

/**
 *
 * //===========================================================================
 * This will give the final files necessary for visualization!
 *
 *
 * Input:
 *
 *
 * 1) file from step M (color coded file)
 *
 *
 * 2) file from step J (average file (this one is temporary)
 *
 *
 * Output:
 *
 *
 * 1) *.VIS file
 *
 *
 * 2) *.ASF FILE
 *
 *
 * 3) *.AVG File
 *
 * //===========================================================================
 *
 *
 * IMPORTANT point to note is that all the lines in ASF and AVG file contains
 * equals number of CHARACHTERS! The ID in the *.VIS file CORROSPONDS to line
 * number in *.AVG and *.ASF file! In this way for each bin from *.VIS file we
 * directly know line number (in *.ASF and *.AVG) and hence the data for this
 * bin
 *
 *
 * Keeping line of same length is necessary to make the used of randomAccess
 * file! Hence we do not need to load *.AVG and *.ASF file into the memory! We
 * just need the line number to go to any line in *.ASF and *.AVG file! GREAT
 *
 * HAVE A LOOK ON RANDOM ACCESS FILE in JAVA!!
 *
 *
 * @author mahendra
 *
 */
public class N_finalDataFiles {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        if (args.length == 0) {

            System.out.println("args 0(in)  = file from step M (color coding)");
            System.out.println("args 1(in)  = file from step J (average File)");
            System.out.println("args 2(out) = *.VIS (seperate for each map)");
            System.out.println("args 3(out) = *.ASF (seperate for each map)");
            System.out.println("args 4(out) = *.AVG (only one for each DB)");
        }

        HashMap<String, String> bins = new HashMap();
        HashMap<String, String> avgMols = new HashMap();

        BufferedReader brColor = new BufferedReader(new FileReader(args[0]));
        BufferedReader brAverage = new BufferedReader(new FileReader(args[1]));

        String str;
        System.out.println("1) Reading File " + args[0]);
        while ((str = brColor.readLine()) != null) {

            String sarr[] = str.split(" ");
            bins.put(sarr[0] + " " + sarr[1] + " " + sarr[2], str);
        }
        brColor.close();
        //======================================================================
        System.out.println("2) WRITING VIS FILE " + args[2] + ".VIS");
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[2] + ".VIS"));
        int counter = 0;

        for (String key : bins.keySet()) {

            counter++;
            String s[] = bins.get(key).split(" ");
            bw.write(counter + " " + s[0] + " " + s[1] + " " + s[2] + " " + s[3] + " " + s[4] + " " + s[5] + "\n");
        }
        bw.close();
        //======================================================================                
        System.out.println("3) WRITING ASF File " + args[3] + ".ASF");
        bw = new BufferedWriter(new FileWriter(args[3] + ".ASF"));

        int biggest = 0;

        for (String key : bins.keySet()) {

            String s[] = bins.get(key).split(" ");
            String q = s[6] + " " + s[7] + " " + s[8];
            if (q.length() > biggest) {
                biggest = q.length();
            }
        }

        for (String key : bins.keySet()) {

            String s[] = bins.get(key).split(" ");
            String q = s[6] + " " + s[7] + " " + s[8];
            for (int i = q.length(); i < biggest; i++) {
                q = q + " ";
            }

            bw.write(q + "\n");
        }
        bw.close();
        //======================================================================
        System.out.println("4) Reading File " + args[1]);
        while ((str = brAverage.readLine()) != null) {

            String sarr[] = str.split(" ");
            avgMols.put(sarr[0] + " " + sarr[1] + " " + sarr[2], sarr[3]);
        }
        brAverage.close();
        //======================================================================
        System.out.println("5)WRITING AVG File " + args[3] + ".AVG");
        biggest = 0;

        for (String key : avgMols.keySet()) {

            String q = avgMols.get(key);
            if (q.length() > biggest) {
                biggest = q.length();
            }
        }
        //======================================================================
        bw = new BufferedWriter(new FileWriter(args[4] + ".AVG"));

        for (String key : bins.keySet()) {

            String q = avgMols.get(key);
            for (int i = q.length(); i < biggest; i++) {
                q = q + " ";
            }
            bw.write(q + "\n");
        }

        bw.close();
        //======================================================================
        System.out.println("END");
    }
}
