package pipe.dataLayer.calculations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import pipe.common.dataLayer.DynamicMarking;

/**
 * Class that handles the run-time creation of a DynamicMarkingImpl class that
 * implements the DynamicMarking interface, according to logical expressions entered
 * by the user. It also compiles and loads the class.
 * 
 * @author Oliver Haggarty
 *
 */
public class DynamicMarkingCompiler {
	private String DynamicMarkingImplFile;

	private String buildFolder;

	private String templateFile;

	private String templateFolder;

	private static String fileSep = System.getProperty("file.separator");
	
	/**
	 * Creates correct string names for the various files needed
	 */
	public DynamicMarkingCompiler() {

		/*StringBuilder sb = new StringBuilder("DynamicCode");
		buildFolder = sb.toString();*/
		StringBuilder sb = new StringBuilder("RTAResources");
		sb.append(fileSep);
		sb.append("DynamicCode");
		buildFolder = sb.toString();
		sb.append(fileSep);
		sb.append("pipe");
		sb.append(fileSep);
		sb.append("common");
		sb.append(fileSep);
		sb.append("dataLayer");
		sb.append(fileSep);
		templateFolder = sb.toString();
		sb.append("DynamicMarkingImpl.java");
		templateFile = templateFolder + "template.java";
		DynamicMarkingImplFile = sb.toString();

		templateFile = (new File(templateFile).getAbsolutePath()).toString();
		DynamicMarkingImplFile = (new File(DynamicMarkingImplFile).getAbsolutePath()).toString();
		buildFolder = (new File(buildFolder).getAbsolutePath()).toString();
		templateFolder = (new File(templateFolder).getAbsolutePath()).toString();
		
	}
	
	/**
	 * Compiles the DynamicMarkingImpl.java file
	 */
	public void compileDynamicMarking() {
		int errorCode = com.sun.tools.javac.Main.compile(new String[] {
	            "-classpath", buildFolder,
	            "-d", buildFolder,
	            DynamicMarkingImplFile });
		//System.out.println("Errorcode = " + errorCode);
	}
	
	/**
	 * Returns an instance of the DynamicMarkingImpl file most recently compiled
	 * @return DynamicMarking
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws MalformedURLException
	 */
	public DynamicMarking getDynamicMarking() throws ClassNotFoundException, 
				InstantiationException, IllegalAccessException, MalformedURLException {
		// The dir contains the compiled classes.
	    File classesDir = new File(buildFolder);

	    // The parent classloader
	    ClassLoader parentLoader = DynamicMarking.class.getClassLoader();

	    
	    // Load class "sample.PostmanImpl" with our own classloader.
	    URLClassLoader loader1 = new URLClassLoader(
	            new URL[] { classesDir.toURL() }, parentLoader);
	    Class cls1 = loader1.loadClass("pipe.common.dataLayer.DynamicMarkingImpl");
	    DynamicMarking dynMark = (DynamicMarking) cls1.newInstance();
	    return dynMark;	    	    
	}
	
	/**
	 * Creates a copy of template.java called DynamicMarkingImpl.java with startExp
	 * and targetExp added at the correct position in the code
	 * 
	 * @param startExp A logical expression describing a start state
	 * @param targetExp A logical expression describing a target state
	 */
	public void setLogicalExpression(String startExp, String targetExp) {
		//Add the logical expressions to an if statement
		String fullStartExp = addIf(startExp);
		String fullTargetExp = addIf(targetExp);
		//Connect to the template file and the output file
		File dynMf = new File(DynamicMarkingImplFile);
		File tempf = new File(templateFile);
		BufferedWriter dynMFile = null;
		BufferedReader tempFile = null;
		try {
			dynMFile = new BufferedWriter(new FileWriter(dynMf));
			tempFile = new BufferedReader(new FileReader(tempf));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//Copy the template file's contents to the output file's, but look
		//for the placeholder comments in the source code for the two expressions.
		//Add the full expressions after each.
		String input = null;
		try {
			while((input = tempFile.readLine()) != null) {
				if(input.equals("//#$#ADDTARGETEXPRESSIONHERE")) {
					//System.out.println(fullTargetExp);
					dynMFile.write(fullTargetExp);
					dynMFile.write("\n");
				}
				else if(input.equals("//#$#ADDSTARTEXPRESSIONHERE")) {
					//System.out.println(fullStartExp);
					dynMFile.write(fullStartExp);
					dynMFile.write("\n");
				}
				else {
					dynMFile.write(input);
					dynMFile.write("\n");
				}
			}
			tempFile.close();
			dynMFile.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Returns the logical expression wrapped in an if(logicExp) statement
	 * @param logicalExp
	 * @return
	 */
	private String addIf(String logicalExp) {
		StringBuilder sb = new StringBuilder("\tif(");
		sb.append(logicalExp);
		sb.append(") ");
		return sb.toString();
	}		
}
