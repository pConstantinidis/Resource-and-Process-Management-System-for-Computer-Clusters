package src.model;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;

public class PlainVM extends VirtualMachine {
    protected int drive;
    protected int allocDrive;

    public int getDrive() {return this.drive;}
    public int getAllocDrive() {return allocDrive;}

    public void addAllocDrive(int drive) throws IllegalArgumentException  {
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
        return (double) ((allocDrive+driveToAlloc)/drive + (allocCPU+cpuToAlloc)/cpu + (ramToAlloc+allocRAM)/ram) / 3;
    }

    /**
     * @param numOfPrograms The number of programs that have terminated and their resources are to be retrieved.
     */
    public void freeResources(int numOfPrograms) {
        Program prg;
        for (int i=0; i<numOfPrograms; i++) {
            prg = programsAssigned.poll();

            super.freeResources(prg);
            this.allocDrive -= prg.getDriveRequired();            
            System.out.println("\n\tThe program with ID "+prg.getID()+" has executed succesfuly.");
        }
    }

    protected void freeResources(Program prg) {
        super.freeResources(prg);
        this.allocDrive -= prg.getDriveRequired();
    }
    
}
