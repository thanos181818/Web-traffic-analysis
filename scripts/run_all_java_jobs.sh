#!/bin/bash

echo "Starting Hadoop MapReduce Java Jobs..."

# Input and Output directories
INPUT_DIR="/user/root/input"
OUTPUT_BASE="/user/root/output"

# Create output directory
docker exec -it namenode hdfs dfs -mkdir -p $OUTPUT_BASE

# Compile all Java files first
echo "Compiling Java classes..."
docker exec -it namenode javac -cp $(hadoop classpath) -d /mapreduce_jobs/classes \
    /mapreduce_jobs/*.java

# Create JAR file
echo "Creating JAR file..."
docker exec -it namenode jar cf /mapreduce_jobs/webanalytics.jar -C /mapreduce_jobs/classes .

# Job 1: Page Analysis
echo "Running Page Analysis..."
docker exec -it namenode hadoop jar /mapreduce_jobs/webanalytics.jar PageAnalysis \
    $INPUT_DIR $OUTPUT_BASE/page_analysis

# Job 2: Traffic Analysis
echo "Running Traffic Analysis..."
docker exec -it namenode hadoop jar /mapreduce_jobs/webanalytics.jar TrafficAnalysis \
    $INPUT_DIR $OUTPUT_BASE/traffic_analysis

# Job 3: User Analysis
echo "Running User Analysis..."
docker exec -it namenode hadoop jar /mapreduce_jobs/webanalytics.jar UserAnalysis \
    $INPUT_DIR $OUTPUT_BASE/user_analysis

# Job 4: Peak Hours Analysis
echo "Running Peak Hours Analysis..."
docker exec -it namenode hadoop jar /mapreduce_jobs/webanalytics.jar PeakHoursAnalysis \
    $INPUT_DIR $OUTPUT_BASE/peak_hours

# Job 5: Error Rate Analysis
echo "Running Error Rate Analysis..."
docker exec -it namenode hadoop jar /mapreduce_jobs/webanalytics.jar ErrorRateAnalysis \
    $INPUT_DIR $OUTPUT_BASE/error_rates

# Job 6: Content Performance Analysis
echo "Running Content Performance Analysis..."
docker exec -it namenode hadoop jar /mapreduce_jobs/webanalytics.jar ContentPerformanceAnalysis \
    $INPUT_DIR $OUTPUT_BASE/content_performance

echo "All MapReduce jobs completed successfully!"
echo "Results available in HDFS: $OUTPUT_BASE"

# List all output directories to verify
echo "Output directories created:"
docker exec -it namenode hdfs dfs -ls $OUTPUT_BASE