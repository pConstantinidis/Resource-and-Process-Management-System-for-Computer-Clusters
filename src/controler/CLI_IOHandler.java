package src.controler;

import java.util.Scanner;

import lib.Dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;
import src.model.Admin;
import src.model.ClusterAdmin;
import src.model.Program;
import src.model.VirtualMachine;

public abstract class CLI_IOHandler extends Admin {
    
    private final String ignoreInputSequence = "-";
    private final Scanner reader = new Scanner(System.in);           //TODO To be closed at the end of the program by the last possible method to be called (or somethung like that)
    private final String doubleLine = "====================================================================================";
    protected final String underLine = "____________________________________________________________________________________";
    protected final String intro = doubleLine+"\n\tThis is a Resource and Process Managment System for a Computer Cluster"+ "\n"+underLine+
                "\n\n\tA software developed for the OOP course fully developed in Java.\n\n\tAuthor: pConstantinidis\n\tDate: 5/2023\n";
    private final String vmPresentation = "\n"+doubleLine+"\nThere are 4 VM types you can choose from...\n\t[1] Plain VM (CPU, RAM, SSD, selected OS)"+
                "\n\t[2] GPU accessible VM (plain + GPU)\n\t[3] Network accessible VM (plain + given bandwidth)\n\t[4] VM with GPU and network access";
    private final String osPresentation = "\n\n\tThere are 3 Operating Systems available.\n\n\t\t"+
                OS.WINDOWS.toString()+"\n\t\t"+OS.UBUNTU.toString()+"\n\t\t"+OS.FEDORA +"\n\n ~Choose one : ";
    protected final String introducePrograms = "\n"+doubleLine+"\n\tNow you can submit any programs that you wish to be executed on the cluster."+
                "\n\n\tThe only limitation concerns the overall hardware capability's of the VMs.\n"+doubleLine;
    private final String programErrMsg = "Your input either exceeds the sources that are used currently by the VMs or is just invalid";

    /**
     * A method that displays to the user the curent state of the clusters reserve
     */
    private void reserveReport() {
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
    private final void showSuccessMsg(String msg) {
        System.out.println("\n"+underLine+"\n\t"+msg+ " completed successfuly!");
    }

    /**
     * A mehtod that asks the user what task he wants to perform.
     * 
     * @return The number (short) corresponding to the choice.
     */
    protected short showMenu() {
        System.out.print("\n"+doubleLine+"\n\t(0) Done with VMs technical specifications\n\t(1) Create a VM\n\t(2) Update an existing VM\n\t(3) Delete a VM\n\t(4) Report\n"+underLine+
        "\n Choose one of the above: ");

        return shortReader((short) 0, (short) 5, "",  (short)0, null);
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

    private short acquirGPU() {
        System.out.print("\n ~GPU - ");
        return shortReader((short) 1, (short) Globals.getAvailableGpu(), "Input out of valid range : 1 - "+Globals.getAvailableGpu(), (short) 3, null);
    }

    private short acquirBandwidth() {
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
    protected short shortReader(short min, short max, String errMsg, short msgFrequency, String ignoreSequnce) {
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
                    reader.nextLine();
                    countInvalid=0;
                }
                else if (!isValid) {
                    System.out.print("\n"+underLine+"\n"+ "Try again: ");
                    reader.nextLine();
                }
            } catch (NumberFormatException e) {
                System.out.print("\n"+underLine+"\n"+ "Try again: ");
                reader.nextLine();
                isValid = false;
                countInvalid++;
            }
        } while(!isValid);
        return Short.parseShort(input);
    }
    /**
     * A method that creates a VM according to user input.
     * 
     * @return
     */
    protected void createVm() {
        short vmChoice;
        short [] basicData = new short[3];    // [CPU, RAM, SSD]
        OS os;
        System.out.print(vmPresentation+"\n"+underLine+"\n ~VM Type: ");
        vmChoice = shortReader((short) 1, (short) 4, null, (short) 3, null);
        reserveReport();
        basicData = acquirBasicData();
        os = acquirOS(null);

        try {
            switch (vmChoice) {
                case 1:
                    createPlainVm(basicData[0], basicData[1], os, basicData[2]);
                    showSuccessMsg("Plain VM creation");
                    break;

                case 2:
                    short gpu = acquirGPU();    // This way the aritmetical submition won't split
                    createVmGPU(basicData[0], basicData[1], os, basicData[2], gpu);
                    showSuccessMsg("GPU accessible VM creation");
                    break;

                case 3:
                    short bandwidth = acquirBandwidth();
                    createVmNetworked(basicData[0], basicData[1], os, basicData[2], bandwidth);
                    showSuccessMsg("Network accessible VM creation");
                    break;

                case 4:
                    short gpu1 = acquirGPU(), bandwidth1 = acquirBandwidth();
                    createVmNetworkedGpu(basicData[0], basicData[1], os, basicData[2], gpu1, bandwidth1);
                    showSuccessMsg("VM with GPU and network access creation");
            }
        } catch(InputOutOfAdminsStandartsException e) {
            System.err.println("\n\n\t*FATAL ERROR*");
            return;
        }
    }

    /**
     * A method that aqcuires a boolean input from tha user.
     * 
     * @param msg The context of the input.
     * @return {@code true} if the answer was positive {@code false} otherwise.
     */
    protected boolean verify(String msg) {
        char input;
        System.out.print("\n\n\t"+ msg +" [Y/N]?  ~");
        do {
        input = Character.toLowerCase(reader.next().charAt(0));
        if (input != 'y' && input != 'n') {
            reader.nextLine();
            System.out.print("\n\tTry again [Y/N]  ~");
        }
        } while (input != 'y' && input != 'n');

        if (input == 'n') return false;
        else if (input == 'y') return true;
        else return false;
    }

    /**
     * A method that updates the elements of a VM.
     */
    protected void updateVm() {
        if (getNumOfVms() == 0) {
            System.out.println("\n\t\tCurrently there are no VMs in the cluster!");
            return;
        }
        int vmId = readID("updated");
        VirtualMachine vm = getVmByID(vmId);
        short newCpu, newRam, newDrive, newGpu, newBandwidth;
        boolean hasBeenUpdated=false;
        OS newOs;

        System.out.println("\n"+underLine+"\n\nIn order to update the VM we will follow the convention : to not change an attribute type '-' (dash)");
        System.out.print("\n\t~CPU cores : ");
        newCpu = shortReader((short) 1, (short) Globals.getAvailableCpu(), "Input out of valid range : 1 - "+Globals.getAvailableCpu(), (short) 3, ignoreInputSequence);
        System.out.print("\n\t~RAM : ");
        newRam = shortReader((short) 1, (short) Globals.getAvailableRam(), "Input out of valid range : 1 - "+Globals.getAvailableRam(), (short) 3, ignoreInputSequence);
        System.out.print("\n\t~SSD : ");
        newDrive = shortReader((short) 1, (short) Globals.getAvailableDrive(), "Input out of valid range : 1 - "+Globals.getAvailableDrive(), (short) 3, ignoreInputSequence);

        switch (getVmsClass(vm)) {
            case "VmGPU":
                System.out.print("\n\t~GPU : ");
                newGpu = shortReader((short) 1, (short) Globals.getAvailableGpu(), "Input out of valid range : 1 - "+Globals.getAvailableGpu(), (short) 3, ignoreInputSequence);
                if (newGpu!=0 && verify("Are you sure that you want to update the VM with ID : "+ vmId)) {
                    try {
                        updateGPU(vmId, newGpu);
                        hasBeenUpdated = true;
                    } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                        System.err.println("\n\n\t*FATAL ERROR*");
                        return;
                    }
                    break;
                }

            case "VmNetworked":
                System.out.print("\n\t~Bandwidth : ");
                newBandwidth = shortReader((short) 4, (short) Globals.getAvailableBandwidth(), "Input out of valid range : 4 - "+Globals.getAvailableBandwidth(), (short) 3, ignoreInputSequence);
                if (newBandwidth!=0 && verify("Are you sure that you want to update the VM with ID : "+ vmId)) {
                    try {
                        updateBandwidth(vmId, newBandwidth);
                        hasBeenUpdated = true;
                    } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                        System.err.println("\n\n\t*FATAL ERROR*");
                        return;
                    }
                    break;
                }

            case "VmNetworkedGPU":
                System.out.print("\n\t~GPU : ");
                newGpu = shortReader((short) 1, (short) Globals.getAvailableGpu(), "Input out of valid range : 1 - "+Globals.getAvailableGpu(), (short) 3, ignoreInputSequence);
                
                System.out.print("\n\t~Bandwidth : ");
                newBandwidth = shortReader((short) 4, (short) Globals.getAvailableBandwidth(), "Input out of valid range : 4 - "+Globals.getAvailableBandwidth(), (short) 3, ignoreInputSequence);
                if ((newGpu!=0 || newBandwidth!=0) && verify("Are you sure that you want to update the VM with ID : "+ vmId)) {
                    try {
                        if(newGpu!=0) updateBandwidth(vmId, newBandwidth);
                        if(newBandwidth!=0) updateGPU(vmId, newGpu);
                        hasBeenUpdated= true;
                    } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                        System.err.println("\n\n\t*FATAL ERROR*");
                        e.printStackTrace();
                        return;
                    }
                }
        }
        newOs = acquirOS(ignoreInputSequence);

        if (hasBeenUpdated || ((newCpu!=0 || newRam!=0 || newDrive!=0) && verify("Are you sure that you want to update the VM with ID : "+ vmId))) {              //! TODO  TO BE RATED
            try {                                                                                                                   //! TODO NEED TO CHANGE THE PROGRAMS RESPONSE WHEN A "FATAL" OCCURRUS
                if (newCpu != 0) updateCPU(vmId, newCpu);
                if (newRam != 0) updateRAM(vmId, newRam);
                if (newDrive != 0) updateDrive(vmId, newDrive);
                if (newOs!=null && verify("Are you sure that you want to update the VMs operating system")) updateOS(vmId, newOs);
            } catch (ClassCastException | NullPointerException | InputOutOfAdminsStandartsException e) {
                System.err.println("\n\n\t*FATAL ERROR*");
                return;
            }
            showSuccessMsg("VM update");
        }
    }

    /**
     * TODO
     * @param operation
     */
    private int readID(String operation) {
        System.out.print("\n"+doubleLine+"\n ~The ID of the VM to be "+operation+" is : ");
         return shortReader((short) 1, (short) getNumOfVms(), "There is no such ID", (short)1, null);
    }
    
    /**
     * TODO
     */
    protected void deleteVm() {
        if (getNumOfVms() == 0) {
            System.out.println("\n\t\tCurrently there are no VMs in the cluster!");
            return;
        }
        int vmId = readID("deleted");
        
        if (verify("Are you sure that you want to delete the VM with ID : "+ vmId)) {
            try {deleteVm(vmId); } catch(IllegalArgumentException e) {
                System.err.println("\n\n\t*FATAL ERROR*");
                return;
            }
        }
        showSuccessMsg("VM deletion");
    }

    /**
     * 
     */
    protected void report() {  //! TODO

    }

    protected void acquirProgramData() {
        short cpu, ram, ssd, gpu, bandwidth, expDuration;
        
        System.out.println("\n\t Submit needed specs...");
        System.out.print(" ~CPU cores required (>0) - ");
        cpu = shortReader((short) 1, (short) Globals.getInUseCpu(), programErrMsg, (short) 2, null);
        System.out.print("\n\t ~RAM required (>0) - ");
        ram = shortReader((short) 1, (short) Globals.getInUseRam(), programErrMsg, (short) 2, null);
        System.out.print("\n\t ~Drive required (>0) - ");
        ssd = shortReader((short) 1, (short) Globals.getInUseDrive(), programErrMsg, (short) 2, null);

        System.out.print("\n\t ~GPU required - ");
        gpu = shortReader((short) 0, (short) Globals.getInUseGpu(), programErrMsg, (short) 2,"-");
        System.out.print("\n\t ~Bandwidth required - ");
        bandwidth = shortReader((short) 0, (short) Globals.getInUseBandwidth(), programErrMsg, (short) 2, "-");

        System.out.print("\n"+underLine+"\nThe expected execution time of the program is: ");
        expDuration = shortReader((short) 0, (short) ClusterAdmin.MAX_PROGRAM_RUNTIME, "The expected execution time is too long", (short) 1, null);

        addProgram(new Program(cpu, ram, ssd, gpu, bandwidth, expDuration));
        queueCapacity++;
    }



}
