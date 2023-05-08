package src.CLI;

import src.controler.InputHandler;

public final class CommandLineInterface {

    public static void main(String[] args) {
        InputHandler IO_Handler = new InputHandler();
        System.out.println(InputHandler.intro);
        short choice;      //TODO choice==0
        
        outer: while (true) {      
            choice = IO_Handler.showMenu();
            switch (choice) {
                case 0: break outer;

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
    }


}
