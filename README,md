# 🧠 Hadoop Web Analytics Dashboard

A comprehensive web analytics platform that processes Apache server logs using **Hadoop MapReduce** and visualizes insights through an interactive **Flask dashboard**.

---

## 📊 Features

- 6 Different Analytics on web server logs  
- Interactive Charts and visualizations  
- Real-time Data processing with Hadoop  
- Multi-tab Dashboard for different insights  
- Responsive Design — works on all devices  

---

## 🏗️ Architecture

```
Raw Logs → Hadoop HDFS → MapReduce Processing → Flask Dashboard → Interactive Visualizations
```

---

## 📁 Project Structure

```
hadoop-web-analytics/
├── mapreduce_jobs/              # Java MapReduce analysis jobs
│   ├── PageAnalysis.java        # Most visited pages
│   ├── TrafficAnalysis.java     # Hourly traffic patterns
│   ├── UserAnalysis.java        # User distribution & referrers
│   ├── PeakHoursAnalysis.java   # Busiest traffic hours
│   ├── ErrorRateAnalysis.java   # HTTP error analysis
│   └── ContentPerformance.java  # Content popularity by time
├── visualization/               # Flask web dashboard
│   ├── web_dashboard.py         # Main Flask application
│   ├── config.py                # Environment configuration
│   ├── templates/               # HTML templates
│   │   ├── index.html
│   │   ├── page_views.html
│   │   ├── traffic_analysis.html
│   │   ├── user_analysis.html
│   │   ├── peak_hours.html
│   │   ├── error_analysis.html
│   │   ├── content_performance.html
│   │   └── charts.html          # Interactive charts
│   └── static/
│       ├── style.css            # Styling
│       └── chart.js             # Chart configurations
├── scripts/                     # Utility scripts
│   ├── run_all_jobs.ps1         # Run all MapReduce jobs
│   └── setup_hdfs.sh            # HDFS setup
├── data/                        # Sample data
│   └── log_generator.py         # Apache log generator
└── results/                     # Analysis outputs (generated)
```

---

## 🚀 Quick Start

### Prerequisites

- Docker Desktop  
- Git  
- Python 3.8+  

---

### 1. Start Hadoop Cluster

```bash
docker-compose up -d
sleep 30
docker ps
```

---

### 2. Initial Setup & Compilation

```bash
docker exec namenode hdfs dfs -rm -r /user/root/output/* 2>/dev/null || true
rm -rf results/
docker exec namenode rm -rf /mapreduce_jobs/classes /mapreduce_jobs/*.jar

docker exec namenode hdfs dfs -mkdir -p /user/root/output
docker exec namenode mkdir -p /mapreduce_jobs/classes
mkdir -p results
```

---

### 3. Copy and Compile Java Files

```bash
docker cp mapreduce_jobs/PageAnalysis.java namenode:/mapreduce_jobs/
docker cp mapreduce_jobs/TrafficAnalysis.java namenode:/mapreduce_jobs/
docker cp mapreduce_jobs/UserAnalysis.java namenode:/mapreduce_jobs/
docker cp mapreduce_jobs/PeakHoursAnalysis.java namenode:/mapreduce_jobs/
docker cp mapreduce_jobs/ErrorRateAnalysis.java namenode:/mapreduce_jobs/
docker cp mapreduce_jobs/ContentPerformanceAnalysis.java namenode:/mapreduce_jobs/

docker exec namenode javac -cp $(docker exec namenode hadoop classpath) -d /mapreduce_jobs/classes /mapreduce_jobs/*.java

docker exec namenode jar cf /mapreduce_jobs/pageanalysis.jar -C /mapreduce_jobs/classes PageAnalysis*.class
docker exec namenode jar cf /mapreduce_jobs/trafficanalysis.jar -C /mapreduce_jobs/classes TrafficAnalysis*.class
docker exec namenode jar cf /mapreduce_jobs/useranalysis.jar -C /mapreduce_jobs/classes UserAnalysis*.class
docker exec namenode jar cf /mapreduce_jobs/peakhours.jar -C /mapreduce_jobs/classes PeakHoursAnalysis*.class
docker exec namenode jar cf /mapreduce_jobs/errorrates.jar -C /mapreduce_jobs/classes ErrorRateAnalysis*.class
docker exec namenode jar cf /mapreduce_jobs/contentperformance.jar -C /mapreduce_jobs/classes ContentPerformanceAnalysis*.class
```

---

### 4. Run All MapReduce Analyses

```bash
docker exec namenode hadoop jar /mapreduce_jobs/pageanalysis.jar PageAnalysis /user/root/input /user/root/output/page_analysis
docker exec namenode hadoop jar /mapreduce_jobs/trafficanalysis.jar TrafficAnalysis /user/root/input /user/root/output/traffic_analysis
docker exec namenode hadoop jar /mapreduce_jobs/useranalysis.jar UserAnalysis /user/root/input /user/root/output/user_analysis
docker exec namenode hadoop jar /mapreduce_jobs/peakhours.jar PeakHoursAnalysis /user/root/input /user/root/output/peak_hours
docker exec namenode hadoop jar /mapreduce_jobs/errorrates.jar ErrorRateAnalysis /user/root/input /user/root/output/error_rates
docker exec namenode hadoop jar /mapreduce_jobs/contentperformance.jar ContentPerformanceAnalysis /user/root/input /user/root/output/content_performance
```

---

### 5. Verify Results

```bash
docker exec namenode hdfs dfs -ls /user/root/output/
docker exec namenode hdfs dfs -cat /user/root/output/page_analysis/part-r-00000 | head -5
docker exec namenode hdfs dfs -cat /user/root/output/traffic_analysis/part-r-00000 | head -5
docker exec namenode hdfs dfs -cat /user/root/output/user_analysis/part-r-00000 | head -5
```

---

### 6. Copy Results Locally (Optional)

```bash
docker exec namenode hdfs dfs -get /user/root/output/* /tmp/
docker cp namenode:/tmp/ ./results/
```

---

### 7. Start Web Dashboard

```bash
cd visualization
pip install flask
python web_dashboard.py
```

---

### 8. Access the Dashboard

Visit:
```
http://localhost:5000
```

---

## 📈 Available Analyses

- **Page Views** – Most visited pages and content popularity  
- **Traffic Analysis** – Hourly patterns and status codes  
- **User Analysis** – Visitor distribution and referral sources  
- **Peak Hours** – Busiest traffic periods and patterns  
- **Error Rates** – HTTP errors and problematic pages  
- **Content Performance** – Page popularity across time slots  
- **Interactive Charts** – Visual representations of all data  

---

## 🛠️ Development

### Environment Configuration

The application automatically detects the environment:

- **Development:** Uses local files, runs on `localhost:5000`  
- **Production:** Uses HDFS directly, runs on `0.0.0.0:5000`  

Set environment variable:
```bash
export FLASK_ENV=production  # or development
```

---

### Regenerating Sample Data

```bash
cd data
python log_generator.py
```

---

### Restarting Services

```bash
docker-compose down
docker-compose up -d
cd visualization
python web_dashboard.py
```

---

## 🔧 Troubleshooting

### Common Issues

#### Docker containers not starting
```bash
docker-compose down
docker system prune -a
docker-compose up -d
```

#### HDFS connection issues
```bash
docker exec namenode hdfs dfs -ls /
```

#### Web dashboard not loading data
Visit:  
[http://localhost:5000/status](http://localhost:5000/status)

#### Port 5000 already in use
```bash
app.run(debug=True, host='localhost', port=8000)
```

---

## 📊 Hadoop Components

- **namenode:** HDFS master server  
- **datanode:** Data storage nodes  
- **resourcemanager:** YARN resource management  
- **nodemanager:** Node task management  
- **historyserver:** Job history tracking  

---

## 🌐 Web URLs

| Service | URL |
|----------|-----|
| **Dashboard** | [http://localhost:5000](http://localhost:5000) |
| **HDFS UI** | [http://localhost:9870](http://localhost:9870) |
| **YARN UI** | [http://localhost:8088](http://localhost:8088) |
| **Job History** | [http://localhost:8188](http://localhost:8188) |
| **System Status** | [http://localhost:5000/status](http://localhost:5000/status) |

---

