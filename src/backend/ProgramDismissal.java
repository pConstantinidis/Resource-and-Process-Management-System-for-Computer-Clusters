package src.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import src.model.ClusterAdmin;
import src.model.Program;

/**
 * A class that handles the programs that are to be dismissed.
 * 
 * @author pConstantinidis
 */
public final class ProgramDismissal {

    private static Program p;
    private final static File dismissed = new File("./log/rejected.out");
    private static FileWriter writer;
    private static ObjectOutput oos;                                                                    //!

    public static void dismiss(Program p) throws IOException {
        ProgramDismissal.p = p;
        if (dismissed.canWrite() && dismissed.exists()) {                                                     //! NEED TO LEARN ABOUT THESE
            writer = new FileWriter(dismissed, true);

            oos = new ObjectOutputStream(new FileOutputStream(dismissed));                              //!
            oos.writeChars("Not a UTF text");
            
            ObjectInput ois = new ObjectInputStream(new FileInputStream(dismissed));                    //!
            System.out.println(ois.readChar());


            //writer.append(p.getID()+':'+p.getCoresRequired()+'|'+p.getRamRequired()+'|'+p.getDriveRequired()+'|'+p.getGpuRequired()+'|'+p.getBandwidthRequired()+"\n");

            ois.close();
            writer.close();
            return;
        }
        throw new IOException();
    }


}
