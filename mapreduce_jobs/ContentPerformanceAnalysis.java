import java.io.IOException;

import javax.naming.Context;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ContentPerformanceAnalysis {

    public static class ContentPerformanceMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text metric = new Text();
    
    public void map(LongWritable key, Text value, Context context) 
            throws IOException, InterruptedException {
        String line = value.toString();
        
        try {
            // Use consistent parsing
            String[] parts = line.split("\"");
            if (parts.length >= 2) {
                String request = parts[1];
                String[] requestParts = request.split(" ");
                if (requestParts.length >= 2) {
                    String method = requestParts[0];
                    String page = requestParts[1];
                    
                    if (method.equals("GET") || method.equals("POST")) {
                        // Extract timestamp
                        String[] initialParts = line.split(" ");
                        if (initialParts.length > 3) {
                            String timestamp = initialParts[3];
                            
                            if (timestamp.startsWith("[")) {
                                String hour = timestamp.split(":")[1];
                                String timeSlot = getTimeSlot(hour);
                                
                                // Count page views by time slot
                                metric.set(page + "_" + timeSlot);
                                context.write(metric, one);
                                
                                // Count overall page popularity
                                metric.set("overall_" + page);
                                context.write(metric, one);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Skip malformed lines
        }
    }
    
    private String getTimeSlot(String hour) {
        int h = Integer.parseInt(hour);
        if (h >= 6 && h < 12) return "morning";
        else if (h >= 12 && h < 18) return "afternoon";
        else if (h >= 18 && h < 24) return "evening";
        else return "night";
    }
}
    
    public static class ContentPerformanceReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        
        public void reduce(Text key, Iterable<IntWritable> values, Context context) 
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
    
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "content performance analysis");
        job.setJarByClass(ContentPerformanceAnalysis.class);
        job.setMapperClass(ContentPerformanceMapper.class);
        job.setCombinerClass(ContentPerformanceReducer.class);
        job.setReducerClass(ContentPerformanceReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}