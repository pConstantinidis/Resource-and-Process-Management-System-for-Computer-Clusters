package src.model;

import lib.Dependencies.InputOutOfAdminsStandartsException;
import lib.utils.Globals;
import lib.utils.Globals.OS;

public class VmGPU extends PlainVM {
    private int gpu;
    private int allocGPU;

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
        return (double) (gpuToAlloc+allocGPU)/ + computeLoad(cpuToAlloc, ramToAlloc, driveToAlloc);
    }
}
