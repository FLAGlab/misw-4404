/**
 * 
 */
package pipe.common.queryresult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import pipe.common.PTNode;
import pipe.common.ServerConstants;

/**
 * @author dazz
 * 
 */
public class PercentileResultWrapper extends PointsResultWrapper implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8107307676047666689L;
	final double				percentile;

	public PercentileResultWrapper(	final double percentile,
									final File resultsDir,
									final String nodeID,
									final PTNode type)	throws FileNotFoundException,
														UnexpectedResultException,
														IOException {
		super(	ServerConstants.cdfResultsFileName,
				resultsDir,
				ServerConstants.percentileNumResultPattern,
				ServerConstants.percentileResultsFileName,
				nodeID,
				type);
		if (type != PTNode.PERCENTILE)
		{
			throw new UnexpectedResultException(type + " not supported for PercentileResultWrapper");
		}
		this.percentile = percentile;
	}

	/**
	 * @return the percentile
	 */
	public double getPercentile()
	{
		return this.percentile;
	}

}
