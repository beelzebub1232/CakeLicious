#!/bin/bash

echo "Compiling CakeLicious..."

# Create build directory if it doesn't exist
mkdir -p build

# Compile Java files
javac -d build -cp "lib/mysql-connector-j-8.0.33.jar:." src/com/cakelicious/database/*.java src/com/cakelicious/models/*.java src/com/cakelicious/services/*.java src/com/cakelicious/handlers/*.java src/com/cakelicious/Server.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run the server, execute:"
    echo "./run.sh"
    chmod +x run.sh
else
    echo "Compilation failed!"
fi