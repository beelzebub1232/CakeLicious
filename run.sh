#!/bin/bash

echo "Starting CakeLicious Server..."
echo ""

# Check if build directory exists
if [ ! -d "build" ]; then
    echo "Build directory not found. Please compile first using ./compile.sh"
    exit 1
fi

# Start the server
java -cp "build:lib/mysql-connector-j-8.0.33.jar" com.cakelicious.Server