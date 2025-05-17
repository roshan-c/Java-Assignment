package flockingsim;

import drawing.Canvas;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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

        // Slider for Simulation Speed (now representing Target FPS)
        // Range: 10 FPS to 100 FPS. Initial: 30 FPS.
        // Delay will be calculated as 1000 / FPS.
        // Min Delay (at 100 FPS) = 10ms. Max Delay (at 10 FPS) = 100ms.
        JSlider simulationSpeedSlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 30); 
        simulationSpeedSlider.setMaximumSize(new java.awt.Dimension(130, 35)); // Increased height for labels
        simulationSpeedSlider.setMajorTickSpacing(20); 
        simulationSpeedSlider.setMinorTickSpacing(5);
        simulationSpeedSlider.setPaintTicks(true);
        simulationSpeedSlider.setPaintLabels(true); 
        simulationSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateSimulationSpeed(source.getValue());
                }
            }
        });
        
        

        JSlider boidSpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
        boidSpeedSlider.setMaximumSize(new java.awt.Dimension(130, 35));
        boidSpeedSlider.setMajorTickSpacing(10);
        boidSpeedSlider.setMinorTickSpacing(5);
        boidSpeedSlider.setPaintTicks(true);
        boidSpeedSlider.setPaintLabels(true);
        boidSpeedSlider.setPaintTrack(true);
        boidSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    simulation.updateBoidSpeed(source.getValue());
                }
            }
        });
       
        JSlider separationSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 15);
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

        JLabel simulationSpeedLabel = new JLabel("Simulation FPS:");    
        JLabel boidSpeedLabel = new JLabel("Boid Speed (m/s):");    
        JLabel separationLabel = new JLabel("Separation Weight:");
        JLabel alignmentLabel = new JLabel("Alignment Weight:");
        JLabel cohesionLabel = new JLabel("Cohesion Weight:");
        JLabel obstacleLabel = new JLabel("Avoid Obstacles:");

        frame.add(controlPanel, BorderLayout.WEST);

        simulationSpeedLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        simulationSpeedSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(simulationSpeedLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(simulationSpeedSlider);
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));
        
        // Add components with alignment
        boidSpeedLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boidSpeedSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(boidSpeedLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(boidSpeedSlider);
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
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));

        // Spinner for Number of Boids
        JLabel boidCountLabel = new JLabel("Number of Boids:");
        boidCountLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        SpinnerNumberModel boidCountModel = new SpinnerNumberModel(100, 0, 1000, 1); // Initial 100, Min 0, Max 1000, Step 1
        JSpinner boidCountSpinner = new JSpinner(boidCountModel);
        boidCountSpinner.setMaximumSize(new java.awt.Dimension(130, 25)); // Adjusted size for spinner
        boidCountSpinner.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        boidCountSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                // JSpinner sends ChangeEvents continuously while value is changing if arrows are held.
                // Unlike JSlider, there isn't a simple 'getValueIsAdjusting' for final value.
                // However, for a spinner, updating on each registered change is usually acceptable.
                JSpinner source = (JSpinner) e.getSource();
                int newBoidCount = (Integer) source.getValue();
                simulation.resetAndSpawnBoids(newBoidCount);
            }
        });

        controlPanel.add(boidCountLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(boidCountSpinner);

        JButton resetSettingsButton = new JButton("Reset Settings");
        resetSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                simulation.resetSettings(); // Resets simulation state and re-spawns boids

                // Update GUI components to reflect default simulation settings
                // Target FPS slider (default: 30 FPS)
                simulationSpeedSlider.setValue(30); 

                // Boid Speed Slider (default from FlockingSimulation.BOID_MAX_SPEED: 10)
                boidSpeedSlider.setValue(10);

                // Separation Slider (default Boid.separationWeight: 1.5; Slider value 15 for effective 1.5)
                separationSlider.setValue(15);

                // Alignment Slider (default Boid.alignmentWeight: 1.1; Slider value 22 for effective 1.1)
                alignmentSlider.setValue(22);

                // Cohesion Slider (default Boid.cohesionWeight: 1.1; Slider value 22 for effective 1.1)
                cohesionSlider.setValue(22);

                // Obstacle Slider (default Boid.obstacleAvoidanceWeight: 4.0; Slider value 40 for effective 4.0)
                obstacleSlider.setValue(40);

                // Boid Count Spinner (default from FlockingSimulation.DEFAULT_INITIAL_BOID_COUNT: 100)
                boidCountSpinner.setValue(100); 
            }
        });

        resetSettingsButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(resetSettingsButton);


    }

} 