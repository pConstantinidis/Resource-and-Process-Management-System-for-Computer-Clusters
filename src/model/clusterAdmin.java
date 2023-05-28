package src.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lib.dependencies.BoundedQueue;
import lib.dependencies.InputOutOfAdminsStandartsException;
import lib.dependencies.NetworkAccessible;
import lib.utils.Globals;
import lib.utils.Globals.OS;
import src.backend.Configure;
import src.controler.CLI_IOHandler;

/**
 * @apiNote Keep in mind that this class takes measures against invalid inputs and
 * logical errors, althought in the view of a fatal error the program will inform the user by a print statment and NOT terminate.
 * 
 * @author pConstantinidis
 */
public final class ClusterAdmin implements Serializable {

    public final static int RAM = 256;                // Gb
    public final static int DRIVE = 2048;             // Gb
    public final static int CPU_CORES = 128;          // Cores
    public final static int GPU = 8;                  // GPUs
    public final static int NETWORK_BANDWIDTH = 320;  // Gb/sec
    public final static int MAX_PROGRAM_RUNTIME = 5400; //Seconds
    public final static short PROGRAM_REJECTIONS_toDISMISS = 2;
    private final static long SLEEP_DURATION = 2;
    private final static TimeUnit time = TimeUnit.SECONDS;
    private final int dummyLoad = 1000;


    /**
     * A {@code Map} that contains all the (vmID, VM) pairs
     */
    private final Map<Integer, VirtualMachine> clusterVms = new HashMap<>();
    private int numOfVms = 0;

    /**
     * A buffer-like data collection in which user inputs are stored prior to be loaded to the clusters VMs.
     */
    private final ArrayList<Program> clustersPrograms = new ArrayList<>();
    private BoundedQueue<Program> programsInQueue;
    public boolean isQueueEmpty() {return programsInQueue.isEmpty();}
    public int queueCapacity = 0;

    public void pushProgram(Program p) {
        programsInQueue.push(p);
    }
    public int getNumOfPrgs() {
        return clustersPrograms.size();
    }
    public void removeProgram(Program p) {
        clustersPrograms.remove(p);
    }
    /**
     * A method that returns the clusters admin based on the singleton design pattern.
     * <p>This way only one single instantiation of this class is possible.
     */
    public static ClusterAdmin getAdmin() {
        return CLUSTERS_ADMIN;
    }
    private static final ClusterAdmin CLUSTERS_ADMIN = new ClusterAdmin();
    private ClusterAdmin() {}

    // Accessors
    public int getNumOfVms() {return numOfVms;}
    public int getQueueCapacity() {return queueCapacity;}
    public VirtualMachine getVmByID(int id) {return clusterVms.get(id);}

    
    /**
     * 
     * @return -1 if neither VMs neither programs where configured, 0 if only VMs where and 1 if both. 
     */
    public int autoConf() {
        Configure conf = new Configure();
        int prgData=0;
        try {
            conf.configVMs();
            prgData = conf.configPrograms();
        } catch (Exception e) {
        } finally {
            if (numOfVms == 0) return -1;
            if (numOfVms != 0) {
                System.out.println(CLI_IOHandler.underLine+"\n\tAuto configuration completed.\n"+CLI_IOHandler.underLine);
                System.out.println("\tWith status:\n\t\t\tValid VMs: "+numOfVms+"\n\t\t\tVMs rejected: "+conf.numOfInvalidVMs());
                System.out.println("\t\t\tValid programs: "+prgData+"\tids in file order "+Program.idsToString()+"\n\t\t\tPrograms rejected: "+conf.numOfInvalidPrgs()+"\n"+CLI_IOHandler.underLine);
                if (prgData == 0) return 0;
            }
        }
        return 1;
    }

    public void addProgram(Program p) {
        if (Globals.isProgramValid(p)) {
            clustersPrograms.add(p);
            queueCapacity++;
        }
    }

    public void createPlainVm(int cpu, int ram, OS os, int drive) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new PlainVM(cpu, ram, os, drive));
        updateClustersReserve(-cpu, -ram, -drive, 0, 0);
        numOfVms++;
    }

    public void createVmGPU(int cpu, int ram, OS os, int drive, int gpu) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmGPU(cpu, ram, os, drive, gpu));
        updateClustersReserve(-cpu, -ram, -drive, -gpu, 0);
        numOfVms++;
    }

    public void createVmNetworked(int cpu, int ram, OS os, int drive, int bandwidth) throws InputOutOfAdminsStandartsException {
        
        clusterVms.put(numOfVms+1, new VmNetworked(cpu, ram, os, drive, bandwidth));
        updateClustersReserve(-cpu, -ram, -drive, 0, -bandwidth);
        numOfVms++;
    }

    public void createVmNetworkedGpu(int cpu, int ram, OS os, int drive, int gpu, int bandwidth) throws InputOutOfAdminsStandartsException {

        clusterVms.put(numOfVms+1, new VmNetworkedGPU(cpu, ram, os, drive, gpu, bandwidth));
        updateClustersReserve(-cpu, -ram, -drive, -gpu, -bandwidth);
        numOfVms++;
    }

    
    public void updateCPU(int vmId, int newCpu) throws InputOutOfAdminsStandartsException {
        VirtualMachine vm = clusterVms.get(vmId);
                            
        updateClustersReserve(vm.getCpu()-newCpu, 0, 0, 0, 0);
        vm.updateCpu(newCpu);
    }

    public void updateRAM(int vmId, int newRam) throws InputOutOfAdminsStandartsException {
        VirtualMachine vm = clusterVms.get(vmId);
        updateClustersReserve(0, vm.getRam()-newRam, 0, 0, 0);
        vm.updateRam(newRam);
    }

    public void updateDrive(int vmId, int newDrive) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
        PlainVM vm = (PlainVM) clusterVms.get(vmId);
        updateClustersReserve(0, 0, vm.getDrive()-newDrive, 0, 0);
        vm.updateDrive(newDrive);
    }

    public void updateGPU(int vmId, int newGpu) throws ClassCastException, InputOutOfAdminsStandartsException, NullPointerException {
        VmGPU vm = (VmGPU) clusterVms.get(vmId);
        updateClustersReserve(0, 0, 0, vm.getGpu()-newGpu, 0);
        vm.updateGpu(newGpu);
    }

    public void updateBandwidth(int vmId, int newBandwidth) throws InputOutOfAdminsStandartsException, ClassCastException, NullPointerException {
        NetworkAccessible vm = (NetworkAccessible) clusterVms.get(vmId);
        updateClustersReserve(0, 0, 0, 0, vm.getBandwidth()-newBandwidth);
        vm.updateBandwidth(newBandwidth);
    }

    public void updateOS(int vmId, OS newOs) throws InputOutOfAdminsStandartsException {
        if (newOs != null)
            clusterVms.get(vmId).updateOs(newOs);
    }

    /**
     * An accesor for the VMs class name.
     * @return The "Simple name" of the VMs class or {@code null} if is not of type {@code VirtualMachine}.
     */
    public String getVmsClass(VirtualMachine vm) {
        if (vm instanceof VirtualMachine)
            return vm.getClass().getSimpleName();
        return null;
    }

    /**
     * A method that deletes a VM from the cluster and updates the reserve accordingly
     * 
     * @param vmId The ID of the VM to be removed.
     */
    public void deleteVm(int vmId) throws IllegalArgumentException {
        if (!clusterVms.containsKey(vmId)) {
            throw new IllegalArgumentException("this ID does not exist");
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
     * @param id For input 0 the method will return a report for all the VMs
     * @return The report is returned in the following formmat: ID/resource/...
     */
    public StringBuilder report(int id) {
        StringBuilder repo = new StringBuilder();
        VirtualMachine vm;
        
        if (id == 0) {
            for (java.util.Map.Entry<Integer, VirtualMachine> entry :clusterVms.entrySet()) {
                repo.append("ID: " +entry.getKey());
                repo.append(appendVmInfo(entry.getValue())+"\n");               
            }
        }
        else {
            vm = clusterVms.get(id);
            repo.append("ID: "+id+ appendVmInfo(vm));
        }
        return repo;
    }

    /**
     * @see #report
     * @param vm
     * @return
     */
    private StringBuilder appendVmInfo(VirtualMachine vm) {
        StringBuilder sb = new StringBuilder();
        String sp = CLI_IOHandler.spacing;

        sb.append(sp+"CPU: "+ (vm.getCpu()-vm.getAllocCPU()));
        sb.append(sp+"RAM: "+ (vm.getRam()-vm.getAllocRAM()));
        sb.append(sp+"SSD: "+ (((PlainVM) vm).getDrive() -((PlainVM) vm).getAllocDrive()));
        sb.append(sp+"OS: "+ vm.getOs());
                        
        switch (getVmsClass(vm).toLowerCase()) {
            case "vmgpu":
                sb.append(sp+"GPU: "+ (((VmGPU) vm).getGpu() - ((VmGPU) vm).getAllocGPU()));
                break;
            case "vmnetworked":
                sb.append(sp+"Bandwidth: "+ (((VmNetworked) vm).getBandwidth() - ((VmNetworked) vm).getAllocBandwidth()));
                break;
            case "vmnetworkedgpu":
                sb.append(sp+"GPU: "+ (((VmGPU) vm).getGpu() - ((VmGPU) vm).getAllocGPU()));
                sb.append(sp+"Bandwidth: "+ (((VmNetworkedGPU) vm).getBandwidth() - ((VmNetworkedGPU) vm).getAllocBandwidth()));
                break;
                default:
                break;
            }
            return sb;
        }
        
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
    public void queuePrograms() {
        Program [] array = new Program[queueCapacity];
        clustersPrograms.toArray(array);
        
        // Could be done in a better way if we used the build-in method
        Globals.sort(array);
        
        programsInQueue = new BoundedQueue<>(queueCapacity);
        for (Program p :array)
            programsInQueue.push(p);
    }
    
    
    
    /*
    * A set of four methods that find the best suited VM according to the programs requirments
    */
    private PlainVM getBetterPlain(int cpu, int ram, int drive) {

        PlainVM betterVm = null;
        double bestLoad = dummyLoad;
        for (VirtualMachine vm:clusterVms.values()) {
            
                if (vm instanceof PlainVM) {
                    double load = ((PlainVM) vm).computeLoad(cpu, ram, drive);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (PlainVM) vm;
                    }
                }
            }
            if (betterVm == null) 
                throw new IllegalArgumentException("suited vm not found");
            return betterVm;
        }
        
        private VmGPU getBetterGPU(int cpu, int ram, int drive, int gpu) {
            
            VmGPU betterVm = null;
            double bestLoad = dummyLoad;

            for (VirtualMachine vm:clusterVms.values()) {
                if (vm instanceof VmGPU) {
                    double load = ((VmGPU) vm).computeLoad(cpu, ram, drive, gpu);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmGPU) vm;
                    }
                }
            }
            if (betterVm == null) 
                throw new IllegalArgumentException("suited vm not found");
            return betterVm;
        }

        /**
         * A method that finds and returns the best suited {@code NetworkAccessible} VM based on the potential load.
         * @apiNote The returned VM may be either of type {@code VmNetworked} or {@code VmnetworkedGPU}
         */
        private VirtualMachine getBetterNetworked(int cpu, int ram, int drive, int network) {

            VirtualMachine betterVm = null;
            double bestLoad = dummyLoad;

            for (VirtualMachine vm:clusterVms.values()) {
                if (vm instanceof VmNetworkedGPU) {
                    double load = ((VmNetworkedGPU) vm).computeLoad(cpu, ram, drive, network);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmNetworkedGPU) vm;
                    }
                }
                else if (vm instanceof VmNetworked) {
                    double load = ((VmNetworked) vm).computeLoad(cpu, ram, drive, network);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmNetworked) vm;
                    }
                }
            }
            if (betterVm == null) 
                throw new IllegalArgumentException("suited vm not found");
            return betterVm;
        }
        
        private VmNetworkedGPU getBetterGPUNetworked(int cpu, int ram, int drive, int gpu, int network) {
            
            VmNetworkedGPU betterVm = null;
            double bestLoad = dummyLoad; //i don't got a better idea
            Iterator<VirtualMachine> iter = clusterVms.values().iterator();

            while (iter.hasNext()) {
                VirtualMachine vm = iter.next();
                if (vm instanceof VmNetworkedGPU) {
                    double load = ((VmNetworkedGPU) vm).computeLoad(cpu, ram, drive, gpu, network);
                    if (bestLoad > load) {
                        bestLoad = load;
                        betterVm = (VmNetworkedGPU) vm;
                    }
                }
            }
            if (betterVm == null) 
                throw new IllegalArgumentException("suited vm not found");
            return betterVm;
        }
        
        
        /**
         * Identifys the programs requirments and calls the prorer method to find the better VM.
         * 
         * @param p The program to be assigned.
         * @return The better VM to assign the program {@code p}
         */
        private VirtualMachine identifyProgram(Program p) {
            String vmType;
        boolean isNetworked = p.getBandwidthRequired() != 0;
        boolean isGPUed = p.getGpuRequired() != 0;

        if (isGPUed && isNetworked) vmType = "vmnetworkedgpu";
        else if (isGPUed && !isNetworked) vmType = "vmgpu";
        else if (!isGPUed && !isNetworked) vmType = "plainvm";
        else vmType = "vmnetworked";

        VirtualMachine betterVM = null;
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
        }
        return betterVM;
    }
    
    /**
     * Pops a program from the queue and assignes it to the optimum VM.
     * @throws IOException
     * @return 0 If the program was assigned succesfuly, 1 if the program was re-pushed to the queue and -1 if it gote dismissed or if the queue is empty.
     */
    public int loadProgram() {
        VirtualMachine vm;
        Program prg;

        try {
            prg = programsInQueue.pop();
        } catch(IllegalStateException e) {
            System.out.println("\n\tThe queue has been empied");
            return -1;
        }

        vm = identifyProgram(prg);
        int result = assignProgramToVm(prg, vm);
        if (result ==0) System.out.println("\n\tThe program with ID "+prg.getID()+" is running on VM "+Globals.getKeyFromValue((HashMap<Integer,VirtualMachine>) clusterVms, vm));
        return result;
    }
    
    /**
     * A method that tries to allocate resources on the {@code vm} and if they are invalid it retrieves them.
     * 
     * @param p The program to be assigned.
     * @param vm the VM where the program is to be assigned.
     * @return 0 If the program was assigned succesfuly, 1 if the program was re-pushed to the queue and -1 if it gote dismissed.
     */
    private int assignProgramToVm(Program p, VirtualMachine vm) {
        boolean rejected=false;
        
        try { ((PlainVM) vm).addAllocCPU(p.getCoresRequired()); }
        catch (IllegalArgumentException e) {
            rejected=true;
        } 
        try { ((PlainVM) vm).addAllocRAM(p.getRamRequired()); }
        catch (IllegalArgumentException e) {
            rejected=true;
        }
        try { ((PlainVM) vm).addAllocDrive(p.getDriveRequired()); }
        catch (IllegalArgumentException e) {
            rejected=true;
        }
        
        
        if (!rejected) {
            
            if (vm instanceof VmNetworked) {
                
                try { ((VmNetworked) vm).addAllocBandwidth(p.getBandwidthRequired());
                } catch(IllegalArgumentException e) {
                    rejected=true;
                    ((VmNetworked) vm).addAllocCPU(-p.getCoresRequired());
                    ((VmNetworked) vm).addAllocRAM(-p.getRamRequired());
                    ((VmNetworked) vm).addAllocDrive(-p.getDriveRequired());
                }
            } else if (vm instanceof VmNetworkedGPU) {
    
                try { ((VmNetworkedGPU)vm).addAllocGPU(p.getGpuRequired());
                } catch (IllegalArgumentException e) {
                    rejected=true;
                    ((VmNetworkedGPU) vm).addAllocCPU(-p.getCoresRequired());
                    ((VmNetworkedGPU) vm).addAllocRAM(-p.getRamRequired());
                    ((VmNetworkedGPU) vm).addAllocDrive(-p.getDriveRequired());
                }
                if (!rejected)
                    try { ((VmNetworkedGPU) vm).addAllocBandwidth(p.getBandwidthRequired());
                    } catch (IllegalArgumentException e) {
                        rejected=true;
                        ((VmNetworkedGPU) vm).addAllocGPU(-p.getGpuRequired());
                        ((VmNetworkedGPU) vm).addAllocCPU(-p.getCoresRequired());
                        ((VmNetworkedGPU) vm).addAllocRAM(-p.getRamRequired());
                        ((VmNetworkedGPU) vm).addAllocDrive(-p.getDriveRequired());
                    }
    
            } else if (vm instanceof VmGPU) {
    
                try { ((VmGPU) vm).addAllocGPU(p.getGpuRequired());
                } catch(IllegalArgumentException e) {
                    rejected=true;
                    ((VmGPU) vm).addAllocCPU(-p.getCoresRequired());
                    ((VmGPU) vm).addAllocRAM(-p.getRamRequired());
                    ((VmGPU) vm).addAllocDrive(-p.getDriveRequired());
                }
            }
        }
        return vm.assignProgram(p, rejected);
    }

    /**
     * A method that checks if programs have executed by calling the {@code peekRunningPrgs()} method for each VM.
     * @return The number of programs that executed succesfuly.
     */
    public int updateRunningPrograms() {
        int finished, count=0;
        Iterator<VirtualMachine> iter = clusterVms.values().iterator();
        PlainVM vm;

        // Iterates through each vm
        while (iter.hasNext()) {
            vm = (PlainVM) iter.next();
            if (!vm.programsAssigned.isEmpty()) {
                finished = vm.peekRunningPrgs();
                if (finished != 0)
                    vm.freeResources(finished);
                count += finished;
            }
        }
        return count;
    }
    
    public void spleep() {
        try {
            time.sleep(SLEEP_DURATION);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}