/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import chemaxon.formats.MolFormatException;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author mahendra
 */
public class getImageForAVGMol4 implements ImageObserver {

    String fileName = "";

    public static void main(String args[]) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(args[0]));

        String str;
        int control = 0;
        while ((str = br.readLine()) != null) {

            String smi = str.split(" ")[0];
            String name = str.split(" ")[1];
            getImageForAVGMol4 image = new getImageForAVGMol4(smi, name + "", "mols");
            control++;
            Molecule mol = new MolHandler(smi).getMolecule();
        }
    }

    public getImageForAVGMol4(String smi, String fileName, String folder) throws MolFormatException, IOException {

//        Molecule mol = new MolHandler(smi).getMolecule();
//        mol.dearomatize();
//        MDocument mdoc = new MDocument(mol);
//        Color clr = new Color(255, 255, 0);
//        mdoc.setBondSetRGB(0, clr.getRGB());

        Molecule mol = new MolHandler(smi).getMolecule();
//        MDocument mdoc = new MDocument(mol);
//        Color green = new Color(255, 255, 255);
//        mdoc.setBondSetRGB(0, green.getRGB());

        Image img = (Image) mol.toObject("image:w200,h200,#00000000,setcolors:b0:#ffffff,wireThickness0.1,atsiz0.6");

        BufferedImage b_img = new BufferedImage(img.getHeight(this), img.getHeight(this), BufferedImage.TYPE_INT_ARGB);
        b_img.getGraphics().drawImage(img, 0, 0, null);
        ImageIO.write(b_img, "png", new File(folder + "/" + fileName + ".png"));
    }

    public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}