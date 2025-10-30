#!/bin/bash

echo "Cleaning up HDFS output directories..."

# Remove all output directories
docker exec -it namenode hdfs dfs -rm -r /user/root/output/page_analysis
docker exec -it namenode hdfs dfs -rm -r /user/root/output/traffic_analysis
docker exec -it namenode hdfs dfs -rm -r /user/root/output/user_analysis
docker exec -it namenode hdfs dfs -rm -r /user/root/output/peak_hours
docker exec -it namenode hdfs dfs -rm -r /user/root/output/error_rates
docker exec -it namenode hdfs dfs -rm -r /user/root/output/content_performance

# Remove compiled classes and JAR
docker exec -it namenode rm -rf /mapreduce_jobs/classes
docker exec -it namenode rm -f /mapreduce_jobs/webanalytics.jar

echo "Cleanup completed"