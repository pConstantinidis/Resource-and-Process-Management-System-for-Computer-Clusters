package src.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import src.model.Program;

/**
 * A class that handles the programs that are to be dismissed. * 
 * @author pConstantinidis
 */
public final class ProgramDismissal {

    public static final char DELIMETER = ';';
    private final static File directory = new File("./log");
    private final static File dismissed = new File("./log/rejected.out");
    private static FileWriter fWriter;

    /**
     * A method that stores a String representation of a {@code Program} object into an .out file
     * @apiNote In the case that the file/directory does not exist, is created automaticly.
     * @param p The program to be dismissed.
     * @throws IOException
     */
    public static void dismiss(Program p) throws IOException {
        if (!dismissed.canWrite() || !dismissed.exists()) {
            if (!directory.isDirectory()) directory.mkdirs();

            dismissed.createNewFile();
        } if (!dismissed.canWrite() || !dismissed.exists()) {
            throw new FileNotFoundException("The directory "+dismissed.getPath()+" doesn't exist.");
        }

        fWriter = new FileWriter(dismissed, true);
        fWriter.write(p.toString()+"\n");
        
        fWriter.close();
        return;
    }


}
