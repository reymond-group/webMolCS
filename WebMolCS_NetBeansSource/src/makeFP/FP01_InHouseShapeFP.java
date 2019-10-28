/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package makeFP;

import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.marvin.io.MolExportException;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.MolAtom;
import chemaxon.struc.MolBond;
import chemaxon.struc.Molecule;
import java.io.IOException;
import java.util.ArrayList;
//////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * Making the FINGERPRINT
 *
 * @author mahendra
 *
 */
public class FP01_InHouseShapeFP {

    Molecule mol;
    public int splittedAtomSets[][];
    static TopologyAnalyserPlugin TPAplugin = new TopologyAnalyserPlugin();
    public float shapeFP[];
    public int shapeFPInt[];
    public int noOfHYBAtm = 0;
    public int noOfHBAtoms = 0;
    public int noOfHBDAtoms = 0;
    public int noOfSP2Atoms = 0;

//////////////////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Constructor of CLASS
     *
     * @param mol
     * @param splittedAtomSets
     */
    public FP01_InHouseShapeFP(Molecule mol, int splittedAtomSets[][]) {
        this.mol = mol;
        this.splittedAtomSets = splittedAtomSets;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////

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

        //////////////////////////////////////////////////////////////////////////////////////////////
        //Hydrophobic-Hydrophobic
        int[][] distMatrix_Hypb = createTopologyDistMatrix(this.splittedAtomSets[1]);

        //Donor-Acceptor
        int[][] distMatrix_Don_Accp = createTopologyDistMatrix(this.splittedAtomSets[2], this.splittedAtomSets[3]);

        //Donor-Donor
        int[][] distMatrix_Don_Don = createTopologyDistMatrix(this.splittedAtomSets[2]);

        //Acceptor-Acceptor
        int[][] distMatrix_Accp_Accp = createTopologyDistMatrix(this.splittedAtomSets[3]);

        //Atoms In Double Bond distance Matrix
        int[] atomsInDoubleBonds = atomsInDoubleBOND(mol);
        int[][] distMatrix_dbA_dbA = createTopologyDistMatrix(atomsInDoubleBonds);
        //////////////////////////////////////////////////////////////////////////////////////////////

        //Count the distances for Hydrophobic-Hydrophobic matrix
        int[] countDistnaces_Hypb = countDistnaces_halfMatrix(distMatrix_Hypb, 0, 10);

        //Count the distances for Donor_Acceptor matrix
        int[] countDistnaces_Don_Accp = countDistnaces(distMatrix_Don_Accp, 0, 10);

        //Count the distances for Donor_Donor matrix
        int[] countDistnaces_Don_Don = countDistnaces_halfMatrix(distMatrix_Don_Don, 0, 10);

        //Count the distances for All_All matrix
        int[] countDistnaces_Accp_Accp = countDistnaces_halfMatrix(distMatrix_Accp_Accp, 0, 10);

        //Count distances for atoms-connected by doble bonds
        int[] countDistnaces_dbA_dbA = countDistnaces_halfMatrix(distMatrix_dbA_dbA, 0, 10);
        //////////////////////////////////////////////////////////////////////////////////////////////

        //Normalized by size
        float[] normalized1 = normalized(countDistnaces_Hypb, distMatrix_Hypb.length);
        float[] normalized2 = normalized(countDistnaces_Don_Accp, distMatrix_Don_Accp.length);
        float[] normalized3 = normalized(countDistnaces_Don_Don, distMatrix_Don_Don.length);
        float[] normalized4 = normalized(countDistnaces_Accp_Accp, distMatrix_Accp_Accp.length);
        float[] normalized5 = normalized(countDistnaces_dbA_dbA, distMatrix_dbA_dbA.length);

        //Merge small fingerprint in to Big Shape FP
        shapeFP = mergeArrays(normalized1, normalized2, normalized3, setWts(normalized4, 2), normalized5);

        //Conver FP to Integer
        shapeFPInt = floatToIntArray(shapeFP, 100);

        //Put the atom Information========================
        noOfHYBAtm = distMatrix_Hypb.length;
        noOfHBDAtoms = this.splittedAtomSets[2].length;
        noOfHBAtoms = this.splittedAtomSets[3].length;
        noOfSP2Atoms = distMatrix_dbA_dbA.length;
        //================================================

        //You reached HERE GOOD!!
        return "SuccessFullyCompleted";
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////    

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
                    continue;
                }
                distMatrix[a][b] = TPAplugin.getShortestPath(a, b);
            }
            k++;
        }
        return distMatrix;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////    
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
/////////////////////////////////////////////////////////////////////////////////////////////////////    

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
/////////////////////////////////////////////////////////////////////////////////////////////////////

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
            str = str + array[a] + seprator;
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

/////////////////////////////////////////////////////////////////////////////////////////////////////    
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
/////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Merging Arrays to big one
     *
     * @param arrayA
     * @param arrayB
     * @param arrayC
     * @param arrayD
     * @param arrayE
     * @return
     */
    float[] mergeArrays(float arrayA[], float arrayB[], float arrayC[], float arrayD[], float arrayE[]) {

        ArrayList<Float> al = new ArrayList();

        for (int a = 0; a < arrayA.length; a++) {
            al.add(arrayA[a]);
        }

        for (int a = 0; a < arrayB.length; a++) {
            al.add(arrayB[a]);
        }

        for (int a = 0; a < arrayC.length; a++) {
            al.add(arrayC[a]);
        }

        for (int a = 0; a < arrayD.length; a++) {
            al.add(arrayD[a]);
        }

        for (int a = 0; a < arrayE.length; a++) {
            al.add(arrayE[a]);
        }

        float mergeArray[] = new float[al.size()];

        //list to array
        for (int a = 0; a < al.size(); a++) {
            mergeArray[a] = al.get(a);
        }
        return mergeArray;
    }

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
///////////////////////////////////////////////////////////////////////////////////////////////////// 

    /**
     * Method Returns SP2 ATOMS(atoms with double bonds)
     *
     * @param mol
     * @return
     */
    int[] atomsInDoubleBOND(Molecule mol) {


        //First make the list of atoms connected to double bond
        ArrayList<Integer> atomWithDoubleBonds = new ArrayList();

        for (int a = 0; a < mol.getAtomCount(); a++) {

            MolAtom atm = mol.getAtom(a);
            int bondCount = atm.getBondCount();
            for (int i = 0; i < bondCount; i++) {
                MolBond bond = atm.getBond(i);
                if (bond.getType() == 2) {
                    atomWithDoubleBonds.add(a);
                }
            }
        }

        int datoms[] = new int[atomWithDoubleBonds.size()];
        for (int a = 0; a < datoms.length; a++) {
            datoms[a] = atomWithDoubleBonds.get(a);
        }

        return datoms;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Set the weight
     *
     * @param array
     * @param wt
     * @return
     */
    float[] setWts(float array[], float wt) {

        for (int i = 0; i < array.length; i++) {

            array[i] = array[i] * wt;
        }

        return array;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////        

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
/////////////////////////////////////////////////////////////////////////////////////////////////////        
}