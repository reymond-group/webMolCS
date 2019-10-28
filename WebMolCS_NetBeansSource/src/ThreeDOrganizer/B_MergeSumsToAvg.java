package ThreeDOrganizer;

import static ThreeDOrganizer.A_CalcMqnAndProps.fplength;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Merges different sumfiles to one average-file. The averages are required in
 * the next step of the pca (mean centralisation) Created 09-Nov-2009
 *
 * @author lori
 */
public class B_MergeSumsToAvg {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length == 0) {
            System.out.println("arg0=totavgs.dat arg1/2/3...=sums.dat\n"
                    + "arg=0 output file with averages of input(;)\n"
                    + "arg>0 input files with sum of mqn values(;) + no. Structures");
            System.exit(0);
        }

        //======================================================================
        //This is pactch to get number of dimentions, which is equal to number of ref.mols
        int noOfrefMols = 0;
        String st;
        BufferedReader reader = new BufferedReader(new FileReader("REF.smi"));
        while ((st = reader.readLine()) != null) {
            noOfrefMols++;
        }
        reader.close();
        fplength = noOfrefMols;
        //======================================================================


        System.out.println("READING IN SUM FILES");
        long noMolecules = 0;
        double[] sums = new double[A_CalcMqnAndProps.fplength];
        for (int i = 1; i < args.length; i++) {
            System.out.println("READING FILE " + args[i]);
            BufferedReader br = new BufferedReader(new FileReader(args[i]));
            String[] sarr = br.readLine().split(" ");
            br.close();
            //actually Integer instead of Long would also work, who cares...
            noMolecules += Long.parseLong(sarr[1]);
            String[] sumarr = sarr[0].split(";");
            for (int j = 0; j < sumarr.length; j++) {
                sums[j] += Double.parseDouble(sumarr[j]);
            }
        }

        System.out.println("WRITING TOTAL AVERAGE (TOTALMOL: " + noMolecules + ")");
        FileWriter fw = new FileWriter(args[0]);
        for (int i = 0; i < sums.length; i++) {
            //*1.0 ensures that no rounding / converting to int is done
            double t = 1.0 * sums[i] / noMolecules;
            fw.write(t + ";");
        }
        fw.close();
        System.out.println("END");
    }
}
