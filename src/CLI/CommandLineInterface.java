package src.CLI;

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
    }


    public CommandLineInterface() {
        System.out.println(intro);
        short choice;
        
        outer: while (true) {
            if (!admin.vmsAutoConf())           //TODO//! to user test it
                System.out.println(underLine+"\n\tThere is invalid data among the .conf file\n\nPlease submit them manually...");
            choice = showMenu();
            switch (choice) {
                case 0: 
                    if (admin.getNumOfVms() == 0) {
                        System.out.println(underLine+"\n It seems that there aren't currently"
                            +" any VMs running on the cluster\n In this case you can not proceed.");
                        break;
                    }
                    else break outer;

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

        System.out.println(introducePrograms);
        do {
            acquirProgramData();
        } while (verify("Do you want to add another program"));
        
        admin.queuePrograms();

        admin.loadProgram();
        while (!admin.isQueueEmpty()) {
            admin.updateRunningPrograms();

            admin.loadProgram();
        }

    }
    
}
