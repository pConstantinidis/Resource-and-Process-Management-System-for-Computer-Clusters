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

    public void setCpu(int cpu) throws InputOutOfAdminsStandartsException {
        Globals.isCpuValid(cpu);
        this.cpu = cpu;
    }
    public void setRam(int ram) throws InputOutOfAdminsStandartsException {
        Globals.isRamValid(ram);
        this.ram = ram;
    }
    public void setOs(OS os) throws InputOutOfAdminsStandartsException {
        Globals.isOsValid(os);
        this.os = os;
    }

    /**
     * A method that updates the number of CPU cores used by the VM.
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
     * A method that updates the RAM used by a VM.
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
     * A method that updates the OS of the VM.
     * 
     * @param newOs The OS to be set.
     * @return The OS used before the update.
     * @throws InputOutOfAdminsStandartsException
     */
    private String updateOs(OS newOs) throws InputOutOfAdminsStandartsException {
            Globals.isOsValid(newOs);

            OS oldOs = os;
            os = newOs;
            return oldOs.toString();
    }
}
