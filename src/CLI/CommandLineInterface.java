package src.CLI;

import java.util.Scanner;
import lib.utils.Globals;
import src.model.ClusterAdmin;

public final class CommandLineInterface {
    
    private final ClusterAdmin admin = new ClusterAdmin();
    private final static Scanner reader = new Scanner(System.in);
    private final static String doubleLine = "====================================================================================";
    private final static String underLine = "____________________________________________________________________________________";
    private final static String intro = doubleLine+"\n\tThis is a Resource and Process Managment System for a Computer Cluster"+ "\n"+underLine+
    "\n\n\tA software model for the OOP course fully developed in Java.\n\n\tAuthor: pConstantinidis\n\tDate: 5/2023\n"+doubleLine;
    private final static String vmPresentation = "\n"+doubleLine+"\nThere are 4 VM types you can choose from...\n\t[1] Plain VM (CPU, RAM, SSD, selected OS)"+
    "\n\t[2] GPU accessible VM (plain + GPU)\n\t[3] Network accessible VM (plain + given bandwidth)\n\t[4] VM with GPU and network access";
    /**
     * A method that displays to the user the curent state of the clusters reserve
     */
    private static void reserveReport() {
        System.out.println("\n"+underLine);
        System.out.println("\tThat's the curent availability of the clusters hardware\n\n\t\t"+
        "CPU cores : "+Globals.getAvailableCpu() +"\n\t\tRAM (GB) : "+Globals.getAvailableRam()
        +"\n\t\tSSD (GB) : "+Globals.getAvailableDrive() +"\n\t\tGPUs : "+Globals.getAvailableGpu()+
        "\n\t\tBandwidth (GB/sec) : "+Globals.getAvailableBandwidth()+"\n");
        System.out.println("\n"+underLine);
    } 

    /**
     * A mehtod that asks the user what task he wants to perform.
     * 
     * @return The number (short) correspondng to the choice.
     */
    private short showMenu() {
        System.out.print("\n"+"\t(0) Exit\n\t(1) Create a VM\n\t(2) Update an existing VM\n\t(3) Delete a VM\n\t(4) Report\n"+underLine+
        "\n Choose one of the above: ");

        return shortReader((short) 0, (short) 5);
    }




//TODO

    /**
     * A method that acquires from the user the CPU, RAM and SSD required to create a VM.
     * 
     * @return An array with the format [CPU, RAM, SSD]
     */
    private short [] acquirBasicData() {


        cpu = shortReader((short) 1, (short) Globals.getAvailableCpu());
        ram = shortReader((short) 0, (short) Globals.getAvailableRam());
        drive = shortReader((short) 0, (short) Globals.getAvailableDrive());
    }








    /**
     * A general purpose method that acquires an OS from the user.
     * 
     * @return The OS acquired.
     */
    private OS acquirOS() {





        
    }






    /**
     * A method that reads a short number from the console and checks if he falls between to bounds.
     * 
     * @param min The lower bound
     * @param max The upper bound
     */
    private short shortReader(short min, short max) {
        boolean isValid;
        String input;

        do {
            input = reader.next();
            try {
                if (Short.valueOf(input) < min || Short.valueOf(input) > max) isValid = false;
                else isValid = true;
                if (!isValid) System.out.print("\n"+underLine+"\n"+ "Try again: ");

            } catch (NumberFormatException e) {
                System.out.print("\n"+underLine+"\n"+ "Try again: ");
                isValid = false;
            }
        } while(!isValid);
        return Short.parseShort(input);
    }

    private boolean createVm() {
        short vmChoice;
        short cpu, ram, drive;
        System.out.print(vmPresentation+"\n"+underLine+"\n VM Type: ");
        vmChoice = shortReader((short) 0, (short) 4);
        reserveReport();


        switch (vmChoice) {
            case 1:

     //           admin.createPlainVm(cpu, ram, null, drive);
        }






        return true;
    }
    
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        short choice;
        
        System.out.println(intro);
        choice = cli.showMenu();
        
        switch (choice) {
            case 0: System.exit(0);

            case 1: cli.createVm();

            case 2:

            case 3:
            
            case 4:
            
            case 5:
            
           default:                
        }

        cli.reader.close();
    }

}
