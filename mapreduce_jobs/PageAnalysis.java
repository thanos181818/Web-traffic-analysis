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

public class PageAnalysis {

    public static class PageMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text page = new Text();
        
        public void map(LongWritable key, Text value, Context context) 
                throws IOException, InterruptedException {
            String line = value.toString();
            
            // Extract page from Apache log format: "GET /home HTTP/1.1"
            try {
                String[] parts = line.split("\"");
                if (parts.length >= 2) {
                    String request = parts[1]; // The request part between quotes
                    String[] requestParts = request.split(" ");
                    if (requestParts.length >= 2) {
                        String method = requestParts[0];
                        String path = requestParts[1];
                        
                        // Only count GET and POST requests
                        if (method.equals("GET") || method.equals("POST")) {
                            page.set(path);
                            context.write(page, one);
                        }
                    }
                }
            } catch (Exception e) {
                // Skip malformed lines
            }
        }
    }
    
    public static class PageReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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
        Job job = Job.getInstance(conf, "page analysis");
        job.setJarByClass(PageAnalysis.class);
        job.setMapperClass(PageMapper.class);
        job.setCombinerClass(PageReducer.class);
        job.setReducerClass(PageReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}