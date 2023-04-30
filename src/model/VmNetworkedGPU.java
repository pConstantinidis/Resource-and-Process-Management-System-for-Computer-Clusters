package src.model;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.NetworkAccessible;
import lib.utils.Globals.OS;

public class VmNetworkedGPU extends VmGPU implements NetworkAccessible {
    
    private int bandwidth;

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
}
