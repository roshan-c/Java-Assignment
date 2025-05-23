package flockingsim;

import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import drawing.Canvas;
import javax.swing.SwingUtilities;

import geometry.CartesianCoordinate;
import tools.Utils;

/**
 * A FlockingSimulation is a simulation that contains a list of boids.
 * It can add, remove, and update boids.
 * It can also draw all the boids in the simulation. 
 */
public class FlockingSimulation {
    private final Canvas canvas;
    private List<SimulatedEntity> entities;
    private final ArrayList<Rectangle> obstacles;
    private boolean running;
    private final Utils utils;
    private SimulationGUI gui;

    // Default simulation parameters
    private static final int DEFAULT_SIMULATION_TARGET_FPS = 30;
    private static final int DEFAULT_SIMULATION_DELAY_MS = 1000 / DEFAULT_SIMULATION_TARGET_FPS; // Approx 33ms
    private static final int DEFAULT_INITIAL_BOID_COUNT = 100;
    // Boid specific defaults are in Boid class or passed during construction in resetAndSpawnBoids

    private int simulationDelayMs = DEFAULT_SIMULATION_DELAY_MS;
    private int initialBoidCount = DEFAULT_INITIAL_BOID_COUNT; // Used by resetSettings

    // Boid behavior parameters (can be overridden by GUI)
    private static final double BOID_MAX_SPEED = 10;
    private static final double BOID_MAX_FORCE = 0.5;
    private static final double BOID_PERCEPTION_RADIUS = 50.0;
    private static final double BOID_SPAWN_MARGIN = 15.0;
    private static final double PREDATOR_SPAWN_CLEARANCE = 20.0; // Clearance for predator spawning

    /**
     * Updates the maximum speed of all boids in the simulation.
     * @param speed The desired speed value (0-30)
     */
    public void updateMaxSpeedForAllEntities(int speed) {
        for (SimulatedEntity entity : this.entities) { 
            if (entity instanceof AbstractSimulatedEntity) {
                ((AbstractSimulatedEntity) entity).setMaxSpeed((double) speed);
            }
        }
    }

    /**
     * Updates the separation weight for all boids.
     * @param weight The new separation weight (0.0 to 0.5)
     */
    public void updateSeparationWeight(double weight) {
        for (SimulatedEntity entity : this.entities) {
            if (entity instanceof Boid) {
                ((Boid) entity).setSeparationWeight(weight);
            }
        }
    }

    /**
     * Updates the alignment weight for all boids.
     * @param weight The new alignment weight (0.0 to 0.5)
     */
    public void updateAlignmentWeight(double weight) {
        for (SimulatedEntity entity : this.entities) {
            if (entity instanceof Boid) {
                ((Boid) entity).setAlignmentWeight(weight);
            }
        }
    }

    /**
     * Updates the cohesion weight for all boids.
     * @param weight The new cohesion weight (0.0 to 0.5)
     */
    public void updateCohesionWeight(double weight) {
        for (SimulatedEntity entity : this.entities) {
            if (entity instanceof Boid) {
                ((Boid) entity).setCohesionWeight(weight);
            }
        }
    }

    /**
     * Updates the obstacle avoidance weight for all entities
     * @param weight The new obstacle avoidance weight (0.0 to 4.0)
     */
    public void updateObstacleAvoidanceWeight(double weight) {
        for (SimulatedEntity entity : this.entities) {
            if (entity instanceof Boid) {
                ((Boid) entity).setObstacleAvoidanceWeight(weight);
            }
        }
    }

    public void updateMouseAvoidanceWeight(double weight) {
        for (SimulatedEntity entity : this.entities) {
            if (entity instanceof Boid) {
                ((Boid) entity).setMouseAvoidanceWeight(weight);
            }
        }
    }

    public FlockingSimulation(Canvas canvas, Utils utils) {
        this.canvas = canvas;
        this.entities = new CopyOnWriteArrayList<>();
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
        this.obstacles.add(new Rectangle(new CartesianCoordinate(100, 300), 120, 80, this.canvas)); 
        this.obstacles.add(new Rectangle(new CartesianCoordinate(350, 200), 80, 150, this.canvas));
        this.obstacles.add(new Rectangle(new CartesianCoordinate(550, 100), 150, 120, this.canvas));
    }

    public void resetAndSpawnBoids(int newCount) {
        List<SimulatedEntity> tempEntityList = new ArrayList<>(newCount);
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
                startX = 10; startY = 10;
            }
            Boid newBoid = new Boid(this.canvas,
                new CartesianCoordinate(startX, startY),
                new CartesianCoordinate(this.utils.randomDouble(-1, 1), this.utils.randomDouble(-1, 1)).normalize().multiply(this.utils.randomDouble(0, BOID_MAX_SPEED)),
                BOID_MAX_SPEED, // Use the class constant for default speed
                BOID_MAX_FORCE,
                BOID_PERCEPTION_RADIUS
            );
            // Individual boid weights (separation, alignment, cohesion, obstacle) are set to their defaults within the Boid constructor.
            // The GUI sliders will override these via the setXWeight methods on all boids if changed from default.
            tempEntityList.add(newBoid);
        }
        this.entities.clear(); // Clear existing entities before adding new ones
        this.entities.addAll(tempEntityList); // Add all new boids
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
        for (SimulatedEntity entity : this.entities) { 
            if (entity instanceof AbstractSimulatedEntity) {
                ((AbstractSimulatedEntity) entity).draw();
            }
        }
    }
    
    // Method to run the simulation loop
    public void runSimulationLoop() {
        this.running = true;
        while (this.running) {
            CartesianCoordinate currentMousePos = null;
            if (this.gui != null) {
                currentMousePos = this.gui.getMousePositionOnCanvas();
            } else {
                // Fallback or default if GUI is not set - though it should be
                currentMousePos = new CartesianCoordinate(-1,-1); // Default off-screen
            }

            // Update all entities
            for (SimulatedEntity entity : this.entities) {
                if (entity instanceof AbstractSimulatedEntity) {
                    ((AbstractSimulatedEntity) entity).update(this.entities, this.obstacles, currentMousePos);
                }
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
            this.utils.pause(this.simulationDelayMs);
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
                simulation.setGui(controller); // Set the GUI reference in the simulation

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

    /**
     * Updates the simulation delay based on a target FPS value.
     * @param targetFPS The desired frames per second.
     */
    public void updateSimulationSpeed(int targetFPS) {
        if (targetFPS <= 0) {
            this.simulationDelayMs = 1000; // Default to 1 FPS if target is 0 or less
        } else {
            this.simulationDelayMs = 1000 / targetFPS;
        }
        // Ensure delay is at least 1ms to avoid excessive CPU usage or issues with pause(0)
        this.simulationDelayMs = Math.max(1, this.simulationDelayMs); 
        System.out.println("Target FPS set to: " + targetFPS + ", Simulation Delay set to: " + this.simulationDelayMs + "ms");
    }

    public void resetSettings() {
        // Reset the simulation's core parameters to their defaults
        this.simulationDelayMs = DEFAULT_SIMULATION_DELAY_MS;
        // this.initialBoidCount = DEFAULT_INITIAL_BOID_COUNT; // This field is just a holder for the default value

        // For boid parameters (speed, weights), re-spawning boids will make them use their constructor defaults.
        // Then, ensure the GUI sliders reflect these defaults.
        // The individual setXWeight and setMaxSpeed methods are for GUI overrides.
        // If we want all *existing* boids to also revert, we would loop, but resetAndSpawnBoids replaces them.
        
        resetAndSpawnBoids(DEFAULT_INITIAL_BOID_COUNT); // Respawn with default count

        // The GUI sliders need to be reset externally by SimulationGUI after this call.
        System.out.println("Simulation settings and boids have been reset to defaults.");
    }

    public CartesianCoordinate getMousePosition() {
        // This method is now less relevant for boid avoidance, 
        // as canvas-relative coordinates are preferred and obtained via SimulationGUI.
        // It can be kept for other purposes or removed if no longer needed.
        if (MouseInfo.getPointerInfo() == null || MouseInfo.getPointerInfo().getLocation() == null) {
            return new CartesianCoordinate(-1000, -1000); 
        }
        double xMousePosition = MouseInfo.getPointerInfo().getLocation().getX();
        double yMousePosition = MouseInfo.getPointerInfo().getLocation().getY();
        CartesianCoordinate mouseCoordinates = new CartesianCoordinate(xMousePosition, yMousePosition);
                return mouseCoordinates;

    }

    // Method to set the GUI reference
    public void setGui(SimulationGUI gui) {
        this.gui = gui;
    }

    public boolean isPositionSafeForSpawning(CartesianCoordinate spawnPosition, double entityClearanceRadius) {
        for (Rectangle obstacle : this.obstacles) {
            // A simple check: if the spawn position is within the obstacle's bounding box expanded by the entityClearanceRadius
            double obsX = obstacle.getPosition().getX();
            double obsY = obstacle.getPosition().getY();
            double obsDX = obstacle.getDx();
            double obsDY = obstacle.getDy();

            // Check if the spawn point is within the expanded no-spawn zone of the obstacle
            if (spawnPosition.getX() >= obsX - entityClearanceRadius && 
                spawnPosition.getX() <= obsX + obsDX + entityClearanceRadius &&
                spawnPosition.getY() >= obsY - entityClearanceRadius && 
                spawnPosition.getY() <= obsY + obsDY + entityClearanceRadius) {
                return false; // Too close to an obstacle
            }
        }
        return true; // Position is safe
    }

    // Method to add a predator to the simulation
    public void addPredator(CartesianCoordinate spawnPosition) {
        Predator newPredator = new Predator(this.canvas, spawnPosition, new CartesianCoordinate(0, 0), BOID_MAX_SPEED, BOID_MAX_FORCE, BOID_PERCEPTION_RADIUS);
        this.entities.add(newPredator);
        System.out.println("Predator added at: " + spawnPosition);


        
        // Example: Create a new Predator instance and add it to the entities list
        // Predator newPredator = new Predator(this.canvas, spawnPosition, /* other params */);
        // this.entities.add(newPredator);
        // System.out.println("Predator added at: " + spawnPosition);
    }

    public void updatePredatorFleeWeight(double weight) {
        for (SimulatedEntity entity : this.entities) {
            if (entity instanceof Boid) {
                ((Boid) entity).setPredatorFleeWeight(weight);
            }
        }
    }
}
