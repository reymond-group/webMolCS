package processAndSplit;

import chemaxon.struc.MolAtom;
import chemaxon.struc.Molecule;
import java.util.ArrayList;
//////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Class will split the given molecule into the different sets of atoms
 * depending upon their pharmacological properties like HBA, HBD or HYDROPHOBIC
 * etc.
 *
 * @author mahendra
 */
public class splitCoordinates {

    /**
     * Creates object of class that assigns atoms.
     */
    private assignfunction af = new assignfunction();

    /**
     *
     * Method splits the atoms of given molecule in to the FOUR GROUPS. Name and
     * positions of these groups(in INT [][] array) are given below:
     *
     * 0. POLAR ATOMS (indices of polar atoms in molecule)
     *
     * 1. APOLAR ATOMS (indices of Apolar/HYDROPHOBIC atoms in molecule)
     *
     * 2. DONOR ATOMS (indices of donor atoms in molecule)
     *
     * 3. ACCEPTOR ATOMS (indices of acceptor atoms in molecule)
     *
     * @param mol
     * @return
     */
    public int[][] splitcoordinates(Molecule mol) {

        /*
         *
         * Define four arraylist to store the indices of atoms: 0. Polar atoms
         * 1. Apolar/Hydrophobic atoms 2. Donor atoms 3. Acceptor atoms
         *
         */

        ArrayList<Integer> sorted[] = new ArrayList[4];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = new ArrayList<Integer>();
        }

        /*
         * Get the assigned atoms: These sets contains the assigned list in
         * different order.VISIT THE af.getOriginalSet(); method!
         */
        ArrayList<MolAtom>[] assigned = af.assignfunction(mol);
        assigned = af.getOriginalSet();

        /*
         * rematch the original sort: "Please note that atom sets in assigned
         * variable (see above) are in differtent order: like 0. H-bond acceptor
         * 1. H-bond donor 2. HYDROPHOBICs: so we are sorting this list now, to
         * match our order: SEE THE METHOD HEADER!!
         */
        for (int a = 0; a < mol.getAtomCount(); a++) {

            /*
             * get the atom
             */
            MolAtom atom = mol.getAtom(a);

            /*
             * check in what list the atom occurs
             */
            if (assigned[0].contains(atom)) {
                /*
                 * atom is an acceptor: add its index
                 */
                sorted[3].add(a);
                sorted[0].add(a);
            }
            if (assigned[1].contains(atom)) {
                /*
                 * atom is a donor: add its index
                 */
                sorted[2].add(a);
                sorted[0].add(a);
            }
            if (assigned[2].contains(atom)) {

                /*
                 * Atom is hydrophobic: add its index
                 */
                sorted[1].add(a);
            }
        }

        /*
         * Transform the ArrayLists to an integer-matrix. It uses the identical
         * positions as the ArrayLists did. See method header for information.
         *
         */
        int sortedint[][] = new int[4][];
        for (int i = 0; i < sortedint.length; i++) {
            sortedint[i] = this.arrayListToIntArray(sorted[i]);
        }
        /*
         * Return the integer-matrix
         */
        return sortedint;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

    protected int[] arrayListToIntArray(ArrayList<Integer> list) {
        int array[] = new int[list.size()];
        for (int a = 0; a < array.length; a++) {
            array[a] = list.get(a);
        }
        return array;
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////
