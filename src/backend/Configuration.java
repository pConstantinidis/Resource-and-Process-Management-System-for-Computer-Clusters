package src.backend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.StringTokenizer;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import src.model.PlainVM;
import src.model.VirtualMachine;
import src.model.VmGPU;
import src.model.VmNetworked;
import src.model.VmNetworkedGPU;

/**
 * TODO
 */
public final class Configuration {
    private FileReader fReader;
    
    /**
     * A static method that reads data from a {@code config} text file and returns a list of the equivilant VMs.
     * <p>The format of the file must be:
     * <blockquote><pre>
     *   os:(String),cores:(Integer),ram:(Integer),ssd:(Integer),bandwidth(or)gpu:(Integer),gpu:(Integer).
     * </pre></blockquote>
     * 
     * @Warning In the case that the configuration format differs from the above, unexpected results may occurred.
     *          
     * @return An {@code ArrayList} with the {@code VirtualMachine} objects casted accordingly.
     * @throws IOException
     */
    private static ArrayList<VirtualMachine> configVMs() throws IOException {
        FileInputStream vms = new FileInputStream("./conf/vms.config");
        BufferedReader fReader = new BufferedReader(new FileReader("./conf/vms.config"));

        ArrayList<VirtualMachine> data = new ArrayList<>();
        String [] line = fReader.readLine().trim().split(",");
        String [] specs;
        boolean isGPUed=false, isNetworked=false;
        
        while (line != null) {
            specs = new String[6];
            for (int i=0; i<line.length; i++) {
                specs[i] = line[i].split(":")[1];
            }

            if (line.length == 5) {
                if (Character.toUpperCase(line[4].charAt(0)) == 'B')
                    isNetworked = true;
                else isGPUed = true;
            }
            else if (line.length != 4) {                                    //! Takes forgranted that the conf file hase the examples order.
                if (Integer.parseInt(specs[4]) != 0) isNetworked = true;
                if (Integer.parseInt(specs[5]) != 0) isGPUed = true;
            }
            if (Globals.isOSValid(specs[0]) == null) throw new IOException("invalid OS");

            try {
                if (isGPUed && isNetworked)
                    data.add( new
                        VmNetworkedGPU(Integer.parseInt(specs[1]), Integer.parseInt(specs[2]), Globals.isOSValid(specs[0]),
                        Integer.parseInt(specs[3]), Integer.parseInt(specs[5]), Integer.parseInt(specs[4])));
                else if (isGPUed && !isNetworked)
                    data.add( new
                        VmGPU(Integer.parseInt(specs[1]), Integer.parseInt(specs[2]), Globals.isOSValid(specs[0]),
                        Integer.parseInt(specs[3]), Integer.parseInt(specs[4])));
                else if (!isGPUed && isNetworked)
                    data.add( new
                        VmNetworked(Integer.parseInt(specs[1]), Integer.parseInt(specs[2]), Globals.isOSValid(specs[0]),
                        Integer.parseInt(specs[3]), Integer.parseInt(specs[4])));
                else
                    data.add( new
                        PlainVM(Integer.parseInt(specs[1]), Integer.parseInt(specs[2]), Globals.isOSValid(specs[0]), Integer.parseInt(specs[3])));
            } catch(Exception e) {
                throw new IOException("invalid VM specs");
            }
            line = fReader.readLine().trim().split(",");
        }
        vms.close();
        fReader.close();
        return data;
    }


    /**
     * 
     * @return
     * @throws IOException
     * @see #configVMs()
     */
    private static String [][] configPrograms() throws IOException {
        FileInputStream programs = new FileInputStream("./conf/programs.config");
        BufferedReader fReader = new BufferedReader(new FileReader("./conf/programs.config"));


    }


}
