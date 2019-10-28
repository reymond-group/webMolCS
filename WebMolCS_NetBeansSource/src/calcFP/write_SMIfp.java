/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calcFP;

import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @author mahendra
 *
 */
////////////////////////////////////////////////////////////////////////////////
public class write_SMIfp {

    static MajorMicrospeciesPlugin mmp = new MajorMicrospeciesPlugin();
    public static String processSMI;
    static long okMolCount = 0;

    public static void main(String[] args) throws IOException {


        System.out.println("USAGE: inFile.smi outFile.smi");
        System.out.println("READING IN, CALCULATING SMIfp and WRITING OUT. HARM WITH VALENCE");

        BufferedReader br1 = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(args[1]));

        String s;
        //read input file and annotate with mqns and properties

        while ((s = br1.readLine()) != null) {

            String sarray[] = s.split(" ");
            String smi = sarray[0];

            write_SMIfp make = new write_SMIfp();
            String smIfp = make.getSMIfp(smi);

            if (smIfp == null) {
                continue;
            }

            //write unique pH adjusted smiles (+ eventually tag) + resulting SMIfp
            bw1.write(processSMI + " " + sarray[1] + " " + sarray[2] + " " + smIfp + "\n");
            okMolCount++;

            if (okMolCount % 1000 == 0) {
                System.out.println(okMolCount + " MOLECULES PROCESS");
            }
        }
        br1.close();
        bw1.close();

        System.out.println("END");
    }
////////////////////////////////////////////////////////////////////////////////

    public String checkAndAdjust(Molecule m) {

        if (m.getAtomCount() == 0) {
            return null;
        }

        String correctSMI = null;
        try {
            m.aromatize(false);
            m.aromatize(true);
            m.hydrogenize(false);
            correctSMI = m.toFormat("smiles:0-Hq");
        } catch (Exception e) {
            return null;
        }

        return correctSMI;
    }

////////////////////////////////////////////////////////////////////////////////
    public String getSMIfp(String smi) {

        Molecule mol = processMolecule(smi);
        if (mol == null) {
            return null;
        }

        smi = checkAndAdjust(mol);

        if (smi == null) {
            return null;
        }

        String fp = null;
        try {
            fp = CountCharacters(smi);
        } catch (Exception ex) {
            return null;
        }

        processSMI = mol.toFormat("smiles:0q-H");
        return fp;
    }
////////////////////////////////////////////////////////////////////////////////

    public String CountCharacters(String SMILES)
            throws Exception {

        int BrkRndOp = 0, BrkSqrOp = 0;
        boolean SqrBrk = false;
        int SB = 0, DB = 0, TB = 0;
        int ChargePlus = 0, ChargeMinus = 0;
        int AtomB = 0, AtomC = 0, AtomN = 0, AtomO = 0, AtomP = 0, AtomS = 0, AtomF = 0, AtomCl = 0, AtomBr = 0, AtomI = 0, AtomH = 0;
        int Atomc = 0, Atomo = 0, Atoms = 0, Atomn = 0, Atomp = 0;
        int AtomOther = 0;

        int[] RingIdx = new int[9];
        for (int i = 0; i < RingIdx.length; i++) {
            RingIdx[i] = 0;
        }
        int PerCent = 0;



        for (int i = 0; i < SMILES.length(); i++) {

            char ThisChar = SMILES.charAt(i);
            char NextChar;
            if (i != (SMILES.length() - 1)) {
                NextChar = SMILES.charAt(i + 1);
            } else {
                NextChar = ' ';
            }

            /*
             * Check for Letters, this might be alittle tricky, cause one has
             * also to check the next Letter, and just around with i
             */

            /*
             * Without Square Brackets, only the following Atoms are allowed:
             *
             * B,C,N,O,P,S,F,Cl,Br,I
             *
             * and ofcourse also their aromatic Version:
             *
             * c,n,o,s
             */
            if (!SqrBrk) {
                /*
                 * Atoms First
                 */
                if (ThisChar == 'C') {
                    if (NextChar == 'l') {
                        AtomCl++;
                        i++;
                    } else {
                        AtomC++;
                    }
                } else if (ThisChar == 'B') {
                    if (NextChar == 'r') {
                        AtomBr++;
                        i++;
                    } else {
                        AtomB++;
                    }
                } else if (ThisChar == 'N') {
                    AtomN++;
                } else if (ThisChar == 'O') {
                    AtomO++;
                } else if (ThisChar == 'P') {
                    AtomP++;
                } else if (ThisChar == 'S') {
                    AtomS++;
                } else if (ThisChar == 'F') {
                    AtomF++;
                } else if (ThisChar == 'I') {
                    AtomI++;
                } else if (ThisChar == 'c') {
                    Atomc++;
                } else if (ThisChar == 'o') {
                    Atomo++;
                } else if (ThisChar == 's') {
                    Atoms++;
                } else if (ThisChar == 'n') {
                    Atomn++;
                } else if (ThisChar == 'p') {
                    Atomp++;
                } /*
                 *
                 */ else if (ThisChar == '(') { /*
                     * Brackets
                     */
                    BrkRndOp++;
                } else if (ThisChar == '[') {
                    BrkSqrOp++;
                    SqrBrk = true;
                } else if (ThisChar == '=') { /*
                     * Bonds
                     */
                    DB++;
                } else if (ThisChar == '#') {
                    TB++;
                } else if (ThisChar == '1') { /*
                     * Eings
                     */
                    RingIdx[0]++;
                } else if (ThisChar == '2') {
                    RingIdx[1]++;
                } else if (ThisChar == '3') {
                    RingIdx[2]++;
                } else if (ThisChar == '4') {
                    RingIdx[3]++;
                } else if (ThisChar == '5') {
                    RingIdx[4]++;
                } else if (ThisChar == '6') {
                    RingIdx[5]++;
                } else if (ThisChar == '7') {
                    RingIdx[6]++;
                } else if (ThisChar == '8') {
                    RingIdx[7]++;
                } else if (ThisChar == '9') {
                    RingIdx[8]++;
                } else if (ThisChar == '0') {
                    RingIdx[9]++;
                } else if (ThisChar == '%') {
                    PerCent++;
                    i += 2;
                } else if (ThisChar == '-') {
                    SB++;
                }
            } else if (SqrBrk) {

                if (ThisChar == ']') {
                    SqrBrk = false;
                } else if (ThisChar == '-') {
                    if ('1' <= NextChar && NextChar <= '9') {
                        ChargeMinus += NextChar - '0';
                    } else {
                        ChargeMinus++;
                    }

                } else if (ThisChar == '+') {
                    if ('1' <= NextChar && NextChar <= '9') {
                        ChargePlus += NextChar - '0';
                    } else {
                        ChargePlus++;
                    }

                } else if (this.isSmallLetter(ThisChar)) {
                    if (ThisChar == 'o') {
                        Atomo++;
                    } else if (ThisChar == 'c') {
                        Atomc++;
                    } else if (ThisChar == 'n') {
                        Atomn++;
                    } else if (ThisChar == 'p') {
                        Atomp++;
                    } else if (ThisChar == 's') {
                        Atoms++;
                    }
                } else if (this.isCaptialLetter(ThisChar)) {
                    if (ThisChar == 'C') {
                        if (this.isSmallLetter(NextChar)) {
                            if (NextChar == 'l') {
                                AtomCl++;
                                i++;
                            } else {
                                AtomOther++;
                                i++;
                            }
                        } else {
                            AtomC++;
                        }
                    } else if (ThisChar == 'B') {
                        if (this.isSmallLetter(NextChar)) {
                            if (NextChar == 'r') {
                                AtomBr++;
                                i++;
                            } else {
                                AtomOther++;
                                i++;
                            }
                        } else {
                            AtomB++;
                        }
                    } else if (ThisChar == 'N') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else {
                            AtomN++;
                        }
                    } else if (ThisChar == 'O') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else {
                            AtomO++;
                        }
                    } else if (ThisChar == 'P') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else {
                            AtomP++;
                        }
                    } else if (ThisChar == 'S') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else {
                            AtomS++;
                        }
                    } else if (ThisChar == 'F') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else {
                            AtomF++;
                        }
                    } else if (ThisChar == 'I') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else {
                            AtomI++;
                        }
                    } else if (ThisChar == 'H') {
                        if (this.isSmallLetter(NextChar)) {
                            AtomOther++;
                            i++;
                        } else if ('1' <= NextChar && NextChar <= '9') {
                            AtomH += NextChar - '0';

                        } else {
                            AtomH++;
                        }
                    } else {
                        AtomOther++;
                        if (this.isSmallLetter(NextChar)) {
                            i++;
                        }
                    }

                }

            }

        }

        int[] SMIfp = new int[34];

        /*
         * 00
         */
        SMIfp[0] = BrkRndOp;
        SMIfp[13] = BrkSqrOp;
        SMIfp[19] = SB;

        SMIfp[2] = DB;
        SMIfp[9] = TB;
        SMIfp[8] = RingIdx[0];
        if (RingIdx[0] != 0) {
            SMIfp[8] = RingIdx[0] / 2;
        }
        SMIfp[7] = RingIdx[1];
        if (RingIdx[1] != 0) {
            SMIfp[7] = RingIdx[1] / 2;
        }
        SMIfp[10] = RingIdx[2];
        if (RingIdx[2] != 0) {
            SMIfp[10] = RingIdx[2] / 2;
        }
        SMIfp[12] = RingIdx[3];
        if (RingIdx[3] != 0) {
            SMIfp[12] = RingIdx[3] / 2;
        }
        SMIfp[18] = RingIdx[4];
        if (RingIdx[4] != 0) {
            SMIfp[18] = RingIdx[4] / 2;
        }
        SMIfp[22] = RingIdx[5];
        if (RingIdx[5] != 0) {
            SMIfp[22] = RingIdx[5] / 2;
        }
        SMIfp[23] = RingIdx[6];
        if (RingIdx[6] != 0) {
            SMIfp[23] = RingIdx[6] / 2;
        }

        SMIfp[31] = RingIdx[7];
        if (RingIdx[7] != 0) {
            SMIfp[31] = RingIdx[7] / 2;
        }
        SMIfp[32] = RingIdx[8];
        if (RingIdx[8] != 0) {
            SMIfp[32] = RingIdx[8] / 2;
        }

        SMIfp[26] = PerCent;
        SMIfp[21] = ChargePlus;
        SMIfp[20] = ChargeMinus;
        SMIfp[28] = AtomB;
        SMIfp[5] = AtomC;
        SMIfp[3] = AtomN;
        SMIfp[1] = AtomO;
        SMIfp[27] = AtomP;
        SMIfp[11] = AtomS;
        SMIfp[24] = AtomF;
        SMIfp[17] = AtomCl;
        SMIfp[25] = AtomBr;
        SMIfp[30] = AtomI;
        SMIfp[4] = Atomc;
        SMIfp[14] = Atomo;
        SMIfp[16] = Atoms;
        SMIfp[6] = Atomn;
        SMIfp[33] = Atomp;
        SMIfp[15] = AtomH;
        SMIfp[29] = AtomOther;

        int sum = 0;
        for (int j = 0; j < SMIfp.length; j++) {
            sum += SMIfp[j];
        }

        return arrayToString(SMIfp, ";");
    }
/////////////////////////////////////////////////////////////////////////////////

    public boolean isCaptialLetter(char Letter) {

        if ('A' <= Letter && Letter <= 'Z') {
            return true;
        }
        return false;
    }
/////////////////////////////////////////////////////////////////////////////////

    public boolean isSmallLetter(char Letter) {

        if ('a' <= Letter && Letter <= 'z') {
            return true;
        }
        return false;
    }
/////////////////////////////////////////////////////////////////////////////////

    String arrayToString(int array[], String seprator) {

        String str = "";
        for (int a = 0; a < array.length; a++) {
            str = str + array[a] + seprator;
        }

        str = str.substring(0, str.length() - 1);
        return str;
    }

/////////////////////////////////////////////////////////////////////////////////
    Molecule processMolecule(String smi) {
        Molecule mol;

        try {
            mol = new MolHandler(smi).getMolecule();
            Molecule[] convertToFrags = mol.convertToFrags();
            int max = 0;
            int atmcount = 0;
            int winner = 0;
            for (int i = 0; i < convertToFrags.length; i++) {
                atmcount = convertToFrags[i].getAtomCount();
                if (atmcount > max) {
                    max = atmcount;
                    winner = i;
                }
            }
            mol = convertToFrags[winner];

        } catch (Exception e) {
            return null;
        }

        if (mol == null) {
            return null;
        }


        //dehydro & dearom molecule, if it fails return null
        if ((mol = harmonize(mol)) == null) {
            return null;
        }

        /*
         * process the molecule: if it fails return null!
         */
        if ((mol = majorSpecies(mol)) == null) {
            return null;
        }

        return mol;
    }

/////////////////////////////////////////////////////////////////////////////////
    /*
     * Do a valence check, dehydrogenize and dearomatize mol
     */
    public static Molecule harmonize(Molecule m) {
        m.valenceCheck();
        if (m.hasValenceError()) {
            System.err.println("VALENCE ERROR ");
            return null;
        }

        m.hydrogenize(false);
        m.aromatize();
        if (!m.dearomatize()) {
            System.err.println("DEAROMATIZE ERROR ");
            return null;
        }
        return m;
    }

    /*
     * to pH 7.4
     */
    public static Molecule majorSpecies(Molecule m) {
        try {
            mmp.setMolecule(m);
            mmp.setpH(7.4);
            mmp.run();
            m = mmp.getMajorMicrospecies();
        } catch (Exception e) {
            e.toString();
            System.err.println("MMP ERROR");
            return null;
        }
        return m;
    }
/////////////////////////////////////////////////////////////////////////////////
}
