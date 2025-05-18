Exam Number: [Please fill in your exam number here]

Source Files:
All .java files in the src directory and its subdirectories should be compiled.

Compilation:
Using a terminal, navigate to the project root directory.
To compile:
javac -d bin -cp src $(find src -name '*.java')
(On Windows, you might need to use a different command to find all .java files, e.g., a PowerShell equivalent or manually listing them if `find` isn't available directly in CMD)

Entry Point (Main Class):
The main class to run the program is:
flockingsim.FlockingSimulation

Execution:
After successful compilation, to run the program from the project root directory:
java -cp bin flockingsim.FlockingSimulation 