/**
 * 
 */
package pipe.common.queryresult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import pipe.common.PTNode;
import pipe.common.StringHelper;

/**
 * @author dazz
 * 
 */
public class FilePointsResultWrapper extends TextFileResultWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8777420664944453420L;
	XYCoordinates				points;

	public FilePointsResultWrapper(	final String fileName,
									final File resultsDir,
									final String numPattern,
									final String pointsPattern,
									final String nodeID,
									final PTNode type) throws FileNotFoundException, IOException {
		super(fileName, resultsDir, numPattern, nodeID, type);

		this.points = this.getValues(StringHelper.findSubStringPoints(this.getFileString(), pointsPattern));
	}

	/**
	 * @return the points
	 */
	public XYCoordinates getPoints()
	{
		return this.points;
	}

}
