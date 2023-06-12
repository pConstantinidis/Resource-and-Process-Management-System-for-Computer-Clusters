package src.controler;

import java.io.IOException;

import lib.dependencies.InputOutOfAdminsStandartsException;
import src.backend.Configure;
import src.model.ClusterAdmin;
import src.view.GraphicalUserInterface;

public class FileInputHandler extends GraphicalUserInterface {

    private final Configure conf = new Configure();
    private final ClusterAdmin admin = ClusterAdmin.getAdmin();
    
    /**
     * @return {@code true} if the VMs where configured succesfuly, {@code false} otherwise.
     */
    public boolean confVms() {
        int numOfVms;
        try {
            conf.configVMs();
        } catch (IOException | InputOutOfAdminsStandartsException e) {
        } finally {
            numOfVms = admin.getNumOfVms();

            if (numOfVms == 0) return false;
            if (numOfVms != 0) showVmConfStatus(conf.numOfInvalidVMs());
        }
        return true;
    }
    
    /**
     * @return {@code true} if the VMs where configured succesfuly, {@code false} otherwise.
     */
    public boolean confPrgs() {
        int numOfPrograms=0;
        int prgsRejected=0;
        try {
            numOfPrograms = conf.configPrograms();
            prgsRejected = conf.numOfInvalidPrgs();
        } catch (IOException e) {
        } finally {
            if (numOfPrograms == 0)
                return false;
        }
        showPrgConfStatus(prgsRejected);
        return true;
    }
}
