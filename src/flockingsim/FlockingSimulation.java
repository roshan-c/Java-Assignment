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

    public FlockingSimulation(Canvas canvas) {
        this.canvas = canvas;
        this.boids = new ArrayList<>();
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
            for (Boid boid_agent : this.boids) { // Renamed to avoid conflict if 'boid' is a field
                boid_agent.update(1); // Assuming dt is 1 for simplicity
            }

            // Schedule drawing on EDT
            SwingUtilities.invokeLater(() -> {
                if (canvas != null) { // Check if canvas is initialized
                    canvas.clear();
                    for (Boid boid_to_draw : this.boids) { // Renamed for clarity
                        boid_to_draw.draw();
                    }
                    canvas.repaint();
                }
            });

            Utils.pause(50); // Pause in the simulation thread
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 1. Create Canvas
                Canvas canvas = new Canvas();

                // 2. Create JFrame
                JFrame frame = new JFrame("Flocking Simulator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // 3. Add Canvas to JFrame's content pane
                // Ensure Canvas is a JComponent or similar, or use a JPanel as a container if needed
                frame.getContentPane().add(canvas);

                // 4. Size the frame
                // frame.pack(); // Use pack if Canvas has a preferred size set.
                frame.setSize(800, 600); // Or set size explicitly.
                frame.setLocationRelativeTo(null); // Center the frame on screen

                // 5. Make JFrame visible (LAST UI step for the frame itself)
                frame.setVisible(true);

                // 6. Create FlockingSimulation instance
                FlockingSimulation simulation = new FlockingSimulation(canvas);

                // Example: Add a boid with fixed coordinates for testing
                int fixedX = 400; // Center for 800 width
                int fixedY = 300; // Center for 600 height
                simulation.addBoid(new Boid(canvas, new CartesianCoordinate(fixedX, fixedY), new CartesianCoordinate(0.5, 0.5))); // Small initial velocity

                simulation.addBoid(new Boid(canvas, new CartesianCoordinate(Utils.randomInt(0, canvas.getWidth()), Utils.randomInt(0, canvas.getHeight())), new CartesianCoordinate(Utils.randomDouble(-1, 1), Utils.randomDouble(-1, 1))));
                simulation.addBoid(new Boid(canvas, new CartesianCoordinate(Utils.randomInt(0, canvas.getWidth()), Utils.randomInt(0, canvas.getHeight())), new CartesianCoordinate(Utils.randomDouble(-1, 1), Utils.randomDouble(-1, 1))));
                simulation.addBoid(new Boid(canvas, new CartesianCoordinate(Utils.randomInt(0, canvas.getWidth()), Utils.randomInt(0, canvas.getHeight())), new CartesianCoordinate(Utils.randomDouble(-1, 1), Utils.randomDouble(-1, 1))));
               
                // 7. Start the simulation loop in a new thread
                new Thread(() -> simulation.runSimulationLoop()).start();
            }
        });
    }

}
