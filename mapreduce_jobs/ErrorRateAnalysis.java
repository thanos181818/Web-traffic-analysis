import java.io.IOException;
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

public class ErrorRateAnalysis {

    public static class ErrorRateMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text metric = new Text();
        
        public void map(LongWritable key, Text value, Context context) 
                throws IOException, InterruptedException {
            String line = value.toString();
            
            try {
                String[] parts = line.split(" ");
                if (parts.length > 8) {
                    String status = parts[8];
                    String page = parts[6];
                    
                    // Count total requests
                    metric.set("total_requests");
                    context.write(metric, one);
                    
                    // Count errors (400 and 500 status codes)
                    if (status.startsWith("4") || status.startsWith("5")) {
                        metric.set("errors_total");
                        context.write(metric, one);
                        
                        metric.set("error_page_" + page);
                        context.write(metric, one);
                    }
                }
            } catch (Exception e) {
                // Skip malformed lines
            }
        }
    }
    
    public static class ErrorRateReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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
        Job job = Job.getInstance(conf, "error rate analysis");
        job.setJarByClass(ErrorRateAnalysis.class);
        job.setMapperClass(ErrorRateMapper.class);
        job.setCombinerClass(ErrorRateReducer.class);
        job.setReducerClass(ErrorRateReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}