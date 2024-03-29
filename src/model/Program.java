package src.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

import lib.utils.Globals;
import src.backend.ProgramDismissal;

/**
 * 
 * @author pConstantinidis
 */
public final class Program implements Comparable<Program>, Serializable {
    
    private final ClusterAdmin admin = ClusterAdmin.getAdmin();
    
    private final int pID;
    private final int coresRequired;
    private final int ramRequired;
    private final int driveRequired;
    private final int gpuRequired;
    private final int bandwidthRequired;
    private final int expectedDuration;  // Secs
    private long startedExecution;
    private static final HashSet<Integer> IDs = new LinkedHashSet<>(5);
    private short countRejections = 0;
    
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
        IDs.add(pID);
    }
    
    public static String idsToString() {
        return IDs.toString();
    }
    
    /**
     * @return The priority factor of a program.
     */
    private double computePriority() {
        return ((double) this.coresRequired / Globals.getAvailableCpu())+
        ((double) this.ramRequired / Globals.getAvailableRam())+
        ((double) this.driveRequired / Globals.getAvailableDrive())+
        ((double) this.gpuRequired / Globals.getAvailableGpu())+
        ((double) this.bandwidthRequired / Globals.getAvailableBandwidth()); 
    }
    
    /**
     * @return A positive integer, whose possible max value depends on the clusters queue size.
     */
    private int generateID() {
        Random ran = new Random();
        int num;
        do {
            num = ran.nextInt(100, 101 + 10*IDs.size());
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
        return Double.compare(otherPriority, thisPriority);
    }
    
    public int getID() {
        return pID;
    }
    
    public static boolean isTheirSuchID(int id) {
        return IDs.contains(id);
    }
    
    /**
     * @return A string representation of the program.
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
    
    void addRejection() {
        this.countRejections++;
        admin.spleep();
    }
    
    public void triggerDismiss() throws IOException {
        new ProgramDismissal().dismiss(this);
    }


}
