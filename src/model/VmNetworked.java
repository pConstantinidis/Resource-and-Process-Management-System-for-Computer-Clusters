package src.model;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.NetworkAccessible;
import lib.utils.Globals.OS;

public class VmNetworked extends PlainVM implements NetworkAccessible {
    private int bandwidth;
    
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

}
