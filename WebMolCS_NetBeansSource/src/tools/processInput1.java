package tools;

import chemaxon.formats.MolFormatException;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *
 */
public class processInput1 {

    static ArrayList<String> al1 = new ArrayList();
    static ArrayList<String> al2 = new ArrayList();

    public static void main(String args[]) throws IOException {

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        //Checked for unqie smiles of compounds
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String str;
        HashMap<String, String> hm = new HashMap();
        ArrayList<String> cpds = new ArrayList();

        while ((str = br.readLine()) != null) {

            String smol[] = str.split(" ");
            if (smol.length < 3) {
                continue;
            }

            if (smol[0].isEmpty()) {
                continue;
            }

            if (smol[1].isEmpty()) {
                continue;
            }

            if (smol[2].isEmpty()) {
                continue;
            }

            try {
                Double val = Double.valueOf(smol[2]);
                if (val == null || val.isNaN()) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            Molecule mol;
            try {
                mol = new MolHandler(smol[0]).getMolecule();
            } catch (MolFormatException ex) {
                continue;
            }

            if (mol == null) {
                continue;
            }

            String smi = mol.toFormat("smiles:q0-H");
            if (hm.containsKey(smi)) {
                continue;
            } else {

                cpds.add(str);
                hm.put(smi, str);
            }
        }
        br.close();
        //======================================================================

        for (int a = 0; a < cpds.size(); a++) {

            str = cpds.get(a);
            String smol[] = str.split(" ");
            if (smol.length < 3) {
                continue;
            }

            Double val = Double.valueOf(smol[2]);
            if (val == null || val.isNaN()) {
                continue;
            }

            if (val < min) {
                min = val;
            }

            if (val > max) {
                max = val;
            }

            smol[0] = smol[0].replaceAll("\\r$", "");
            smol[1] = smol[1].replaceAll("\\r$", "");
            smol[2] = smol[2].replaceAll("\\r$", "");
            smol[1] = smol[1].split("_")[0];
            smol[1] = smol[1].split(";")[0];

            al1.add(smol[0] + " " + smol[1] + " " + smol[2]);
        }
        //======================================================================        
        //Scale scores to 0-1 (1==high similarity, 0=low similarity)
        if (max > min && max != 0) {
            for (int a = 0; a < al1.size(); a++) {

                String data[] = al1.get(a).split(" ");
                double score = Double.valueOf(data[2]);
                double scaleScore = 1 - ((score - min) / (max - min));
                String out = data[0] + " " + data[1] + " " + scaleScore;
                al1.set(a, out);
            }
        }
        //======================================================================
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(args[1]));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(args[2]));

        double noofdbmols = al1.size();
        double refmols = 50;

        if (refmols > noofdbmols) {
            refmols = noofdbmols;
        }

        int steps = (int) (noofdbmols / refmols);
        //======================================================================

        for (int a = 0; a < noofdbmols; a = a + steps) {
            al2.add(al1.get(a));
        }
        //======================================================================

        for (int a = 0; a < al1.size(); a++) {
            bw1.write(al1.get(a) + "\n");
        }
        bw1.close();

        for (int a = 0; a < al2.size(); a++) {
            bw2.write(al2.get(a) + "\n");
        }
        bw2.close();
    }
}
