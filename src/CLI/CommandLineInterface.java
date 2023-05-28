package src.CLI;

import lib.utils.Globals;
import src.controler.CLI_IOHandler;

/**
 * ! ! !    TODO
 * This class is propably going to be merged with the {@code InputHandler} class and form a CLI class.
 * The CLI class and the GUI class will communicate there inputs to the {@code ClusterAdmin} class and form there (and not only)
 * the errors will be thrown and each UI will iplement different error handling
 
 * @author pConstantinidis
 */
public final class CommandLineInterface extends CLI_IOHandler {
    
    public static void main(String[] args) {
        new CommandLineInterface();
        System.exit(0);
    }
    
    
    public CommandLineInterface() {
        System.out.println(intro);
        short choice;
        
        int fileStat = admin.autoConf();
        if (fileStat == -1) {
            outer: while (true) {
                choice = showMenu();
                switch (choice) {
                    case 0: 
                        if (Globals.areThereAnyVms()) break outer;
                        break;
                    case 1: createVm();
                    break;
                    
                    case 2: updateVm();
                    break;
                    
                    case 3: deleteVm();
                    break;
                    
                    case 4: report();
                    break;
                }
            }
        }
        if (fileStat==0 || fileStat==-1) {
            System.out.println(introducePrograms);
            do {
                System.out.println(underLine+"\nProgram added succesfuly with ID: "+acquirProgramData()+"\n"+underLine);
            } while (verify("Do you want to add another program"));
        }
        
        admin.queuePrograms();

        int programsFinished = 0, loadStatus = 0, programsDismissed = 0;
        while (programsFinished < admin.getNumOfPrgs()-programsDismissed) {

            while (!admin.isQueueEmpty()) {
                loadStatus = admin.loadProgram();
                if (loadStatus != 0) break;
                
            }
            if (loadStatus == -1) {
                programsDismissed++;
                loadStatus = 1;
            }
            programsFinished += admin.updateRunningPrograms();
        }
        System.out.println(doubleLine+"\n\tSession ended : "+programsFinished+" programs where executed.\n"+doubleLine+"\n");
        closeReader();
    }
}
