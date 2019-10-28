package ThreeDOrganizer;

import static ThreeDOrganizer.A_CalcMqnAndProps.fplength;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import organize.tools.SMIReadWriter;

/**
 * This is the first actual step of a PCA calculation: The mean centralization.
 * From all MQN values the avg is subtracted (actually to have the
 * value-distribution-gauss around 0. see pca tutorial to see exactly why.)
 * After each mean centralisation the sums required later for the covariance
 * matrix are calculated, these sums are then written out in the .covsums file.
 * Created 09-Nov-2009
 *
 * @author lori
 */
public class C_MeanCentralSumCov {

    public static void main(String[] args) throws IOException {


        SMIReadWriter smio = new SMIReadWriter(
                "usage: -i smimqnpropfile[.gz] -o meancentralized[.gz] -a totavgs.dat -c covsums.dat\n"
                + "-i input file with smiles mqn and CSAproperties\n"
                + "-o output file with smiles + meancentralized mqns + csaproperties\n"
                + "-a input file with total averages\n"
                + "-c output file with no. Molecules (first line) + covariance sums(;) (second line)", args);

        System.out.println("IN: " + smio.getArg("-i") + " OUT: " + smio.getArg("-o") + " TOTALAVGS: " + smio.getArg("-a") + " COVSUMS: " + smio.getArg("-c"));

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

        //First read in averages
        System.out.println("READING TOTALAVGS");
        BufferedReader br = new BufferedReader(new FileReader(smio.getArg("-a")));
        String[] avgarr = br.readLine().split(";");
        br.close();
        double[] avgs = new double[A_CalcMqnAndProps.fplength];
        for (int i = 0; i < avgs.length; i++) {
            avgs[i] = Double.parseDouble(avgarr[i]);
        }

        String s;
        System.out.println("READING IN, SUMMING COVARIANCES, CENTRALIZING AND WRITING OUT");
        double[][] covsums = new double[A_CalcMqnAndProps.fplength][A_CalcMqnAndProps.fplength];
        while ((s = smio.readLine()) != null) {
            smio.displayReadCounter(100000);
            String[] sarr = s.split(" ");
            //Substract avg from mqns
            String[] mqns = sarr[1].split(";");
            double[] mqnd = new double[A_CalcMqnAndProps.fplength];
            String ret = "";
            for (int i = 0; i < mqns.length; i++) {
                mqnd[i] = Double.parseDouble(mqns[i]);
                ret += mqnd[i] - avgs[i] + ";";
            }

            //sum up for covariances
            for (int i = 0; i < covsums.length; i++) {
                covsums[i][i] += (mqnd[i] - avgs[i]) * (mqnd[i] - avgs[i]);
                for (int j = (i + 1); j < covsums.length; j++) {
                    double prod = (mqnd[i] - avgs[i]) * (mqnd[j] - avgs[j]);
                    covsums[i][j] += prod;
                    covsums[j][i] += prod;
                }
            }
            //write back smi, substracted mqns, csaproperties
            smio.writeLine(sarr[0] + " " + ret + " " + sarr[2]);
        }
        smio.end();

        System.out.println("WRITING COVSUMS");
        FileWriter fw = new FileWriter(smio.getArg("-c"));
        fw.write(smio.getReadCounter() + "\n");
        for (int i = 0; i < covsums.length; i++) {
            for (int j = 0; j < covsums.length; j++) {
                fw.write(covsums[i][j] + ";");
            }
            fw.write("\n");
        }
        fw.close();
        System.out.println("END");
    }
}
