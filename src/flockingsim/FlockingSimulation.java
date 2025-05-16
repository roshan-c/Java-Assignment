package flockingsim;

import java.util.ArrayList;
import drawing.Canvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import flockingsim.Boid;
import geometry.CartesianCoordinate;
import tools.Utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A FlockingSimulation is a simulation that contains a list of boids.
 * It can add, remove, and update boids.
 * It can also draw all the boids in the simulation. 
 */
public class FlockingSimulation {
    private Canvas canvas;
    private ArrayList<Boid> boids; // array that holds all the boids in the simulation
    private boolean running;
    private Utils utils;

    // Define some simulation parameters
    private static final double BOID_MAX_SPEED = 3.0; // Adjust as needed
    private static final double BOID_MAX_FORCE = 0.1; // Adjust as needed (limits steering strength)
    private static final double BOID_PERCEPTION_RADIUS = 50.0; // Adjust as needed
    private static final int SIMULATION_DELAY_MS = 20; // Approx 50 FPS
    // Using a fixed reasonable dt often works well. Adjust if simulation seems too fast/slow.
    private static final double DELTA_TIME = 0.5; // Match dt to frame rate (adjust scale if needed)

    public FlockingSimulation(Canvas canvas, Utils utils) {
        this.canvas = canvas;
        this.boids = new ArrayList<>();
        this.utils = utils;
    }

    /**
     * Adds a boid to the simulation.
     * @param boid The boid to add. */
    public void addBoid(Boid boid) {
        this.boids.add(boid);
    }

    /**
     * Removes a boid from the simulation.
     * @param boid The boid to remove. */
    public void removeBoid(Boid boid) {
        this.boids.remove(boid);
    }

    /**
     * Draws all boids in the simulation.
     * This method should be called every time the simulation updates. */
    public void draw() {
        Rectangle rectangleSmall = new Rectangle(new CartesianCoordinate(100, 30), 80, 120);
        Rectangle rectangleMedium = new Rectangle(new CartesianCoordinate(350, 200), 80, 150);
        Rectangle rectangleLarge = new Rectangle(new CartesianCoordinate(550, 100), 150, 120);
        rectangleSmall.draw();
        rectangleMedium.draw();
        rectangleLarge.draw();
        for (Boid boid : this.boids) {
            boid.draw();
        }
    }
    
    // Method to run the simulation loop
    public void runSimulationLoop() {
        this.running = true;
        while (this.running) {
            // --- Update Phase ---
            // 1. Calculate flocking forces for all boids based on current state
            // Create a snapshot of the current boids list to avoid concurrent modification issues
            // if boids could theoretically be added/removed during force calculation (unlikely here but safer).
            ArrayList<Boid> currentBoids = new ArrayList<>(this.boids);
            for (Boid boid : currentBoids) {
                boid.applyFlockingRules(currentBoids); // Pass the snapshot
            }

            // 2. Update position and velocity for all boids.
            //    First calculate forces, then determine turn/move based on physics,
            //    then execute using turn(int) and move(int).
            for (Boid boidAgent : this.boids) {
                // Note: applyFlockingRules was already called above for all boids 
                // using the snapshot `currentBoids`. This is correct.
                
                // Calculate the required turn angle and move distance for this frame
                // based on the acceleration calculated in applyFlockingRules.
                double[] turnMoveParams = boidAgent.calculateTurnAndMove(DELTA_TIME);
                double angleToTurnDegrees = turnMoveParams[0];
                double distanceToMove = turnMoveParams[1];

                // Execute the turn and move using the integer-based methods as required
                boidAgent.turn((int) angleToTurnDegrees); // Cast angle to int
                // boidAgent.move((int) distanceToMove);    // Cast distance to int
                // Handle movement using the accumulator method
                boidAgent.accumulateAndMove(distanceToMove); // Pass the double distance
            }

            // --- Draw Phase (on EDT) ---
            // Schedule drawing on EDT
            SwingUtilities.invokeLater(() -> {
                if (canvas != null) { // Check if canvas is initialized
                    canvas.clear();
                    for (Boid boidToDraw : this.boids) { // Renamed from boid_to_draw
                        boidToDraw.draw();
                    }
                    canvas.repaint(); // Request repaint
                }
            });

            // Pause to control simulation speed
            this.utils.pause(SIMULATION_DELAY_MS);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 1. Create Canvas
                Canvas canvas = new Canvas(); // Canvas constructor sets preferred size 800x600
                Utils utils = new Utils();

                // 2. Create JFrame
                JFrame frame = new JFrame("Flocking Simulator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(canvas); // Add canvas to frame
                frame.setSize(800, 600); // Set frame size
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true); // Make frame visible - layout happens here

                // Original structure: setup simulation directly after frame is visible
                // 6. Create FlockingSimulation instance
                FlockingSimulation simulation = new FlockingSimulation(canvas, utils);

                // Add boids with the new parameters
                int numBoids = 50; // Example number of boids
                for (int i = 0; i < numBoids; i++) {
                     // Ensure canvas dimensions are available if needed for random placement
                     // It's safer to get width/height *after* frame is visible and laid out,
                     // but using initial defaults or fixed values might be necessary if called before.
                     // Here, assuming canvas has its default size or size set by frame.
                     double startX = utils.randomDouble(0, canvas.getWidth());
                     double startY = utils.randomDouble(0, canvas.getHeight());
                     // Check for invalid dimensions just in case
                     if (canvas.getWidth() <= 0) startX = 400;
                     if (canvas.getHeight() <= 0) startY = 300;

                     simulation.addBoid(new Boid(canvas,
                        // Random initial position
                        new CartesianCoordinate(startX, startY),
                        // Random initial velocity (magnitude between 0 and maxSpeed)
                        new CartesianCoordinate(utils.randomDouble(-1, 1), utils.randomDouble(-1, 1)).normalize().multiply(utils.randomDouble(0, BOID_MAX_SPEED)),
                        // Flocking parameters
                        BOID_MAX_SPEED,
                        BOID_MAX_FORCE,
                        BOID_PERCEPTION_RADIUS
                    ));
                }

                // 7. Start the simulation loop in a new thread
                new Thread(() -> simulation.runSimulationLoop()).start();
            }
        });
    }

}
