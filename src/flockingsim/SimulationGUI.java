package flockingsim;

import drawing.Canvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout; // Import BorderLayout

/**
 * Manages the GUI elements (JFrame, controls) for the Flocking Simulation.
 */
public class SimulationGUI {

    private FlockingSimulation simulation;
    private Canvas canvas;
    private JFrame frame;

    /**
     * Constructor for the SimulationController.
     * Creates the main JFrame and sets up the basic layout.
     * @param simulation The FlockingSimulation instance to control.
     * @param canvas The Canvas to display the simulation.
     */
    public SimulationGUI(FlockingSimulation simulation, Canvas canvas) {
        this.simulation = simulation;
        this.canvas = canvas;

        // Create JFrame
        this.frame = new JFrame("Flocking Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Basic layout: Canvas in the center (more controls can be added later)
        frame.setLayout(new BorderLayout()); // Use BorderLayout
        frame.add(this.canvas, BorderLayout.CENTER); 

        // Set frame size, position, and visibility
        frame.setSize(800, 600); // Match canvas default or desired size
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true); // Make frame visible 
        
        // Add logic here later to create and add JPanels with sliders, buttons etc.
        // e.g., JPanel controlPanel = new JPanel();
        // frame.add(controlPanel, BorderLayout.SOUTH);
    }

    // Methods to add controls and handle events will go here later.

} 