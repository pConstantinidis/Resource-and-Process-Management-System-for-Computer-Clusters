package src.model;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JSpinner.DateEditor;

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
    private final HashSet<Integer> programsIDs = new HashSet<>(5);
    
    public Program(int coresRequired, int ramRequired, int driveRequired, int expectedDuration) {
        this.coresRequired = coresRequired;
        this.ramRequired = ramRequired;
        this.driveRequired = driveRequired;
        this.expectedDuration = expectedDuration;
        this.pID = generateID();
    }

    public Program(int coresRequired, int ramRequired, int driveRequired, int gpuRequired, int expectedDuration) {
        this.coresRequired = coresRequired;
        this.ramRequired = ramRequired;
        this.driveRequired = driveRequired;
        this.expectedDuration = expectedDuration;
        this.gpuRequired = gpuRequired;
        this.pID = generateID();
    }
    
    public Program(int coresRequired, int ramRequired, int driveRequired, int bandwidthRequired, int expectedDuration) {
        this.coresRequired = coresRequired;
        this.ramRequired = ramRequired;
        this.driveRequired = driveRequired;
        this.expectedDuration = expectedDuration;
        this.bandwidthRequired = bandwidthRequired;
        this.pID = generateID();
    }
    
    public Program(int coresRequired, int ramRequired, int driveRequired, int gpuRequired, int bandwidthRequired, int expectedDuration) {
        this.coresRequired = coresRequired;
        this.ramRequired = ramRequired;
        this.driveRequired = driveRequired;
        this.expectedDuration = expectedDuration;
        this.gpuRequired = gpuRequired;
        this.bandwidthRequired = bandwidthRequired;
        this.pID = generateID();
    }
    
    /**
     * @return An unbounded positive integer.
     */
    private int generateID() {
        Random ran = new Random();
        int num;
        do {
        num = Math.abs(ran.nextInt());        
        } while (!programsIDs.add(num));
        return num;
    }

    /**
     * @return The priority factor of a program.
     */
    private double computePriority() {
        return (coresRequired / Globals.getInUseCpu())+(ramRequired / Globals.getInUseRam())+(driveRequired / Globals.getInUseDrive())+
            (gpuRequired / Globals.getInUseGpu())+(bandwidthRequired / Globals.getInUseBandwidth()); 
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

    
}
