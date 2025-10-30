// Chart configuration and data fetching
document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/data')
        .then(response => response.json())
        .then(data => {
            renderPageViewsChart(data.page_views);
            renderTrafficCharts(data.traffic_data);
            renderReferralChart(data.user_data);
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
});

function renderPageViewsChart(pageViews) {
    const ctx = document.getElementById('pageViewsChart').getContext('2d');
    
    const labels = pageViews.map(item => {
        // Shorten long page URLs for display
        const page = item.page;
        return page.length > 20 ? page.substring(0, 20) + '...' : page;
    });
    const data = pageViews.map(item => item.views);
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Page Views',
                data: data,
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: 'Most Visited Pages'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Number of Views'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Page URLs'
                    }
                }
            }
        }
    });
}

function renderTrafficCharts(trafficData) {
    // Extract hourly data
    const hourlyData = trafficData.filter(item => item.metric.startsWith('hour_'));
    const hourLabels = hourlyData.map(item => {
        const hour = item.metric.replace('hour_', '');
        return hour === '-1' ? 'Unknown' : `${hour}:00`;
    });
    const hourValues = hourlyData.map(item => item.count);
    
    // Hourly Traffic Chart
    const hourlyCtx = document.getElementById('hourlyTrafficChart').getContext('2d');
    new Chart(hourlyCtx, {
        type: 'line',
        data: {
            labels: hourLabels,
            datasets: [{
                label: 'Requests per Hour',
                data: hourValues,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: 'Traffic Distribution by Hour'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Number of Requests'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Hour of Day'
                    }
                }
            }
        }
    });
    
    // Status Codes Chart
    const statusData = trafficData.filter(item => item.metric.startsWith('status_'));
    const statusLabels = statusData.map(item => {
        const code = item.metric.replace('status_', '');
        return `HTTP ${code}`;
    });
    const statusValues = statusData.map(item => item.count);
    
    const statusCtx = document.getElementById('statusCodesChart').getContext('2d');
    new Chart(statusCtx, {
        type: 'doughnut',
        data: {
            labels: statusLabels,
            datasets: [{
                data: statusValues,
                backgroundColor: [
                    'rgba(75, 192, 192, 0.8)',
                    'rgba(255, 205, 86, 0.8)',
                    'rgba(255, 99, 132, 0.8)',
                    'rgba(153, 102, 255, 0.8)'
                ],
                borderColor: [
                    'rgba(75, 192, 192, 1)',
                    'rgba(255, 205, 86, 1)',
                    'rgba(255, 99, 132, 1)',
                    'rgba(153, 102, 255, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                title: {
                    display: true,
                    text: 'HTTP Status Code Distribution'
                }
            }
        }
    });
}

function renderReferralChart(userData) {
    const referralData = userData.filter(item => item.metric.startsWith('referrer_'));
    const referralLabels = referralData.map(item => {
        const referrer = item.metric.replace('referrer_', '');
        return referrer === 'direct' ? 'Direct' : 
               referrer.includes('google') ? 'Google' :
               referrer.includes('twitter') ? 'Twitter' :
               referrer.includes('facebook') ? 'Facebook' :
               referrer.includes('linkedin') ? 'LinkedIn' :
               referrer.includes('bing') ? 'Bing' : referrer;
    });
    const referralValues = referralData.map(item => item.count);
    
    const ctx = document.getElementById('referralChart').getContext('2d');
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: referralLabels,
            datasets: [{
                data: referralValues,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.8)',
                    'rgba(54, 162, 235, 0.8)',
                    'rgba(255, 205, 86, 0.8)',
                    'rgba(75, 192, 192, 0.8)',
                    'rgba(153, 102, 255, 0.8)',
                    'rgba(201, 203, 207, 0.8)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 205, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(201, 203, 207, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                title: {
                    display: true,
                    text: 'Traffic Sources'
                }
            }
        }
    });
}

// Utility function to format numbers
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}