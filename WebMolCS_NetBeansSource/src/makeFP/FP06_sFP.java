/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package makeFP;

import chemaxon.marvin.calculations.ElementalAnalyserPlugin;
import chemaxon.marvin.calculations.HBDAPlugin;
import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.calculations.TPSAPlugin;
import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.marvin.calculations.logPPlugin;
import chemaxon.descriptors.CFParameters;
import chemaxon.descriptors.ChemicalFingerprint;
import chemaxon.descriptors.MDGeneratorException;
import chemaxon.struc.*;
import java.io.File;

/**
 *
 * @author mahendra
 */
public class FP06_sFP {

    static HBDAPlugin hbdap = new HBDAPlugin();
    static MajorMicrospeciesPlugin mmp = new MajorMicrospeciesPlugin();
    static TopologyAnalyserPlugin tap = new TopologyAnalyserPlugin();
    static ElementalAnalyserPlugin eap = new ElementalAnalyserPlugin();
    static logPPlugin lpp = new logPPlugin();
    static TPSAPlugin psap = new TPSAPlugin();
    static double okMolCount = 0;
    static String config = "cfp1024-7-2.xml";

    /*
     * generate substructure fingerPrint
     */
    public String generateSubStructureFP(Molecule mol) throws MDGeneratorException {

        /*
         * Use config-file for sFP configuration. (e.g. bitlength, bondcount and
         * bitcount)
         */
        CFParameters cfp = new CFParameters();
        cfp.setParameters(new File(config));
        ChemicalFingerprint cf = new ChemicalFingerprint(cfp);

        cf.generate(mol);
        float[] fp = cf.toFloatArray();
        String sFP = "";



        for (int i = 0; i < fp.length; i++) {
            /*
             * Write all Bits in a row.
             */
            sFP += "" + (int) fp[i] + ";";
        }
        return sFP;
    }

    /*
     * Do a valence check, dehydrogenize and dearomatize mol
     */
    public static Molecule harmonize(Molecule m) {
        m.valenceCheck();
        if (m.hasValenceError()) {

            try {
                System.err.println("VALENCE ERROR " + m.toFormat("smiles"));
                return null;
            } catch (Exception e) {
                System.err.println("VALENCE ERROR ");
                return null;
            }
        }

        m.hydrogenize(false);
        if (!m.dearomatize()) {

            try {
                System.err.println("DEAROMATIZE ERROR " + m.toFormat("smiles"));
                return null;
            } catch (Exception e) {
                System.err.println("DEAROMATIZE ERROR ");
                return null;
            }
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

            try {
                System.err.println("MMP ERROR " + m.toFormat("smiles"));
                return null;
            } catch (Exception e1) {
                System.err.println("MMP ERROR  ");
                return null;
            }
        }
        return m;
    }
}
