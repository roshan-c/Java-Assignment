# Phase 2: Enhancing Boid Behavior & Environment Interaction

- [X] **Boundary Handling**: Implement logic to keep boids within the canvas boundaries.
    - [X] Choose method: Screen wrap-around (if off one edge, appear on opposite).
    - [X] Or: Bouncing off edges (reflect velocity).
    - [X] Add method to `Boid.java` (e.g., `wrapPosition(int canvasWidth, int canvasHeight)` and integrated into `move()`).
    - [X] Call this method in `Boid.update()` or from `FlockingSimulation` after boid updates (Handled within `Boid.move()`).
- [X] **Refine Boid Visuals**:
    - [X] Modify `Boid.draw()` to represent the boid as a triangle or arrow pointing in the direction of its `velocity`.
- [X] **Physics Constraints**:
    - [X] Add `maxSpeed` to `Boid.java`:
        - [X] Field: `private double maxSpeed;`
        - [X] Initialize in constructor.
        - [X] In `Boid.update()`, after `velocity` is updated, limit its magnitude.
    - [X] Add `maxForce` to `Boid.java` (for steering behaviors in Phase 3):
        - [X] Field: `private double maxForce;`
        - [X] Initialize in constructor.
        - [X] When applying steering forces, limit the magnitude of the calculated steering vector before adding it to acceleration. 