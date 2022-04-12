package common.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import ElevatorSubsystem.ElevatorController;
import common.exceptions.ElevatorStateException;
import common.messages.Message;
import common.messages.elevator.ElevatorStatusMessage;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * This class creates and maintains the GUI
 * 
 * @author Jacob Charpentier, Favour Olotu
 *
 */
public class GUI extends JFrame{
    private JPanel mainPanel;
    private JScrollPane logSP;
    private JTextArea logTA;
    private JLabel[] elevatorFlrLabels;
    private JLabel[] elevatorDoors;
    private JLabel[] elevatorDoorsStatus;
    private JLabel[] elevatorStatus;
    private JLabel[] elevatorErrorStatus;
    
    private ImageIcon imgClosed = new ImageIcon("resources/elevDoorsClose.png");
    private ImageIcon imgOpen = new ImageIcon("resources/elevDoorsOpen.png");
    
    private int numberElevators;
    
	/**
	 * RPC communications channel for the scheduler
	 */
	private SubsystemCommunicationRPC schedulerSubsystemCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.GUI, SubsystemComponentType.SCHEDULER);

	/**
	 * GUI class Constructor given a number of elevators
	 * 
	 * @param numElevs
	 */
    public GUI(int numElevs) {
        super("Elevator GUI");
        
        buildDisplay(numElevs);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setResizable(true);
        this.setVisible(true);
        
    }
    
    /**
     * GUI class constructor without a given number of elevators
     * 
     */
    public GUI() {
        super("Elevator GUI");
        
        buildDisplay();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setResizable(true);
        this.setVisible(true);
        
    }

    /**
     * Method to request how many elevators the user would like, then builds a GUI based on that
     */
    private void buildDisplay(){
    	// Request user input
        Integer[] options = {1, 2, 3, 4, 5, 6};
        numberElevators = (Integer)JOptionPane.showInputDialog(this, "Select Number of Elevators",
                "Elevator Setup", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        // Specify size
        this.setSize(500 + numberElevators * 100, 350);

        // Create base GUI
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(7, 2 + numberElevators, new Insets(0, 0, 0, 0), -1, -1));
        
        final JLabel label1 = new JLabel();
        label1.setText("Log(s)");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(82, 50), null, 0, false));

        logSP = new JScrollPane();
        mainPanel.add(logSP, new GridConstraints(1, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, numberElevators + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        final JLabel label6 = new JLabel();
        label6.setText(" ");
        panel1.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        logTA = new JTextArea();
        logTA.setEditable(false);
        logTA.setLineWrap(true);
        logSP.setViewportView(logTA);

        elevatorFlrLabels = new JLabel[numberElevators];
        elevatorDoors = new JLabel[numberElevators];
        elevatorDoorsStatus = new JLabel[numberElevators];
        elevatorStatus = new JLabel[numberElevators];
        elevatorErrorStatus = new JLabel[numberElevators];

        // Add columns for each elevator
        for (int i = 2; i <= numberElevators + 1; i++){
            int index = i - 2;

            JLabel label = new JLabel();
            label.setText("Elevator " + (i-2));
            mainPanel.add(label, new GridConstraints(0, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(53, 50), null, 0, false));

            elevatorFlrLabels[index] = new JLabel();
            elevatorFlrLabels[index].setText("Current Floor: 1");
            mainPanel.add(elevatorFlrLabels[index], new GridConstraints(1, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            elevatorDoors[index] = new JLabel();
            elevatorDoors[index].setIcon(imgClosed);
            mainPanel.add(elevatorDoors[index], new GridConstraints(2, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

            elevatorDoorsStatus[index] = new JLabel();
            elevatorDoorsStatus[index].setText("Door: Closed");
            mainPanel.add(elevatorDoorsStatus[index], new GridConstraints(3, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            elevatorStatus[index] = new JLabel();
            elevatorStatus[index].setText("Status: Down");
            mainPanel.add(elevatorStatus[index], new GridConstraints(4, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
            
            elevatorErrorStatus[index] = new JLabel();
            elevatorErrorStatus[index].setText("");
            mainPanel.add(elevatorErrorStatus[index], new GridConstraints(5, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        }
    }

    public void buildDisplay(int numElevs){
        numberElevators = numElevs;

        // Specify size
        this.setSize(500 + numberElevators * 100, 350);

        // Create base GUI
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(7, 2 + numberElevators, new Insets(0, 0, 0, 0), -1, -1));
        
        final JLabel label1 = new JLabel();
        label1.setText("Log(s)");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(82, 50), null, 0, false));

        logSP = new JScrollPane();
        mainPanel.add(logSP, new GridConstraints(1, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, numberElevators + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        final JLabel label6 = new JLabel();
        label6.setText(" ");
        panel1.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        logTA = new JTextArea();
        logTA.setEditable(false);
        logTA.setLineWrap(true);
        logSP.setViewportView(logTA);

        elevatorFlrLabels = new JLabel[numberElevators];
        elevatorDoors = new JLabel[numberElevators];
        elevatorDoorsStatus = new JLabel[numberElevators];
        elevatorStatus = new JLabel[numberElevators];
        elevatorErrorStatus = new JLabel[numberElevators];

        // Add columns for each elevator
        for (int i = 2; i <= numberElevators + 1; i++){
            int index = i - 2;

            JLabel label = new JLabel();
            label.setText("Elevator " + (i-2));
            mainPanel.add(label, new GridConstraints(0, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(53, 50), null, 0, false));

            elevatorFlrLabels[index] = new JLabel();
            elevatorFlrLabels[index].setText("Current Floor: 1");
            mainPanel.add(elevatorFlrLabels[index], new GridConstraints(1, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            elevatorDoors[index] = new JLabel();
            elevatorDoors[index].setIcon(imgClosed);
            mainPanel.add(elevatorDoors[index], new GridConstraints(2, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

            elevatorDoorsStatus[index] = new JLabel();
            elevatorDoorsStatus[index].setText("Door: Closed");
            mainPanel.add(elevatorDoorsStatus[index], new GridConstraints(3, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            elevatorStatus[index] = new JLabel();
            elevatorStatus[index].setText("Status: Down");
            mainPanel.add(elevatorStatus[index], new GridConstraints(4, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
            
            elevatorErrorStatus[index] = new JLabel();
            elevatorErrorStatus[index].setText("");
            mainPanel.add(elevatorErrorStatus[index], new GridConstraints(5, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        }
    }

    /**
     * get Number of elevators
     * 
     * @return int number of elevators
     */
    public int getNumberOfElevators() {
    	return numberElevators;
    }
    /**
     * This method set-up a thread to continuously receive elevator status 
     * messages from the scheduler
     */
    public void receiveUpdates() {
		(new Thread() {
			@Override
			public void run() {
				// wait for scheduler messages
				while (true) {
					Message message;
					try {
						message = schedulerSubsystemCommunication.receiveMessage();
						//Handle message by updating the GUI
						handleStatusUpdate(message);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
    }
    
    /**
     * This method updates the UI using the message recieved from the scheduler
     * 
     * @param msg - message to handle
     */
	private void handleStatusUpdate(Message msg) {
		ElevatorStatusMessage message = (ElevatorStatusMessage) msg;
		
		// Updating the log component
		logTA.append("Elevator " + message.getElevatorId() + " is at floor: " + message.getFloorNumber() +"\n");
		logTA.append("--------------------------\n");

		// Updating the needed elevator
		int index = message.getElevatorId();

        elevatorFlrLabels[index].setText("Current Floor: " + message.getFloorNumber());

		
        if(message.isDoorOpen()) {
        	elevatorDoors[index].setIcon(imgOpen);
        	elevatorDoorsStatus[index].setText("Door: Open");
        } else {
        	elevatorDoors[index].setIcon(imgClosed);
        	elevatorDoorsStatus[index].setText("Door: Closed");
        }
        
        elevatorStatus[index].setText("Direction: " + message.getDirection());
        
        if (message.getErrorState() != null){
        	elevatorErrorStatus[index].setText("" + ((ElevatorStateException)message.getErrorState()).getFault());
        	elevatorErrorStatus[index].setForeground(Color.RED);
        }else {
        	elevatorErrorStatus[index].setText("");
        }

	}

	public static void main(String[] args) {
		int numElevs = ElevatorController.NUMBER_OF_ELEVATORS;
		
		GUI programDisplay = new GUI(numElevs);
		programDisplay.receiveUpdates();
	}
	
}
