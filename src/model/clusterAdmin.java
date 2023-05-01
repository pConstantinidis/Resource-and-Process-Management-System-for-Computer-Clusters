package src.model;

import java.util.HashMap;
import java.util.Map;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.NetworkAccessible;
import lib.utils.Globals.OS;

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

    private Map<Integer, VirtualMachine> clusterVms = new HashMap<>();
    private int numOfVms = 0;

    // Accessors
    public static int getCpuCores() {return CPU_CORES;}
    public static int getRam() {return RAM;}
    public static int getDrive() {return DRIVE;}
    public static int getGpu() {return GPU;}
    public static int getNetworkBandwidth() {return NETWORK_BANDWIDTH;}
    public int getNumOfVms() {return this.numOfVms;}
    
    private void createPlainVm(int cpu, int ram, OS os, int drive) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new PlainVM(cpu, ram, os, drive));
        updateClustersReserve(-cpu, ram, drive, 0, 0);
        numOfVms++;
    }

    private void createVmGPU(int cpu, int ram, OS os, int drive, int gpu) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmGPU(cpu, ram, os, drive, gpu));
        updateClustersReserve(-cpu, ram, drive, gpu, 0);
        numOfVms++;
    }

    private void createVmNetworked(int cpu, int ram, OS os, int drive, int bandwidth) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmNetworked(cpu, ram, os, drive, bandwidth));
        updateClustersReserve(-cpu, ram, drive, 0, bandwidth);
        numOfVms++;
    }

    private void createVmNetworkedGpu(int cpu, int ram, OS os, int drive, int gpu, int bandwidth) throws InputOutOfAdminsStandartsException {

        clusterVms.put(numOfVms+1, new VmNetworkedGPU(cpu, ram, os, drive, gpu, bandwidth));
        updateClustersReserve(-cpu, ram, drive, gpu, bandwidth);
        numOfVms++;
    }

    //TODO  NEED TO VERIFY THE FUNCTIONALITY OF THE FOLLOWING METHODS!
                        private void updateCpu(int vmId, int newCpu) throws InputOutOfAdminsStandartsException {
                            VirtualMachine vm = clusterVms.get(vmId);
                            
                            double ratio = (double)newCpu / (double)vm.getCpu();
                            updateClustersReserve((int) ((-newCpu+vm.getCpu())/ratio), 0, 0, 0, 0);
                            vm.updateCpu(newCpu);
                        }

                        private void updateRam(int vmId, int newRam) throws InputOutOfAdminsStandartsException {
                            VirtualMachine vm = clusterVms.get(vmId);
                            vm.updateRam(newRam);
                            double ratio = vm.getRam()/newRam;
                            updateClustersReserve(0, (int) ((vm.getRam()-newRam)/ratio), 0, 0, 0);
                        }

                        private void updateDrive(int vmId, int newDrive) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {       //! Exceptions should be handled inside the View package
                            PlainVM vm = (PlainVM) clusterVms.get(vmId);
                            vm.updateDrive(newDrive);
                            double ratio = vm.getDrive()/newDrive;
                            updateClustersReserve(0, 0, (int) ((vm.getDrive()-newDrive)/ratio), 0, 0);
                        }

                        private void updateGpu(int vmId, int newGpu) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
                            VmGPU vm = (VmGPU) clusterVms.get(vmId);
                            vm.updateGpu(newGpu);
                            double ratio = vm.getGpu()/newGpu;
                            updateClustersReserve(0, 0, 0, (int) ((vm.getGpu()-newGpu)/ratio), 0);
                        }

                        private void updateBandwidth(int vmId, int newBandwidth) throws InputOutOfAdminsStandartsException, ClassCastException, NullPointerException {
                            NetworkAccessible vm = (NetworkAccessible) clusterVms.get(vmId);
                            vm.updateBandwidth(newBandwidth);
                            double ratio = vm.getBandwidth()/newBandwidth;
                            updateClustersReserve(0, 0, 0, 0, (int) ((vm.getBandwidth()-newBandwidth)/ratio));
                        }

    /**
     * A method that updates the clusters stock of materials.
     */
    private static void updateClustersReserve(int cpu, int ram, int drive, int gpu, int bandwidth) {
            Globals.setAvailableCpu(Globals.getAvailableCpu() + cpu);
            Globals.setAvailableRam(Globals.getAvailableRam() + ram);
            Globals.setAvailableDrive(Globals.getAvailableDrive() + drive);
            Globals.setAvailableGpu(Globals.getAvailableGpu() + gpu);
            Globals.setAvailableBandwidth(Globals.getAvailableBandwidth() + bandwidth);
    }


    public static void main(String[] args) throws InputOutOfAdminsStandartsException {
        ClusterAdmin admin = new ClusterAdmin();

        admin.createPlainVm(4, 12, Globals.OS.UBUNTU, 612);
        admin.updateCpu(1, 10);
        System.out.println(admin.clusterVms.get(1).getCpu());
        System.out.println(Globals.getAvailableCpu());
    }
}