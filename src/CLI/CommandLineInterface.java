package src.CLI;

import java.util.Scanner;
import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.Globals.OS;
import src.model.ClusterAdmin;

public final class CommandLineInterface {
    
    private final static String ignoreInputSequence = "-";
    private final ClusterAdmin admin = new ClusterAdmin();
    private final static Scanner reader = new Scanner(System.in);
    private final static String doubleLine = "====================================================================================";
    private final static String underLine = "____________________________________________________________________________________";
    private final static String intro = doubleLine+"\n\tThis is a Resource and Process Managment System for a Computer Cluster"+ "\n"+underLine+
    "\n\n\tA software developed for the OOP course fully developed in Java.\n\n\tAuthor: pConstantinidis\n\tDate: 5/2023\n"+doubleLine;
    private final static String vmPresentation = "\n"+doubleLine+"\nThere are 4 VM types you can choose from...\n\t[1] Plain VM (CPU, RAM, SSD, selected OS)"+
    "\n\t[2] GPU accessible VM (plain + GPU)\n\t[3] Network accessible VM (plain + given bandwidth)\n\t[4] VM with GPU and network access";
    private final static String osPresentation = "\n\n\tThere are 3 Operating Systems available.\n\n "+
    OS.WINDOWS.toString()+"\n "+OS.UBUNTU.toString()+"\n "+OS.FEDORA +"\n\n ~Choose one : ";
    
    /**
     * A method that displays to the user the curent state of the clusters reserve
     */
    private static void reserveReport() {
        System.out.println("\n"+doubleLine);
        System.out.println("\tThat's the curent availability of the clusters hardware\n\n\t\t"+
        "CPU cores : "+Globals.getAvailableCpu() +"\n\t\tRAM (GB) : "+Globals.getAvailableRam()
        +"\n\t\tSSD (GB) : "+Globals.getAvailableDrive() +"\n\t\tGPUs : "+Globals.getAvailableGpu()+
        "\n\t\tBandwidth (GB/sec) : "+Globals.getAvailableBandwidth()+"\n");
        System.out.println(underLine);
    } 
    
    /**
     * 
     * @param msg The messages topic.
     */
    private final static void showSuccessMsg(String msg) {
        System.out.println("\n"+underLine+"\n\t\t"+msg+ " completed successfuly!\n" +doubleLine);
    }

    /**
     * A mehtod that asks the user what task he wants to perform.
     * 
     * @return The number (short) correspondng to the choice.
     */
    private short showMenu() {
        System.out.print("\n"+"\t(0) Done with VMs technical specifications\n\t(1) Create a VM\n\t(2) Update an existing VM\n\t(3) Delete a VM\n\t(4) Report\n"+underLine+
        "\n Choose one of the above: ");

        return shortReader((short) 0, (short) 5, null,  (short)0, null);
    }

    /**
     * A method that acquires from the user the CPU, RAM and SSD required to create a VM.
     * 
     * @return An array with the format [CPU, RAM, SSD]
     */
    private short [] acquirBasicData() {
        short [] data = new short[3];

        System.out.println("\n\tSubmit needed specs...");
        System.out.print("\n ~CPU - ");
        data[0] = shortReader((short) 1, (short) Globals.getAvailableCpu(), "Input out of valid range : 1 - "+Globals.getAvailableCpu(), (short) 3, null);
        System.out.print("\n ~RAM - ");
        data[1] = shortReader((short) 1, (short) Globals.getAvailableRam(), "Input out of valid range : 1 - "+Globals.getAvailableRam(), (short) 3, null);
        System.out.print("\n ~SSD - ");
        data[2] = shortReader((short) 1, (short) Globals.getAvailableDrive(), "Input out of valid range : 1 - "+Globals.getAvailableDrive(), (short) 3, null);

        return data;
    }

    /**
     * A general purpose method that acquires an OS from the user.
     * 
     * @return The OS acquired or {@code null} if the {@code ignoreSequnce} is acquired.
     */
    private OS acquirOS(String ignoreSequnce) {
        String os;
        OS finalOS;
        
        System.out.print(osPresentation);
        do {
            os = reader.next();
            if (os.equals(ignoreSequnce))
                return null;
            finalOS = Globals.isOSValid(os);
            if (finalOS == null) {
                reader.nextLine();
                System.out.print("\n"+underLine+"\n" +" Try again: ");
        }
        } while (finalOS == null);
        return finalOS;
    }

    private static short acquirGPU() {
        System.out.print("\n ~GPU - ");
        return shortReader((short) 1, (short) Globals.getAvailableGpu(), "Input out of valid range : 1 - "+Globals.getAvailableGpu(), (short) 3, null);
    }

    private static short acquirBandwidth() {
        System.out.print("\n ~Bandwidth - ");
        return shortReader((short) 4, (short) Globals.getAvailableBandwidth(), "Input out of valid range : 4 - "+Globals.getAvailableBandwidth(), (short) 3, null);
    }

    /**
     * A method that reads a short number from the console and checks if he falls between to bounds.
     * 
     * @param min The lower bound
     * @param max The upper bound
     * @param errMsg A message printed to the console after {@code msgFrequency} invalid inputs
     * @param ignoreSequnce A character that if it's given the method will return 0
     */
    private static short shortReader(short min, short max, String errMsg, short msgFrequency, String ignoreSequnce) {
        boolean isValid;
        String input;
        int countInvalid = 0;

        do {
            input = reader.next();
            if (input.equals(ignoreSequnce))
                return 0;
            try {
                if (Short.valueOf(input) < min || Short.valueOf(input) > max) {
                    isValid = false;
                    countInvalid++;
                }
                    else isValid = true;
                if (!isValid && countInvalid >= msgFrequency) {
                    System.out.print("\n"+underLine+"\n\t"+errMsg+"\n"+underLine +"\n Try again: ");
                    countInvalid=0;
                }
                else if (!isValid) System.out.print("\n"+underLine+"\n"+ "Try again: ");

            } catch (NumberFormatException e) {
                System.out.print("\n"+underLine+"\n"+ "Try again: ");
                isValid = false;
            }
        } while(!isValid);
        return Short.parseShort(input);
    }
    /**
     * A method that creates a VM according to user input.
     * 
     * @return
     */
    private void createVm() {
        short vmChoice;
        short [] basicData = new short[3];    // [CPU, RAM, SSD]
        OS os;
        System.out.print(vmPresentation+"\n"+underLine+"\n VM Type: ");
        vmChoice = shortReader((short) 0, (short) 4, null, (short) 3, null);
        reserveReport();
        basicData = acquirBasicData();
        os = acquirOS(null);

        try {
            switch (vmChoice) {
                case 1:
                    admin.createPlainVm(basicData[0], basicData[1], os, basicData[2]);
                    showSuccessMsg("Plain VM creation");

                case 2:
                    short gpu = acquirGPU();    // This way the aritmetical submition won't split
                    admin.createVmGPU(basicData[0], basicData[1], os, basicData[2], gpu);
                    showSuccessMsg("GPU accessible VM creation");

                case 3:
                    short bandwidth = acquirBandwidth();
                    admin.createVmNetworked(basicData[0], basicData[1], os, basicData[2], bandwidth);
                    showSuccessMsg("Network accessible VM creation");

                case 4:
                    short gpu1 = acquirGPU(), bandwidth1 = acquirBandwidth();
                    admin.createVmNetworkedGpu(basicData[0], basicData[1], os, basicData[2], gpu1, bandwidth1);
                    showSuccessMsg("VM with GPU and network access creation");
            }
        } catch(InputOutOfAdminsStandartsException e) {
            System.err.println("\n\n\t*FATAL ERROR* deu to\n"+ e.getClass());
            if (e.getMessage() != null) System.out.print( " : "+e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * TODO
     */
    private void updateVm() {
        if (admin.getNumOfVms() == 0) {
            System.out.println("\n\t\tCurrently there are no VMs in the cluster!");
            return;
        }
        short vmId;
        System.out.print("\n"+doubleLine+"\n ~The ID of the VM to be updated is : ");
        vmId = shortReader((short) 1, (short) admin.getNumOfVms(), "There is no such ID", (short)1, null);
        short newCpu, newRam, newDrive, newGpu, newBandwidth;
        OS newOs;

        System.out.println("\n"+underLine+"\n\n\tIn order to update the VM we will follow the convention : to not change an attribute type '-' (dash)");
        System.out.print("\t\t~CPU cores : ");
        newCpu = shortReader((short) 1, (short) Globals.getAvailableCpu(), "Input out of valid range : 1 - "+Globals.getAvailableCpu(), (short) 3, ignoreInputSequence);
        System.out.print("\n\t\t~RAM : ");
        newRam = shortReader((short) 1, (short) Globals.getAvailableRam(), "Input out of valid range : 1 - "+Globals.getAvailableRam(), (short) 3, ignoreInputSequence);
        System.out.print("\n\t\t~SSD : ");
        newDrive = shortReader((short) 1, (short) Globals.getAvailableDrive(), "Input out of valid range : 1 - "+Globals.getAvailableDrive(), (short) 3, ignoreInputSequence);

        switch (admin.getVmsClass(vmId)) {
            case "VmGPU":
                System.out.print("\n\t\t~GPU : ");
                newGpu = shortReader((short) 1, (short) Globals.getAvailableGpu(), "Input out of valid range : 1 - "+Globals.getAvailableGpu(), (short) 3, ignoreInputSequence);
                try {
                    admin.updateGPU(vmId, newGpu);
                } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            case "VmNetworked":
                System.out.print("\n\t\t~Bandwidth : ");
                newBandwidth = shortReader((short) 4, (short) Globals.getAvailableBandwidth(), "Input out of valid range : 4 - "+Globals.getAvailableBandwidth(), (short) 3, ignoreInputSequence);
                try {
                    admin.updateBandwidth(vmId, newBandwidth);
                } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            
            case "VmNetworkedGPU":
                System.out.print("\n\t\t~GPU : ");
                newGpu = shortReader((short) 1, (short) Globals.getAvailableGpu(), "Input out of valid range : 1 - "+Globals.getAvailableGpu(), (short) 3, ignoreInputSequence);
                
                System.out.print("\n\t\t~Bandwidth : ");
                newBandwidth = shortReader((short) 4, (short) Globals.getAvailableBandwidth(), "Input out of valid range : 4 - "+Globals.getAvailableBandwidth(), (short) 3, ignoreInputSequence);
                
                try {
                    admin.updateBandwidth(vmId, newBandwidth);
                    admin.updateGPU(vmId, newGpu);
                } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        System.out.print("\t\t~OS : ");
        newOs = acquirOS(ignoreInputSequence);

        try {
            if (newCpu != 0) admin.updateCPU(vmId, newCpu);
            if (newRam != 0) admin.updateRAM(vmId, newRam);
            if (newDrive != 0) admin.updateDrive(vmId, newDrive);
            admin.updateOS(vmId, newOs);
        } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        
    }
    
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        System.out.println(intro);
        short choice;      //TODO choice==0
        
        outer: while (true) {      
            choice = cli.showMenu();
            switch (choice) {
                case 0: break outer;

                case 1: cli.createVm();

                case 2: cli.updateVm();

                case 3:
                
                case 4:
            }
        }






        cli.reader.close();
    }

}
