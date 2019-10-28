/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package organize.tools;

/**
 *
 * @author mahendra
 */
public class molContainer implements Comparable<molContainer> {

    public String SMI = "";
    public double VAL = 0.0;

    public molContainer(String smi, double val) {

        SMI = smi;
        VAL = val;
    }

    @Override
    public int compareTo(molContainer arg) {
        if (VAL > arg.VAL) {
            return 1;
        }
        if (VAL < arg.VAL) {
            return -1;
        }
        return 0;
    }
}
