/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ThreeDOrganizer2;

import static ThreeDOrganizer2.A_CalcMqnAndProps.fplength;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import organize.tools.molContainer;

//=========================================================================//
/**
 *
 * 3D BINNING, AVG FILE and SORTING OF BINS:
 *
 * =============================INPUT=========================
 *
 * 1) folder for holding all the bins
 *
 *
 * 2) Total Min Max file
 *
 *
 * 3) Start X and End X
 *
 *
 * 4) Total Size of X, Y and Z
 *
 *
 * 5) Transposed file
 *
 * =============================OUTPUT=========================
 *
 * 1) BINS----> outputFolder/x/y_z
 *
 *
 * 2) Average file----->> X_startX_endX.AVG (this average file is temporary one)
 *
 * ============================================================
 *
 * For given range of x coordinate it will do the binning for:
 *
 * X---startX till endX
 *
 *
 * Y---0 till MaxY-1
 *
 *
 * Z---0 till MaxZ-1
 *
 * Specify the starting and ending X! Y and Z directly extends to maximum size-1
 *
 * *****for e.g******
 *
 * If you specify the startX 10 and endX 20 and size of X,Y,Z as 1000: It will
 * do the BINNING for coordinate in the range x(10-19), y (0-1000) and z
 * (0-1000)
 *
 * ============================================================.
 *
 * @author mahendra
 *
 */
public class I_Binning {

    static DecimalFormat df = new DecimalFormat("#.##");

    public static void main(String args[]) throws FileNotFoundException, IOException {

        if (args.length == 0) {
            System.out.println("Creates files in outfolder with molecules per 3d bin\n"
                    + "plus additional 'avg' file with average molecules\n"
                    + "arg0=outFolderName arg1=totminmaxfile arg2=startX arg3=endX "
                    + "arg4=sizeX arg5=sizeY arg6=sizeZ arg7,8..=transposed.gz");
            System.exit(0);
        }

        //======================================================================
        //This is pactch to get number of dimentions, which is equal to number of ref.mols
        int noOfrefMols = 0;
        String st;
        BufferedReader reader = new BufferedReader(new FileReader("DB.smi"));
        String sarray[] = reader.readLine().split(" ");
        reader.close();
        fplength = sarray[3].split(";").length;
        //======================================================================


        //==================================================================
        System.out.println("1) READING TOTAL MINMAX - NONSKEWED\n");

        BufferedReader br = new BufferedReader(new FileReader(args[1]));
        String[] sarr = br.readLine().split(";| ");
        br.close();
        //===================================================================
        double pcMin;

        if (Double.parseDouble(sarr[0]) < Double.parseDouble(sarr[2])) {

            if (Double.parseDouble(sarr[0]) < Double.parseDouble(sarr[4])) {
                pcMin = Double.parseDouble(sarr[0]);
            } else {
                pcMin = Double.parseDouble(sarr[4]);
            }
        } else {

            if (Double.parseDouble(sarr[2]) < Double.parseDouble(sarr[4])) {
                pcMin = Double.parseDouble(sarr[2]);
            } else {
                pcMin = Double.parseDouble(sarr[4]);
            }
        }

        double pcMax;
        if (Double.parseDouble(sarr[1]) > Double.parseDouble(sarr[3])) {

            if (Double.parseDouble(sarr[1]) > Double.parseDouble(sarr[5])) {
                pcMax = Double.parseDouble(sarr[1]);
            } else {
                pcMax = Double.parseDouble(sarr[5]);
            }
        } else {

            if (Double.parseDouble(sarr[3]) > Double.parseDouble(sarr[5])) {
                pcMax = Double.parseDouble(sarr[3]);
            } else {
                pcMax = Double.parseDouble(sarr[5]);
            }
        }
        //==================================================================
        int startX = Integer.valueOf(args[2]);

        //We need to go till size-1:
        int endX = Integer.valueOf(args[3]) - 1;

        //We need to go till size-1: as we start at 0
        int startY = 0;
        int startZ = 0;
        int maxX = Integer.valueOf(args[4]) - 1;
        int maxY = Integer.valueOf(args[5]) - 1;
        int maxZ = Integer.valueOf(args[6]) - 1;

        System.out.println("2) BINNING FROM\n"
                + "X\t=\t" + startX + "\tTO\t" + endX + "\n"
                + "Y\t=\t" + startY + "\tTO\t" + maxY + "\n"
                + "Z\t=\t" + startZ + "\tTO\t" + maxZ + "\n");
        //==================================================================            
        System.out.println("3) FINDING OUT AVG MQNS PER 3D BIN\n");
        HashMap<String, avgMQNAndMol> avgMQNsandMols = new HashMap();

        for (int i = 7; i < args.length; i++) {

            System.out.println("GOING THROUGH AFILE " + args[i]);
            BufferedReader brgz;
            if (args[i].endsWith(".gz")) {
                brgz = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[i]))));
            } else {
                brgz = new BufferedReader(new FileReader(args[i]));
            }

            String s;
            int cnt = 0;
            while ((s = brgz.readLine()) != null) {

                if (++cnt % 1000000 == 0) {
                    System.out.println("Line " + cnt);
                }

                String[] starr = s.split(" ");
                String[] xyzarr = starr[1].split(";");

                int x = (int) (Math.floor((Double.parseDouble(xyzarr[0]) - pcMin) * maxX / (pcMax - pcMin)));
                int y = (int) (Math.floor((Double.parseDouble(xyzarr[1]) - pcMin) * maxY / (pcMax - pcMin)));
                int z = (int) (Math.floor((Double.parseDouble(xyzarr[2]) - pcMin) * maxZ / (pcMax - pcMin)));

                if (x < startX || x > endX) {
                    continue;
                }

                //convert the string mqn to Float MQN
                float[] floatMQNs = stringMQNtoFloatMQN(starr[3]);

                //check if this bin already knwon or not
                if (!avgMQNsandMols.containsKey(x + " " + y + " " + z)) {

                    avgMQNAndMol newBin = new avgMQNAndMol(floatMQNs);
                    avgMQNsandMols.put(x + " " + y + " " + z, newBin);
                    continue;
                }

                avgMQNAndMol get = avgMQNsandMols.get(x + " " + y + " " + z);
                get.updateMQN(floatMQNs);
                get.updateCount();
                avgMQNsandMols.put(x + " " + y + " " + z, get);
            }
            brgz.close();
        }
        //==================================================================
        for (String key : avgMQNsandMols.keySet()) {
            avgMQNAndMol get = avgMQNsandMols.get(key);
            get.getAvgMQN();
            avgMQNsandMols.put(key, get);
        }
        //==================================================================
        System.out.println("4) PUTTING MOLECULES IN THE BINS and CREATING AVG FILE\n");

        for (int i = 7; i < args.length; i++) {

            System.out.println("AFILE " + args[i]);
            BufferedReader brgz;
            if (args[i].endsWith(".gz")) {
                brgz = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[i]))));
            } else {
                brgz = new BufferedReader(new FileReader(args[i]));
            }

            String s;
            while ((s = brgz.readLine()) != null) {

                String[] starr = s.split(" ");
                String[] xyzarr = starr[1].split(";");

                int x = (int) (Math.floor((Double.parseDouble(xyzarr[0]) - pcMin) * maxX / (pcMax - pcMin)));
                int y = (int) (Math.floor((Double.parseDouble(xyzarr[1]) - pcMin) * maxY / (pcMax - pcMin)));
                int z = (int) (Math.floor((Double.parseDouble(xyzarr[2]) - pcMin) * maxZ / (pcMax - pcMin)));

                if (x < startX || x > endX) {
                    continue;
                }

                //convert the string mqn to Float MQN
                float[] floatMQNs = stringMQNtoFloatMQN(starr[3]);

                //here is the actual moving: open file / attach mol to file
                File f = new File(args[0] + "/" + x + "/" + y + "_" + z);
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }

                FileWriter fw = new FileWriter(f, true);
                fw.write(starr[0].replace(';', ' ') + " " + starr[1].split(";")[2] + "\n");
                fw.close();

                //update the avg mol
                avgMQNAndMol get = avgMQNsandMols.get(x + " " + y + " " + z);
                get.updateAvgMol(floatMQNs, starr[0].split(";")[0]);
                avgMQNsandMols.put(x + " " + y + " " + z, get);
            }
            brgz.close();
        }

        //==================================================================
        System.out.println("5) WRITING AVERAGE MOLECULE FILE\n");
        BufferedWriter bw = new BufferedWriter(new FileWriter("X_" + startX + "_" + endX + ".avg"));
        for (String key : avgMQNsandMols.keySet()) {
            avgMQNAndMol get = avgMQNsandMols.get(key);
            bw.write(key + " " + get.avgMol + "\n");
        }
        bw.close();
        //================================================================== 
        System.out.println("6) SORTING BIN FILES\n");
        for (String key : avgMQNsandMols.keySet()) {
            String s[] = key.split(" ");
            br = new BufferedReader(new FileReader(args[0] + "/" + s[0] + "/" + s[1] + "_" + s[2]));
            ArrayList<molContainer> mols = new ArrayList();
            String str;
            while ((str = br.readLine()) != null) {

                sarr = str.split(" ");
                String smi = sarr[0];
                if (!sarr[1].isEmpty()) {
                    smi = smi + " " + sarr[1];
                }

                double val = Double.valueOf(sarr[sarr.length - 1]);
                molContainer mol = new molContainer(smi, val);
                mols.add(mol);
            }
            br.close();
            Collections.sort(mols);

            //rewrite the same file again
            bw = new BufferedWriter(new FileWriter(args[0] + "/" + s[0] + "/" + s[1] + "_" + s[2]));
            for (int a = 0; a < mols.size(); a++) {
                molContainer get = mols.get(a);
                bw.write(get.SMI + " " + df.format(get.VAL) + "\n");
            }
            bw.close();
        }
        System.out.println("=====================END==========================");
    }
//=============================================================================//    

    static float[] stringMQNtoFloatMQN(String mqn) {

        String s[] = mqn.split(";");
        float fMQN[] = new float[fplength];
        for (int a = 0; a < fplength; a++) {
            fMQN[a] = Float.valueOf(s[a]);
        }

        return fMQN;
    }

    //==========================================================================
    static String getString(int array[]) {
        String out = "";
        for (int a = 0; a < array.length; a++) {
            out = out + array[a] + ";";
        }

        return out;
    }
}

class avgMQNAndMol {

    float MQN[] = new float[fplength];
    int count = 0;
    String avgMol = "";
    String avgMolActualMQN = "";
    float distToavgMol = Float.MAX_VALUE;

    avgMQNAndMol(float[] mqn) {
        MQN = mqn;
        count++;
    }

    void updateMQN(float[] mqn) {
        for (int a = 0; a < fplength; a++) {
            MQN[a] = MQN[a] + mqn[a];
        }
    }

    void updateCount() {
        count++;
    }

    void getAvgMQN() {
        for (int a = 0; a < fplength; a++) {

            if (MQN[a] != 0) {
                MQN[a] = MQN[a] / count;
            }
        }
    }

    //=========================================================================//
    void updateAvgMol(float mqn[], String smi) {

        float dist = 0;
        for (int a = 0; a < fplength; a++) {
            dist = dist + Math.abs(mqn[a] - MQN[a]);
        }

        if (dist < distToavgMol) {

            distToavgMol = dist;
            avgMol = smi;
        }
    }
    //=========================================================================//
}
