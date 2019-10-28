package organize.tools;

/**
 * ArgReader Provides functions to easily retrieve command line arguments
 * (isArg, getArg) and also takes over displaying of a helptext if user did not
 * provide any arguments at startup
 *
 * One of the first classes I ever made and is still in use!
 *
 * @since 2007
 * @author lori
 * @version 1.0
 */
public class ArgReader {
    /* Global Variable */

    private String[] arguments;

    /* Constructor */
    public ArgReader(String usage, String[] args) {
        arguments = args;
        if ((arguments.length == 0) || this.isArg("-h")) {
            System.out.println("Usage: " + usage);
            System.exit(0);
        }
    }

    /* GetArg(arg) */
    public String getArg(String arg) {
        String theArg = "";
        for (int i = 0; i < arguments.length - 1; i++) {
            /* If ArgumentOfArray = arg and does not start with - (=next argument)*/
            if ((arguments[i].equals(arg)) && (!arguments[i + 1].startsWith("-"))) {
                theArg = arguments[i + 1];
            }
        }

        if (theArg.equals("")) {
            System.out.println("Problems with argument \"" + arg
                    + "\"! Try -h/--help for help text.\n");
            System.exit(2);
        }

        return theArg;
    }

    public boolean isArg(String arg) {
        boolean isArg = false;
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equals(arg)) {
                isArg = true;
            }
        }
        return isArg;
    }
}
