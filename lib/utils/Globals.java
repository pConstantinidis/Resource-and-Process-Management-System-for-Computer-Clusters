package lib.utils;

import src.model.ClusterAdmin;

/**
 * A class that is to be used widely around the project
 * 
 * @Purpose Here we declare fundemental logic and model standarts 
 * @author pConstantinidis
 */
public final class Globals {
    
    public static enum OS {
        WINDOWS,
        UBUNTU,
        FEDORA
    }

    public final static void isCpuValid(int newNumOfCores) throws InputOutOfAdminsStandartsException{
        if (newNumOfCores < ClusterAdmin.getAvailableCpu() || newNumOfCores < 1)
            throw new InputOutOfAdminsStandartsException();
    }

    public final static void isRamValid(int newRam) throws InputOutOfAdminsStandartsException{
        if (newRam < ClusterAdmin.getAvailableRam() || newRam < 1)
            throw new InputOutOfAdminsStandartsException();
    }

    public final static void isOsValid(OS newOs) throws InputOutOfAdminsStandartsException {
        for (Globals.OS os:Globals.OS.values()) {
            if (newOs.equals(os))
                return;
        }
        throw new InputOutOfAdminsStandartsException();
    }

    public final static void isDriveValid(int newDrive) throws InputOutOfAdminsStandartsException {
        if (newDrive < ClusterAdmin.getAvailableDrive() || newDrive < 1)
            throw new InputOutOfAdminsStandartsException();
    }

    public final static void isGpuValid(int newGpu) throws InputOutOfAdminsStandartsException {
        if (newGpu < ClusterAdmin.getAvailableGpu() || newGpu < 1)
         throw new InputOutOfAdminsStandartsException();
    }

    public final static void isBandwidthValid(int newBandwidth) throws InputOutOfAdminsStandartsException {
        if (newBandwidth < ClusterAdmin.getAvailableBandwidth() || newBandwidth < 4)
            throw new InputOutOfAdminsStandartsException();
    }

}
