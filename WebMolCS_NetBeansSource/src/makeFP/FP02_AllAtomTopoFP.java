/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package makeFP;

//==============================================================================
import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.marvin.io.MolExportException;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.MolBond;
import chemaxon.struc.Molecule;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
//==============================================================================

/**
 *
 * Making the FINGERPRINT
 *
 * @author mahendra
 *
 */
public class FP02_AllAtomTopoFP {

    DecimalFormat df = new DecimalFormat("#.######");
    Molecule mol;
    public int splittedAtomSets[][];
    static TopologyAnalyserPlugin TPAplugin = new TopologyAnalyserPlugin();
    public int shapeFPInt_10[];
    public int shapeFPInt_20[];
    public float shapeFP_10[];
    public float shapeFP_20[];

//==============================================================================
    /**
     * Constructor of CLASS
     *
     * @param mol
     * @param splittedAtomSets
     */
    public FP02_AllAtomTopoFP(Molecule mol) {
        this.mol = mol;
    }
//==============================================================================

    /**
     * CREATING FP
     *
     * @return
     * @throws MolExportException
     * @throws IOException
     */
    public String createFP() throws MolExportException, IOException {
        try {
            //set target molecule
            TPAplugin.setMolecule(mol);
        } catch (PluginException ex) {
            return null;
        }
        try {
            // run the calculation
            TPAplugin.run();
        } catch (PluginException ex) {
            return null;
        }

//==============================================================================
        //All Atom distance matrix
        int[][] distMatrix_AllAtoms = createTopologyDistMatrix(this.mol);

        //Count the distances for Hydrophobic-Hydrophobic matrix
        int[] countDistnaces_AllAtoms_20 = countDistnaces_halfMatrix(distMatrix_AllAtoms, 0, 20);

        //Normalized by size
        shapeFP_20 = normalized(countDistnaces_AllAtoms_20, distMatrix_AllAtoms.length);

        //Conver FP to Integer
        shapeFPInt_20 = floatToIntArray(shapeFP_20, 100);

        return "SuccessFullyCompleted";
    }
//==============================================================================

    /**
     *
     * Generate topology distance Matrix(for all the pairs in given matrix)
     * Consider only half of the matrix(matrix is same on the both side of
     * diagonal..for e.g A to B is same as B to A
     *
     * @param atomNumbers
     * @return
     *
     */
    int[][] createTopologyDistMatrix(int atomNumbers[]) {

        int k = 0;
        int distMatrix[][] = new int[atomNumbers.length][atomNumbers.length];
        for (int a = 0; a < atomNumbers.length; a++) {
            for (int b = 0; b <= k; b++) {
                if (a == b) {
                    distMatrix[a][b] = 0;
                    continue;
                }

                distMatrix[a][b] = TPAplugin.getShortestPath(atomNumbers[a], atomNumbers[b]);
            }
            k++;
        }
        return distMatrix;
    }
//==============================================================================

    /**
     *
     * Generate topology distance Matrix(for all the possible pairs between
     * members of two given matrixes), considering that two matrices are
     * different full matrix need to used(not the half).
     *
     * @param atomNumbersA
     * @param atomNumbersB
     * @return
     *
     */
    int[][] createTopologyDistMatrix(int atomNumbersA[], int atomNumbersB[]) {

        int distMatrix[][] = new int[atomNumbersA.length][atomNumbersB.length];
        for (int a = 0; a < atomNumbersA.length; a++) {
            for (int b = 0; b < atomNumbersB.length; b++) {
                distMatrix[a][b] = TPAplugin.getShortestPath(atomNumbersA[a], atomNumbersB[b]);
            }
        }
        return distMatrix;
    }
//==============================================================================

    /**
     *
     * Generate topology distance Matrix(for all the possible pairs in given
     * molecule), Consider only half of the matrix(matrix is same on the both
     * side of diagonal..for e.g A to B is same as B to A..
     *
     * @param mol
     * @return
     *
     */
    int[][] createTopologyDistMatrix(Molecule mol) {

        int atomCount = mol.getAtomCount();
        int k = 0;
        int distMatrix[][] = new int[atomCount][atomCount];
        for (int a = 0; a < atomCount; a++) {
            for (int b = 0; b <= k; b++) {
                if (a == b) {
                    distMatrix[a][b] = 0;
                    distMatrix[b][a] = 0;
                    continue;
                }
                distMatrix[a][b] = TPAplugin.getShortestPath(a, b);
                distMatrix[b][a] = distMatrix[a][b];
            }
            k++;
        }
        return distMatrix;
    }

//==============================================================================
    /**
     *
     * Method counts the number of times distances appear in given matrix. One
     * can specify the distance range with distanceMin, distanceMax.
     *
     * for e.g if you want to count how many time distances 2,3,4,5 appear in
     * matrix: Specify the distanceMin=2 and distanceMax=5
     *
     * @param distMatrix: matrix containing topological distances
     * @param distanceMin: Starting distance
     * @param distanceMax: Ending distance
     */
    int[] countDistnaces(int[][] distMatrix, int distanceMin, int distanceMax) {

        //At the max 2000 distance count(I hope no molecule will be bigger than this)
        int distanceCounts[] = new int[2000];
        for (int a = 0; a < distMatrix.length; a++) {
            for (int b = 0; b < distMatrix[a].length; b++) {
                distanceCounts[distMatrix[a][b]]++;
            }
        }

        int requiredDistRange[] = new int[(distanceMax - distanceMin) + 1];
        int k = 0;
        for (int a = distanceMin; a <= distanceMax; a++) {
            requiredDistRange[k] = distanceCounts[a];
            k++;
        }
        return requiredDistRange;
    }
//==============================================================================   

    /**
     *
     * IMPORTANT IT ONLY COUNTS LOWER HALF OF DIGONAL MATRIX!!
     *
     * Method counts the number of times distances appear in given matrix. One
     * can specify the distance range with distanceMin, distanceMax.
     *
     * for e.g if you want to count how many time distances 2,3,4,5 appear in
     * matrix: Specify the distanceMin=2 and distanceMax=5
     *
     * @param distMatrix: matrix containing topological distances
     * @param distanceMin: Starting distance
     * @param distanceMax: Ending distance
     */
    int[] countDistnaces_halfMatrix(int[][] distMatrix, int distanceMin, int distanceMax) {

        //At the max 2000 distance count(I hope no molecule will be bigger than this)
        int distanceCounts[] = new int[2000];
        int diagonalControl = 0;

        for (int a = 0; a < distMatrix.length; a++) {
            for (int b = 0; b <= diagonalControl; b++) {
                distanceCounts[distMatrix[a][b]]++;
            }
            diagonalControl++;
        }

        int requiredDistRange[] = new int[(distanceMax - distanceMin) + 1];
        int k = 0;
        for (int a = distanceMin; a <= distanceMax; a++) {
            requiredDistRange[k] = distanceCounts[a];
            k++;
        }
        return requiredDistRange;
    }
//==============================================================================

    /**
     * Convert array To string!
     *
     * @param array
     * @param seprator (field delimeter)
     * @return
     */
    public String arrayToString(float array[], String seprator) {

        String str = "";
        for (int a = 0; a < array.length; a++) {
            str = str + df.format(array[a]) + seprator;
        }

        str = str.substring(0, str.length() - 1);
        return str;
    }

    public String arrayToString(int array[], String seprator) {

        String str = "";
        for (int a = 0; a < array.length; a++) {
            str = str + array[a] + seprator;
        }

        str = str.substring(0, str.length() - 1);
        return str;
    }

//==============================================================================
    /**
     * Print the given matrix
     *
     * @param matrix (input matrix)
     * @param seperator (field delimeter)
     */
    void printMatrix(int matrix[][], String seperator) {

        for (int a = 0; a < matrix.length; a++) {
            for (int b = 0; b < matrix[a].length; b++) {

                if (b == matrix[a].length - 1) {
                    System.out.print(matrix[a][b]);
                    continue;
                }
                System.out.print(matrix[a][b] + seperator);
            }
            System.out.println();
        }
    }
//==============================================================================

    float[] normalized(int array[], int val) {

        float outArray[] = new float[array.length];

        for (int a = 0; a < array.length; a++) {

            if (array[a] > 0) {
                outArray[a] = (float) array[a] / (float) val;
            }
        }
        return outArray;
    }

    float[] normalized(int array[], float val) {

        float outArray[] = new float[array.length];

        for (int a = 0; a < array.length; a++) {

            if (array[a] > 0) {
                outArray[a] = (float) array[a] / (float) val;
            }
        }
        return outArray;
    }
//==============================================================================

    float[] mergeArrays(float fp1[], float fp2[]) {

        float fpOut[] = new float[fp1.length + fp2.length];
        for (int a = 0; a < fp1.length; a++) {
            fpOut[a] = fp1[a];
        }

        int startIndex = fp1.length;
        for (int a = 0; a < fp2.length; a++) {
            fpOut[startIndex] = fp2[a];
            startIndex++;
        }

        return fpOut;
    }
//==============================================================================

    /**
     *
     * Convert float array to integer after multiplication with given factor
     *
     * @param farray: floating array
     * @param multiply: multiplication factor
     *
     */
    int[] floatToIntArray(float farray[], int multiply) {
        int iarray[] = new int[farray.length];
        for (int a = 0; a < farray.length; a++) {
            iarray[a] = (int) (farray[a] * multiply);
        }
        return iarray;
    }
//==============================================================================

    int[] getSp3Atoms(Molecule mol) {

        ArrayList<Integer> atomIndexList = new ArrayList();

        try {
            mol.calcHybridization();
        } catch (Exception e) {

            return null;
        }

        for (int a = 0; a < mol.getAtomCount(); a++) {
            int hybridizationState = mol.getAtom(a).getHybridizationState();
            if (hybridizationState == 4) {
                atomIndexList.add(a);
            }
        }

        //Convert to Array
        int out[] = new int[atomIndexList.size()];
        for (int a = 0; a < atomIndexList.size(); a++) {
            out[a] = atomIndexList.get(a);
        }

        return out;
    }
//==============================================================================

    int[] getSp2Atoms(Molecule mol) {

        ArrayList<Integer> atomIndexList = new ArrayList();

        try {
            mol.calcHybridization();
        } catch (Exception e) {
            return null;
        }

        for (int a = 0; a < mol.getAtomCount(); a++) {
            int hybridizationState = mol.getAtom(a).getHybridizationState();
            if (hybridizationState == 3) {
                atomIndexList.add(a);
            }
        }

        //Convert to Array
        int out[] = new int[atomIndexList.size()];
        for (int a = 0; a < atomIndexList.size(); a++) {
            out[a] = atomIndexList.get(a);
        }

        return out;
    }
//==============================================================================

    int[] getSpAtoms(Molecule mol) {

        ArrayList<Integer> atomIndexList = new ArrayList();

        try {
            mol.calcHybridization();
        } catch (Exception e) {

            return null;
        }

        for (int a = 0; a < mol.getAtomCount(); a++) {
            int hybridizationState = mol.getAtom(a).getHybridizationState();
            if (hybridizationState == 2) {
                atomIndexList.add(a);
            }
        }

        //Convert to Array
        int out[] = new int[atomIndexList.size()];
        for (int a = 0; a < atomIndexList.size(); a++) {
            out[a] = atomIndexList.get(a);
        }

        return out;
    }
//==============================================================================

    int[] getSp2AtomsWithNearByHeteroatoms(Molecule mol) {

        HashMap<Integer, Integer> atomList_01 = new HashMap();

        try {
            mol.calcHybridization();
        } catch (Exception e) {
            return null;
        }

        for (int a = 0; a < mol.getAtomCount(); a++) {
            int hybridizationState = mol.getAtom(a).getHybridizationState();
            if (hybridizationState == 3) {
                atomList_01.put(a, a);
            }
        }

        //Now check for heteroatoms: Any hetero atoms attached to sp2 atom is sp2 itself 
        HashMap<Integer, Integer> atomList_02 = new HashMap();
        for (int a = 0; a < mol.getAtomCount(); a++) {

            if (atomList_01.containsKey(a)) {
                continue;
            }

            if ((mol.getAtom(a).getAtno()) == 6) {
                continue;
            }

            for (Integer key : atomList_01.keySet()) {
                MolBond bondTo = mol.getAtom(a).getBondTo(mol.getAtom(key));
                if (bondTo != null) {
                    atomList_02.put(a, a);
                }
            }
        }


        //Convert to Array
        int out[] = new int[atomList_01.size() + atomList_02.size()];

        int control = 0;
        for (Integer key : atomList_01.keySet()) {
            out[control] = key;
            control++;
        }

        for (Integer key : atomList_02.keySet()) {
            out[control] = key;
            control++;
        }

        return out;
    }

//==============================================================================
    int[] filterSp3Atoms(int sp2AtomList[], int sp3AtomList[]) {


        ArrayList<Integer> atomList = new ArrayList();

        for (int a = 0; a < sp3AtomList.length; a++) {

            boolean control = true;
            for (int b = 0; b < sp2AtomList.length; b++) {

                if (sp3AtomList[a] == sp2AtomList[b]) {
                    control = false;
                    break;
                }
            }

            if (control) {
                atomList.add(a);
            }
        }

        int out[] = new int[atomList.size()];
        for (int a = 0; a < atomList.size(); a++) {

            out[a] = atomList.get(a);
        }

        return out;
    }
//==============================================================================
}