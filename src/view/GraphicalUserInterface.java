package src.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.CLI.CommandLineInterface;
import src.controler.FileInputHandler;
import src.model.ClusterAdmin;

public class GraphicalUserInterface extends JFrame {

    private ClusterAdmin admin = ClusterAdmin.getAdmin();
    private final CommandLineInterface cli = new CommandLineInterface(true);
;
    
    private final String title = "Choose data input";
    private Container guiContainer = getContentPane();
    private JPanel mainPanel = new JPanel();
    private JButton vmFileButton = new JButton("Create VMs from file");
    private JButton prgFileButton = new JButton("Create Programs from File");
    private JButton manualButton = new JButton("Creaty manually");
    
    public GraphicalUserInterface() {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 150);
        setLocationRelativeTo(null);
        setVisible(true);
        
        JPanel outerPanel = new JPanel(new FlowLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(prgFileButton);
        mainPanel.add(vmFileButton);
        prgFileButton.setEnabled(false);
        mainPanel.add(manualButton);
        
        guiContainer.setLayout(new BorderLayout());
        outerPanel.add(mainPanel);
        guiContainer.add(outerPanel, BorderLayout.CENTER);
        


        vmFileButton.addActionListener((actionPerformed) -> {
            setVisible(false); // Hide the main frame

            FileInputHandler fio = new FileInputHandler();
            if (!fio.confVms()) {
                reDirectToCLI(-1);
            }

        });
    
        prgFileButton.addActionListener((actionPerformed) -> {
            setVisible(false);

            FileInputHandler fio = new FileInputHandler();
            if (!fio.confPrgs())
                reDirectToCLI(0);
        });

        manualButton.addActionListener((actionPerformed) -> {
            if (admin.getNumOfVms() != 0)
                reDirectToCLI(0);
            else if (admin.getNumOfVms() == 0) {
                reDirectToCLI(-1);
            }
        });
    }



    /**
     * Redirects the user to a CLI
     */
    private void reDirectToCLI(int confStatus) {
        this.setVisible(false);

        JDialog output = new JDialog(this, "Redirect to CLI");
        output.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        output.setSize(310, 140);
        output.setLocationRelativeTo(null);
        
        cli.printIntro();
        output.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                switch (confStatus) {
                    case -1:
                        cli.displayMenu();
                        cli.createPrgsManualy();
                        cli.processPrgs();
                        break;
                    case 0:
                        cli.createPrgsManualy();
                        cli.processPrgs();
                        break;
                    case 1:
                        cli.processPrgs();
                        break;
                }
                
                System.exit(0);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Use the running CLI instead.");
        
/*      if (thereIsError) {
            JLabel errorTxt = new JLabel("Error during file configuration.");
            errorTxt.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
            panel.add(errorTxt);
        }   
*/
        label.setFont(new Font(Font.DIALOG, Font.ITALIC, 20));
        panel.add(label);

        output.getContentPane().setLayout(new BorderLayout());
        output.getContentPane().add(panel, BorderLayout.CENTER);

        output.setVisible(true);
    }
    
    /**
     * 
     * @param vmsRejected
     */
    protected void showVmConfStatus(int vmsRejected) {
        this.setVisible(false);

        JDialog output = new JDialog(this, "Configuration status");
        output.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        output.setSize(310, 140);
        output.setLocationRelativeTo(null);

        vmFileButton.setEnabled(false);
        prgFileButton.setEnabled(true);

        output.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setVisible(true); // Reappear the main frame when the dialog is closed
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel validVms = new JLabel("Valid VMs: " + admin.getNumOfVms());
        JLabel invalidVms = new JLabel("VMs rejected: " + vmsRejected);
        validVms.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        invalidVms.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        panel.add(validVms);
        panel.add(invalidVms);

        output.getContentPane().setLayout(new BorderLayout());
        output.getContentPane().add(panel, BorderLayout.CENTER);

        ImageIcon icon = new ImageIcon("./lib/utils/infoIcon.png");
        output.setIconImage(icon.getImage());
        output.setVisible(true);
        System.out.println(admin.report(0));    // print log
    }

    /**
     * 
     */
    protected void showPrgConfStatus(int programsRejected) {
        this.setVisible(false);

        JDialog output = new JDialog(this, "Configuration status");
        output.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        output.setSize(320, 140);
        output.setLocationRelativeTo(null);

        vmFileButton.setEnabled(false);
        manualButton.setEnabled(false);

        output.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                showExeButton();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel validPrgs = new JLabel("Valid programs: " + admin.getNumOfPrgs());
        JLabel invalidPrgs = new JLabel("Programs rejected: " + programsRejected);
        validPrgs.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        invalidPrgs.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        panel.add(validPrgs);
        panel.add(invalidPrgs);

        output.getContentPane().setLayout(new BorderLayout());
        output.getContentPane().add(panel, BorderLayout.CENTER);

        ImageIcon icon = new ImageIcon("./lib/utils/infoIcon.png");
        output.setIconImage(icon.getImage());
        output.setVisible(true);

    }

    /**
     * 
     */
    private void showExeButton() {
        this.setVisible(false);

        JFrame frame = new JFrame("Execution");
        JButton exeButton = new JButton("Run programs");

        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        frame.setLocationRelativeTo(null);

        frame.add(exeButton);
        frame.setVisible(true);

        exeButton.addActionListener((actionPerformed) -> {
            cli.processPrgs();
            System.exit(0);
        });
    }

}
