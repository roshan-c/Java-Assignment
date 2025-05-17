package flockingsim;

import drawing.Canvas;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout; // Import BorderLayout
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.ColorConvertOp;

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
        frame.setSize(1000, 600); // Match canvas default or desired size
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true); // Make frame visible 

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new javax.swing.BoxLayout(controlPanel, javax.swing.BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new java.awt.Dimension(150, 600));
        controlPanel.setBackground(Color.GRAY);
        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
        speedSlider.setMaximumSize(new java.awt.Dimension(130, 20));
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateBoidSpeed(source.getValue());
                }
            }
        });
       
        JSlider separationSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 20);
        separationSlider.setMaximumSize(new java.awt.Dimension(130, 20));
        separationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateSeparationWeight(source.getValue() / 10.0);
                }
            }
        });

        JSlider alignmentSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 22);
        alignmentSlider.setMaximumSize(new java.awt.Dimension(130, 20));
        alignmentSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateAlignmentWeight(source.getValue() / 20.0);
                }
            }
        });

        JSlider cohesionSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 22);
        cohesionSlider.setMaximumSize(new java.awt.Dimension(130, 20));
        cohesionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateCohesionWeight(source.getValue() / 20.0);
                }
            }
        });

        JSlider obstacleSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 40);
        obstacleSlider.setMaximumSize(new java.awt.Dimension(130, 20));
        obstacleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateObstacleWeight(source.getValue() / 10.0); // 0.0 to 4.0
                }
            }
        });

        JLabel speedLabel = new JLabel("Speed:");    
        JLabel separationLabel = new JLabel("Separation:");
        JLabel alignmentLabel = new JLabel("Alignment:");
        JLabel cohesionLabel = new JLabel("Cohesion:");
        JLabel obstacleLabel = new JLabel("Obstacle:");

        frame.add(controlPanel, BorderLayout.WEST);
        
        // Add components with alignment
        speedLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        speedSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(speedLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(speedSlider);
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));
        
        separationLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        separationSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(separationLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(separationSlider);
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));
        
        alignmentLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        alignmentSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(alignmentLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(alignmentSlider);
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));
        
        cohesionLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        cohesionSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(cohesionLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(cohesionSlider);
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));

        obstacleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        obstacleSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(obstacleLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(obstacleSlider);
        // Add logic here later to create and add JPanels with sliders, buttons etc.
        // e.g., JPanel controlPanel = new JPanel();
        // frame.add(controlPanel, BorderLayout.SOUTH);
    }

    // Methods to add controls and handle events will go here later.

} 