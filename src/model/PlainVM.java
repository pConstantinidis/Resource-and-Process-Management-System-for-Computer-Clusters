package src.model;

import lib.utils.Globals.*;

public class PlainVM extends VirtualMachine {
    private int drive;

    public PlainVM(int cpu, int ram, OS os, int drive) {
        if (cpu > ClusterAdmin.getAvailableCpu() || ram > ClusterAdmin.getAvailableRam() || os )
      
      
        this.cpu = cpu;
        this.ram = ram;
        this.os = os;
        this.drive = drive;
    }
}
