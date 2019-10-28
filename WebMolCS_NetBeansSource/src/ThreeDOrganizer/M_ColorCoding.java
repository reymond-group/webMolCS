/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeDOrganizer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import organize.tools.ArgReader;

/**
 * Color the 3D Bins
 *
 * @author mahendra
 */
public class M_ColorCoding {

    static DecimalFormat df = new DecimalFormat("#.##");

    ////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws FileNotFoundException, IOException {

        ArgReader argr = new ArgReader("Creates Color Coding for map\n"
                + "-i file    ASF input\n"
                + "-o base    out file with bin Color\n"
                + "[-c min,max] set minimum/maximum color/hue\n"
                + "[-m a_min,a_max,s_min,s_max,f_min,f_max] border values for avg,stdev,freq, auto if none set\n", args);

        //Parse arguments, go to subroutines if necessary
        String inFile = argr.getArg("-i");
        String outFile = argr.getArg("-o");

        int minhue = 240, maxhue = 360;
        if (argr.isArg("-c")) {
            String[] sarr = argr.getArg("-c").split(",");
            minhue = Integer.parseInt(sarr[0]);
            maxhue = Integer.parseInt(sarr[1]);
        }

        //maxdegrees to use is minhue to 0 (e.g. 240 to 0), then 0(=360) to maxhue (e.g. 360 - 300)
        int maxdegrees = minhue + (360 - maxhue);

        //read max from cmdline or find out...
        double maxa = 0;
        double maxs = 0;
        double maxf = 0;
        double mina = 0;
        double mins = 0;
        double minf = 0;

        if (argr.isArg("-m")) {
            System.out.println("Max ASF from command line");
            String[] sarr = argr.getArg("-m").split(",");
            maxa = Double.parseDouble(sarr[0]);
            mina = Double.parseDouble(sarr[1]);
            maxs = Double.parseDouble(sarr[2]);
            mins = Double.parseDouble(sarr[3]);
            maxf = Double.parseDouble(sarr[4]);
            minf = Double.parseDouble(sarr[5]);
        } else {
            System.out.println("Please Provide Min/Max Values!");
            System.exit(0);
        }

        //go through file and set
        System.out.println("GOING THROUGH ASF " + inFile);
        BufferedReader br = new BufferedReader(new FileReader(inFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        //bw.write("x y z R G B Avg STDev Freq\n");
        String line;
        while ((line = br.readLine()) != null) {

            String s[] = line.split(" ");

            double avg = Double.parseDouble(s[3]);
            double stdev = Double.parseDouble(s[4]);
            double freq = Double.parseDouble(s[5]);

            //Make percentages
            int aPerc = (int) Math.round((avg - mina) * 100.0 / (maxa - mina));
            aPerc = aPerc < 0 ? 0 : aPerc;
            aPerc = aPerc > 100 ? 100 : aPerc;

            int sPerc = (int) Math.round((stdev - mins) * 100.0 / (maxs - mins));
            sPerc = sPerc < 0 ? 0 : sPerc;
            sPerc = sPerc > 100 ? 100 : sPerc;

            int fPerc = (int) Math.round((freq - minf) * 100.0 / (maxf - minf));
            fPerc = fPerc < 0 ? 0 : fPerc;
            fPerc = fPerc > 100 ? 100 : fPerc;

            //FIRST hue. This one is bit more complicated
            //convert percent to degrees. Since I want to go counter clockwise
            int degrees = maxdegrees * aPerc / 100;
            //start from minimum, go degrees
            int hue = minhue - degrees;

            //SECOND saturation 0(gray)-100(color)
            //I want large s to be gray, so "100 - ..."
            int sat = 100 - sPerc;

            //THIRD lightness 0(black)-50(full color)-100(pure white)
            //I only want up to 50, so scale percent to max 50
            //for white bg pics it must be in range 50-100 logically
            int light = fPerc / 2;

            int rgb = organize.tools.HSLColor.toRGB(hue, sat, light).getRGB();

            Color clr = new Color(rgb);
            bw.write(s[0] + " " + s[1] + " " + s[2] + " " + clr.getRed() + " " + clr.getGreen() + " " + clr.getBlue()
                    + " " + df.format(avg) + " " + df.format(stdev) + " " + df.format(freq));
            bw.newLine();
        }

        bw.close();
        br.close();

        System.out.println("END");
    }
    ////////////////////////////////////////////////////////////////////////////
}
