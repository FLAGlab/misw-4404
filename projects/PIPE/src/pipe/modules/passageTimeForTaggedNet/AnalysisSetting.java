package pipe.modules.passageTimeForTaggedNet;

public class AnalysisSetting {


	private static final long serialVersionUID = 1L;
	
	public double startTime, endTime, timeStep;

	
	public AnalysisSetting(double start, double end, double step)
	{
		startTime = start;
		endTime = end;
		timeStep = step;

	}
}
