package lib.utils;

/**
 * An exception that is been thrown whenever the value given mismatches the clusters
 * standarts (CPU cores, GPUs, RAM etc) initialized by the admin 
 */
public class InputOutOfAdminsStandartsException extends IllegalArgumentException{
    
    public InputOutOfAdminsStandartsException() {}

    public InputOutOfAdminsStandartsException(String msg) {
        super(msg);
    }
}
