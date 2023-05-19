package src.model;

import java.io.IOException;

import lib.Dependencies.InputOutOfAdminsStandartsException;
import lib.Dependencies.NetworkAccessible;
import lib.utils.Globals;
import lib.utils.Globals.OS;

/**
 * 
 * @author pConstantinidis
 */
public class VmNetworkedGPU extends VmGPU implements NetworkAccessible {
    
    private int bandwidth;
    private int allocBandwidth;

    public int getAllocBandwidth() {return allocBandwidth;}

    @Override
    public void addAllocBandwidth(int bandwidth) throws IllegalArgumentException {
        if (bandwidth <= this.bandwidth-allocBandwidth)
            this.allocBandwidth += bandwidth;
        else throw new IllegalArgumentException();
    }

    @Override
    public int getBandwidth() {
        return this.bandwidth;
    }

    public VmNetworkedGPU(int cpu, int ram, OS os, int drive, int gpu, int bandwidth) throws InputOutOfAdminsStandartsException {
        super(cpu, ram, os, drive, gpu);

        Globals.isBandwidthValid(bandwidth);
        this.bandwidth = bandwidth;
    }

    @Override
    public int updateBandwidth(int newBandwidth) throws InputOutOfAdminsStandartsException {
        Globals.isBandwidthValid(newBandwidth);

        int oldBandwidth = this.bandwidth;
        this.bandwidth = newBandwidth;
        return oldBandwidth;
    }

    protected double computeLoad(int cpuToAlloc, int ramToAlloc, int driveToAlloc, int gpuToAlloc, int bandwidthToAlloc) {
        return (double) (bandwidthToAlloc+allocBandwidth)/bandwidth + computeLoad(cpuToAlloc, ramToAlloc, driveToAlloc, gpuToAlloc);
    }

    /**
     * @param p The program to be assigned.
     * @return {@code true} if {@code p} wasn't already in the set, {@code false} otherwise.
     */
    @Override
    protected boolean assignProgram(Program p) throws IOException {
        this.addAllocBandwidth(p.getBandwidthRequired());
        return super.assignProgram(p);
    }
}
