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
     * Updates the position of all boids in the simulation.
     * This method should be called every time the simulation updates. */
    public void update() {
        for (Boid boid : this.boids) {
            boid.update(1); // Assuming dt is 1 for simplicity
        }
    }

    /**
     * Draws all boids in the simulation.
     * This method should be called every time the simulation updates. */
    public void draw() {
        for (Boid boid : this.boids) {
            boid.draw();
        }
    }
    
    // Method to run the simulation loop
    public void runSimulationLoop() {
        this.running = true;
        while (this.running) {
            // Update boid logic (can be off EDT)
            for (Boid boidAgent : this.boids) { // Renamed from boid_agent
                boidAgent.update(1); // Assuming dt is 1 for simplicity
            }

            // Schedule drawing on EDT
            SwingUtilities.invokeLater(() -> {
                if (canvas != null) { // Check if canvas is initialized
                    canvas.clear();
                    for (Boid boidToDraw : this.boids) { // Renamed from boid_to_draw
                        boidToDraw.draw();
                    }
                    canvas.repaint();
                }
            });

            this.utils.pause(50); // Pause in the simulation thread
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

                // Example: Add a boid with fixed coordinates for testing (original values)
                int fixedX = 400; 
                int fixedY = 300; 
                simulation.addBoid(new Boid(canvas, new CartesianCoordinate(fixedX, fixedY), new CartesianCoordinate(0.5, 0.5)));

                // Add some random boids using current canvas dimensions
                simulation.addBoid(new Boid(canvas, 
                    new CartesianCoordinate(utils.randomInt(0, canvas.getWidth()), utils.randomInt(0, canvas.getHeight())),
                    new CartesianCoordinate(utils.randomDouble(-1, 1), utils.randomDouble(-1, 1))));
                simulation.addBoid(new Boid(canvas, 
                    new CartesianCoordinate(utils.randomInt(0, canvas.getWidth()), utils.randomInt(0, canvas.getHeight())),
                    new CartesianCoordinate(utils.randomDouble(-1, 1), utils.randomDouble(-1, 1))));
                simulation.addBoid(new Boid(canvas, 
                    new CartesianCoordinate(utils.randomInt(0, canvas.getWidth()), utils.randomInt(0, canvas.getHeight())),
                    new CartesianCoordinate(utils.randomDouble(-1, 1), utils.randomDouble(-1, 1))));
               
                // 7. Start the simulation loop in a new thread
                new Thread(() -> simulation.runSimulationLoop()).start();
            }
        });
    }

}
