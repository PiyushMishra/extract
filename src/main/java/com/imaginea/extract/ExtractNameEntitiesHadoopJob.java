package com.imaginea.extract;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.List;

/**
 * Created by piyushm on 5/18/15.
 */
public class ExtractNameEntitiesHadoopJob{

    static Extract ex = new Extract();
    static StanfordCoreNLP pipeline = ex.initializeNLPPipeLine();

    public static class FileReaderMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            List<EmbeddedToken> tokens = ex.extractNameEntities(pipeline, value.toString());

            for (EmbeddedToken token : tokens) {
                System.out.println("##" + token.getName() + "$$" + token.getValue());
                context.write(new Text(token.getName()), new Text(token.getValue()));
            }
        }
    }

    public static class EntityExtracterReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            for (Text text : values) {
                System.out.println("####" + key.toString() + "$$$$$" + text.toString());
                context.write(key, text);
            }
        }
    }

    public static void main(String args[]) throws IOException,
            InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = new Job(conf, "Extract Entity");
        job.setJarByClass(ExtractNameEntitiesHadoopJob.class);
        job.setMapperClass(FileReaderMapper.class);
        job.setReducerClass(EntityExtracterReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

}
