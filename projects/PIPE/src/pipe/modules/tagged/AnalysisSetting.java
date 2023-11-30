package pipe.modules.tagged;

import java.io.Serializable;

/**
 * 
 * @author Barry Kearns
 * @date September 2007
 *
 */
public class AnalysisSetting implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public double startTime, endTime, timeStep;
	public String inversionMethod = null; //stores either lagurre or euler
	public int numProcessors;
	
	public AnalysisSetting(double start, double end, double step, String method, int processors)
	{
		startTime = start;
		endTime = end;
		timeStep = step;
		inversionMethod = method;
		numProcessors = processors;
	}

}
