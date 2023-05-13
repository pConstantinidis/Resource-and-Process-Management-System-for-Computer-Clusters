package src.model;

import lib.Dependencies.InputOutOfAdminsStandartsException;
import lib.Dependencies.NetworkAccessible;
import lib.utils.Globals;
import lib.utils.Globals.OS;

public class VmNetworked extends PlainVM implements NetworkAccessible {
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

    public VmNetworked(int cpu, int ram, OS os, int drive, int bandwidth) throws InputOutOfAdminsStandartsException {
        super(cpu, ram, os, drive);

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

    protected double computeLoad(int cpuToAlloc, int ramToAlloc, int driveToAlloc, int bandwidthToAlloc) {
        return (double) (allocBandwidth+bandwidthToAlloc)/bandwidth + computeLoad(cpuToAlloc, ramToAlloc, driveToAlloc);
    }

    /**
     * @param p The program to be assigned.
     * @return {@code true} if {@code p} wasn't already in the set, {@code false} otherwise.
     */
    @Override
    protected boolean assignProgram(Program p) {
        this.addAllocBandwidth(p.getBandwidthRequired());
        return super.assignProgram(p);
    }

}
