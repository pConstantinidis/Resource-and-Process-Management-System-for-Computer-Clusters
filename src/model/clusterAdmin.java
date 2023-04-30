package src.model;

/**
 * 
 * @author pConstantinidis
 */
public class ClusterAdmin {
    private final static int CPU_CORES = 128;          // Cores
    private final static int RAM = 256;                // Gb
    private final static int DRIVE = 2048;             // Gb
    private final static int GPU = 8;                  // GPUs
    private final static int NETWORK_BANDWIDTH = 320;  // Gb/sec
    
    // Accessors
    public static int getCpuCores() {return CPU_CORES;}
    public static int getRam() {return RAM;}
    public static int getDrive() {return DRIVE;}
    public static int getGpu() {return GPU;}
    public static int getNetworkBandwidth() {return NETWORK_BANDWIDTH;}
        
}