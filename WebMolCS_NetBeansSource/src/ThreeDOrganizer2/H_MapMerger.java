package ThreeDOrganizer2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

//==============================================================================
/**
 * Merges partial 3D data into final 3d Map Data
 *
 * @author mahendra
 */
public class H_MapMerger {

    static DecimalFormat df = new DecimalFormat("#.##");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length == 0) {
            System.out.println("arg0=outname arg1=size arg2/3...=PartMaps\n"
                    + "arg=0 output file name (final merged ASF map readable with CSAview)\n"
                    + "arg=1/2... input files (partmaps)");
            System.exit(0);
        }

        HashMap<String, completMapData> data = new HashMap();
        System.out.println("READING MAPS");
        for (int i = 1; i < args.length; i++) {
            System.out.println("READING FILE " + args[i]);
            BufferedReader br = new BufferedReader(new FileReader(args[i]));
            String str;
            while ((str = br.readLine()) != null) {

                String sarr[] = str.split(" ");
                if (!data.containsKey(sarr[0] + " " + sarr[1] + " " + sarr[2])) {
                    completMapData mapd = new completMapData();
                    data.put(sarr[0] + " " + sarr[1] + " " + sarr[2], mapd);
                }

                //Update the data
                completMapData get = data.get(sarr[0] + " " + sarr[1] + " " + sarr[2]);
                get.setFreq(Integer.valueOf(sarr[3]));
                get.setSums(Double.valueOf(sarr[4]));
                get.setSumsq(Double.valueOf(sarr[5]));
            }
            br.close();
        }

        System.out.println("WRITING MAP " + args[0]);
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[0]));
        for (String key : data.keySet()) {
            completMapData get = data.get(key);
            double num = get.freq;
            double avg = get.sums * 1.0 / num;
            Double stdev = Math.sqrt((1.0 * get.sumSQ - (1.0 * get.sums * get.sums / num)) / (num - 1.0));

            if (stdev.isNaN()) {
                stdev = 0.0;
            }

            bw.write(key + " " + df.format(avg) + " " + df.format(stdev) + " " + df.format(num) + "\n");
        }
        bw.close();
        System.out.println("END");
    }
}

class completMapData {

    double sums;
    double sumSQ;
    int freq;

    completMapData() {
        sums = 0;
        sumSQ = 0;
        freq = 0;
    }

    void setSums(double suM) {
        sums = sums + suM;
    }

    void setSumsq(double sumsq) {
        sumSQ = sumSQ + sumsq;
    }

    void setFreq(int f) {
        freq = freq + f;
    }
    //==========================================================================
}