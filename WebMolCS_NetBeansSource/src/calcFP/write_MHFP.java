/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calcFP;

import chemaxon.descriptors.MDGeneratorException;
import chemaxon.struc.MolAtom;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;
import processAndSplit.processMolecule;

/**
 * @author mahendra
 */
public class write_MHFP {

    public static void main(String args[]) throws IOException, MDGeneratorException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));

        Molecule m;
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        if (args[0].endsWith(".gz")) {
            bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(args[1]))));
        } else {
            bw = new BufferedWriter(new FileWriter(args[1]));
        }
//==============================================================================
        double counter = 0;
        String str;
        String posdata = "";
        while ((str = br.readLine()) != null) {

            String sarray[] = str.split(" ");
            String smi = sarray[0];

            //Process Molecule
            try {
                m = new MolHandler(smi).getMolecule();
            } catch (Exception e) {
                continue;
            }

            processMolecule pm = new processMolecule(m);
            String isOK = pm.process(7.4);
            if (isOK == null) {
                continue;
            }

            String smiline = pm.mol.toFormat("smiles:q0-H") + " " + sarray[1] + " " + sarray[2];
            if (posdata.isEmpty()) {
                posdata = smiline;

            } else {
                posdata = posdata + "\n" + smiline;
            }
        }

        try {
            String mhfpsdata = splitDataAndGetMHFP(posdata);
            bw.write(mhfpsdata);
        } catch (Exception e) {
        }

        bw.close();
        br.close();
        System.out.println("END");
    }
//==============================================================================

    public static String splitDataAndGetMHFP(String postdata) throws IOException {

        String[] splittedLines = postdata.split("\n");
        if (splittedLines.length < 500) {
            String mhfps = getMHFP(postdata);
            return mhfps;
        }

        String mhfps = "";
        for (int a = 0; a < splittedLines.length; a = a + 500) {

            String data = "";
            for (int b = a; b < (a + 500); b++) {

                if (b >= splittedLines.length) {
                    break;
                }

                if (data.isEmpty()) {
                    data = splittedLines[b];
                } else {
                    data = data + "\n" + splittedLines[b];
                }
            }

            String mhfpspart = getMHFP(data);
            if (mhfps.isEmpty()) {
                mhfps = mhfpspart;
            } else {
                mhfps = mhfps + "" + mhfpspart;
            }
        }

        return mhfps;
    }

    public static String getMHFP(String postData) throws MalformedURLException, IOException {

        //Connection
        URL url = new URL("http://mhfp.gdb.tools/from_smiles/128");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        if (postData.isEmpty()) {
            return "ERROR";
        }

        con.setConnectTimeout(2000000);
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "text");
        con.setDoOutput(true);
        con.getOutputStream().write(postDataBytes);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        StringBuilder data = new StringBuilder();
        for (int c; (c = in.read()) >= 0;) {
            data.append((char) c);
        }
        String mhfps = data.toString();
        String splitted[] = mhfps.split("\n");
        mhfps = "";
        for (int a = 0; a < splitted.length; a++) {

            if (splitted[a].contains("null")) {
                continue;
            }
            mhfps = mhfps  + splitted[a] + "\n";
        }
        
        return mhfps;
    }
//==============================================================================    
}
