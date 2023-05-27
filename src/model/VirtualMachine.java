package src.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;

/**
 * An abstract description of a Virtual Machine.
 * 
 * @author pConstantinidis
 */
public abstract class VirtualMachine {
    
    protected int cpu;
    protected int ram;
    private OS os;
    private ClusterAdmin admin = ClusterAdmin.getAdmin();


    /**
     * TODO
     */
    protected Queue<Program> programsAssigned = new PriorityQueue<>((p1, p2) -> {
        long expToEnd1 = p1.getExpectedDuration()*1000 + p1.getStartedExecution();
        long expToEnd2 = p2.getExpectedDuration()*1000 + p2.getStartedExecution();
        return Long.compare(expToEnd1, expToEnd2);
    });

    
    
    /**
     * The CPU/RAM that is allready allocated on the VM.
     */
    protected int allocCPU, allocRAM;
    
    public int getCpu() {return cpu;}
    public int getRam() {return ram;}
    public OS getOs() {return os;}
    public int getAllocCPU() {return allocCPU;}
    public int getAllocRAM() {return allocRAM;}

    /**
     * @param cpu
     * @throws IllegalArgumentException In the case that the {@code cpu} exceeds the one allocated on the VMs.
     */
    public void addAllocCPU(int cpu) throws IllegalArgumentException {
        if (cpu <= this.cpu-allocCPU)
        allocCPU += cpu;
        else throw new IllegalArgumentException();
    }
    
    /**
     * @return The number of programs that hve finished are to be "removed" from this VM.
     */
    int peekRunningPrgs() {
        int count = 0;
        ArrayList<Program> temp = new ArrayList<>();
        Program nullTester;
        Program prg = programsAssigned.poll();

        while (prg != null && (prg.getExpectedDuration()*1000 + prg.getStartedExecution() <= System.currentTimeMillis())) {
            temp.add(prg);
            count++;
            nullTester = programsAssigned.poll();
            prg = nullTester;
        }
        programsAssigned.addAll(temp);
        return count;
    }

    /**
     * @param ram
     * @throws IllegalArgumentException In the case that the {@code ram} exceeds the one allocated on the VMs.
     */
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

    /**
     * @param p The program to be assigned.
     * @return 0 If the program was assigned succesfuly, 1 if the program was re-pushed to the queue and -1 if it gote dismissed.
     */
    protected int assignProgram(Program p) {
        try {
            this.addAllocCPU(p.getCoresRequired());
            this.addAllocRAM(p.getRamRequired());            
        } catch (IllegalArgumentException e) {
            if (p.getNumOfRejections() == ClusterAdmin.PROGRAM_REJECTIONS_toDISMISS) {
                try {
                    p.triggerDismiss();
                } catch (IOException e1) {
                    System.err.println(e1.toString()+" -> propable resource leak.");
                }
                return -1;
            }
            admin.pushProgram(p);
            p.addRejection();
            return 1;
        }
        programsAssigned.add(p);
        p.setStartedExecution(System.currentTimeMillis());
        return 0;
    }

    protected void terminateProgram(Program prg) {
        this.allocCPU += prg.getCoresRequired();
        this.allocRAM += prg.getRamRequired();
    }

}
