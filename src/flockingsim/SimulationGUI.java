package flockingsim;

import drawing.Canvas;
import geometry.CartesianCoordinate;

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
import java.awt.MouseInfo;
import java.awt.image.ColorConvertOp;

/**
 * Manages the GUI elements (JFrame, controls) for the Flocking Simulation.
 */
public class SimulationGUI {

    private FlockingSimulation simulation;
    private Canvas canvas;
    private JFrame frame;
    private CartesianCoordinate mousePositionOnCanvas; // To store canvas-relative mouse position

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

        // Initialize mouse position to a default (e.g., center or off-screen)
        this.mousePositionOnCanvas = new CartesianCoordinate(-1, -1); // Default off-screen

        // Add MouseMotionListener to the canvas for MOVING and DRAGGING
        this.canvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mousePositionOnCanvas = new CartesianCoordinate(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                mousePositionOnCanvas = new CartesianCoordinate(e.getX(), e.getY());
            }
            // DO NOT add mouseClicked here, MouseMotionAdapter doesn't handle it well.
        });

        // Add a separate MouseListener (using MouseAdapter for conciseness) for CLICKING
        this.canvas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Check if it's a left click (Button1)
                if (SwingUtilities.isLeftMouseButton(e)) {
                    System.out.println("Canvas clicked at: " + e.getX() + ", " + e.getY() + " - Attempting to spawn Predator");
                    CartesianCoordinate clickPos = new CartesianCoordinate(e.getX(), e.getY());
                    // Use a clearance radius for the predator, e.g., PREDATOR_SPAWN_CLEARANCE from FlockingSimulation (or define one here)
                    // For now, using a hardcoded value as an example, ideally get from simulation or constants.
                    double predatorClearance = 20.0; // Example clearance, sync with FlockingSimulation.PREDATOR_SPAWN_CLEARANCE if possible
                    if (simulation.isPositionSafeForSpawning(clickPos, predatorClearance)) {
                        simulation.addPredator(clickPos);
                    } else {
                        System.out.println("Spawn position for predator is unsafe (too close to obstacle).");
                    }
                }
            }
        });

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
                simulation.updateSimulationSpeed(source.getValue());
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
                simulation.updateMaxSpeedForAllEntities(source.getValue());
            }
        });
       
        JSlider separationSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 15);
        separationSlider.setMaximumSize(new java.awt.Dimension(130, 25));
        separationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                simulation.updateSeparationWeight(source.getValue() / 10.0);
            }
        });

        JSlider alignmentSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 20);
        alignmentSlider.setMaximumSize(new java.awt.Dimension(130, 25));
        alignmentSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                simulation.updateAlignmentWeight(source.getValue() / 20.0);
            }
        });

        JSlider cohesionSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 20);
        cohesionSlider.setMaximumSize(new java.awt.Dimension(130, 25));
        cohesionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                simulation.updateCohesionWeight(source.getValue() / 20.0);
            }
        });

        JSlider obstacleSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 40);
        obstacleSlider.setMaximumSize(new java.awt.Dimension(130, 25));
        obstacleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                simulation.updateObstacleAvoidanceWeight(source.getValue() / 10.0);
            }
        });

        JSlider mouseWeightSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        mouseWeightSlider.setMaximumSize(new java.awt.Dimension(130, 25));
        mouseWeightSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                simulation.updateMouseAvoidanceWeight(source.getValue() / 10.0);
            }
        });

        JLabel simulationSpeedLabel = new JLabel("Simulation FPS:");    
        JLabel boidSpeedLabel = new JLabel("Boid Speed (m/s):");    
        JLabel separationLabel = new JLabel("Separation Weight:");
        JLabel alignmentLabel = new JLabel("Alignment Weight:");
        JLabel cohesionLabel = new JLabel("Cohesion Weight:");
        JLabel obstacleLabel = new JLabel("Avoid Obstacles:");
        JLabel mouseAvoidanceLabel = new JLabel("Avoid Mouse:");

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

        mouseAvoidanceLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        mouseWeightSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(mouseAvoidanceLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(mouseWeightSlider);
        controlPanel.add(javax.swing.Box.createVerticalStrut(20));

        // Slider for Predator Flee Weight
        JLabel predatorFleeLabel = new JLabel("Predator Flee Weight:");
        predatorFleeLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JSlider predatorFleeSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 25); // Range 0.0 to 5.0, default 2.5
        predatorFleeSlider.setMaximumSize(new java.awt.Dimension(130, 25));
        predatorFleeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                simulation.updatePredatorFleeWeight(source.getValue() / 10.0); // Divide by 10 for 0.0 to 5.0 range
            }
        });
        predatorFleeSlider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(predatorFleeLabel);
        controlPanel.add(javax.swing.Box.createVerticalStrut(5));
        controlPanel.add(predatorFleeSlider);
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
                System.out.println("\n=== Before Reset ===");
                System.out.println("SimSpeed Slider: " + simulationSpeedSlider.getValue());
                System.out.println("BoidSpeed Slider: " + boidSpeedSlider.getValue());
                System.out.println("Separation Slider: " + separationSlider.getValue());
                System.out.println("Alignment Slider: " + alignmentSlider.getValue());
                System.out.println("Cohesion Slider: " + cohesionSlider.getValue());
                System.out.println("Obstacle Slider: " + obstacleSlider.getValue());
                System.out.println("MouseAvoid Slider: " + mouseWeightSlider.getValue());
                System.out.println("PredatorFlee Slider: " + predatorFleeSlider.getValue());
                System.out.println("BoidCount Spinner: " + boidCountSpinner.getValue());

                simulation.resetSettings(); // Resets simulation state and re-spawns boids

                // Update GUI components to reflect default simulation settings
                simulationSpeedSlider.setModel(new javax.swing.DefaultBoundedRangeModel(30, 0, 10, 100));
                boidSpeedSlider.setModel(new javax.swing.DefaultBoundedRangeModel(10, 0, 0, 50));
                separationSlider.setModel(new javax.swing.DefaultBoundedRangeModel(15, 0, 0, 40));
                alignmentSlider.setModel(new javax.swing.DefaultBoundedRangeModel(20, 0, 0, 50));
                cohesionSlider.setModel(new javax.swing.DefaultBoundedRangeModel(20, 0, 0, 50));
                obstacleSlider.setModel(new javax.swing.DefaultBoundedRangeModel(40, 0, 0, 40));
                mouseWeightSlider.setModel(new javax.swing.DefaultBoundedRangeModel(5,0,0,10)); // Reset mouse slider
                predatorFleeSlider.setModel(new javax.swing.DefaultBoundedRangeModel(25, 0, 0, 50)); // Reset predator flee slider
                boidCountSpinner.setValue(100);

                System.out.println("\n=== After Reset ===");
                System.out.println("SimSpeed Slider: " + simulationSpeedSlider.getValue());
                System.out.println("BoidSpeed Slider: " + boidSpeedSlider.getValue());
                System.out.println("Separation Slider: " + separationSlider.getValue());
                System.out.println("Alignment Slider: " + alignmentSlider.getValue());
                System.out.println("Cohesion Slider: " + cohesionSlider.getValue());
                System.out.println("Obstacle Slider: " + obstacleSlider.getValue());
                System.out.println("MouseAvoid Slider: " + mouseWeightSlider.getValue());
                System.out.println("PredatorFlee Slider: " + predatorFleeSlider.getValue());
                System.out.println("BoidCount Spinner: " + boidCountSpinner.getValue());
                System.out.println("==================\n");
            }
        });


        resetSettingsButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        controlPanel.add(resetSettingsButton);

    



    }

    public CartesianCoordinate getMousePositionOnCanvas() {
        return this.mousePositionOnCanvas;
    }

} 