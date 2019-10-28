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
public class processInput3 {

    static ArrayList<String> al1 = new ArrayList();
    static ArrayList<String> al2 = new ArrayList();

    public static void main(String args[]) throws IOException {

        //======================================================================
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        //Checked for unqie smiles of compounds
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String str;
        HashMap<String, String> hm = new HashMap();
        ArrayList<String> cpds = new ArrayList();
        int counter = 0;
        //======================================================================
        while ((str = br.readLine()) != null) {

            String smol[] = str.split("\\s+");

            if (smol.length < 2) {
                continue;
            }

            if (smol[0].isEmpty()) {
                continue;
            }

            if (smol[1].isEmpty()) {
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

            //String smi = mol.toFormat("smiles:q0-H");
            cpds.add(str);
//            if (hm.containsKey(smi)) {
//                continue;
//            } else {
//                cpds.add(str);
//                hm.put(smi, str);
//            }
        }
        br.close();
        //======================================================================
        counter = 0;
        boolean usesatelightsfromuser = false;
        int userprovidedsatelights = 0;
        for (int a = 0; a < cpds.size(); a++) {

            str = cpds.get(a);
            String smol[] = str.split("\\s+");
            if (smol.length < 2) {
                continue;
            }

            smol[0] = smol[0].replaceAll("\\r$", "");
            smol[1] = smol[1].replaceAll("\\r$", "");

            smol[0] = smol[0].replaceAll("\\s+", "");
            smol[1] = smol[1].replaceAll("\\s+", "");

            smol[1] = smol[1].split("_")[0];
            smol[1] = smol[1].split(";")[0];

            //Add actual values
            double actual = 0;
            if (smol.length > 2) {
                try {
                    actual = Double.valueOf(smol[2]);
                } catch (Exception e) {
                }
            }

            //select as satelight or not
            //Add actual values
            double useassatellights = 0;
            if (smol.length > 3) {
                try {
                    useassatellights = Double.valueOf(smol[3]);
                    if (useassatellights == 1) {
                        userprovidedsatelights = userprovidedsatelights + 1;
                    }
                } catch (Exception e) {
                }
            }

            counter++;

            al1.add(smol[0] + " " + counter + "-" + smol[1] + " " + counter + " " + actual + " " + useassatellights);
        }
        min = 1;
        max = counter;

        //Use the satelights from user or not
        if (userprovidedsatelights > 0) {
            usesatelightsfromuser = true;
        }

        //======================================================================        
        //Scale scores to 0-1 (1==high similarity, 0=low similarity)
        if (max > min && max != 0) {
            for (int a = 0; a < al1.size(); a++) {

                String data[] = al1.get(a).split(" ");
                double score = Double.valueOf(data[2]);
                double scaleScore = 1 - ((score - min) / (max - min));
                String out = data[0] + " " + data[1] + " " + scaleScore + ";" + data[3] + " " + data[4];
                al1.set(a, out);
            }
        }
        //======================================================================
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(args[1]));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(args[2]));

        double noofdbmols = al1.size();
        double refmols = 50;

        if (al1.isEmpty() || al1.size() <= 20) {
            bw1.close();
            bw2.close();
            System.exit(0);
        }

        if (refmols > noofdbmols) {
            refmols = noofdbmols;
        }

        int steps = (int) (noofdbmols / refmols);
        //======================================================================

        if (usesatelightsfromuser) {

            for (int a = 0; a < noofdbmols; a++) {
                String tag = al1.get(a).split(" ")[3];
                double tagval = Double.valueOf(tag);
                if (tagval == 1 && al2.size() <= refmols) {
                    al2.add(al1.get(a));
                }
            }

        } else {
            for (int a = 0; a < noofdbmols; a = a + steps) {
                al2.add(al1.get(a));
            }
        }
        //======================================================================

        for (int a = 0; a < al1.size(); a++) {
            String data[] = al1.get(a).split(" ");
            bw1.write(data[0] + " " + data[1] + " " + data[2] + "\n");
        }
        bw1.close();

        for (int a = 0; a < al2.size(); a++) {
            String data[] = al2.get(a).split(" ");
            bw2.write(data[0] + " " + data[1] + " " + data[2] + "\n");
        }
        bw2.close();
    }
}
