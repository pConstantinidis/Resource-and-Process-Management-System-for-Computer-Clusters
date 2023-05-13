package src.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import lib.Dependencies.BoundedQueue;
import lib.Dependencies.InputOutOfAdminsStandartsException;
import lib.Dependencies.NetworkAccessible;
import lib.utils.Globals;
import lib.utils.Globals.OS;

/**
 * The class is declared as abstarct to prevent from intstatiating //! Is that ok??
 * TODO
 * 
 * @apiNote Keep in mind that this class takes measures against invalid inputs and
 * logical errors althought in the view of a fatal error the program will return (void) and not terminate.
 * 
 * @author pConstantinidis
 */
public class ClusterAdmin {

    public final static int RAM = 256;                // Gb
    public final static int DRIVE = 2048;             // Gb
    public final static int CPU_CORES = 128;          // Cores
    public final static int GPU = 8;                  // GPUs
    public final static int NETWORK_BANDWIDTH = 320;  // Gb/sec
    public final static int MAX_PROGRAM_RUNTIME = 5400; //Seconds
    public final static short PROGRAM_REJECTIONS_toDISMISS = 3;

    /**
     * A {@code Map} that contains all the (vmID, VM) pairs
     */
    private final Map<Integer, VirtualMachine> clusterVms = new TreeMap<>();
    private int numOfVms = 0;

    /**
     * A buffer-like data collection in which user inputs are stored prior to be loaded to the clusters VMs.
     */
    private final ArrayList<Program> clustersPrograms = new ArrayList<>();
    private BoundedQueue<Program> programsInQueue;
    protected int queueCapacity = 0;

    // Accessors
    protected int getNumOfVms() {return numOfVms;}
    public int getQueueCapacity() {return queueCapacity;}
    protected VirtualMachine getVmByID(int id) {return clusterVms.get(id);}            //TODO     Is this error prone?


    protected void addProgram(Program p) {
        clustersPrograms.add(p);
    }

    protected void createPlainVm(int cpu, int ram, OS os, int drive) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new PlainVM(cpu, ram, os, drive));
        updateClustersReserve(-cpu, -ram, -drive, 0, 0);
        numOfVms++;
    }

    protected void createVmGPU(int cpu, int ram, OS os, int drive, int gpu) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmGPU(cpu, ram, os, drive, gpu));
        updateClustersReserve(-cpu, -ram, -drive, -gpu, 0);
        numOfVms++;
    }

    protected void createVmNetworked(int cpu, int ram, OS os, int drive, int bandwidth) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmNetworked(cpu, ram, os, drive, bandwidth));
        updateClustersReserve(-cpu, -ram, -drive, 0, -bandwidth);
        numOfVms++;
    }

    protected void createVmNetworkedGpu(int cpu, int ram, OS os, int drive, int gpu, int bandwidth) throws InputOutOfAdminsStandartsException {

        clusterVms.put(numOfVms+1, new VmNetworkedGPU(cpu, ram, os, drive, gpu, bandwidth));
        updateClustersReserve(-cpu, -ram, -drive, -gpu, -bandwidth);
        numOfVms++;
    }

    
    protected void updateCPU(int vmId, int newCpu) throws InputOutOfAdminsStandartsException {
        VirtualMachine vm = clusterVms.get(vmId);
                            
        updateClustersReserve(vm.getCpu()-newCpu, 0, 0, 0, 0);
        vm.updateCpu(newCpu);
    }

    protected void updateRAM(int vmId, int newRam) throws InputOutOfAdminsStandartsException {
        VirtualMachine vm = clusterVms.get(vmId);
        updateClustersReserve(0, vm.getRam()-newRam, 0, 0, 0);
        vm.updateRam(newRam);
    }

    protected void updateDrive(int vmId, int newDrive) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
        PlainVM vm = (PlainVM) clusterVms.get(vmId);
        updateClustersReserve(0, 0, vm.getDrive()-newDrive, 0, 0);
        vm.updateDrive(newDrive);
    }

    protected void updateGPU(int vmId, int newGpu) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
        VmGPU vm = (VmGPU) clusterVms.get(vmId);
        updateClustersReserve(0, 0, 0, vm.getGpu()-newGpu, 0);
        vm.updateGpu(newGpu);
    }

    protected void updateBandwidth(int vmId, int newBandwidth) throws InputOutOfAdminsStandartsException, ClassCastException, NullPointerException {
        NetworkAccessible vm = (NetworkAccessible) clusterVms.get(vmId);
        updateClustersReserve(0, 0, 0, 0, vm.getBandwidth()-newBandwidth);
        vm.updateBandwidth(newBandwidth);
    }

    protected void updateOS(int vmId, OS newOs) throws InputOutOfAdminsStandartsException {
        if (newOs != null)
            clusterVms.get(vmId).updateOs(newOs);
    }

    /**
     * An accesor for the VMs class name.
     * @return The "Simple name" of the VMs class.
     */
    protected String getVmsClass(VirtualMachine vm) {
        return clusterVms.getClass().getSimpleName();
    }

    /**
     * A method that deletes a VM from the cluster and updates the reserve accordingly
     * 
     * @param vmId The ID of the VM to be removed.
     */
    protected void deleteVm(int vmId) throws IllegalArgumentException {                   //TODO      NEED TO IMPLEMENT TRY-CATCH AT HIGHER LEVEL
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
   // private StringBuilder report(int vmId) {

   //     StringBuilder finalReport;
   //     if (vmId == 0) {

    //    }
    //    else if (clusterVms.containsKey(vmId)) {

     //   }
    //}

    /**
     * A method that updates the clusters stock of materials.
     */
    private void updateClustersReserve(int cpu, int ram, int drive, int gpu, int bandwidth) {
            Globals.setAvailableCpu(Globals.getAvailableCpu() + cpu);
            Globals.setAvailableRam(Globals.getAvailableRam() + ram);
            Globals.setAvailableDrive(Globals.getAvailableDrive() + drive);
            Globals.setAvailableGpu(Globals.getAvailableGpu() + gpu);
            Globals.setAvailableBandwidth(Globals.getAvailableBandwidth() + bandwidth);
    }

    /**
     * A method that adds all the {@code Program} elements
     * from the {@code clustersPrograms} to the queue.
     */
    protected void queuePrograms() {
        Program [] array = new Program[queueCapacity];
        clustersPrograms.toArray(array);

        // Could be done in a better way if we used the build-in method
        Globals.sort(array);
        
        programsInQueue = new BoundedQueue<>(queueCapacity);
        for (Program p:clustersPrograms)
            programsInQueue.push(p);
    }



    /*
     * A set of four methods that find the best suited VM according to the programs requirments
     */
        private PlainVM getBetterPlain(int cpu, int ram, int drive) {

            PlainVM betterVm = (PlainVM) clusterVms.values().iterator().next();
            double bestLoad = betterVm.computeLoad(cpu, ram, drive);
            for (VirtualMachine vm:clusterVms.values()) {

                if (vm instanceof PlainVM) {
                    double load = ((PlainVM) vm).computeLoad(cpu, ram, drive);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (PlainVM) vm;
                    }
                }
            }
            return betterVm;
        }
        
        private VmGPU getBetterGPU(int cpu, int ram, int drive, int gpu) {

            VmGPU betterVm = (VmGPU) clusterVms.values().iterator().next();
            double bestLoad = betterVm.computeLoad(cpu, ram, drive, gpu);

            for (VirtualMachine vm:clusterVms.values()) {
                if (vm instanceof VmGPU) {
                    double load = ((VmGPU) vm).computeLoad(cpu, ram, drive, gpu);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmGPU) vm;
                    }
                }
            }
            return betterVm;
        }

        private VmNetworked getBetterNetworked(int cpu, int ram, int drive, int network) {

            VmNetworked betterVm = (VmNetworked) clusterVms.values().iterator().next();
            double bestLoad = betterVm.computeLoad(cpu, ram, drive, network);

            for (VirtualMachine vm:clusterVms.values()) {
                if (vm instanceof NetworkAccessible) {
                    double load = ((VmNetworked) vm).computeLoad(cpu, ram, drive, network);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmNetworked) vm;
                    }
                }
            }
            return betterVm;
        }

        private VmNetworkedGPU getBetterGPUNetworked(int cpu, int ram, int drive, int gpu, int network) {

            VmNetworkedGPU betterVm = (VmNetworkedGPU) clusterVms.values().iterator().next();
            double bestLoad = betterVm.computeLoad(cpu, ram, drive, gpu);

            for (VirtualMachine vm:clusterVms.values()) {
                if (vm instanceof VmNetworkedGPU) {
                    double load = ((VmNetworkedGPU) vm).computeLoad(cpu, ram, drive, gpu, network);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmNetworkedGPU) vm;
                    }
                }
            }
            return betterVm;
        }


    /**
     * Identifys the programs requirments and calls the prorer method to find the better VM.
     * 
     * @param p The program to be assigned.
     * @return The better VM to assign the program {@code p}
     * @throws IllegalArgumentException
     */
    private VirtualMachine identifyProgram(Program p) throws IllegalArgumentException {
        String vmType;
        boolean isNetworked = p.getBandwidthRequired() != 0;
        boolean isGPUed = p.getGpuRequired() != 0;

        if (isGPUed && isNetworked) vmType = "vmnetworkedgpu";
        else if (isGPUed && !isNetworked) vmType = "vmgpu";
        else if (!isGPUed && !isNetworked) vmType = "plainvm";
        else vmType = "vmnetworked";

        VirtualMachine betterVM;
        switch (vmType) {

            case "plainvm":
                betterVM = getBetterPlain(p.getCoresRequired(), p.getRamRequired(), p.getDriveRequired());
                break;
            case "vmgpu":
                betterVM = getBetterGPU(p.getCoresRequired(), p.getRamRequired(), p.getDriveRequired(), p.getGpuRequired());
                break;
            case "vmnetworked":
                betterVM = getBetterNetworked(p.getCoresRequired(), p.getRamRequired(), p.getDriveRequired(), p.getBandwidthRequired());
                break;
            case "vmnetworkedgpu":
                betterVM = getBetterGPUNetworked(p.getCoresRequired(), p.getRamRequired(), p.getDriveRequired(), p.getGpuRequired(), p.getBandwidthRequired());
                break;

            default: throw new IllegalArgumentException();
        }
        return betterVM;
    }
    
    public void loadPrograms() {
        VirtualMachine vm;
        Program p;
        while (!programsInQueue.isEmpty()) {
            p = programsInQueue.pop();
            vm = identifyProgram(p);
            vm.assignProgram(p);
        }
    }
    

}