package src.CLI;

import src.controler.InputHandler;
import src.model.ClusterAdmin;

/**
 * ! ! !    TODO
    * This class is propably going to be merged with the {@code InputHandler} class and form a CLI class.
    * The CLI class and the GUI class will communicate there inputs to the {@code ClusterAdmin} class and form there (and not only)
    * the errors will be thrown and each UI will iplement different error handling
 */
public final class CommandLineInterface {

    public static void main(String[] args) {
        InputHandler IO_Handler = new InputHandler();
        System.out.println(InputHandler.intro);
        short choice;      //TODO choice==0
        
        outer: while (true) {      
            choice = IO_Handler.showMenu();
            switch (choice) {
                case 0: 
                    if (IO_Handler.getNumOfVMs() == 0) {
                        System.out.println(InputHandler.underLine+"\n It seems that there aren't currently"
                            +" any VMs running on the cluster\n In this case you can not proceed.");
                        break;
                    }
                    else break outer;

                case 1: IO_Handler.createVm();
                    break;

                case 2: IO_Handler.updateVm();
                    break;

                case 3: IO_Handler.deleteVm();
                    break;
            
                case 4: IO_Handler.report();
                    break;
            }
        }

        System.out.println(InputHandler.introducePrograms);
        do {
            IO_Handler.acquirProgramData();
        } while (InputHandler.verify("Do you want to add another program"));
        ClusterAdmin.loadPrograms();

    }
}
