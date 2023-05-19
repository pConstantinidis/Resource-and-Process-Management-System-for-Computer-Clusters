package src.model;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
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

    /**
     * An ascending collection of all the programs assigned to a VM.
     * 
     * <p>
     */
    private Queue<Program> programsAssigned = new PriorityQueue<>( (p1, p2) -> {
        return Integer.compare(p1.getExecutionTime(), p2.getExecutionTime());
    });

    /**
     * The CPU/RAM that is allready allocated on the VM.
     */
    private int allocCPU, allocRAM;

    public int getCpu() {return cpu;}
    public int getRam() {return ram;}
    public OS getOs() {return os;}
    public int getAllocCPU() {return allocCPU;}
    public int getAllocRAM() {return allocRAM;}

    public void addAllocCPU(int cpu) throws IllegalArgumentException {
        if (cpu <= this.cpu-allocCPU)
            allocCPU += cpu;
        else throw new IllegalArgumentException();
    }
    
    public void addAllocRAM(int ram) throws IllegalArgumentException {
        if (ram <= this.ram-allocRAM)
            allocRAM += ram;
        else throw new IllegalArgumentException();
    }

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
    protected String updateOs(OS newOs) {
            OS oldOs = os;
            os = newOs;
            if (oldOs != null)
                return oldOs.toString();
            return null;
    }

    public double computeLoad(int cpuToAlloc, int ramToAlloc) {
        return (double) (cpuToAlloc+allocCPU)/cpu + (double) (ramToAlloc+ramToAlloc)/ram;
    }

    /**
     * @param p The program to be assigned.
     * @return {@code true} if {@code p} wasn't already in the set, {@code false} otherwise.
     */
    protected boolean assignProgram(Program p) throws IOException {
        try {
            this.addAllocCPU(p.getCoresRequired());
            this.addAllocRAM(p.getRamRequired());            
        } catch (IllegalArgumentException e) {
            p.addRejection();
            if (p.getNumOfRejections() == ClusterAdmin.PROGRAM_REJECTIONS_toDISMISS)
                p.triggerDismiss();
        }

        return programsAssigned.add(p);
    }

}
