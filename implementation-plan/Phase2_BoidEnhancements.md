# Phase 2: Enhancing Boid Behavior & Environment Interaction

- [ ] **Boundary Handling**: Implement logic to keep boids within the canvas boundaries.
    - [ ] Choose method: Screen wrap-around (if off one edge, appear on opposite).
    - [ ] Or: Bouncing off edges (reflect velocity).
    - [ ] Add method to `Boid.java` (e.g., `applyBounds(int canvasWidth, int canvasHeight)`).
    - [ ] Call this method in `Boid.update()` or from `FlockingSimulation` after boid updates.
- [ ] **Refine Boid Visuals**:
    - [ ] Modify `Boid.draw()` to represent the boid as a triangle or arrow pointing in the direction of its `velocity`. This will require some basic geometry calculations.
- [ ] **Physics Constraints**:
    - [ ] Add `maxSpeed` to `Boid.java`:
        - [ ] Field: `private double maxSpeed;`
        - [ ] Initialize in constructor.
        - [ ] In `Boid.update()`, after `velocity` is updated, limit its magnitude: `if (velocity.magnitude() > maxSpeed) velocity = velocity.normalize().multiply(maxSpeed);` (Requires `magnitude()` and `normalize()` in `CartesianCoordinate`).
    - [ ] Add `maxForce` to `Boid.java` (for steering behaviors in Phase 3):
        - [ ] Field: `private double maxForce;`
        - [ ] Initialize in constructor.
        - [ ] When applying steering forces, limit the magnitude of the calculated steering vector before adding it to acceleration. 