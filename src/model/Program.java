package src.model;

import java.util.HashSet;
import java.util.Random;

import lib.utils.Globals;

public final class Program implements Comparable<Program> {

    private final int pID;
    private final int coresRequired;
    private final int ramRequired;
    private final int driveRequired;
    private final int gpuRequired;
    private final int bandwidthRequired;
    private final int expectedDuration;  // Secs
    private int executionTime = 0;
    private static final HashSet<Integer> IDs = new HashSet<>(5);
    
    /**
     * Parameters that aren't required should be set to 0.
     */    
    public Program(int coresRequired, int ramRequired, int driveRequired, int gpuRequired, int bandwidthRequired, int expectedDuration) {
        this.coresRequired = coresRequired;
        this.ramRequired = ramRequired;
        this.driveRequired = driveRequired;
        this.expectedDuration = expectedDuration;
        this.gpuRequired = gpuRequired;
        this.bandwidthRequired = bandwidthRequired;
        pID = generateID();
    }
    
    /**
     * @return The priority factor of a program.
     */
    private double computePriority() {
        return ((double) this.coresRequired / Globals.getInUseCpu())+
        ((double) this.ramRequired / Globals.getInUseRam())+
        ((double) this.driveRequired / Globals.getInUseDrive())+
        ((double) this.gpuRequired / Globals.getInUseGpu())+
        ((double) this.bandwidthRequired / Globals.getInUseBandwidth()); 
    }

    /**
     * @return A positive integer, whose possible max value depends on the clusters queue size.
     */
    public static int generateID() {
        Random ran = new Random();
        int num;
        do {
            num = ran.nextInt(100, ClusterAdmin.QUEUE_CAPACITY+100);
        } while (IDs.contains(num));
        return num;        
    }

    /**
     * A method that implements the {@code compareTo} method of the interface
     * {@code Comparable} regarding the priority of two programs.
     */
    @Override
    public int compareTo(Program o) {

        double thisPriority = this.computePriority();
        double otherPriority = o.computePriority();
        return Double.compare(thisPriority, otherPriority);
    }

    /**
     * @return Just the programs ID.
     */
    @Override
    public String toString() {
        return String.valueOf(pID);
    }
    
}
