/*
 * Created on 15-Jul-2005
 */
package pipe.io;

/**
 * @author Nadeem
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImmediateAbortException extends Exception {
	public ImmediateAbortException(){
		super("Generate method could not carry out file io.");
	}
	public ImmediateAbortException(String reason){
		super(reason);
	}

}
