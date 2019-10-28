package ThreeDOrganizer2;

import static ThreeDOrganizer2.A_CalcMqnAndProps.fplength;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Merges the covariance matrix files so far and does the actual PCA by using
 * JSci functions. Writes an EigenWertEigenVector file which is important for
 * transposing molecules later. Created 09-Nov-2009
 *
 * @author lori
 */
public class D_MergeCovCalcEigen {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length == 0) {
            System.out.println("arg0=eWeV.dat arg1/2/3...=covsums.dat\n"
                    + "arg=0 output file with sorted eigenvalues + eigenvectors(;)\n"
                    + "arg>0 input files with no. Molecules (first line) + covariance sums(;) (second line)");
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

        System.out.println("READING COVSUM FILES");
        long noMolecules = 0;
        double[][] covsums = new double[A_CalcMqnAndProps.fplength][A_CalcMqnAndProps.fplength];
        for (int i = 1; i < args.length; i++) {
            System.out.println("READING FILE " + args[i]);
            BufferedReader br = new BufferedReader(new FileReader(args[i]));
            noMolecules += Long.parseLong(br.readLine());
            for (int j = 0; j < covsums.length; j++) {
                String[] sarr = br.readLine().split(";");
                for (int k = 0; k < covsums[j].length; k++) {
                    covsums[j][k] += Double.parseDouble(sarr[k]);
                }
            }
            br.close();
        }

        System.out.println("CALCULATING EIGENWERTE/EIGENVECTORS");
        /* calculate EV EW this is the main PCA stuff */
        JSci.maths.matrices.DoubleSquareMatrix ecovmatrix = new JSci.maths.matrices.DoubleSquareMatrix(A_CalcMqnAndProps.fplength);
        for (int i = 0; i < A_CalcMqnAndProps.fplength; i++) {
            for (int j = 0; j < A_CalcMqnAndProps.fplength; j++) {
                if (i == j) {
                    System.err.println(covsums[i][j] / (1.0 * noMolecules - 1.0)+" OKKK");
                }
                ecovmatrix.setElement(i, j, covsums[i][j] / (1.0 * noMolecules - 1.0));
            }
        }
        
        JSci.maths.vectors.DoubleVector[] evectors = new JSci.maths.vectors.DoubleVector[A_CalcMqnAndProps.fplength];
        double eigenvalues[] = null;
        
        try {
            eigenvalues = JSci.maths.LinearMath.eigenSolveSymmetric(ecovmatrix, evectors);
              
        } catch (JSci.maths.MaximumIterationsExceededException e) {
            System.err.println(e.toString());
            System.exit(69);
        }

        /* add the result to a TreeMap (sorts the data automatically!!!)*/
        TreeMap<Double, String> tm = new TreeMap<Double, String>();
        for (int e = 0; e < A_CalcMqnAndProps.fplength; e++) {
            tm.put(eigenvalues[e], evectors[e].toString().replace(",", ";"));
        }

        //write the eigenwerte and eigenvectors out into a file this file is quite important
        System.out.println("WRITING EWEV");
        FileWriter fw = new FileWriter(args[0]);
        //go through TreeMap. sorted by decreasing order.
        Iterator it = tm.descendingMap().entrySet().iterator();
        while (it.hasNext()) {
            Entry me = (Entry) it.next();
            fw.write(me.getKey() + " " + me.getValue() + "\n");
        }
        fw.close();
        System.out.println("END");
    }
}
