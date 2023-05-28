package src.model;

import java.io.Serializable;

import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;

public class VmGPU extends PlainVM implements Serializable {
    protected int gpu;
    protected int allocGPU;

    public int getGpu() {return gpu;}
    public int getAllocGPU() {return allocGPU;}

    public void addAllocGPU(int gpu) throws IllegalArgumentException {
        if (gpu <= this.gpu-allocGPU)
            allocGPU += gpu;
        else throw new IllegalArgumentException();
    }

    public VmGPU(int cpu, int ram, OS os, int drive, int gpu) throws InputOutOfAdminsStandartsException {
        super(cpu, ram, os, drive);

        Globals.isGpuValid(gpu);
        this.gpu = gpu;
    }

    /**
     * A method that updates the GPU of a VM.
     * 
     * @param newGpu The total GPU that the VM is going to have access to after the update.
     * @return The GPU that the VM had access to before the update.
     * @throws InputOutOfAdminsStandartsException
     */
    protected int updateGpu(int newGpu) throws InputOutOfAdminsStandartsException {
        Globals.isGpuValid(newGpu);

        int oldGpu = this.gpu;
        this.gpu = newGpu;
        return oldGpu;
    }

    protected double computeLoad(int cpuToAlloc, int ramToAlloc, int driveToAlloc, int gpuToAlloc) {
       return (double) ((allocCPU+cpuToAlloc)/cpu + (ramToAlloc+allocRAM)/ram + (driveToAlloc+allocDrive)/drive + (gpuToAlloc+allocGPU)/gpu) / 4;
    }

    public void freeResources(int numOfPrograms) {
        Program prg;
        for (int i=0; i<numOfPrograms; i++) {
            prg = programsAssigned.poll();

            super.freeResources(prg);
            this.allocGPU -= prg.getGpuRequired();
            System.out.println("\n\tThe program with ID: "+prg.getID()+" has executed succesfuly.");
        }
    }

    protected void freeResources(Program prg) {
        super.freeResources(prg);
        this.allocGPU -= prg.getGpuRequired();
    }
}
