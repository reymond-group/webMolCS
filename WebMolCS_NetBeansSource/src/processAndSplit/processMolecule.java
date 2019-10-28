package processAndSplit;

import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.io.MolExportException;
import chemaxon.reaction.Standardizer;
import chemaxon.reaction.StandardizerException;
import chemaxon.struc.Molecule;
import java.io.IOException;

//==============================================================================
/**
 *
 * Class will process given molecule and make it ready for shape fingerprint
 * generation!!.
 *
 * @author mahendra
 */
public class processMolecule {

    public Molecule mol;
    public int splittedAtomSets[][];
    static MajorMicrospeciesPlugin mmp = new MajorMicrospeciesPlugin();

//==============================================================================
    public processMolecule(Molecule mol) {
        this.mol = mol;
    }
//==============================================================================    

    /**
     *
     * Process The given Molecule at given pH
     *
     * @param pH
     * @return
     * @throws MolExportException
     * @throws IOException
     */
    public String process(double pH) throws MolExportException, IOException {

        //DEAROMATRIZED, DEHYDROGENISED, VALENCE CHECK
        mol = harmonize(mol);
        if (mol == null) {
            return null;
        }

        //Take major species at Specified pH value
        mol = takeMjorSpecies(mol, pH);
        if (mol == null) {
            return null;
        }

        try {
            splitSets(mol);
        } catch (Exception e) {
            return null;
        }

        return "SuccessfullyProcess";
    }

    public String splitSets(Molecule m) {

        try {
            mol = m;
            splitCoordinates sc = new splitCoordinates();
            splittedAtomSets = sc.splitcoordinates(mol);
            return "SuccessfullyProcess";
        } catch (Exception e) {
            return null;
        }
    }
//==============================================================================
    /**
     * Process Given Molecule without pH setting
     *
     * @return
     * @throws MolExportException
     * @throws IOException
     */
    static Standardizer st;

    public String process() throws MolExportException, IOException, StandardizerException {

        //DEAROMATRIZED, DEHYDROGENISED, VALENCE CHECK
        mol = harmonize(mol);

        if (mol == null) {
            return null;
        }

        //Neutralized Molecule
        st = new Standardizer("neutralize");
        try {
            mol = st.standardize(mol);
        } catch (Exception ex) {
            return null;
        }

        return "SuccessfullyProcess";
    }
//==============================================================================

    /**
     * Method generates the major species for given molecule
     *
     * @param mol
     * @param pH
     * @return
     */
    Molecule takeMjorSpecies(Molecule mol, double pH) {
        try {
            mmp.setMolecule(mol);
            mmp.setpH(pH);
            mmp.run();
            mol = mmp.getMajorMicrospecies();
            return mol;
        } catch (Exception e) {
            e.toString();
            try {
                System.err.println("MMP ERROR " + mol.toFormat("smiles"));
                return null;
            } catch (Exception e1) {
                System.err.println("MMP ERROR ");
                return null;
            }
        }
    }
//==============================================================================

    /**
     *
     * CHECK FOR THE VALENCE ERROR in MOLECULE
     *
     * @param mol
     * @return
     *
     */
    boolean valenceCheck(Molecule mol) {

        mol.valenceCheck();
        if (mol.hasValenceError()) {
            return false;
        }
        return true;
    }
//============================================================================== 

    /**
     * DO VALENCE CHECK, DEAROMATIZED and DEHYDROGENISED MOLECULE
     *
     * @param m
     * @return
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
        m.aromatize();
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
//==============================================================================    
}