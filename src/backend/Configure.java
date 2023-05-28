package src.backend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;
import src.model.ClusterAdmin;
import src.model.Program;

/**
 * TODO
 */
public final class Configure implements Serializable {

    private ClusterAdmin admin = ClusterAdmin.getAdmin();

    private int invalidVMs, invalidPrgs;
    /**
     * @return The number of the VMs that where invalid found in the file, based on the current state of the cluster.
     */
    public int numOfInvalidVMs() {
        return invalidVMs;
    }

    /**
     * @return The number of the programs that where invalid found in the file, based on the current state of the cluster.
     */
    public int numOfInvalidPrgs() {
        return invalidPrgs;
    }
    
    /**
     * TODO
     * <p>The format of the file should be:<b>resource:(it's data type),e.t.c.
     *
     * <p>Keep in mind that an OS must be defined.
     * 
     * @apiNote The VMs will be assigned with IDs in ascending order, based on the order that they where inputed.
     * 
     * @return The number of valid VMs.
     * @throws IOException
     * @throws InputOutOfAdminsStandartsException
     * @see #configPrograms()
     */
    public void configVMs() throws IOException, InputOutOfAdminsStandartsException  {
        FileInputStream vms = new FileInputStream("./cfg/vms.config");
        BufferedReader fReader = new BufferedReader(new FileReader("./cfg/vms.config"));

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
                invalidVMs++;
                String nullTester = fReader.readLine();
                if (nullTester != null)
                line = nullTester.trim().split(",");
                else line = null;
                continue;
            }

            switch (concludeType(gpu, bandwidth)) {
                case 4:
                    admin.createVmNetworkedGpu(cpu, ram, os, drive, gpu, bandwidth);
                    break;
                case 2:
                    admin.createVmGPU(cpu, ram, os, drive, gpu);
                    break;
                case 3:
                    admin.createVmNetworked(cpu, ram, os, drive, bandwidth);
                    break;
                case 1:
                    admin.createPlainVm(cpu, ram, os, drive);
                    break;
            }
            
            String nullTester;
            nullTester = fReader.readLine();
            if (nullTester != null)
                line = nullTester.trim().split(",");
            else line = null;
        }
        vms.close();
        fReader.close();
    }

    private int getBandwidth(String[] line, boolean isVm) throws NumberFormatException, InputOutOfAdminsStandartsException {
        String [] resource;
        for (String str:line) {
            resource = str.split(":");
            if (resource[0].toLowerCase().equals("bandwidth")) {

                if(isVm) Globals.isBandwidthValid(Integer.parseInt(resource[1]));
                else {
                    if (Integer.parseInt(resource[1]) < 0 || Integer.parseInt(resource[1]) > Globals.getInUseBandwidth())
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
     * A method that reads data from a {@code config} text file.
     * <p>The format of the file should be:<b>resource:(it's data type),e.t.c.
     * 
     * @return The number of valid programs found in the file.
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws InputOutOfAdminsStandartsException
     * @see #configVMs()
     */
    public int configPrograms() throws IOException {
        FileInputStream programs = new FileInputStream("./cfg/programs.config");
        BufferedReader fReader = new BufferedReader(new FileReader("./cfg/programs.config"));
        int validPrograms=0;

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
                invalidPrgs++;
                String nullTester = fReader.readLine();
                if (nullTester != null)
                line = nullTester.trim().split(",");
                else line = null;
                continue;
            }
            validPrograms++;//23
            admin.addProgram(new Program(cpu, ram, drive, gpu, bandwidth, time));
            
            String nullTester;
            nullTester = fReader.readLine();
            if (nullTester != null)
                line = nullTester.trim().split(",");
            else line = null;
        }

        programs.close();
        fReader.close();
        return validPrograms;
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
