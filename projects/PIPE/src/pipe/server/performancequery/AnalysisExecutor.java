/**
 * 
 */
package pipe.server.performancequery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.ggf.drmaa.DrmaaException;

import pipe.common.queryresult.ResultWrapper;
import pipe.common.queryresult.UnexpectedResultException;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;
import pipe.server.performancequery.structure.ParentSubtree;

/**
 * @author dazz
 * 
 */
public abstract class AnalysisExecutor implements Callable<ResultWrapper>, ServerLoggingHandler
{

	protected final ParentSubtree	subtree;
	private final ResultSender		sender;

	public AnalysisExecutor(final ParentSubtree subtree, final ResultSender resultSender) {
		this.subtree = subtree;
		this.sender = resultSender;
	}

	public final ResultWrapper call()	throws IOException,
										UnexpectedResultException,
										InvalidNodeAnalyserException,
										QueryServerException,
										DrmaaException,
										InterruptedException,
										ExecutionException
	{
		this.subtree.inProgress();

		ResultWrapper r = null;
		for (final ResultWrapper result : this.doCall())
		{
			this.sender.sendObject(result);
			r = result;
		}

		this.subtree.evaluated();
		return r;
	}

	public abstract ArrayList<ResultWrapper> doCall()	throws IOException,
														UnexpectedResultException,
														InvalidNodeAnalyserException,
														QueryServerException,
														DrmaaException,
														InterruptedException,
														ExecutionException;
}
