# build-and-run.ps1

# Step 1: Find all .java files
$files = Get-ChildItem -Recurse -Filter *.java .\src | ForEach-Object { $_.FullName }

# Step 2: Compile them
javac -d bin -cp src $files

# Step 3: Run the main class
java -cp bin flockingsim.FlockingSimulation
