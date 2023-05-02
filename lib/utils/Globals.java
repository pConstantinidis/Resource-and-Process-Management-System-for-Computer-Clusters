package lib.utils;

import src.model.ClusterAdmin;

/**
 * A class that is to be used widely around the project
 * 
 * @Purpose Here we declare fundemental logic and model standarts 
 * @author pConstantinidis
 */
public final class Globals {
    
    /**
     * We declare the types of Operating Systems that are aplicable to the VMs.
     */
    public static enum OS {
        WINDOWS,
        UBUNTU,
        FEDORA
    }

    private static int availableCpu = ClusterAdmin.getCpuCores();
    private static int availableRam = ClusterAdmin.getRam();
    private static int availableDrive = ClusterAdmin.getDrive();
    private static int availableGpu = ClusterAdmin.getGpu();
    private static int availableBandwidth = ClusterAdmin.getNetworkBandwidth();

    // Accesors
    public static int getAvailableCpu() {return availableCpu;}
    public static int getAvailableRam() {return availableRam;}
    public static int getAvailableDrive() {return availableDrive;}
    public static int getAvailableGpu() {return availableGpu;}
    public static int getAvailableBandwidth() {return availableBandwidth;}

    // Mutators
    public static void setAvailableCpu(int availableCpu) {Globals.availableCpu = availableCpu;}
    public static void setAvailableRam(int availableRam) {Globals.availableRam = availableRam;}
    public static void setAvailableDrive(int availableDrive) {Globals.availableDrive = availableDrive;}
    public static void setAvailableGpu(int availableGpu) {Globals.availableGpu = availableGpu;}
    public static void setAvailableBandwidth(int availableBandwidth) {Globals.availableBandwidth = availableBandwidth;}
    
    /*
     * Methods that implement validation checks ocording to potential logical errors and model standarts
     */
        public final static void isCpuValid(int newNumOfCores) throws InputOutOfAdminsStandartsException{
            if (newNumOfCores > availableCpu || newNumOfCores < 1)
                throw new InputOutOfAdminsStandartsException();
        }

        public final static void isRamValid(int newRam) throws InputOutOfAdminsStandartsException{
            if (newRam > availableRam || newRam < 1)
                throw new InputOutOfAdminsStandartsException();
        }

        public final static void isDriveValid(int newDrive) throws InputOutOfAdminsStandartsException {
            if (newDrive > availableDrive || newDrive < 1)
                throw new InputOutOfAdminsStandartsException();
        }

        public final static void isGpuValid(int newGpu) throws InputOutOfAdminsStandartsException {
            if (newGpu > availableGpu || newGpu < 1)
            throw new InputOutOfAdminsStandartsException();
        }

        public final static void isBandwidthValid(int newBandwidth) throws InputOutOfAdminsStandartsException {
            if (newBandwidth > availableBandwidth || newBandwidth < 4)
                throw new InputOutOfAdminsStandartsException();
        }

}
