package src.CLI;

import java.util.Scanner;

public final class CommandLineInterface {
    
    final static String doubleLine = "===============================================================================";
    final static String underLine = "_______________________________________________________________________________";
    final static String intro = doubleLine+"\n\tThis is a Resource and Process Managment System for a Computer Cluster"+ "\n"+underLine+
    "\n\n\tA software model for the OOP course fully developed in Java.\n\n\tAuthor: pConstantinidis\n\tDate: 5/2023\n"+doubleLine;

    /**
     * A mehtod that asks the user what task he wants to perform.
     * 
     * @return The number (short) correspondng to the choice.
     */
    private short showMenu() {
        Scanner reader = new Scanner(System.in);
        String choice;
        boolean isValid;

        System.out.print("\n"+"\t(0) Exit\n\t(1) Create a VM\n\t(2) Update an existing VM\n\t(3) Delete a VM\n\t(4) Report\n"+underLine+
        "\n Choose one of the above: ");

        do {
            choice = reader.next();
            try {
                if (Short.valueOf(choice) < 0 || Short.valueOf(choice) > 5) isValid = false;
                else isValid = true;
                if (!isValid) System.out.print("\n"+underLine+"\n"+ "Try again: ");

            } catch (NumberFormatException e) {
                System.out.print("\n"+underLine+"\n"+ "Try again: ");
                isValid = false;
            }
        } while(!isValid);
    
        reader.close();
    return Short.valueOf(choice);
    }

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        Scanner reader = new Scanner(System.in);
        short choice;

        System.out.println(intro);
        choice = cli.showMenu();

        switch (choice) {
            case 0: System.exit(0);

            case 1:
        }

        reader.close();
    }

}
