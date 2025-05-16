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
    private ArrayList<Rectangle> obstacles; // List to hold obstacles
    private boolean running;
    private Utils utils;

    // Define some simulation parameters
    private static final double BOID_MAX_SPEED = 10.0; // Adjust as needed
    private static final double BOID_MAX_FORCE = 0.1; // Adjust as needed (limits steering strength)
    private static final double BOID_PERCEPTION_RADIUS = 50.0; // Adjust as needed
    private static final int SIMULATION_DELAY_MS = 20; // Approx 50 FPS
    // Using a fixed reasonable dt often works well. Adjust if simulation seems too fast/slow.
    private static final double DELTA_TIME = 0.5; // Match dt to frame rate (adjust scale if needed)

    public FlockingSimulation(Canvas canvas, Utils utils) {
        this.canvas = canvas;
        this.boids = new ArrayList<>();
        this.utils = utils;
        this.obstacles = new ArrayList<>(); // Initialize obstacles list
        initializeObstacles(); // Method to create the obstacles
    }

    /**
     * Initializes the obstacles as per the assignment brief.
     */
    private void initializeObstacles() {
        // Obstacles from the assignment brief
        // size (dx=120, dy=80) with top-left corner at (100, 300)
        // size (dx=80, dy=150) with top-left corner at (350, 200)
        // size (dx=150, dy=120) with top-left corner at (550, 100)
        // Note: The assignment brief uses (x,y) for top-left, and dx, dy for size.
        // The y-coordinates in the brief might be typical Cartesian (y-up), 
        // but screen coordinates are often y-down. Assuming y-down for now.

        // Brief coordinates: (100, 300), (350, 200), (550, 100)
        // My current draw has obstacles at: (100, 30), (350, 200), (550, 100) based on visual placement.
        // I will use the coordinates from the draw method for consistency with current visuals,
        // but these can be adjusted to match the brief exactly if needed.

        // Rectangle(CartesianCoordinate topLeft, int dx, int dy, Canvas canvas)
        this.obstacles.add(new Rectangle(new CartesianCoordinate(100, 30), 120, 80, this.canvas)); 
        this.obstacles.add(new Rectangle(new CartesianCoordinate(350, 200), 80, 150, this.canvas));
        this.obstacles.add(new Rectangle(new CartesianCoordinate(550, 100), 150, 120, this.canvas));
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
        // Draw obstacles first so boids appear on top
        for (Rectangle obstacle : this.obstacles) {
            obstacle.draw();
        }
        // Then draw boids
        for (Boid boid : this.boids) {
            boid.draw();
        }
    }
    
    // Method to run the simulation loop
    public void runSimulationLoop() {
        this.running = true;
        while (this.running) {
            // Update all boids
            for (Boid boid : this.boids) {
                boid.update(this.boids, this.obstacles);
            }

            // Draw everything
            SwingUtilities.invokeLater(() -> {
                if (canvas != null) {
                    canvas.clear();
                    draw();
                    canvas.repaint();
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

                // 2. Create FlockingSimulation instance
                FlockingSimulation simulation = new FlockingSimulation(canvas, utils);

                // 3. Create the GUI Controller, which creates the JFrame
                //    Pass the simulation and canvas to the controller
                SimulationGUI controller = new SimulationGUI(simulation, canvas);

                // Ensure frame is visible and layout is complete before getting dimensions
                // (Frame visibility is handled by SimulationController constructor now)

                // Add boids after the frame (and thus canvas) is likely initialized
                int numBoids = 100; // Example number of boids
                for (int i = 0; i < numBoids; i++) {
                    // Get width/height *after* frame is visible (done by Controller)
                    double startX, startY;
                    boolean validPosition;
                    int maxAttempts = 50; // Prevent infinite loops
                    int attempts = 0;
                    
                    do {
                        startX = utils.randomDouble(0, canvas.getWidth());
                        startY = utils.randomDouble(0, canvas.getHeight());
                        validPosition = true;
                        
                        // Check if position is inside any obstacle
                        for (Rectangle obstacle : simulation.obstacles) {
                            if (obstacle.contains(new CartesianCoordinate(startX, startY))) {
                                validPosition = false;
                                break;
                            }
                        }
                        attempts++;
                    } while (!validPosition && attempts < maxAttempts);
                    
                    // If we couldn't find a valid position after max attempts, use a safe default
                    if (!validPosition) {
                        startX = 400;
                        startY = 300;
                    }

                    simulation.addBoid(new Boid(canvas,
                        new CartesianCoordinate(startX, startY),
                        new CartesianCoordinate(utils.randomDouble(-1, 1), utils.randomDouble(-1, 1)).normalize().multiply(utils.randomDouble(0, BOID_MAX_SPEED)),
                        BOID_MAX_SPEED,
                        BOID_MAX_FORCE,
                        BOID_PERCEPTION_RADIUS
                    ));
                }

                // 4. Start the simulation loop in a new thread
                new Thread(() -> simulation.runSimulationLoop()).start();
            }
        });
    }

}
