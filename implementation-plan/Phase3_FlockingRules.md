# Phase 3: Implementing Core Flocking Rules

- [X] **`CartesianCoordinate` Enhancements**: Ensure these vector math methods are present:
    - [X] `subtract(CartesianCoordinate other)`: Returns `this - other`.
    - [X] `magnitude()`: Returns the length of the vector.
    - [X] `normalize()`: Returns a unit vector (length 1) in the same direction.
    - [X] `distance(CartesianCoordinate other)`: Returns the vector difference, then its magnitude is used for scalar distance.
    - [X] `limit(double max)`: (Used `limitMagnitude` from earlier name, now `limit`)
- [X] **Neighborhood Detection (in `Boid.java`)**:
    - [X] Define `perceptionRadius` field in `Boid`.
    - [X] Method like `getNeighbors(List<Boid> allBoids)` that returns a list of boids within the `perceptionRadius` (excluding itself).
- [X] **Separation Rule (in `Boid.java`)**:
    - [X] Method `calculateSeparationForce(List<Boid> neighbors)`:
        - [X] Iterates through neighbors.
        - [X] For each neighbor closer than a desired separation distance, calculate a force vector pointing away from it.
        - [X] Scale this force inversely by distance (stronger repulsion for closer boids).
        - [X] Sum these repulsion forces.
        - [X] Normalize and scale to `maxSpeed` (then limited by `maxForce` effectively).
    - [X] Apply this force to `boid.acceleration` (weighted).
- [X] **Alignment Rule (in `Boid.java`)**:
    - [X] Method `calculateAlignmentForce(List<Boid> neighbors)`:
        - [X] Calculate the average `velocity` of all neighbors.
        - [X] Calculate a steering force vector towards this average velocity (`desired_velocity - current_velocity`).
        - [X] Normalize and scale to `maxSpeed` (then limited by `maxForce`).
    - [X] Apply this force to `boid.acceleration` (weighted).
- [X] **Cohesion Rule (in `Boid.java`)**:
    - [X] Method `calculateCohesionForce(List<Boid> neighbors)`:
        - [X] Calculate the average `position` (center of mass) of all neighbors.
        - [X] Calculate a steering force vector from the boid's current position towards this average position (`seek(centerOfMass)` method used).
        - [X] This `seek` method normalizes, scales to `maxSpeed`, then subtracts current velocity and limits by `maxForce`.
    - [X] Apply this force to `boid.acceleration` (weighted).
- [X] **Combining Steering Forces (in `Boid.java`)**:
    - [X] The `applyFlockingRules` was integrated into `Boid.update()` directly.
    - [X] Inside `Boid.update()`:
        - [X] Gets neighbors (from `allBoids` based on `perceptionRadius`).
        - [X] Calculates separation, alignment, and cohesion forces (and obstacle avoidance).
        - [X] Adds these forces together with weighting factors to `this.acceleration`. 