/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myKMST;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * ==============================================================================
 *
 * @author mahendra
 * ==============================================================================
 */
public class runIt {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //======================================================================
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String data = br.readLine();
        String sarray[] = data.split(";");
        int coordMatrix[][] = new int[sarray.length][3];

        for (int a = 0; a < sarray.length; a++) {

            String sarraySplit[] = sarray[a].split("_");
            int x = Integer.valueOf(sarraySplit[0]);
            int y = Integer.valueOf(sarraySplit[1]);
            int z = Integer.valueOf(sarraySplit[2]);

            coordMatrix[a][0] = x;
            coordMatrix[a][1] = y;
            coordMatrix[a][2] = z;
        }

        //======================================================================
        MSST mst = new MSST();
        int[][] calcDistMatrix = calcDistMatrix(coordMatrix);
        mst.getMST(calcDistMatrix);
    }
//==============================================================================
    //calculate distance matriy for given coordinates

    static int[][] calcDistMatrix(int[][] coord) {

        int distMatrix[][] = new int[coord.length][coord.length];
        for (int a = 0; a < coord.length; a++) {

            for (int b = 0; b < coord.length; b++) {
                distMatrix[a][b] = calCityBlockDistance(coord[a], coord[b]);
            }
        }
        return distMatrix;
    }
//==============================================================================    

    static int calCityBlockDistance(int refFP[], int queryFP[]) {

        float cbd = 0;
        for (int a = 0; a < refFP.length; a++) {
            cbd = cbd + Math.abs(refFP[a] - queryFP[a]);
        }
        return (int) cbd;
    }
//==============================================================================
    static void printMatrix(int[][] matrix) {
        for (int a = 0; a < matrix.length; a++) {

            for (int b = 0; b < matrix[a].length; b++) {

                System.out.print(matrix[a][b] + "\t");
            }
            System.out.println("");
        }
    }
//==============================================================================
}
