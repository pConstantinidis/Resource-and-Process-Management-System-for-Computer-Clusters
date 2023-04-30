package lib.utils;

/**
 * An exception that is been thrown whenever the value given mismatches the clusters
 * standarts (CPU cores, GPUs, RAM etc) initialized by the admin 
 * 
 * @author pConstantinidis
 */
public class InputOutOfAdminsStandartsException extends Exception{
    
    public InputOutOfAdminsStandartsException() {}

    public InputOutOfAdminsStandartsException(String msg) {
        super(msg);
    }
}
