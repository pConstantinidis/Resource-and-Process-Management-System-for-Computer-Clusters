package src.model;

import lib.Dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;

public class PlainVM extends VirtualMachine {
    private int drive;
    private int allocDrive;

    public int getDrive() {return this.drive;}
    public int getAllocDrive() {return allocDrive;}

    public void addAllocDrive(int drive) {
        if (drive <= this.drive-allocDrive)
            allocDrive += drive;
        else throw new IllegalArgumentException();
    }

    public PlainVM(int cpu, int ram, OS os, int drive) throws InputOutOfAdminsStandartsException {
        Globals.isDriveValid(drive);
      
        updateCpu(cpu);
        updateRam(ram);
        updateOs(os);
        this.drive = drive;
    }

    /**
     * A method that updates the SSD of a VM.
     * 
     * @param newDrive The total drive that the VM is going to have after the update.
     * @return The drive that the VM had access to before the update.
     * @throws InputOutOfAdminsStandartsException
     */
    protected int updateDrive(int newDrive) throws InputOutOfAdminsStandartsException {
        Globals.isDriveValid(newDrive);

        int oldDrive = drive;
        drive = newDrive;
        return oldDrive;
    }
    
    protected double computeLoad(int cpuToAlloc, int ramToAlloc, int driveToAlloc) {
        return (double) (allocDrive+driveToAlloc)/drive + computeLoad(cpuToAlloc, ramToAlloc);
    }
    
    
}
