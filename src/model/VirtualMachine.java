package src.model;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.Globals.OS;

/**
 * An abstract description of a Virtual Machine.
 * 
 * @author pConstantinidis
 */
public abstract class VirtualMachine {
    
    private int cpu;
    private int ram;
    private OS os;

    public int getCpu() {return cpu;}
    public int getRam() {return ram;}
    public OS getOs() {return os;}

    /**
     * A method that updates/initializes the number of CPU cores used by the VM.
     * 
     * @param newNumOfCores The number of the total cores after the update.
     * @return The number of cores that the VM had before the update.
     * @throws InputOutOfAdminsStandartsException
     */
    protected int updateCpu(int newNumOfCores) throws InputOutOfAdminsStandartsException {
        Globals.isCpuValid(newNumOfCores);
        
        int oldCpu = cpu;
        cpu = newNumOfCores;
        return oldCpu;
    }

    /**
     * A method that updates/initializes the RAM used by a VM.
     * 
     * @param newRam The number of the total RAM (in GBs) after the update
     * @return The RAM of the VM before the update.
     * @throws InputOutOfAdminsStandartsException
     */
    protected int updateRam(int newRam) throws InputOutOfAdminsStandartsException {
        Globals.isRamValid(newRam);

        int oldRam = ram;
        ram = newRam;
        return oldRam;
    }

    /**
     * A method that updates/initializes the OS of the VM.
     * 
     * @apiNote In the case that an invalid type of OS manages to be set, the program will
     * throw an error at compile time, for the reason why the newOs won't be of type OS.
     * 
     * @param newOs The OS to be set.
     * @return The OS used before the update.
     * @throws InputOutOfAdminsStandartsException
     */
    protected String updateOs(OS newOs) throws InputOutOfAdminsStandartsException {
            OS oldOs = os;
            os = newOs;
            return oldOs.toString();
    }
}
