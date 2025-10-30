from flask import Flask, render_template, jsonify
import subprocess
import os

app = Flask(__name__)

class SimpleResultsParser:
    def __init__(self):
        self.results_dir = "/user/root/output"
    
    def read_hdfs_file(self, path):
        """Simple HDFS file reader"""
        try:
            result = subprocess.check_output(
                f"docker exec namenode hdfs dfs -cat {path}", 
                shell=True, 
                stderr=subprocess.DEVNULL,
                timeout=10
            ).decode('utf-8')
            return result
        except:
            return ""
    
    def parse_simple(self, output_path):
        """Super simple parser"""
        content = self.read_hdfs_file(f"{output_path}/part-r-00000")
        data = []
        
        for line in content.split('\n'):
            line = line.strip()
            # Skip empty lines and log messages
            if line and not any(x in line for x in ['INFO', 'WARN', 'ERROR', 'sasl.', '2025-']):
                parts = line.split()
                if len(parts) >= 2:
                    try:
                        data.append({
                            "metric": parts[0], 
                            "count": int(parts[1])
                        })
                    except:
                        continue
        return data
    
    def parse_page_views(self):
        data = self.parse_simple(f"{self.results_dir}/page_analysis")
        return [{"page": item["metric"], "views": item["count"]} for item in data]
    
    def parse_traffic_analysis(self):
        return self.parse_simple(f"{self.results_dir}/traffic_analysis")
    
    def parse_user_analysis(self):
        return self.parse_simple(f"{self.results_dir}/user_analysis")
    
    def parse_peak_hours(self):
        data = self.parse_simple(f"{self.results_dir}/peak_hours")
        return [{"hour": item["metric"], "visits": item["count"]} for item in data]
    
    def parse_error_rates(self):
        return self.parse_simple(f"{self.results_dir}/error_rates")
    
    def parse_content_performance(self):
        data = self.parse_simple(f"{self.results_dir}/content_performance")
        return [{"metric": item["metric"], "views": item["count"]} for item in data]

parser = SimpleResultsParser()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/page-views')
def page_views():
    return render_template('page_views.html', page_views=parser.parse_page_views())

@app.route('/traffic')
def traffic():
    return render_template('traffic_analysis.html', traffic_data=parser.parse_traffic_analysis())

@app.route('/users')
def users():
    return render_template('user_analysis.html', user_data=parser.parse_user_analysis())

@app.route('/peak-hours')
def peak_hours():
    return render_template('peak_hours.html', peak_hours=parser.parse_peak_hours())

@app.route('/errors')
def errors():
    return render_template('error_analysis.html', error_rates=parser.parse_error_rates())

@app.route('/content')
def content():
    return render_template('content_performance.html', content_performance=parser.parse_content_performance())

@app.route('/charts')
def charts():
    return render_template('charts.html')

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)