/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calcFP;
//==============================================================================
import chemaxon.descriptors.MDGeneratorException;
import chemaxon.struc.MolAtom;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import makeFP.FP06_sFP;
import processAndSplit.processMolecule;
//==============================================================================

/**
 * @author mahendra
 */
public class write_Sfp {

    public static void main(String args[]) throws IOException, MDGeneratorException {
//==============================================================================        
        BufferedReader br = null;
        if (args[0].endsWith(".gz")) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0]))));
        } else {
            br = new BufferedReader(new FileReader(args[0]));
        }

        Molecule m;
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
        String str;
        if (args[0].endsWith(".gz")) {
            bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(args[1]))));
        } else {
            bw = new BufferedWriter(new FileWriter(args[1]));
        }
//==============================================================================
        double counter = 0;
        while ((str = br.readLine()) != null) {

            String sarray[] = str.split(" ");
            String smi = sarray[0];

            //Process Molecule
            try {
                m = new MolHandler(smi).getMolecule();
            } catch (Exception e) {
                continue;
            }

            //Process Molecule
            processMolecule pm = new processMolecule(m);
            String isOK = pm.process(7.4);
            if (isOK == null) {
                continue;
            }

            FP06_sFP fp6 = new FP06_sFP();
            String sfp = fp6.generateSubStructureFP(pm.mol);
            if (sfp == null) {
                continue;
            }

            smi = pm.mol.toFormat("smiles:q0-H");

            bw.write(smi + " " + sarray[1] + " " + sarray[2] + " " + sfp + "\n");
            counter++;
            if (counter % 10000 == 0) {
                System.out.println(counter + " MOLECULES PROCESS");
            }
        }

        bw.close();
        br.close();
        System.out.println("END");
    }
//==============================================================================

    static double[][] getMolCoordinates(Molecule m) {

        /* get the coordinates */
        double xyz[][] = new double[m.getAtomCount()][3];
        for (int i = 0; i < m.getAtomCount(); i++) {
            MolAtom atom = m.getAtom(i);
            xyz[i] = new double[]{atom.getX(), atom.getY(), atom.getZ()};
        }

        return xyz;
    }

//==============================================================================
    public static String arrayToString(float array[], String seprator) {

        String str = "";
        for (int a = 0; a < array.length; a++) {
            str = str + array[a] + seprator;
        }

        str = str.substring(0, str.length() - 1);
        return str;
    }
//==============================================================================

    public static String arrayToString(int array[], String seprator) {

        String str = "";
        for (int a = 0; a < array.length; a++) {
            str = str + array[a] + seprator;
        }

        str = str.substring(0, str.length() - 1);
        return str;
    }
//==============================================================================

    public static String arrayToString(double array[], String seprator) {

        String str = "";
        for (int a = 0; a < array.length; a++) {
            str = str + array[a] + seprator;
        }

        str = str.substring(0, str.length() - 1);
        return str;
    }
//==============================================================================    
}
