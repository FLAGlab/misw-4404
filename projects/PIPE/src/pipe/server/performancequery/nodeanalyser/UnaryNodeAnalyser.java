/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PTNode;

/**
 * @author dazz
 * 
 */
public abstract class UnaryNodeAnalyser extends CalculationNodeAnalyser
{

	private final NodeAnalyser	child;

	protected UnaryNodeAnalyser(final PTNode type, final NodeAnalyser child) throws InvalidNodeAnalyserException {
		super(type);
		this.child = this.checkChildValid(child);
	}

	@Override
	public boolean canEvaluate()
	{
		return this.child.canEvaluate();
	}

	/**
	 * @return the child
	 */
	protected NodeAnalyser getChild()
	{
		return this.child;
	}
}
