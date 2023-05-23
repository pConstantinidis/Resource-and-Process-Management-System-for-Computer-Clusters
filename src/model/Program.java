package src.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import lib.utils.Globals;
import src.backend.ProgramDismissal;

/**
 * 
 * @author pConstantinidis
 */
public final class Program implements Comparable<Program> {

    private final ClusterAdmin admin = ClusterAdmin.getAdmin();

    private final int pID;
    private final int coresRequired;
    private final int ramRequired;
    private final int driveRequired;
    private final int gpuRequired;
    private final int bandwidthRequired;
    private final int expectedDuration;  // Secs
    private long startedExecution;
    private static final HashSet<Integer> IDs = new HashSet<>(5);
    private short countRejections = 0;

    void addRejection() {this.countRejections++;}
    
    public int getCoresRequired() {return coresRequired;}
    public int getRamRequired() {return ramRequired;}
    public int getDriveRequired() {return driveRequired;}
    public int getGpuRequired() {return gpuRequired;}
    public int getBandwidthRequired() {return bandwidthRequired;}

    int getExpectedDuration() {return this.expectedDuration;}
    short getNumOfRejections() {return this.countRejections;}
    long getStartedExecution() {return this.startedExecution;}
    void setStartedExecution(long timeInMillis) {this.startedExecution = timeInMillis;}

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
    private int generateID() {
        Random ran = new Random();
        int num;
        do {
            num = ran.nextInt(100, admin.getQueueCapacity()+101);
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
     * @return The programs ID.
     */
    public int getID() {
        return pID;
    }

    /**
     * TODO
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(20);
        char delimeter = ProgramDismissal.DELIMETER;
        str.append("ID:"+this.pID+delimeter);
        str.append("cores:"+this.coresRequired+delimeter);
        str.append("ram:"+this.ramRequired+delimeter);
        str.append("ssd:"+this.driveRequired+delimeter);

        if (this.bandwidthRequired !=0) str.append("bandwidth:"+this.bandwidthRequired+delimeter);
        if (this.gpuRequired !=0) str.append("gpu:"+this.gpuRequired+delimeter);
        str.append("exeTimeReq:"+this.expectedDuration);
        return str.toString();
    }
    
    public void triggerDismiss() throws IOException {
        ProgramDismissal.dismiss(this);
    }
}
