/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author mahendra
 */
public class aid01 {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //======================================================================
        BufferedReader br1 = new BufferedReader(new FileReader(args[0]));
        String str;
        ArrayList<String> list = new ArrayList();

        while ((str = br1.readLine()) != null) {
            list.add(str.split(" ")[0]);
        }
        br1.close();
        //======================================================================
        BufferedReader br2 = new BufferedReader(new FileReader(args[1]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[2]));

        while ((str = br2.readLine()) != null) {

            String sarray1[] = str.split(" ");
            String smi = sarray1[0];
            String id = "NULL";

            for (int a = 0; a < list.size(); a++) {
                String sarray2[] = list.get(a).split(";");
                if (smi.equals(sarray2[0])) {
                    id = sarray2[1];
                }
            }

            if (id.equals("NULL")) {
                System.out.println("NOT FOUND " + id);
            }
            bw.write(id + "\n");
        }

        br2.close();
        bw.close();
        System.out.println("END");
        //======================================================================
    }
}
