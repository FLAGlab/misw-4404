/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import java.io.Serializable;

import pipe.common.PTNode;

/**
 * @author dazz
 * 
 */
public abstract class NodeAnalyser implements NodeAnalyserLoggingHandler, Serializable
{
	private final PTNode	type;

	protected NodeAnalyser(final PTNode type) {
		this.type = type;
	}

	public abstract ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException;

	public abstract boolean canEvaluate();

	protected abstract NodeAnalyser checkChildValid(final NodeAnalyser child) throws InvalidNodeAnalyserException;

	/**
	 * @return the type
	 */
	public PTNode getType()
	{
		return this.type;
	}
}
