package processAndSplit;

import chemaxon.marvin.calculations.HBDAPlugin;
import chemaxon.struc.MolAtom;
import chemaxon.struc.MolBond;
import chemaxon.struc.Molecule;
import java.util.ArrayList;

//////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Class determines the pharmacological properties of atoms in given molecule.
 * It creates 5 different sets of atoms, which are then used for fingerprint
 * generation!!
 *
 * @author Mahendra
 */
public class assignfunction {

    /**
     * Constructor of class(by default no ionization)
     */
    public assignfunction() {
    }
    /**
     * Hydrogen bond donor acceptor function
     */
    private HBDAPlugin hbda = new HBDAPlugin();
    /**
     * pH used for ionization
     */
    private double pH = 7.4;

    /**
     * Constructor with direct settings of pH.
     *
     * @param pH
     */
    public assignfunction(double pH) {
        this.pH = pH;
        hbda.setpH(pH);
    }
    /**
     * Assembly of ArrayLists for the storage of the atom sets.
     */
    private ArrayList<MolAtom>[] assigned = null;
//////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method assigns the properties to the atoms. The TreeSet has the following
     * six categories. 0. Polar atoms 1. H-bond acceptors 2. H-bond donors 3.
     * Hydrophobic atoms 4. SP2-carbons 5. SP3-carbons
     *
     * @param molecule
     * @return
     */
    public ArrayList<MolAtom>[] assignfunction(Molecule molecule) {
        /*
         * add the molecule to the hbda-plugin
         */
        boolean hbdaok = true;
        try {
            hbda.setMolecule(molecule);
            hbda.run();
        } catch (Exception e) {
            /*
             * an exception occured: so
             */
            hbdaok = false;
        }

        /*
         * Initiate the matrix of ArrayList<MolAtom> for the MolAtoms
         */
        assigned = new ArrayList[6];
        for (int i = 0; i < assigned.length; i++) {
            assigned[i] = new ArrayList<MolAtom>();
        }

        /*
         * calculate the hybridisation state
         */
        molecule.calcHybridization();
        molecule.valenceCheck();

        /*
         * assign the function to an atom
         */
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            MolAtom atomi = molecule.getAtom(i);
            int atno = atomi.getAtno();

            /*
             * get the counts for H-bond donor and acceptor for this atom. Try
             * to get them from the plugin. If this failed use the lone pair
             * count and the hydrogen count on the atom!
             */
            int acccount = (hbdaok) ? hbda.getAcceptorCount(i) : atomi.getLonePairCount();
            int doncount = (hbdaok) ? hbda.getDonorCount(i) : atomi.getImplicitHcount() + atomi.getExplicitHcount();

            /*
             * get the valence and electrons
             */
            int hs = atomi.getHybridizationState();

            /*
             * assigned the atoms according to some rules
             */
            if (this.isHalogen(atno)) {
                /*
                 * add the atoms to categories 3 (hydrophobic) and 5
                 * (sp3-carbons)
                 */
                this.assignAtom(atomi, assigned, 3);
                this.assignAtom(atomi, assigned, 5);
                continue;
            }
            /*
             * assign a nitrogen
             */
            if (atno == 7) {
                /*
                 * check if the atom is an acceptor
                 */
                if (acccount > 0) {
                    this.assignAtom(atomi, assigned, 0);
                    /*
                     * nitrogen has acceptor properties
                     */
                    if (!this.isAttachedToCarbonyl(atomi)) {
                        /*
                         * explicitely exclude amides
                         */
                        this.assignAtom(atomi, assigned, 1);
                    }
                }

                /*
                 * check if atom is a donor
                 */
                if (doncount > 0) {
                    this.assignAtom(atomi, assigned, 0);
                    /*
                     * nitrogen has donor properties
                     */
                    this.assignAtom(atomi, assigned, 2);
                }

                /*
                 * check if no properties at all
                 */
                if (acccount == 0 && doncount == 0) {
                    this.assignAtom(atomi, assigned, 3);
                    if (this.isAttachedToCarbonyl(atomi)) {
                        this.assignAtom(atomi, assigned, 4);
                    } else {
                        this.assignAtom(atomi, assigned, 5);
                    }
                }

                /*
                 * continue with next atom
                 */
                continue;
            }
            if (atno == 8) {
                /*
                 * check if atom is a donor
                 */
                if (doncount > 0) {
                    /*
                     * atom is a donor
                     */
                    this.assignAtom(atomi, assigned, 0);
                    this.assignAtom(atomi, assigned, 2);
                }

                /*
                 * check if an atom is an acceptor
                 */
                if (acccount > 0) {
                    /*
                     * atom is an acceptor: make sure it is not an ester
                     */
                    if (!atomi.isTerminalAtom()) {
                        if (this.isAttachedToCarbonyl(atomi)) {
                            /*
                             * go to the next atom
                             */
                            continue;
                        }
                    }

                    /*
                     * atom survived: add to acceptor
                     */
                    this.assignAtom(atomi, assigned, 1);
                }

                /*
                 * if no properties at all: assign to sp3-carbons
                 */
                if (acccount == 0 && doncount == 0) {
                    if (this.isAttachedToCarbonyl(atomi)) {
                        this.assignAtom(atomi, assigned, 3);
                        this.assignAtom(atomi, assigned, 5);
                    }
                }

                /*
                 * go to the next atom
                 */
                continue;
            }

            /*
             * Assign sulphur atoms
             */
            if (atno == 16) {
                if (acccount > 0) {
                    /*
                     * add to acceptors
                     */
                    this.assignAtom(atomi, assigned, 0);
                    this.assignAtom(atomi, assigned, 1);
                }
                if (doncount > 0) {
                    /*
                     * add to donors
                     */
                    this.assignAtom(atomi, assigned, 0);
                    this.assignAtom(atomi, assigned, 2);
                }
                if (acccount == 0 && doncount == 0) {
                    /*
                     * atom is assymetric
                     */
                    this.assignAtom(atomi, assigned, 3);
                    this.assignAtom(atomi, assigned, 5);
                }

                /*
                 * go to the next atom
                 */
                continue;
            }

            /*
             * assign phosphor atoms
             */
            if (atno == 15) {
                /*
                 * phosphor can never be an acceptor/donor - add to sp3-carbons
                 */
                this.assignAtom(atomi, assigned, 3);
                this.assignAtom(atomi, assigned, 5);

                /*
                 * go to next atom
                 */
                continue;
            }

            /*
             * check if atom is a carbon
             */
            if (atno == 6) {
                /*
                 * atom is carbon: add to apolar
                 */
                this.assignAtom(atomi, assigned, 3);

                if (hs == MolAtom.HS_SP3) {
                    /*
                     * atom is an sp3-carbon: add to category 3
                     */
                    this.assignAtom(atomi, assigned, 5);
                } else {
                    this.assignAtom(atomi, assigned, 4);
                }

                /*
                 * go to next atom
                 */
                continue;
            }

            /*
             * any other atom: add to hydrophics and sp3-atoms
             */
            this.assignAtom(atomi, assigned, 3);
            this.assignAtom(atomi, assigned, 5);

        }
        /*
         * return the lists
         */
        return assigned;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method returns the original set of atoms that was originally used for the
     * translational-LOS. The list have the following order: 0. H-bond acceptor
     * 1. H-bond donor 2. HYDROPHOBICs
     *
     * @return
     */
    public ArrayList<MolAtom>[] getOriginalSet() {
        ArrayList<MolAtom>[] original = new ArrayList[3];
        original[0] = assigned[1];
        original[1] = assigned[2];
        original[2] = assigned[3];
        return original;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method tells if an atom is a halogen
     *
     * @param atom
     * @return
     */
    private boolean isHalogen(int atno) {
        int halogen[] = new int[]{9, 17, 35, 53};
        for (int i = 0; i < halogen.length; i++) {
            if (atno == halogen[i]) {
                /*
                 * atom is a halogen: return true
                 */
                return true;
            }
        }
        /*
         * atom was no halogen
         */
        return false;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method checks if an atom is attached to a carbonyl group.
     *
     * @param atom
     * @return
     */
    private boolean isAttachedToCarbonyl(MolAtom atom) {
        /*
         * check all bonds for this atom
         */
        for (int i = 0; i < atom.getBondCount(); i++) {
            MolBond bondi = atom.getBond(i);

            /*
             * only consider single bonds
             */
            if (bondi.getType() == 1) {
                MolAtom otheri = bondi.getOtherAtom(atom);

                /*
                 * attached atom has to be carbon
                 */
                if (otheri.getAtno() == 6) {

                    /*
                     * check all bonds of this atom
                     */
                    for (int j = 0; j < otheri.getBondCount(); j++) {
                        MolBond bondj = otheri.getBond(j);

                        /*
                         * bond has to be double to be part of carbonyl
                         */
                        if (bondj.getType() == 2) {

                            /*
                             * check if the other atom is an oxygen
                             */
                            int atnoj = bondj.getOtherAtom(otheri).getAtno();
                            if (atnoj == 7 || atnoj == 8 || atnoj == 16) {
                                /*
                                 * group is carbonyl
                                 */
                                return true;
                            }
                        }
                    }
                }
            }
        }

        /*
         * survived till here: so not attached
         */
        return false;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method assigns the atoms to the correct ArrayList.
     *
     * @param atomi
     * @param assigned
     * @param category
     */
    private void assignAtom(MolAtom atomi, ArrayList<MolAtom> assigned[], int category) {
        assigned[category].add(atomi);
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////