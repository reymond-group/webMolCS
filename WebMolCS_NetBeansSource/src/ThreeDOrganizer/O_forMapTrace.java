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
import java.util.ArrayList;
import java.util.Collections;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @author mahendra
 */
public class O_forMapTrace {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        /////Read the File
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
        ArrayList<bin> bins = new ArrayList();

        String str;
        while ((str = br.readLine()) != null) {
            String s[] = str.split(" ");
            bin BIN = new bin(Integer.valueOf(s[1]) + Integer.valueOf(s[2]) + Integer.valueOf(s[3]));
            BIN.RGB = s[4] + " " + s[5] + " " + s[6];
            BIN.sertXYZ(s[1] + " " + s[2] + " " + s[3]);
            bins.add(BIN);
        }
        br.close();

        Collections.sort(bins);
        int total = bins.size();
        int stepSize = total / 50000;

        for (int a = 0; a < total; a = a + stepSize) {
            bin get = bins.get(a);
            bw.write(get.sXYZ + " " + get.RGB + "\n");
        }

        bw.close();
        System.out.println("END");
    }
}

class bin implements Comparable<bin> {

    int XYZ = 0;
    String RGB = "";
    String sXYZ = "";

    bin(int xyz) {
        XYZ = xyz;
    }

    void sertRGB(String rgb) {

        RGB = rgb;
    }

    void sertXYZ(String stringXYZ) {

        sXYZ = stringXYZ;
    }

    @Override
    public int compareTo(bin o) {
        if (XYZ < o.XYZ) {
            return -1;
        }
        if (XYZ > o.XYZ) {
            return 1;
        }
        return 0;
    }
////////////////////////////////////////////////////////////////////////////////    
}
