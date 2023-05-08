package src.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lib.utils.Globals;
import lib.utils.InputOutOfAdminsStandartsException;
import lib.utils.NetworkAccessible;
import lib.utils.Globals.OS;

/**
 * 
 * @author pConstantinidis
 */
public final class ClusterAdmin {
    public final static int CPU_CORES = 128;          // Cores
    public final static int RAM = 256;                // Gb
    public final static int DRIVE = 2048;             // Gb
    public final static int GPU = 8;                  // GPUs
    public final static int NETWORK_BANDWIDTH = 320;  // Gb/sec
    public final static int MAX_PROGRAM_RUNTIME = 5400; //Seconds

    private static Map<Integer, VirtualMachine> clusterVms = new HashMap<>();
    private static int numOfVms = 0;
    private static Set<Integer> programsIDs = new HashSet<>(5);

    // Accessors
    public int getNumOfVms() {return this.numOfVms;}
    
    public void createPlainVm(int cpu, int ram, OS os, int drive) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new PlainVM(cpu, ram, os, drive));
        updateClustersReserve(-cpu, -ram, -drive, 0, 0);
        numOfVms++;
    }

    public static boolean addID(int id) {
        return programsIDs.add(id);
    }

    public static void createVmGPU(int cpu, int ram, OS os, int drive, int gpu) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmGPU(cpu, ram, os, drive, gpu));
        updateClustersReserve(-cpu, -ram, -drive, -gpu, 0);
        numOfVms++;
    }

    public static void createVmNetworked(int cpu, int ram, OS os, int drive, int bandwidth) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmNetworked(cpu, ram, os, drive, bandwidth));
        updateClustersReserve(-cpu, -ram, -drive, 0, -bandwidth);
        numOfVms++;
    }

    public static void createVmNetworkedGpu(int cpu, int ram, OS os, int drive, int gpu, int bandwidth) throws InputOutOfAdminsStandartsException {

        clusterVms.put(numOfVms+1, new VmNetworkedGPU(cpu, ram, os, drive, gpu, bandwidth));
        updateClustersReserve(-cpu, -ram, -drive, -gpu, -bandwidth);
        numOfVms++;
    }

    
    public static void updateCPU(int vmId, int newCpu) throws InputOutOfAdminsStandartsException {
        VirtualMachine vm = clusterVms.get(vmId);
                            
        updateClustersReserve(vm.getCpu()-newCpu, 0, 0, 0, 0);
        vm.updateCpu(newCpu);
    }

    public static void updateRAM(int vmId, int newRam) throws InputOutOfAdminsStandartsException {
        VirtualMachine vm = clusterVms.get(vmId);
        updateClustersReserve(0, vm.getRam()-newRam, 0, 0, 0);
        vm.updateRam(newRam);
    }

    public static void updateDrive(int vmId, int newDrive) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
        PlainVM vm = (PlainVM) clusterVms.get(vmId);
        updateClustersReserve(0, 0, vm.getDrive()-newDrive, 0, 0);
        vm.updateDrive(newDrive);
    }

    public static void updateGPU(int vmId, int newGpu) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
        VmGPU vm = (VmGPU) clusterVms.get(vmId);
        updateClustersReserve(0, 0, 0, vm.getGpu()-newGpu, 0);
        vm.updateGpu(newGpu);
    }

    public static void updateBandwidth(int vmId, int newBandwidth) throws InputOutOfAdminsStandartsException, ClassCastException, NullPointerException {
        NetworkAccessible vm = (NetworkAccessible) clusterVms.get(vmId);
        updateClustersReserve(0, 0, 0, 0, vm.getBandwidth()-newBandwidth);
        vm.updateBandwidth(newBandwidth);
    }

    public static void updateOS(int vmId, OS newOs) throws InputOutOfAdminsStandartsException {
        if (newOs != null)
            clusterVms.get(vmId).updateOs(newOs);
    }

    /**
     * An accesor for the VMs class name.
     * @return The "Simple name" of the VMs class.
     */
    public static String getVmsClass(int vmId) {
        return clusterVms.get(vmId).getClass().getSimpleName();
    }

    /**
     * A method that deletes a VM from the cluster and updates the reserve accordingly
     * 
     * @param vmId The ID of the VM to be removed.
     */
    public static void deleteVm(int vmId) throws IllegalArgumentException {                   //TODO      NEED TO IMPLEMENT TRY-CATCH AT HIGHER LEVEL
        if (!clusterVms.containsKey(vmId)) {
            throw new IllegalArgumentException("This ID does not exist");
        }
        PlainVM vm = (PlainVM) clusterVms.remove(vmId);

        if (vm instanceof NetworkAccessible) {
            updateClustersReserve( 0, 0, 0, 0, ((NetworkAccessible) vm).getBandwidth());
        }
        if (vm instanceof VmGPU) {
            updateClustersReserve( 0, 0, 0, ((VmGPU) vm).getGpu() , 0);
        }
        updateClustersReserve(vm.getCpu(), vm.getRam(), vm.getDrive(), 0, 0);
    }

    /**
     * A method that reports the available sources of a single VM if an ID is given or of all the clusters VMs if the ID is zero.
     * 
     * @param vmId For input 0 the method will return a report for all the VMs
     * @return The report is returned in the following formmat: // TODO
     */
    private static StringBuilder report(int vmId) {

        StringBuilder finalReport;
        if (vmId == 0) {

        }
        else if (clusterVms.containsKey(vmId)) {

        }
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



}