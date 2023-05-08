package src.CLI;

import src.controler.InputHandler;

public final class CommandLineInterface {

    public static void main(String[] args) {
        InputHandler IO_Handler = new InputHandler();
        System.out.println(intro);
        short choice;      //TODO choice==0
        
        outer: while (true) {      
            choice = cli.showMenu();
            switch (choice) {
                case 0: break outer;

                case 1: cli.createVm();
                    break;

                case 2: cli.updateVm();
                    break;

                case 3: cli.deleteVm();
                    break;
            
                case 4: cli.report();
                    break;
            }
        }


    }


}
