package organize.tools;

import chemaxon.util.*;
import chemaxon.struc.*;
import java.io.*;

/**
 * SMIRW: Smiles Reader Writer Opens a file to read (specified with -i NAME at
 * the command line) and a file to write (specified with -o NAME at the command
 * line) It is then possible to read the lines as molecules
 *
 */
public class SMIReadWriter extends ReadWriter {

    /*Global variables*/
    MolHandler mh = new MolHandler();
    Molecule mol = new Molecule();

    public SMIReadWriter(String helptext, String[] args) {
        super(helptext, args);
    }

    public Molecule readMolecule() {
        //try to read the line, if null (=EOF) then return null
        try {
            line = inreader.readLine();
        } catch (IOException ex) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
        if (line == null) {
            return null;
        }

        //try to set up the molecule, on error call function recursively until you get a valid one
        readcounter++; //Readcounter must be increased here, not before return null!
        try { //
            //line = line.split(" \t")[0];
            mh.setMolecule(line);
            mol = mh.getMolecule();
        } catch (Exception ex) {
            System.err.println(getClass().getName() + " Warning: Skipping \"" + line + "\"");
            mol = readMolecule();
        }

        return mol;
    }

    public void writeUMolecule(Molecule mol) {
        try {
            outwriter.write(mol.toFormat("smiles:q0-H") + lf);
            writecounter++;
        } catch (IOException ex) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
    }

    public void writeMolecule(Molecule mol) {
        try {
            outwriter.write(mol.toFormat("smiles") + lf);
            writecounter++;
        } catch (IOException ex) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
    }
}
