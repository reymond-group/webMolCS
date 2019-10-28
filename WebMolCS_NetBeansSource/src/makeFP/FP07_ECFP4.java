
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
package makeFP;

import chemaxon.descriptors.ECFP;
import chemaxon.descriptors.ECFPParameters;
import chemaxon.descriptors.MDGeneratorException;
import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import java.io.File;

/**
 *
 * @author mahendra
 */
public class FP07_ECFP4 {

    ////////////////////////////////////////////////////////////////////////////
    static String config = "ecfp.xml";
    static MajorMicrospeciesPlugin mmp = new MajorMicrospeciesPlugin();
    public String processSmile = "";
    public Molecule processMol = null;
    ECFPParameters ecPARAM;
    String molName = null;
    ////////////////////////////////////////////////////////////////////////////

    public FP07_ECFP4() {

        try {
            ecPARAM = new ECFPParameters();
            ecPARAM.setParameters(new File(config));
        } catch (Exception e) {
            System.out.println("FATAL ERROR: PROBLEM IN CONFIG FILE");
            System.out.println("EXITING!!");
            System.exit(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public String make_StringECFP(String smi) {

        //Process Molecule//
        processMol = processMolecule(smi);
        if (processMol == null) {
            return null;
        }

        ECFP fp = new ECFP(ecPARAM);
        try {
            fp.generate(processMol);
        } catch (MDGeneratorException ex) {
            return null;
        }

        processSmile = processMol.toFormat("smiles:0q-H");
        String tags[] = smi.split(" ");
        if (tags.length > 1) {
            molName = tags[1];
        } else {
            molName = "NoNAME";
        }

        String strFP = fp.toBinaryString();
        strFP = strFP.replace("|", "");
        String outFP = strFP.charAt(0) + "";

        for (int a = 1; a < strFP.length(); a++) {

            String CHAR = strFP.charAt(a) + "";
            outFP = outFP + ";" + CHAR;
        }

        return outFP;
    }

    /////////////////////////////////////////////////////////////////////////////
    int[] make_BitArrayECFP(String smi) {

        //Process Molecule//
        processMol = processMolecule(smi);
        if (processMol == null) {
            return null;
        }

        ECFP fp = new ECFP(ecPARAM);
        try {
            fp.generate(processMol);
        } catch (MDGeneratorException ex) {
            return null;
        }

        processSmile = processMol.toFormat("smiles:0q");
        String tags[] = smi.split(" ");
        if (tags.length > 1) {
            molName = tags[1];
        } else {
            molName = "NoNAME";
        }

        String strFP = fp.toBinaryString();
        strFP = strFP.replace("|", "");

        int[] outFP = new int[strFP.length()];
        outFP[0] = Integer.valueOf(strFP.charAt(0) + "");

        for (int a = 1; a < strFP.length(); a++) {

            String CHAR = strFP.charAt(a) + "";
            outFP[a] = Integer.valueOf(CHAR);
        }

        return outFP;
    }

    ////////////////////////////////////////////////////////////////////////////    
    Molecule processMolecule(String smi) {
        Molecule mol;

        try {
            mol = new MolHandler(smi).getMolecule();
            Molecule[] convertToFrags = mol.convertToFrags();
            int max = 0;
            int atmcount = 0;
            int winner = 0;
            for (int i = 0; i < convertToFrags.length; i++) {
                atmcount = convertToFrags[i].getAtomCount();
                if (atmcount > max) {
                    max = atmcount;
                    winner = i;
                }
            }
            mol = convertToFrags[winner];

        } catch (Exception e) {
            return null;
        }

        if (mol == null) {
            return null;
        }

        /*
         * process the molecule: if it fails return null!
         */
        if ((mol = majorSpecies(mol)) == null) {
            return null;
        }

        //dehydro & dearom molecule, if it fails return null
        if ((mol = harmonize(mol)) == null) {
            return null;
        }

        return mol;
    }

    /*
     * Do a valence check, dehydrogenize and dearomatize mol
     */
    public static Molecule harmonize(Molecule m) {
        m.valenceCheck();
        if (m.hasValenceError()) {
            System.err.println("VALENCE ERROR ");
            return null;
        }

        m.hydrogenize(false);
        m.aromatize();
        if (!m.dearomatize()) {
            System.err.println("DEAROMATIZE ERROR ");
            return null;
        }
        return m;
    }

    /*
     * to pH 7.4
     */
    public static Molecule majorSpecies(Molecule m) {
        try {
            mmp.setMolecule(m);
            mmp.setpH(7.4);
            mmp.run();
            m = mmp.getMajorMicrospecies();
        } catch (Exception e) {
            e.toString();
            System.err.println("MMP ERROR");
            return null;
        }
        return m;
    }
    ////////////////////////////////////////////////////////////////////////////    
}
