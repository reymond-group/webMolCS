package organize.tools;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * ReadWriter Opens a file to read (specified with -i NAME at the command line)
 * and a file to write (specified with -o NAME at the command line) Several
 * handy functions for reading and writing are provided
 */
public class ReadWriter {

    /*Global variables*/
    BufferedReader inreader;
    BufferedWriter outwriter;
    ArgReader aread;
    String line;
    String lf = System.getProperty("line.separator");
    long readcounter = 0, writecounter = 0;
    long starttime = System.currentTimeMillis();

    /**
     * Constructor
     *
     * @param helptext Helptext to show if argument is missing
     * @param args CommandLine Arguments passed to the program
     *
     */
    public ReadWriter() {
    }

    public ReadWriter(String helptext, String[] args) {

        //Read command line arguments
        aread = new ArgReader(helptext, args);
        String infile = aread.getArg("-i");
        String outfile = aread.getArg("-o");

        //try open files
        try {
            if (infile.endsWith(".gz")) {
                inreader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(infile))));
            } else {
                inreader = new BufferedReader(new FileReader(infile));
            }
            if (outfile.endsWith(".gz")) {
                outwriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outfile))));
            } else {
                outwriter = new BufferedWriter(new FileWriter(outfile));
            }
        } catch (IOException e) {
            System.err.println("ReadWriter Error: Opening files");
            System.exit(1);
        }
    }

    public void displayReadCounter(int interval) {
        if (readcounter % interval == 0) {
            System.out.println("Read line " + readcounter);
        }
    }

    public void displayWriteCounter(int interval) {
        if (writecounter % interval == 0) {
            System.out.println("Wrote line " + writecounter);
        }
    }

    /**
     * Closes files and prints read/writecounter
     */
    public void end() {
        try {
            inreader.close();
            inreader = null;
            outwriter.close();
            outwriter = null;
        } catch (IOException ex) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (inreader != null || outwriter != null) {
            System.err.println(getClass().getName() + " Did you forget to call end()?");
        }
        super.finalize();
    }

    public boolean isArg(String arg) {
        return aread.isArg(arg);
    }

    public String getArg(String arg) {
        return aread.getArg(arg);
    }

    /**
     * @return Line read with readLine before
     */
    public String getCurrentLine() {
        return line;
    }

    public long getReadCounter() {
        return readcounter;
    }

    public long getWriteCounter() {
        return writecounter;
    }

    public long secondsRunning() {
        return (System.currentTimeMillis() - starttime) / 1000;
    }

    /**
     *
     * @return next line from infile
     */
    public String readLine() {
        try {
            line = inreader.readLine();
            if (line != null) {
                readcounter++;
            }
        } catch (IOException e) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
        return line;
    }

    /**
     * Writes line read with readLine
     */
    public void writeCurrentLine() {
        try {
            outwriter.write(line + lf);
            writecounter++;
        } catch (IOException ex) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
    }

    /**
     * Writes out s
     */
    public void writeLine(String s) {
        try {
            outwriter.write(s + lf);
            writecounter++;
        } catch (IOException ex) {
            System.err.println(getClass().getName() + " Error: " + new Exception().getStackTrace()[0].getMethodName());
        }
    }
}
