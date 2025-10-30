#!/bin/bash

echo "Setting up HDFS directory structure..."

# Create HDFS directories
docker exec -it namenode hdfs dfs -mkdir -p /user/root/input
docker exec -it namenode hdfs dfs -mkdir -p /user/root/output

# List created directories
echo "HDFS directory structure:"
docker exec -it namenode hdfs dfs -ls -R /user/root

echo "HDFS setup completed successfully"