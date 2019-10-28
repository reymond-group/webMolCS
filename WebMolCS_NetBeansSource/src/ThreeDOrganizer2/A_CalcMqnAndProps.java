package ThreeDOrganizer2;

import calcFP.write_AllInOne;
import chemaxon.license.LicenseManager;
import chemaxon.license.LicenseProcessingException;
import chemaxon.marvin.calculations.HBDAPlugin;
import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.struc.*;
import chemaxon.util.MolHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import organize.tools.SMIReadWriter;

public class A_CalcMqnAndProps {

    public static int fplength = 0;
    static HBDAPlugin hbdap = new HBDAPlugin();
    static MajorMicrospeciesPlugin mmp = new MajorMicrospeciesPlugin();
    static TopologyAnalyserPlugin tap = new TopologyAnalyserPlugin();
    static double[] sums = new double[fplength];
    static long okMolCount = 0;

    public static void main(String[] args) throws IOException {

        try {
            LicenseManager.setLicenseFile("license.cxl");
        } catch (LicenseProcessingException ex) {
            Logger.getLogger(write_AllInOne.class.getName()).log(Level.SEVERE, null, ex);
        }

        SMIReadWriter smio = new SMIReadWriter(
                "usage: -i smiles[.gz] -o mqns[.gz] -s sums.dat [-m]\n"
                + "-i input file with smiles (with space separated tag)\n"
                + "-o output file with smiles + mqn(;) + csaproperties(;)\n"
                + "-s output file with sum of mqn values(;) + no molecules\n"
                + "[-m calculates major species at pH7.4]\n"
                + "dehydrogenize & dearomatize is done by default", args);

        System.out.println("IN: " + smio.getArg("-i") + " OUT: " + smio.getArg("-o")
                + " SUMS: " + smio.getArg("-s") + " MMS: " + smio.isArg("-m"));

        System.out.println("READING IN, CALCULATING MQNS AND PROPERTIES, SUMMING MQNS AND WRITING OUT. HARM WITH VALENCE");

        String res;
        String s;
        //======================================================================
        //This is pactch to get number of dimentions, which is equal to number of ref.mols
        int noOfrefMols = 0;
        String st;
        BufferedReader reader = new BufferedReader(new FileReader("DB.smi"));
        String sarray[] = reader.readLine().split(" ");
        fplength = sarray[3].split(";").length;
        sums = new double[fplength];
        reader.close();
        //======================================================================

        //read input file and annotate with mqns and properties
        while ((s = smio.readLine()) != null) {
            smio.displayReadCounter(1000);
            String[] sarr = s.split(" ");

            Molecule m;
            try {
                m = new MolHandler(sarr[0]).getMolecule();
            } catch (Exception e) {
                continue;
            }

            //calculate values for that thing
            if ((res = getProps(m)) == null) {
                continue;
            }

            addSums(sarr[2].split(";"));

            //[0]=smiles; [1]=tag; [2]=simfp; [3]=simtoQuery;MaxSimToRef; res=additional props for colors 
            smio.writeLine(sarr[0] + ";" + sarr[1] + " " + sarr[2] + " " + sarr[3] + ";" + res);
        }
        smio.end();

        //write out the sums of the mqn values plus the number of processed molecules
        //for calculating the average in the next step B_MergeSums
        System.out.println("WRITING SUMS");
        FileWriter fw = new FileWriter(smio.getArg("-s"));
        for (int i = 0; i < sums.length; i++) {
            fw.write(sums[i] + ";");
        }
        fw.write(" " + okMolCount + "\n");
        fw.close();
        System.out.println("END");
    }

    static void addSums(String fp[]) {
        for (int a = 0; a < sums.length; a++) {
            sums[a] = sums[a] + Double.valueOf(fp[a]);
        }
        okMolCount++;
    }
    /*This code is a mixture of the calcMQN method from the CMC original paper plus some more lines
     for the ASF map properties (logP TPSA rigatoms...) */

    public static String getProps(Molecule m) {

        //initialize everything
        try {
            hbdap.setMolecule(m);
            hbdap.run();
            tap.setMolecule(m);
            tap.run();
        } catch (Exception e) {
            e.toString();
            System.err.println("CalcPlugin Error " + m.toFormat("smiles"));
            return null;
        }


        /* THIS CODE IS MOSTLY COPIED FROM MQN PAPER */
        //Classic descriptors
        int thac = m.getAtomCount();
        int hbd = hbdap.getDonorAtomCount();
        int hba = hbdap.getAcceptorAtomCount();
        int rbc = tap.getRotatableBondCount();
        int ringc = tap.getRingCount();
        int noOfHeteroatoms = 0;
        int aromC = tap.getAromaticAtomCount();
        double fHetero = 0;
//        double fsp3 = tap.getFsp3();
        double fsp3 = 0;
        double farom = (double) tap.getAromaticAtomCount() / (double) m.getAtomCount();


        for (int a = 0; a < m.getAtomCount(); a++) {

            if (m.getAtom(a).getAtno() != 6) {
                noOfHeteroatoms++;
            }
        }

        fHetero = (double) noOfHeteroatoms / (double) m.getAtomCount();
        return thac + ";" + hbd + ";" + hba + ";" + rbc + ";" + ringc + ";" + noOfHeteroatoms + ";" + aromC + ";" + fHetero + ";" + fsp3 + ";" + farom;
    }
}
