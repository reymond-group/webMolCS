package makeFP;

import chemaxon.marvin.calculations.ElementalAnalyserPlugin;
import chemaxon.marvin.calculations.HBDAPlugin;
import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.calculations.TPSAPlugin;
import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.marvin.calculations.logPPlugin;
import chemaxon.struc.*;

/**
 * Calculating MQNs
 */
public class FP04_MQN {

    static HBDAPlugin hbdap = new HBDAPlugin();
    static MajorMicrospeciesPlugin mmp = new MajorMicrospeciesPlugin();
    static TopologyAnalyserPlugin tap = new TopologyAnalyserPlugin();
    static ElementalAnalyserPlugin eap = new ElementalAnalyserPlugin();
    static logPPlugin lpp = new logPPlugin();
    static TPSAPlugin psap = new TPSAPlugin();
    static long okMolCount = 0;
    public String colorProp = "";

    /*This code is a mixture of the calcMQN method from the CMC original paper plus some more lines
     for the ASF map properties (logP TPSA rigatoms...) */
    public String calculateFingerprint(Molecule m) {
        //initialize everything
        try {
            hbdap.setMolecule(m);
            hbdap.run();
            tap.setMolecule(m);
            tap.run();
            eap.setMolecule(m);
            eap.run();
            lpp.setMolecule(m);
            lpp.run();
            psap.setMolecule(m);
            psap.run();
        } catch (Exception e) {
            e.toString();
            System.err.println("CalcPlugin Error " + m.toFormat("smiles"));
            return null;
        }

        /* THIS CODE IS MOSTLY COPIED FROM MQN PAPER */
        //Classic descriptors
        int hbd = hbdap.getDonorAtomCount();
        int hbdm = hbdap.getDonorCount();
        int hba = hbdap.getAcceptorAtomCount();
        int hbam = hbdap.getAcceptorCount();
        int rbc = tap.getRotatableBondCount();

        //Ring properties / ring sizes count
        int r3 = 0, r4 = 0, r5 = 0, r6 = 0, r7 = 0, r8 = 0, r9 = 0, rg10 = 0;
        int[][] sssr = m.getSSSR();
        for (int i = 0; i < sssr.length; i++) {
            switch (sssr[i].length) {
                case 3:
                    r3++;
                    break;
                case 4:
                    r4++;
                    break;
                case 5:
                    r5++;
                    break;
                case 6:
                    r6++;
                    break;
                case 7:
                    r7++;
                    break;
                case 8:
                    r8++;
                    break;
                case 9:
                    r9++;
                    break;
                default:
                    rg10++;
                    break;
            }
        }

        //Atom properties
        int c = 0, f = 0, cl = 0, br = 0, I = 0, thac = 0, asv = 0, adv = 0, atv = 0, aqv = 0,
                cdv = 0, ctv = 0, cqv = 0, p = 0, s = 0, posc = 0, negc = 0,
                afrc = 0, cn = 0, an = 0, co = 0, ao = 0;
        int ringat = 0;
        for (int i = 0; i < m.getAtomCount(); i++) {
            MolAtom at = m.getAtom(i);
            boolean isRingAt = tap.isRingAtom(i);
            if (isRingAt) {
                ringat++;
            }
            if (at.getAtno() != 1) {
                thac++;
            }
            //element counts
            switch (at.getAtno()) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    break;
                case 6:
                    c++;
                    break;
                case 7:
                    if (isRingAt) {
                        cn++;
                    } else {
                        an++;
                    }
                    break;
                case 8:
                    if (isRingAt) {
                        co++;
                    } else {
                        ao++;
                    }
                    break;
                case 15:
                    p++;
                    break;
                case 16:
                    s++;
                    break;

                case 9:
                    f++;
                    break;
                case 17:
                    cl++;
                    break;
                case 35:
                    br++;
                    break;
                case 53:
                    I++;
                    break;
            }

            //valency count
            switch (at.getBondCount()) {
                case 0:
                    System.err.println("ATOM WITH NO BONDS ");
                    return null;
                case 1:
                    asv++; //single valent can only be acyclic
                    break;
                case 2:
                    if (isRingAt) {
                        cdv++;
                    } else {
                        adv++;
                    }
                    break;
                case 3:
                    if (isRingAt) {
                        ctv++;
                    } else {
                        atv++;
                    }
                    break;
                case 4:
                    if (isRingAt) {
                        cqv++;
                    } else {
                        aqv++;
                    }
                    break;
            }
            if (tap.getRingCountOfAtom(i) > 1) {
                afrc++;
            }
        }

        //Bond properties
        int csb = 0, cdb = 0, ctb = 0, asb = 0, adb = 0, atb = 0, bfrc = 0;
        for (int i = 0; i < m.getBondCount(); i++) {
            MolBond bd = m.getBond(i);
            if (tap.isRingBond(i)) {
                switch (bd.getType()) {
                    case 1:
                        csb++;
                        break;
                    case 2:
                        cdb++;
                        break;
                    case 3:
                        ctb++;
                        break;
                    default:
                        System.err.println("UNKNOWN CYCLIC BOND TYPE " + bd.getType());
                        return null;
                }
            } else {
                switch (bd.getType()) {
                    case 1:
                        asb++;
                        break;
                    case 2:
                        adb++;
                        break;
                    case 3:
                        atb++;
                        break;
                    default:
                        System.err.println("UNKNOWN ACYCLIC BOND TYPE " + bd.getType());
                        return null;
                }
            }
        }

        //bond's fused ring count
        int[][] sssre = m.getSSSRBonds();
        int[] brc = new int[m.getBondCount()];
        for (int j = 0; j < sssre.length; j++) {
            for (int k = 0; k < sssre[j].length; k++) {
                brc[sssre[j][k]]++;
            }
        }
        for (int j = 0; j < brc.length; j++) {
            if (brc[j] > 1) { //if bond's ring count > 1
                bfrc++; //increase fused ring bonds count
            }
        }

        for (int i = 0; i < m.getAtomCount(); i++) {
            int crg = m.getAtom(i).getCharge();
            if (crg > 0) {
                posc += crg;
            }
            if (crg < 0) {
                negc += Math.abs(crg);
            }
        }

        //additional values for CSA map
        double mass = eap.getMass();
        double psa = psap.getSurfaceArea();
        double logP = lpp.getlogPTrue();
        int ringc = sssr.length;

        colorProp = thac + ";"
                + rbc + ";"
                + ringc + ";"
                + hbd + ";"
                + hba + ";"
                + logP + ";"
                + psa + ";"
                + c + ";"
                + ringat + ";" + mass + ";";

        return //first block is MQNs
                //CLASSIC PROPERTIES
                hbd + ";" //hydrogen bond donor atom count 1
                + hbdm + ";" //HBD with multivalency 2
                + hba + ";" //hydrogen bond acceptor atom count 3
                + hbam + ";" //HBA with multivalency 4
                + rbc + ";" //rotatable bond count 5
                //RING PROPERTIES Counts 6-13
                + r3 + ";" + r4 + ";" + r5 + ";" + r6 + ";" + r7 + ";" + r8 + ";" + r9 + ";" + rg10 + ";" //ATOM PROPERTIES
                + thac + ";"//total heavy atom count (= everything else than H D T) 14
                + c + ";" //carbon count 15
                + p + ";"//phosphorus 16
                + s + ";"//sulfur atom count 17
                + f + ";" //fluor atom count 18
                + cl + ";" //chlorine atom count 19
                + br + ";" //bromine atom count 20
                + I + ";" //iodine atom count 21
                + cn + ";" //cyclic nitrogen count 22
                + an + ";" //acyclic nitrogen count 23
                + co + ";" //cyclic oxygen count 24
                + ao + ";" //acyclic oxygen count 25
                + asv + ";"//acyclic single valent atom count 26
                + adv + ";"//acyclic double valent atom count 27
                + atv + ";"//acyclic triple valent atom count 28
                + aqv + ";"//acyclic quart valent atom count 29
                + cdv + ";"//cyclic double valent atom count 30
                + ctv + ";"//cyclic triple valent atom count 31
                + cqv + ";"//cyclic quart valent atom count 32
                + afrc + ";"//atoms-in-fused-ring count 33
                + posc + ";" // Positive charges 34
                + negc + ";" // Negative charges 35
                //BOND PROPERTIES
                + csb + ";"//cyclic single bonds 36
                + cdb + ";"//cyclic double bonds 37
                + ctb + ";"//cyclic triple bonds 38
                + asb + ";"//acyclic singe bonds 39
                + adb + ";"//acyclic double bonds 40
                + atb + ";"//acyclic triple bonds 41
                + bfrc;//bonds-in-fused-ring count 42
        //last block is csa properties WARNING IF YOU MODIFIY HERE YOU HAVE TO MODIFY G_CREATEPARTMAPS
        //String[] names = {"hac","rbc","ringc","hbd","hba","logp","psa","carb","ringat"};

    }
}
