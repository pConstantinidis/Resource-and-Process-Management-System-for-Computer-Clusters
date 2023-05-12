package lib.Dependencies;

/**
 * An interface that describes virtual machines that have access to the web.
 * 
 * @author pConstantinidis
 */
public interface NetworkAccessible {

  /**
   * An accesor for the bandwidth attribute.
   */
  int getBandwidth();

  /**
    * Updates the network bandwidth that the VM has access to.
    * 
    * @param newBandwidth The total bandwidth that the VM is going to have access to after the update.
    * @return The network in GBs/sec that the VM had access to before the update.
    * @throws InputOutOfAdminsStandartsException
    */
  int updateBandwidth(int newBandwidth) throws InputOutOfAdminsStandartsException;
  

  /**
   * A method that should implement needed checks in order to set {@code Allocated Bandwidth} attribute.
   * 
   * @param bandwidth
   * @throws IllegalArgumentException
   */
  void addAllocBandwidth(int bandwidth) throws IllegalArgumentException ; 

}
