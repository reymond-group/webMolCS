package ThreeDOrganizer2;

import static ThreeDOrganizer2.A_CalcMqnAndProps.fplength;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import organize.tools.SMIReadWriter;

/**
 * Transposing to 1st three PCs
 *
 * @author mahendra
 */
public class E_TransposeToPC123 {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        SMIReadWriter smio = new SMIReadWriter(
                "usage: -i meancentralized[.gz] -o transposed[.gz] -e ewev.dat -m minmax.dat\n"
                + "-i mean centralized input file with smi + centralized mqns + csaproperties\n"
                + "-o output file with smi + transposed PC1;PC2;PC3 + csaproperties\n"
                + "-e input file with sorted eigenvalues + eigenvectors(;)\n"
                + "-m output file with min&max of PC1&2&3", args);

        //Check arguments immediately
        System.out.println("IN: " + smio.getArg("-i") + " OUT: " + smio.getArg("-o")
                + " EWEV: " + smio.getArg("-e") + " MINMAX: " + smio.getArg("-m"));

        //======================================================================
        //This is pactch to get number of dimentions, which is equal to number of ref.mols
        int noOfrefMols = 0;
        String st;
        BufferedReader reader = new BufferedReader(new FileReader("DB.smi"));
        String sarray[] = reader.readLine().split(" ");
        reader.close();
        fplength = sarray[3].split(";").length;
        //======================================================================

        /*Read 1st Three Eigen Vectors */
        double[][] loadings = new double[3][A_CalcMqnAndProps.fplength];
        BufferedReader br = new BufferedReader(new FileReader(smio.getArg("-e")));
        for (int i = 0; i < loadings.length; i++) {
            String[] rarr = br.readLine().split(" ");
            System.err.println("Eigenwert " + i + ": " + rarr[0]);
            String[] larr = rarr[1].split(";");
            for (int j = 0; j < loadings[i].length; j++) {
                loadings[i][j] = Double.parseDouble(larr[j]);
            }
        }
        br.close();

        /*1st three PCs from file *.eWeV will be used to tranposed the molecule and to
         *produced the 3D data: 3rd PC will be used for sorting of molecules
         *in bins..
         */

        System.out.println("READING IN, TRANSPOSING TO 1st THREE PCs (ONLY) AND WRITING OUT");
        String s;
        double[] mmpc = new double[6];
        mmpc[0] = Double.MAX_VALUE;
        mmpc[1] = Double.MIN_VALUE;
        mmpc[2] = Double.MAX_VALUE;
        mmpc[3] = Double.MIN_VALUE;
        mmpc[4] = Double.MAX_VALUE;
        mmpc[5] = Double.MIN_VALUE;

        while ((s = smio.readLine()) != null) {
            smio.displayReadCounter(100000);
            String[] sarr = s.split(" ");
            String[] pcarr = sarr[1].split(";");
            double origvals[] = new double[A_CalcMqnAndProps.fplength];
            for (int i = 0; i < pcarr.length; i++) {
                origvals[i] = Double.parseDouble(pcarr[i]);
            }
            double[] newPC = new double[3];
            for (int i = 0; i < newPC.length; i++) {
                for (int j = 0; j < origvals.length; j++) {
                    newPC[i] += (origvals[j] * loadings[i][j]);
                }
            }
///////////////////////////////////////////////////////////////////////////////////////////////////
            //0=minPC1 1=maxPC1 2=maxPC2 3=maxPC2
            if (mmpc[0] > newPC[0]) {
                mmpc[0] = newPC[0];
            }
            if (mmpc[1] < newPC[0]) {
                mmpc[1] = newPC[0];
            }
            if (mmpc[2] > newPC[1]) {
                mmpc[2] = newPC[1];
            }
            if (mmpc[3] < newPC[1]) {
                mmpc[3] = newPC[1];
            }

            if (mmpc[4] > newPC[2]) {
                mmpc[4] = newPC[2];
            }
            if (mmpc[5] < newPC[2]) {
                mmpc[5] = newPC[2];
            }

            //write smi+tag newpc1;newpc2;newpc3 map-values mqns. pleas note the switch of sequence
            //sarr[2] then sarr[1] because sarr[2] is used in "G" and sarr[1] then only in "I"
            //kind of stupid I think now, but now its also too late...
            smio.writeLine(sarr[0] + " " + newPC[0] + ";" + newPC[1] + ";" + newPC[2] + " " + sarr[2] + " " + sarr[1]);
        }
        smio.end();

        System.out.println("WRITING MINMAX");
        FileWriter fw = new FileWriter(smio.getArg("-m"));
        fw.write(mmpc[0] + ";" + mmpc[1] + " " + mmpc[2] + ";" + mmpc[3] + " " + mmpc[4] + ";" + mmpc[5] + "\n");
        fw.close();
        System.out.println("END");
    }
}
