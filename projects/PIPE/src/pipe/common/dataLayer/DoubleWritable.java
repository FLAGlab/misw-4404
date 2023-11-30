package pipe.common.dataLayer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * A writable comparable class for doubles. Used by Hadoop to represent doubles
 * when used as the key for a mapreduce task
 * 
 * @author Oliver Haggarty
 * 
 */
public class DoubleWritable implements WritableComparable
{
	/** A Comparator optimized for DoubleWritable. */
	public static class Comparator extends WritableComparator
	{
		public Comparator() {
			super(DoubleWritable.class);
		}

		@Override
		public int compare(	final byte[] b1,
							final int s1,
							final int l1,
							final byte[] b2,
							final int s2,
							final int l2)
		{
			final double thisValue = WritableComparator.readDouble(b1, s1);
			final double thatValue = WritableComparator.readDouble(b2, s2);
			return thisValue < thatValue ? -1 : thisValue == thatValue ? 0 : 1;
		}
	}

	static
	{ // register default comparator
		WritableComparator.define(DoubleWritable.class, new Comparator());
	}

	private double	value;

	public DoubleWritable() {
	}

	public DoubleWritable(final double value) {
		this.set(value);
	}

	/** Compares two DoubleWritables. */
	public int compareTo(final Object o)
	{
		final double thisValue = this.value;
		final double thatValue = ((DoubleWritable) o).value;
		return thisValue < thatValue ? -1 : thisValue == thatValue ? 0 : 1;
	}

	/**
	 * Returns true iff <code>o</code> is a DoubleWritable with the same
	 * value.
	 */
	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof DoubleWritable))
		{
			return false;
		}
		final DoubleWritable other = (DoubleWritable) o;
		return this.value == other.value;
	}

	/** Return the value of this DoubleWritable. */
	public double get()
	{
		return this.value;
	}

	@Override
	public int hashCode()
	{
		return (int) this.value;
	}

	/**
	 * Sets the value of this DoubleWritable from a DataInput stream
	 */
	public void readFields(final DataInput in) throws IOException
	{
		this.value = in.readDouble();
	}

	/** Set the value of this DoubleWritable. */
	public void set(final double value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return Double.toString(this.value);
	}

	/**
	 * Writes the value of this DoubleWritable to a DataOutput stream
	 */
	public void write(final DataOutput out) throws IOException
	{
		out.writeDouble(this.value);
	}

}