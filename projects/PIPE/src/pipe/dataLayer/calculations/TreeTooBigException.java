/*
 * Created on Mar 11, 2004
 *
 */
package pipe.dataLayer.calculations;

/**
 * @author Matthew
 *
 * 
 */
public class TreeTooBigException extends Exception {
	TreeTooBigException() {
	super("The state-space tree for this net has more than 10,000 nodes.  DNAMACA might be a more appropriate tool for this analysis");
	}
}
