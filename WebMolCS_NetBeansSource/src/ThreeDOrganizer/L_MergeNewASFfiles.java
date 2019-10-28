/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeDOrganizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author mahendra
 */
public class L_MergeNewASFfiles {

    public static void main(String args[]) throws IOException {

        if (args.length == 0) {

            System.out.println("arg 0= outputFileName");
            System.out.println("arg 1/2..= list of ASF files");
            System.exit(0);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(args[0]));
        for (int i = 1; i < args.length; i++) {
            BufferedReader br = new BufferedReader(new FileReader(args[i]));
            String str;
            while ((str = br.readLine()) != null) {
                bw.write(str + "\n");
            }
            br.close();
        }
        bw.close();
        System.out.println("END");
    }
}
