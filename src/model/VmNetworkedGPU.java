package src.model;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.dependencies.NetworkAccessible;
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
        return (double) ((bandwidthToAlloc+allocBandwidth)/bandwidth + (allocCPU+cpuToAlloc)/cpu + (ramToAlloc+allocRAM)/ram + (driveToAlloc+allocDrive)/drive + (gpuToAlloc+allocGPU)/gpu) / 5;
    }

    public void freeResources(int numOfInvalidPrgs) {
        Program prg;
        for (int i=0; i<numOfInvalidPrgs; i++) {
            prg = programsAssigned.poll();

            super.freeResources(prg);
            this.allocBandwidth -= prg.getBandwidthRequired();
            System.out.println("\n\tThe program with ID "+prg.getID()+" has executed succesfuly.");
        }
    }
}
