package pipe.gui;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import pipe.common.dataLayer.DataLayer;

/* $Author: dkb03 $
 * $Date: 2008/02/08 23:32:19 $ */

/**
 * ModuleMethod encapsulates information about a module method  and is designed to be used as a userobject in nodes in a JTree.
 * In this case for nodes representing module methods.
 */

public class ModuleMethod
{
	private Method modMeth;
	private Class modClass;
	private String name;

	/* Sets up the Class and Method that this class encapsulates
	 * @param cl The Class that the Method belongs to
	 * @param m The Method that this class represents
	 */
	public ModuleMethod(Class cl,Method m) {
		modClass = cl;
		modMeth = m;
		name=m.getName();
	}

	/** Returns the name of the modMeth */
	public String toString() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}


	/** Executes the Method that this class represents.
	 * @param data The dataLayer object that will be passed as an argument to the method.
	 */
	protected void execute(DataLayer data) {
		Object[] args = {data};
		try {
			Constructor ct = modClass.getDeclaredConstructor(new Class[0]);
			Object moduleObj = ct.newInstance(new Object[0]);

			// handy debug to see what's being passed to the module
			//System.out.println("dataLayer obj being passed to module: ");
			//args[0].print();

			// invoke the name method for display
			modMeth.invoke(moduleObj, args);
		}
		catch (Exception e)
		{
			System.out.println("Error in module method invocation:" + e.toString());
			e.printStackTrace();
		}

	}
	/**
	 * @return Returns the modClass.
	 */
	public Class getModClass() {
		return modClass;
	}
}



