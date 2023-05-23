package src.backend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.IllegalBlockSizeException;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;
import src.model.PlainVM;
import src.model.Program;
import src.model.VirtualMachine;
import src.model.VmGPU;
import src.model.VmNetworked;
import src.model.VmNetworkedGPU;

/**
 * TODO
 */
public final class Configure {
    
    /**
     * A method that reads data from a {@code config} text file and returns a list of the equivilant VMs.
     * <p>The format of the file should be:<b>resource:(it's data type),e.t.c.
     *
     * <p>Keep in mind that an OS must be defined.
     * 
     * @apiNote <b>Warning: to maintain the softwares interaction consistant, if a single exception occurres it will discard all the files data and will ask the user to submit them manually.
     * 
     * @return An {@code ArrayList} with the {@code VirtualMachine} objects casted accordingly.
     * @throws IOException
     * @throws InputOutOfAdminsStandartsException
     * @see #configPrograms()
     */
    public ArrayList<VirtualMachine> configVMs() throws IOException, InputOutOfAdminsStandartsException {
        FileInputStream vms = new FileInputStream("./cfg/vms.config");
        BufferedReader fReader = new BufferedReader(new FileReader("./cfg/vms.config"));

        ArrayList<VirtualMachine> data = new ArrayList<>();
        
        OS os=null;
        int cpu=0, ram=0, drive=0, gpu=0, bandwidth=0;
        String [] line = fReader.readLine().trim().split(",");        
        while (line != null) {

            try {
                os = getOS(line);
                cpu = getCPU(line, true);
                ram = getRAM(line, true);
                drive = getDrive(line, true);
                gpu = getGPU(line, true);
                bandwidth = getBandwidth(line, true);
            } catch (Exception e) {
                throw new IllegalArgumentException("invalid resource value");
            } finally {
                vms.close();
                fReader.close();
            }

            switch (concludeType(gpu, bandwidth)) {
                case 4:
                    data.add( new VmNetworkedGPU(cpu, ram, os, drive, gpu, bandwidth));
    
                case 2:
                    data.add( new VmGPU(cpu, ram, os, drive, gpu));
    
                case 3:
                    data.add( new VmNetworked(cpu, ram, os, drive, bandwidth));
    
                case 1:
                    data.add( new PlainVM(cpu, ram, os, drive));
            }
            
            line = fReader.readLine().trim().split(",");
        }
        vms.close();
        fReader.close();
        return data;
    }

    private int getBandwidth(String[] line, boolean isVm) throws NumberFormatException, InputOutOfAdminsStandartsException {
        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("bandwidth")) {

                if(isVm) Globals.isGpuValid(Integer.parseInt(resource[1]));
                else {
                    if (Integer.parseInt(resource[1]) < 4 || Integer.parseInt(resource[1]) > Globals.getInUseBandwidth())
                        throw new InputOutOfAdminsStandartsException();
                }
                return Integer.parseInt(resource[1]);
            }
        }
        return 0;
    }

    private int getGPU(String[] line, boolean isVm) throws NumberFormatException, InputOutOfAdminsStandartsException {
        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("gpu")) {

                if (isVm) Globals.isGpuValid(Integer.parseInt(resource[1]));
                else {
                    if (Integer.parseInt(resource[1]) < 1 || Integer.parseInt(resource[1]) > Globals.getInUseGpu())
                        throw new InputOutOfAdminsStandartsException();
                }
                return Integer.parseInt(resource[1]);
            }
        }
        return 0;
    }

    private int getDrive(String[] line, boolean isVm) throws NumberFormatException, InputOutOfAdminsStandartsException, IOException {
        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("ssd")) {

                if(isVm) Globals.isDriveValid(Integer.parseInt(resource[1]));
                else {
                    if (Integer.parseInt(resource[1]) < 1 || Integer.parseInt(resource[1]) > Globals.getInUseDrive())
                        throw new InputOutOfAdminsStandartsException();
                }
                return Integer.parseInt(resource[1]);
            }
        }
        throw new IOException("ssd not found");
    }

    private int getRAM(String[] line, boolean isVm) throws NumberFormatException, InputOutOfAdminsStandartsException, IOException {
        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("ram")) {

                if (isVm) Globals.isRamValid(Integer.parseInt(resource[1]));
                else {
                    if (Integer.parseInt(resource[1]) < 1 || Integer.parseInt(resource[1]) > Globals.getInUseRam())
                        throw new InputOutOfAdminsStandartsException();
                }
                return Integer.parseInt(resource[1]);
            }
        }
        throw new IOException("ram not found");
    }

    private int getCPU(String[] line, boolean isVm) throws NumberFormatException, InputOutOfAdminsStandartsException, IOException {

        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("cores")) {
                if (isVm) Globals.isCpuValid(Integer.parseInt(resource[1]));
                else {
                    if (Integer.parseInt(resource[1]) < 1 || Integer.parseInt(resource[1]) > Globals.getInUseCpu())
                        throw new InputOutOfAdminsStandartsException();
                }
                return Integer.parseInt(resource[1]);
            }
        }
        throw new IOException("CPU not found");
    }

    private OS getOS(String[] line) throws IOException {

        String [] resource;
        OS os;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("os")) {
                os = Globals.isOSValid(resource[1]);
                if (os!=null) return os;
            }
        }
        throw new IOException("OS not found");
    }


    /**
     * @return 1 for {@code PlainVM} <p>2 for {@code VmGPU} <p>3 for {@code VmNetworked} <p>4 for {@code VmNetworkedGPU}
     */
    private int concludeType(int gpu, int bandwidth) {
        if (gpu == 0 && bandwidth == 0) return 1;
        else if (gpu != 0 && bandwidth == 0) return 2;
        else if (gpu == 0 && bandwidth != 0) return 3;
        return 4;
    }

    /**
     * A method that reads data from a {@code config} text file and returns a list of the equivilant programs.
     * <p>The format of the file should be:<b>resource:(it's data type),e.t.c.
     * 
     * @return An {@code ArrayList} with the {@code VirtualMachine} objects casted accordingly.
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws InputOutOfAdminsStandartsException
     * @see #configVMs()
     */
    public ArrayList<Program> configPrograms() throws IOException, IllegalBlockSizeException {
        FileInputStream programs = new FileInputStream("./cfg/programs.config");
        BufferedReader fReader = new BufferedReader(new FileReader("./cfg/programs.config"));

        ArrayList<Program> data = new ArrayList<>();
        int cpu=0, ram=0, drive=0, gpu=0, bandwidth=0, time=0;
        String [] line = fReader.readLine().trim().split(",");        
        while (line != null) {

            try {
                cpu = getCPU(line, false);
                ram = getRAM(line, false);
                drive = getDrive(line, false);
                gpu = getGPU(line, false);
                bandwidth = getBandwidth(line, false);
                time = getExeTime(line);
            } catch (Exception e) {
                throw new IllegalBlockSizeException("invalid resource value");
            } finally {
                programs.close();
                fReader.close();
            }
            data.add(new Program(cpu, ram, drive, gpu, bandwidth, time));
            
            line = fReader.readLine().trim().split(",");
        }
        return data;
    }

    private int getExeTime(String[] line) throws IOException, NumberFormatException, InputOutOfAdminsStandartsException {
        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("time")) {

                if (Integer.parseInt(resource[1]) <= 0) throw new IllegalArgumentException("illegal execution time value");
                return Integer.parseInt(resource[1]);
            }
        }
        throw new IOException("execution time not found");
    }


}
