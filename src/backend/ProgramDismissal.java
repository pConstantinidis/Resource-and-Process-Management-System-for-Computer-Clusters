package src.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import src.CLI.CLI_IOHandler;
import src.model.Program;

/**
 * A class that handles the programs that are to be dismissed. * 
 * @author pConstantinidis
 */
public final class ProgramDismissal {

    public static final char DELIMETER = '/';
    private final File dismissed = new File("./log/rejected.out");
    private FileOutputStream fos;
    private ObjectOutputStream oos;

    /**
     * A method that stores a byte representation of a {@code Program} object into an .out file
     * 
     * @apiNote Programs with requirments that exceed the total resources allocated at the cluster will be rejected on input!
     * 
     * @apiNote In the case that the file/directory does not exist, is created automaticly.
     * @param p The program to be dismissed.
     * @throws IOException
     */
    public void dismiss(Program p) throws IOException  {
        try {
            fos = new FileOutputStream(dismissed);
            oos =  new ObjectOutputStream(fos);

            oos.writeObject(p);
            System.out.println(CLI_IOHandler.underLine+"\n\tThe program with ID "+p.getID()+" has been dismissed.\n"+CLI_IOHandler.underLine);
        } catch (Exception e) {
            System.out.println("Nailed it exception occurred\t !Rejected programs may not have been stored to properly!");
            
            e.printStackTrace();
        } finally {
            oos.close();
            fos.close();
        }
    }


}
