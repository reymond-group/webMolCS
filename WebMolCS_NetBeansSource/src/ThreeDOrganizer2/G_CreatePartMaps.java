package ThreeDOrganizer2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

//==========================================================================
/**
 *
 * CREATING 3D PARTIAL MAP!!!
 *
 * During the generation of map i am not creating explicit NxNxN matrix as its
 * too much resource intensive! For example in case of 1000x1000x1000 map its
 * already 1 billion double or floating point numbers which easily going to
 * explode the virtual memory!
 *
 * But not to worry we can still create this 3D partial map, only thing is that
 * do not explictly defined the 3D matrix! Only points which are occupied are
 * written to file! I am doing 3d map creation in a bit different way:!!Follow
 * the code carefully its easy to understand!!
 *
 * @author mahendra
 *
 */
public class G_CreatePartMaps {

    public static void main(String args[]) throws FileNotFoundException, IOException {

//==============================================================================        
        if (args.length == 0) {
            System.out.println(
                    "arg0=basename arg1=totalminmax.dat arg2=size arg3=transposed[.gz]\n"
                    + "arg=0 basename for the map files (.hac.asf, .mass.asf, etc...)\n"
                    + "arg=1 input file with overall min&max of PC1&2\n"
                    + "arg=2 size for (quadratic) maps\n"
                    + "arg=3 input files with smi + transposed PC1;PC2 + csaproperties");
            System.exit(0);
        }

        System.out.println("BASE: " + args[0] + " TOTALMINMAX: " + args[1] + " SIZE: "
                + args[2]);
        String base = args[0];
//==============================================================================

        System.out.println("READING TOTAL MINMAX - NONSKEWED");
        BufferedReader br = new BufferedReader(new FileReader(args[1]));
        String[] sarr = br.readLine().split(";| ");
        br.close();
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
        //======================================================================
        int size = Integer.parseInt(args[2]);
        int arrmax = size - 1; //arrays only go to size-1, so define that here
        //======================================================================

        HashMap<String, mapData> data = new HashMap();
        BufferedReader brgz = null;
        if (args[3].endsWith(".gz")) {
            brgz = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[3]))));
        } else {
            brgz = new BufferedReader(new FileReader(args[3]));
        }
        String s;
        int cnt = 0;

//        String[] names = {"simToQuery","maxSimToRef", "hac", "hbd", "hba", "rbc", "ring", "hetero", "aromAtoms", "fHetero", "fsp3", "fArom"};

        String[] names = {"simToQuery1", "ActualValues", "simToQuery2", "maxSimToRef"};

        while ((s = brgz.readLine()) != null) {

            if (++cnt % 100000 == 0) {
                System.out.println("Line " + cnt);
            }

            String[] starr = s.split(" ");
            String[] xyzarr = starr[1].split(";");

            int x = (int) (Math.floor((Double.parseDouble(xyzarr[0]) - pcMin) * arrmax / (pcMax - pcMin)));
            int y = (int) (Math.floor((Double.parseDouble(xyzarr[1]) - pcMin) * arrmax / (pcMax - pcMin)));
            int z = (int) (Math.floor((Double.parseDouble(xyzarr[2]) - pcMin) * arrmax / (pcMax - pcMin)));

            if (!data.containsKey(x + " " + y + " " + z)) {
                mapData mapd = new mapData(names.length);
                data.put(x + " " + y + " " + z, mapd);
            }

            mapData get = data.get(x + " " + y + " " + z);
            String[] proparr = starr[2].split(";");

            for (int j = 0; j < names.length; j++) {
                double propval = Double.parseDouble(proparr[j]);
                get.setSums(j, propval);
                get.setSumsq(j, propval * propval);
            }

            get.setFreq();
            data.put(x + " " + y + " " + z, get);
        }

        brgz.close();
        //======================================================================
        System.out.println("WRITING MAPS");
        for (int a = 0; a < names.length; a++) {

            System.out.println("WRITING MAP " + names[a]);
            BufferedWriter bw = new BufferedWriter(new FileWriter(base + "." + names[a] + ".partmap"));

            for (String key : data.keySet()) {
                mapData get = data.get(key);
                bw.write(key + " " + get.freq + " " + get.sums[a] + " " + get.sumSQ[a] + "\n");
            }

            bw.close();
        }
    }
}

class mapData {

    double sums[];
    double sumSQ[];
    int freq;

    mapData(int size) {
        sums = new double[size];
        sumSQ = new double[size];
        freq = 0;
    }

    void setSums(int index, double sum) {
        sums[index] = sums[index] + sum;
    }

    void setSumsq(int index, double sumsq) {
        sumSQ[index] = sumSQ[index] + sumsq;
    }

    void setFreq() {
        freq++;
    }
    //==========================================================================
}
