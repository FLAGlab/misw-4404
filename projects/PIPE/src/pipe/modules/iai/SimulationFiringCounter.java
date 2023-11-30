/**
 * This is a placeholder for a class to be added as part of the
 * support of exponential distributions.
 * 
 * This will be a module to simulate a Petri Net based on a
 * counting firings (much like the original "Simulation" module
 * did. 
 * 
 * @author David Patterson 
 *
 */

package pipe.modules.iai;

import pipe.common.dataLayer.DataLayer;
import pipe.gui.CreateGui;
import pipe.modules.Module;

public class SimulationFiringCounter 
	implements Module
	
{
	private static final String MODULE_NAME = 
		"IAI - Simulation: Firing Counter";
	
	public SimulationFiringCounter() {
		// Nothing here.
	} // end of constructor for this class

	public void run( DataLayer pnmlData )
	{
		CreateGui.getApp().getStatusBar().changeText( 
		"ERROR: This simulation module is not implemented yet." );
	}

	public String getName()
	{
		return "Firing Counter Simulation (Not ready)";
	}
}
