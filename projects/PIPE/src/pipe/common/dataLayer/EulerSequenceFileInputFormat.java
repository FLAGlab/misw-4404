package pipe.common.dataLayer;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileRecordReader;

/**
 * A SequenceFileInputFormat specifically created for the Euler mapreduce class
 * found in PIPE2. It allows finer granulation of FileSplits than the standard
 * SequenceFileInputFormat found in Hadoop.
 * 
 * @author Oliver Haggarty August 2007
 * 
 */
public class EulerSequenceFileInputFormat extends FileInputFormat
{
	/**
	 * Sets the minimum split size to 0, as opposed to a multiple of the dfs
	 * block size
	 */
	public EulerSequenceFileInputFormat() {
		this.setMinSplitSize(0);
	}

	@Override
	public RecordReader getRecordReader(final InputSplit split, final JobConf job, final Reporter reporter) throws IOException
	{

		reporter.setStatus(split.toString());

		return new SequenceFileRecordReader(job, (FileSplit) split);
	}

	@Override
	protected Path[] listPaths(final JobConf job) throws IOException
	{

		final Path[] files = super.listPaths(job);
		for (int i = 0; i < files.length; i++)
		{
			final Path file = files[i];
			if (file.getFileSystem(job).isDirectory(file))
			{ // it's a MapFile
				files[i] = new Path(file, MapFile.DATA_FILE_NAME); // use the
																	// data file
			}
		}
		return files;
	}

}
