/*
 * Created on 25-Jul-2005
 */
package pipe.io;

import java.io.IOException;

/**
 * @author Nadeem
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IncorrectFileFormatException extends IOException {
	public IncorrectFileFormatException(String format){
		super("The specified file is not an " + format + ".");
	}

}
