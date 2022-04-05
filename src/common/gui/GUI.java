package common.gui;

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

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorController;
import common.messages.elevator.ElevatorStatusMessage;

/**
 * @author Jacob Charpentier, Favour Olotu
 *
 */
public class GUI extends JFrame implements ElevatorControllerObserver{
	private ElevatorController elevatorController;
    private JPanel mainPanel;
    private JScrollPane logSP;
    private JTextArea logTA;
    private JLabel[] elevatorFlrLabels;
    private JLabel[] elevatorDoors;
    private JLabel[] elevatorDoorsStatus;
    private JLabel[] elevatorStatus;

    public GUI() {
        super("Elevator GUI");
        
        buildDisplay();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setResizable(true);
        this.elevatorController = null;
        this.setVisible(true);
        
    }

    private void buildDisplay(){
        Integer[] options = {1, 2, 3, 4, 5, 6};
        int numElevs = (Integer)JOptionPane.showInputDialog(this, "Select Number of Elevators",
                "Elevator Setup", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        ImageIcon imgClosed = new ImageIcon("resources/elevDoorsClose.png");
        ImageIcon imgOpen = new ImageIcon("resources/elevDoorsOpen.png");

        this.setSize(500 + numElevs * 100, 350);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(6, 2 + numElevs, new Insets(0, 0, 0, 0), -1, -1));
        
        final JLabel label1 = new JLabel();
        label1.setText("Log / Processes");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(82, 50), null, 0, false));

        logSP = new JScrollPane();
        mainPanel.add(logSP, new GridConstraints(1, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, numElevs + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        final JLabel label6 = new JLabel();
        label6.setText(" ");
        panel1.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        logTA = new JTextArea();
        logTA.setLineWrap(true);
        logSP.setViewportView(logTA);

        elevatorFlrLabels = new JLabel[numElevs];
        elevatorDoors = new JLabel[numElevs];
        elevatorDoorsStatus = new JLabel[numElevs];
        elevatorStatus = new JLabel[numElevs];

        for (int i = 1; i <= numElevs; i++){
            int index = i - 1;

            JLabel label = new JLabel();
            label.setText("Elevator " + i);
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

        }
    }

    public void setEle(ElevatorController elevatorController) {
    	this.elevatorController = elevatorController;
    }
    
    @Override
	public void handleStatusUpdate(ElevatorStatusMessage message) {
    	ElevatorCar car = elevatorController.getElevatorCar(message.getElevatorId());
    	
		logTA.append("" + car.getFloorNumber() +"\n");
		logTA.append("" + car.getId() +"\n");
		logTA.append("" + car.getErrorState() +"\n");

	}
//    public static void main(String[] args) {
//        JFrame frame = new GUI();
//        frame.setVisible(true);
//    }

	
}
