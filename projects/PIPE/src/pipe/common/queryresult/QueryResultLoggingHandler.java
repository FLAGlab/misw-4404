/**
 * 
 */
package pipe.common.queryresult;

import java.util.logging.Logger;

/**
 * @author dazz
 * 
 */
interface QueryResultLoggingHandler
{
	public static final String	pipeCommonQResult	= "pipe.common.queryresult";
	public static Logger		logger				= Logger.getLogger(QueryResultLoggingHandler.pipeCommonQResult);
}
