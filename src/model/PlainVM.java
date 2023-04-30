package src.model;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.Globals.*;

public class PlainVM extends VirtualMachine {
    private int drive;

    // Constructor
    public PlainVM(int cpu, int ram, OS os, int drive) throws InputOutOfAdminsStandartsException {
        Globals.isCpuValid(cpu);
        Globals.isRamValid(ram);
        Globals.isOsValid(os);
        Globals.isDriveValid(drive);
      
        this.cpu = cpu;
        this.ram = ram;
        this.os = os;
        this.drive = drive;
    }

    /**
     * A method that updates the SSD of a VM.
     * 
     * @param newDrive The total drive that the VM is going to have after the update.
     * @return The drive that the VM had access to before the update.
     */
    private int updateDrive(int newDrive) throws InputOutOfAdminsStandartsException {
        Globals.isDriveValid(newDrive);

        int oldDrive = drive;
        drive = newDrive;
        return oldDrive;
    }
}
