# Phase 4: Tuning and Advanced Features (Optional)

- [ ] **Tuning Parameters**:
    - [ ] Experiment with `perceptionRadius`, `desiredSeparation`, `maxSpeed`, `maxForce`.
    - [ ] Adjust weights for separation, alignment, and cohesion forces to achieve different flocking behaviors (e.g., tight clusters, loose groups, smooth alignment).
- [ ] **Obstacle Avoidance**:
    - [ ] Define obstacle shapes (e.g., circles, rectangles).
    - [ ] Implement logic for boids to detect and steer away from obstacles.
- [ ] **Mouse Interaction**:
    - [ ] Add mouse listeners to the `Canvas`.
    - [ ] Implement behaviors like:
        - [ ] Attracting boids towards the mouse cursor.
        - [ ] Repelling boids from the mouse cursor.
        - [ ] Adding new boids on mouse click.
- [ ] **Goal Seeking / Path Following**:
    - [ ] Define target points or paths for boids to follow.
- [ ] **User Interface (GUI Controls)**:
    - [ ] Add Swing components (sliders, checkboxes, buttons) to the `JFrame` to allow real-time adjustment of simulation parameters (e.g., number of boids, `maxSpeed`, rule weights).
- [ ] **Performance Optimization** (if dealing with many boids):
    - [ ] Consider spatial partitioning techniques (e.g., grid or quadtree) for more efficient neighborhood searches if performance becomes an issue with a large number of boids. 