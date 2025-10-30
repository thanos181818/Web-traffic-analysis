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

public class PeakHoursAnalysis {

    public static class PeakHoursMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text hour = new Text();
        
        public void map(LongWritable key, Text value, Context context) 
                throws IOException, InterruptedException {
            String line = value.toString();
            
            try {
                String[] parts = line.split(" ");
                if (parts.length > 3) {
                    // Extract hour from timestamp like [27/Oct/2024:10:15:32
                    String timestamp = parts[3];
                    if (timestamp.startsWith("[")) {
                        String hourStr = timestamp.split(":")[1];
                        hour.set("hour_" + hourStr);
                        context.write(hour, one);
                    }
                }
            } catch (Exception e) {
                // Skip malformed lines
            }
        }
    }
    
    public static class PeakHoursReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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
        Job job = Job.getInstance(conf, "peak hours analysis");
        job.setJarByClass(PeakHoursAnalysis.class);
        job.setMapperClass(PeakHoursMapper.class);
        job.setCombinerClass(PeakHoursReducer.class);
        job.setReducerClass(PeakHoursReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));  // This should use args[1]
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

