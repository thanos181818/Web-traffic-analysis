import random
import datetime

class ApacheLogGenerator:
    def __init__(self):
        self.ips = self._generate_ips(100)
        self.user_agents = [
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15"
        ]
        self.pages = [
            "/", "/home", "/products", "/about", "/contact",
            "/products/laptops", "/products/phones", "/blog",
            "/login", "/register", "/api/data", "/admin"
        ]
        self.referrers = [
            "https://www.google.com", "https://www.bing.com",
            "https://twitter.com", "https://facebook.com",
            "https://linkedin.com", "direct"
        ]
        self.methods = ["GET", "POST", "PUT", "DELETE"]
        self.status_codes = [200, 301, 404, 500]

    def _generate_ips(self, count):
        ips = []
        for i in range(count):
            ips.append(f"192.168.1.{random.randint(1, 255)}")
        return ips

    def generate_log_entry(self, timestamp):
        ip = random.choice(self.ips)
        method = random.choice(self.methods)
        page = random.choice(self.pages)
        status = random.choice(self.status_codes)
        size = random.randint(500, 5000)
        referrer = random.choice(self.referrers)
        user_agent = random.choice(self.user_agents)
        
        return f'{ip} - - [{timestamp}] "{method} {page} HTTP/1.1" {status} {size} "{referrer}" "{user_agent}"'

    def generate_logs(self, num_entries, start_date, end_date):
        logs = []
        current = start_date
        time_step = (end_date - start_date) / num_entries
        
        for i in range(num_entries):
            timestamp = current.strftime('%d/%b/%Y:%H:%M:%S +0000')
            logs.append(self.generate_log_entry(timestamp))
            current += time_step
            
        return logs

def main():
    generator = ApacheLogGenerator()
    start_date = datetime.datetime(2024, 1, 1)
    end_date = datetime.datetime(2024, 1, 31)
    
    logs_10k = generator.generate_logs(10000, start_date, end_date)
    with open('sample_apache_logs_10k.txt', 'w') as f:
        f.write('\n'.join(logs_10k))
    
    logs_100k = generator.generate_logs(100000, start_date, end_date)
    with open('sample_apache_logs_100k.txt', 'w') as f:
        f.write('\n'.join(logs_100k))
    
    print("Generated sample Apache log entries")

if __name__ == "__main__":
    main()