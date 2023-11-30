package pipe.common.dataLayer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

import flanagan.complex.Complex;

/**
 * A writable comparable class for complex number. Used by Hadoop to represent
 * Complex numbers when used as the key for a mapreduce task
 * 
 * @author Oliver Haggarty August 2007
 * 
 */
public class ComplexWritable implements WritableComparable
{

	private double	real, imaginary;

	public ComplexWritable() {
	}

	/**
	 * Constructor that sets the values of the complex number
	 * 
	 * @param real
	 * @param imaginary
	 */
	public ComplexWritable(final double real, final double imaginary) {
		this.set(real, imaginary);
	}

	/** Compares two ComplexWritables. */
	public int compareTo(final Object o)
	{
		final double thisReal = this.real;
		final double thatReal = ((ComplexWritable) o).real;
		final double thisImag = this.imaginary;
		final double thatImag = ((ComplexWritable) o).imaginary;
		return thisReal < thatReal	? -1
									: thisReal == thatReal	? (thisImag < thatImag	? -1
																					: thisImag == thatImag	? 0
																											: 1)
															: 1;
	}

	/**
	 * Returns true iff <code>o</code> is a ComplexWritable with the same
	 * value.
	 */
	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ComplexWritable))
		{
			return false;
		}
		final ComplexWritable other = (ComplexWritable) o;
		return this.real == other.real && this.imaginary == other.imaginary;
	}

	/**
	 * Returns the values of the complex number as a flanagan.complex.Complex
	 * object
	 * 
	 * @return Complex number
	 */
	public Complex get()
	{
		return new Complex(this.real, this.imaginary);
	}

	@Override
	public int hashCode()
	{
		return (int) (this.real + this.imaginary);
	}

	/**
	 * Sets the values of the complex number by reading from a DataInput stream
	 * 
	 * @param in
	 */
	public void readFields(final DataInput in) throws IOException
	{
		this.real = in.readDouble();
		this.imaginary = in.readDouble();
	}

	/**
	 * Sets the values of the complex number
	 * 
	 * @param real
	 * @param imaginary
	 */
	public void set(final double real, final double imaginary)
	{
		this.real = real;
		this.imaginary = imaginary;
	}

	/**
	 * Returns string of the form "Re: xx, Im: xx"
	 */
	@Override
	public String toString()
	{
		return "Re: " + Double.toString(this.real) + ", Im: " + Double.toString(this.imaginary);
	}

	/**
	 * Writes the values of the complex number to a DataOutput stream
	 * 
	 * @param out
	 */
	public void write(final DataOutput out) throws IOException
	{
		out.writeDouble(this.real);
		out.writeDouble(this.imaginary);
	}

}