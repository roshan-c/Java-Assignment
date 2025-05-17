# High Marks Enhancement Plan

This plan outlines features and tasks to target for achieving high marks, focusing on advanced functionality, object-oriented design, and report quality, as per the assignment brief.

## I. Extended Functionality & GUI Polish (Program Implementation - Extended Functionality)

- [ ] **Control Over Simulation Speed**:
    - [ ] Add a GUI slider (e.g., `JSlider`) to control `SIMULATION_DELAY_MS` in `FlockingSimulation`.
    - [ ] Implement a setter method in `FlockingSimulation` for the simulation delay.
    - [ ] Ensure the GUI updates this value and the simulation loop uses it for `Utils.pause()`.
    - [ ] *Brief Relevance: "extended to include other complexities, such as control over simulation speed"*

- [ ] **Mouse Interaction Features**:
    - [ ] Implement `MouseListener` and `MouseMotionListener` on the `Canvas`.
    - [ ] **Option 1: Repeller/Attractor**: 
        - [ ] Mouse cursor acts as a point that repels boids (if button held, or always) or attracts them.
        - [ ] Requires adding a new steering force calculation in `Boid.java` based on mouse position.
        - [ ] Add a GUI toggle (e.g., `JCheckBox` or `JRadioButton`) to switch between repel/attract or enable/disable.
    - [ ] **Option 2: Add Boid on Click**: 
        - [ ] Clicking on the canvas adds a new boid at the mouse cursor's location with a random initial velocity.
        - [ ] Requires modifying `FlockingSimulation.resetAndSpawnBoids` or adding a new `addSingleBoidAt(position)` method that safely updates the `CopyOnWriteArrayList`.
    - [ ] *Brief Relevance: "extended to include other complexities" (general enhancement)*

## II. Advanced Object-Oriented Design (Appropriate Object-Oriented Design Practices)

- [ ] **Introduce Inheritance & Polymorphism (Predator/Prey or Specialized Boids)**:
    - [ ] Define an abstract base class or interface (e.g., `AbstractAgent` or `SimulatableAgent`).
        - [ ] Common properties (`position`, `velocity`, `canvas`, `maxSpeed`, etc.).
        - [ ] Abstract methods (e.g., `calculateForces()`, `specificUpdate()`, `draw()`).
    - [ ] Refactor `Boid` to extend/implement this base class/interface.
    - [ ] Create a new class, e.g., `PredatorBoid`, that also extends/implements the base.
        - [ ] `PredatorBoid` has different behaviors: Hunts normal `Boid`s (steers towards nearest `Boid`).
        - [ ] Normal `Boid`s need a new behavior: Flee from `PredatorBoid`s if within a certain perception radius.
    - [ ] Modify `FlockingSimulation` to manage a `List<AbstractAgent> agents`.
    - [ ] Update GUI to potentially control number/type of different agents.
    - [ ] *Brief Relevance: "appropriate inheritance and polymorphism", "other types of individuals for the flock to interact with (perhaps add predators into the simulation)"*

- [ ] **Use of Interfaces (Beyond Listeners)**:
    - [ ] If not using an abstract class for agents, ensure `SimulatableAgent` (or similar) is an interface defining the contract for updatable/drawable agents.
    - [ ] Consider other small interfaces if they clarify roles, e.g., `Obstacle` interface if you were to add different obstacle shapes (though current brief only mandates rectangles).
    - [ ] *Brief Relevance: "Use of interfaces within your class structure"*

## III. Code Quality & Documentation (Program Implementation - various)

- [ ] **Thorough Code Commenting & Javadoc**: 
    - [ ] Ensure all classes, public methods, and complex logic blocks have clear Javadoc comments.
    - [ ] Explain *why* things are done, not just *what* is done.
    - [ ] *Brief Relevance: "Program documentation (commenting, formatting, suitable names)"*

- [ ] **Modular, Non-Duplicated Code**: 
    - [ ] Continuously review for opportunities to refactor and create short, reusable, single-purpose methods.
    - [ ] *Brief Relevance: "Modular, non-duplicated code"*

## IV. Performance Optimization (If Necessary)

- [ ] **Spatial Partitioning for Neighbor Search (if >300-500 boids cause slowdown)**:
    - [ ] If performance becomes an issue with a high number of boids, implement a spatial partitioning scheme (e.g., a simple grid).
    - [ ] `Boid.getNeighbors()` would then query the grid for boids in its own and adjacent cells, significantly reducing checks compared to iterating all boids.
    - [ ] This is a significant undertaking, only pursue if performance is a clear bottleneck with desired boid counts.
    - [ ] *Brief Relevance: Implied by ability to handle a reasonable simulation; not explicitly asked for unless program execution suffers.*

## V. Report Excellence (Report Marks)

- [ ] **Detailed Explanation of Flocking Algorithm**: 
    - [ ] Clearly describe the rules you've implemented (Separation, Alignment, Cohesion, Obstacle Avoidance).
    - [ ] Explain the vector math and logic for each force calculation.
    - [ ] Discuss how these simple rules lead to emergent flocking behavior.
- [ ] **Comprehensive Explanation of Design**: 
    - [ ] Detail your class structure (UML diagrams can be very helpful here).
    - [ ] Justify your OO design choices (why you used certain classes, inheritance, composition, interfaces).
    - [ ] Explain how different parts of the system interact (e.g., GUI and simulation logic).
- [ ] **Thorough Explanation of Program Implementation**: 
    - [ ] Discuss key algorithms or complex sections of your code (e.g., boid update cycle, obstacle interaction, GUI event handling).
    - [ ] Highlight any challenges and how you overcame them.
- [ ] **Systematic Summary of Test Procedures and Results**: 
    - [ ] Describe how you tested different parts of your simulation (e.g., individual boid movement, flocking rules, obstacle avoidance, GUI controls).
    - [ ] Include test cases (e.g., specific slider values and expected outcomes, as we discussed).
    - [ ] Summarize the results – does the simulation behave as expected?
- [ ] **Professional Readability and Formatting**: 
    - [ ] Ensure the report is well-structured, clear, concise, and free of errors.
    - [ ] Use diagrams/screenshots where they aid explanation.
    - [ ] Adhere to page limits and submission guidelines.

**Key to High Marks (as per brief):** "good object-oriented design practices ... modular well-written code with good documentation and a well written and well-structured report." and "extended to include other complexities". 