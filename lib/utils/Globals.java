package lib.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import lib.dependencies.InputOutOfAdminsStandartsException;
import src.CLI.CLI_IOHandler;
import src.model.ClusterAdmin;
import src.model.Program;

/**
 * A class that is to be used widely around the project
 * 
 * @Purpose Here we declare fundemental logic and model standarts 
 * @author pConstantinidis
 */
public final class Globals {
    
    /**
     * The types of Operating Systems that are aplicable to the VMs are predetermined.
     */
    public static enum OS {
        WINDOWS,
        UBUNTU,
        FEDORA
    }

    private static int availableCpu = ClusterAdmin.CPU_CORES;
    private static int availableRam = ClusterAdmin.RAM;
    private static int availableDrive = ClusterAdmin.DRIVE;
    private static int availableGpu = ClusterAdmin.GPU;
    private static int availableBandwidth = ClusterAdmin.NETWORK_BANDWIDTH;

    public static final ClusterAdmin admin = ClusterAdmin.getAdmin();

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
                throw new InputOutOfAdminsStandartsException("invalid CPU request");
        }

        public final static void isRamValid(int newRam) throws InputOutOfAdminsStandartsException{
            if (newRam > availableRam || newRam < 1)
                throw new InputOutOfAdminsStandartsException("invalid RAM request");
        }

        public final static void isDriveValid(int newDrive) throws InputOutOfAdminsStandartsException {
            if (newDrive > availableDrive || newDrive < 1)
                throw new InputOutOfAdminsStandartsException("invalid SSD request");
        }

        public final static void isGpuValid(int newGpu) throws InputOutOfAdminsStandartsException {
            if (newGpu > availableGpu || newGpu < 1)
            throw new InputOutOfAdminsStandartsException("invalid GPU request");
        }

        public final static void isBandwidthValid(int newBandwidth) throws InputOutOfAdminsStandartsException {
            if (newBandwidth > availableBandwidth || newBandwidth < 4)
                throw new InputOutOfAdminsStandartsException("invalid bandwidth request");
        }

        public final static boolean areThereAnyVms() {
            if (admin.getNumOfVms() == 0) {
                System.out.println(CLI_IOHandler.underLine+"\n It seems that there aren't currently any VMs running on the cluster\n In this case you can not proceed.");
                return false;
            }
            return true;
        }

        public final static boolean isProgramValid(Program p) {
            if (!Program.isTheirSuchID(p.getID()) ||
                p.getCoresRequired() > getAvailableCpu() ||
                p.getRamRequired() > getAvailableRam() ||
                p.getDriveRequired() > getAvailableDrive() ||
                p.getGpuRequired() > getAvailableGpu() ||
                p.getBandwidthRequired() > getAvailableBandwidth())     {return false;}

                return true;
        }

        /**
         * @param input The OS to be checked.
         * @return The enum value corresponding to the {@code input} or null if the input is invalid.
         */
        public final static OS isOSValid(String input) {
            String givenOS = input.toUpperCase();

            if (!givenOS.equals(OS.FEDORA.toString()) && !givenOS.equals(OS.UBUNTU.toString()) && !givenOS.equals(OS.WINDOWS.toString()))
                return null;
            
            switch (givenOS.charAt(0)) {
                case 'F': return OS.FEDORA;
                case 'U': return OS.UBUNTU;
                case 'W': return OS.WINDOWS;
                default : return null;
            }
        }
        /**
         * Sorts in ascending order an array of elements that implements the {@code Comparable} interface.
         * 
         * @param a An array of objects that implement the comparable class.
         */
        public static <E extends Comparable<E>> void sort(E[] a) {
            for (int nextPos =1; nextPos < a.length; nextPos++) {
                insert(a, nextPos);
            }
        }
        
        private static <E extends Comparable<E>> void insert(E[] a, int nextPos) {
            E nextValue = a[nextPos];
            while (nextPos > 0 && nextValue.compareTo(a[nextPos-1]) < 0) {
                a[nextPos] = a[nextPos-1];
                    nextPos--;
            }
            a[nextPos] = nextValue;
        }

        /**
         * A method that returns the corresponding key of a given value inside a HashMap
         */
        public static <K, V> K getKeyFromValue(HashMap<K, V> hashMap, V value) {
            for (Map.Entry<K, V> entry : hashMap.entrySet()) {
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
                }
            }
            return null;
        }

}
