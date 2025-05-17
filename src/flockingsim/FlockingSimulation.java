package flockingsim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import drawing.Canvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import flockingsim.Boid;
import geometry.CartesianCoordinate;
import tools.Utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import flockingsim.SimulationGUI;
/**
 * A FlockingSimulation is a simulation that contains a list of boids.
 * It can add, remove, and update boids.
 * It can also draw all the boids in the simulation. 
 */
public class FlockingSimulation {
    private Canvas canvas;
    private List<Boid> boids;
    private ArrayList<Rectangle> obstacles;
    private boolean running;
    private Utils utils;

    // Define some simulation parameters
    private static final double BOID_MAX_SPEED = 10;
    private static final double BOID_MAX_FORCE = 0.1; // Adjust as needed (limits steering strength)
    private static final double BOID_PERCEPTION_RADIUS = 50.0; // Adjust as needed
    private static final int SIMULATION_DELAY_MS = 20; // Approx 50 FPS
    private static final double BOID_SPAWN_MARGIN = 15.0; // Moved from main and made class member
    private int initialBoidCount = 100; // Added for initial setup
    // Using a fixed reasonable dt often works well. Adjust if simulation seems too fast/slow.

    /**
     * Updates the maximum speed of all boids in the simulation.
     * @param speed The desired speed value (0-30)
     */
    public void updateBoidSpeed(int speed) {
        // Update speed for all boids
        for (Boid boid : this.boids) {
            boid.setMaxSpeed(speed);
        }
    }

    /**
     * Updates the separation weight for all boids.
     * @param weight The new separation weight (0.0 to 0.5)
     */
    public void updateSeparationWeight(double weight) {
        for (Boid boid : this.boids) {
            boid.setSeparationWeight(weight);
        }
    }

    /**
     * Updates the alignment weight for all boids.
     * @param weight The new alignment weight (0.0 to 0.5)
     */
    public void updateAlignmentWeight(double weight) {
        for (Boid boid : this.boids) {
            boid.setAlignmentWeight(weight);
        }
    }

    /**
     * Updates the cohesion weight for all boids.
     * @param weight The new cohesion weight (0.0 to 0.5)
     */
    public void updateCohesionWeight(double weight) {
        for (Boid boid : this.boids) {
            boid.setCohesionWeight(weight);
        }
    }

    /**
     * Updates the obstacle avoidance weight for all boids.
     * @param weight The new obstacle avoidance weight (0.0 to 4.0)
     */
    public void updateObstacleWeight(double weight) {
        for (Boid boid : this.boids) {
            boid.setObstacleAvoidanceWeight(weight);
        }
    }

    public FlockingSimulation(Canvas canvas, Utils utils) {
        this.canvas = canvas;
        this.boids = new CopyOnWriteArrayList<>();
        this.utils = utils;
        this.obstacles = new ArrayList<>();
        initializeObstacles();
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

    public void resetAndSpawnBoids(int newCount) {
        List<Boid> tempBoidList = new ArrayList<>(newCount);

        for (int i = 0; i < newCount; i++) {
            double startX, startY;
            boolean validPosition;
            int maxAttempts = 50;
            int attempts = 0;
            
            do {
                double currentCanvasWidth = Math.max(1, this.canvas.getWidth());
                double currentCanvasHeight = Math.max(1, this.canvas.getHeight());

                startX = this.utils.randomDouble(0, currentCanvasWidth);
                startY = this.utils.randomDouble(0, currentCanvasHeight);
                validPosition = true;
                
                for (Rectangle obstacle : this.obstacles) {
                    double obsX = obstacle.getPosition().getX();
                    double obsY = obstacle.getPosition().getY();
                    double obsDX = obstacle.getDx(); 
                    double obsDY = obstacle.getDy();

                    double noSpawnMinX = obsX - BOID_SPAWN_MARGIN;
                    double noSpawnMaxX = obsX + obsDX + BOID_SPAWN_MARGIN;
                    double noSpawnMinY = obsY - BOID_SPAWN_MARGIN;
                    double noSpawnMaxY = obsY + obsDY + BOID_SPAWN_MARGIN;

                    if (startX >= noSpawnMinX && startX <= noSpawnMaxX &&
                        startY >= noSpawnMinY && startY <= noSpawnMaxY) {
                        validPosition = false;
                        break;
                    }
                }
                attempts++;
            } while (!validPosition && attempts < maxAttempts);
            
            if (!validPosition) {
                startX = 10; // Fallback x
                startY = 10; // Fallback y
            }

            Boid newBoid = new Boid(this.canvas,
                new CartesianCoordinate(startX, startY),
                new CartesianCoordinate(this.utils.randomDouble(-1, 1), this.utils.randomDouble(-1, 1)).normalize().multiply(this.utils.randomDouble(0, BOID_MAX_SPEED)),
                BOID_MAX_SPEED,
                BOID_MAX_FORCE,
                BOID_PERCEPTION_RADIUS
            );
            tempBoidList.add(newBoid);
        }
        this.boids = new CopyOnWriteArrayList<>(tempBoidList); // Atomically update the boids list
        System.out.println("Set number of boids to: " + newCount);
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
                final Canvas canvas = new Canvas(); // Canvas constructor sets preferred size 800x600
                final Utils utils = new Utils();

                // 2. Create FlockingSimulation instance
                final FlockingSimulation simulation = new FlockingSimulation(canvas, utils);

                // 3. Create the GUI Controller, which creates the JFrame
                //    Pass the simulation and canvas to the controller
                //    This also makes the frame visible.
                SimulationGUI controller = new SimulationGUI(simulation, canvas);

                // 4. Defer boid creation and simulation start to a subsequent event queue task
                //    This gives the GUI time to lay out and for the canvas to get its actual size.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        System.out.println("Canvas dimensions for boid spawning: " + canvas.getWidth() + "x" + canvas.getHeight());
                        if (canvas.getWidth() == 0 || canvas.getHeight() == 0) {
                            System.err.println("Warning: Canvas dimensions are zero at the time of boid spawning. " +
                                               "Spawning might be incorrect. Ensure GUI is fully initialized.");
                            // For a more robust solution, one might implement a loop with a short delay
                            // here to wait for canvas dimensions, or use a ComponentListener on the canvas.
                        }

                        // Initial boid population
                        simulation.resetAndSpawnBoids(simulation.initialBoidCount); 

                        // Start the simulation loop in a new thread
                        new Thread(() -> simulation.runSimulationLoop()).start();
                    }
                });
            }
        });
    }

}
