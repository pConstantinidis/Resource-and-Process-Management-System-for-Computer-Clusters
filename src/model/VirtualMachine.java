package src.model;

import java.nio.channels.NetworkChannel;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandarts;
import lib.utils.InputOutOfAdminsStandartsException;

/**
 * 
 * @author pConstantinidis
 */
public abstract class VirtualMachine {
    
    private final int vmID;
    private int cpu;
    private int ram;
    private String os;

    /**
     * A method that updates the number of CPU cores used by the VM.
     * 
     * @param newNumOfCores The number of the total cores after the update.
     * @return The number of cores that the VM had before the update.
     */
    private int updateCpu(int newNumOfCores) throws InputOutOfAdminsStandartsException {
        if (newNumOfCores > ClusterAdmin.getAvailableCpu() || newNumOfCores < 0)
            throw new InputOutOfAdminsStandartsException();
        
        int oldCpu = cpu;
        cpu = newNumOfCores;
        return oldCpu;
    }

    /**
     * A method that updates the RAM used by a VM.
     * 
     * @param newRam The number of the total RAM (in GBs) after the update
     * @return The RAM of the VM before the update.
     */
    private int updateRam(int newRam) {
        if (newRam > ClusterAdmin.getAvailableRam() || newRam < 0)
            throw new InputOutOfAdminsStandartsException();

        int oldRam = ram;
        ram = newRam;
        return oldRam;
    }

    /**
     * A method that updates the OS of the VM.
     * 
     * @param os The OS to be set.
     * @return The OS used before the update.
     */
    private String updateOs(String newOs) {
        boolean isValid = false;
        for (Globals.OS os:Globals.OS.values()) {
            if (newOs.equals(os.toString())) {
                isValid = true;
                break;
            }
        }
        if (isValid == true) {
            String oldOs = os;
            os = newOs;
            return oldOs;
        }
        throw new InputOutOfAdminsStandartsException();
    }
}
