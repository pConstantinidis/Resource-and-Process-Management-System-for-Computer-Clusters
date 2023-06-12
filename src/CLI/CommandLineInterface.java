package src.CLI;

import lib.utils.Globals;

/**
 * An uper level class from which the CLI starts to execute.
 
 * @author pConstantinidis
 */
public final class CommandLineInterface extends CLI_IOHandler {
    
    public static void main(String[] args) {
        new CommandLineInterface(false).run();
        System.exit(0);
    }
    
    
    private int fileStat = -1;
    private boolean ignoreFileStat;

    public CommandLineInterface(boolean isGui) {
        ignoreFileStat = isGui;
    }
    
    private void run() {
        printIntro();
        autoConfigure();
        displayMenu();
        createPrgsManualy();
        processPrgs();
    }
    
    public void printIntro() {
        System.out.println(intro);
    }

    public void processPrgs() {
        if (ignoreFileStat || fileStat == 1) {
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
        }
        closeReader();
    }


    public void createPrgsManualy() {
        if (fileStat==0 || fileStat==-1) {
            System.out.println(introducePrograms);
            do {
                System.out.println(underLine+"\nProgram added succesfuly with ID: "+acquirProgramData()+"\n"+underLine);
            } while (verify("Do you want to add another program"));
        }
        fileStat = 1;
    }

    private void autoConfigure() {
        fileStat = admin.autoConf();
    }
    
    public void displayMenu() {
        short choice;
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
            fileStat = 0;
        }
    }

}
