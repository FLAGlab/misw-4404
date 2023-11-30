package pipe.modules.steadyState;

import javax.swing.JList;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;
import pipe.modules.clientCommon.PetriNetBrowsePanel;

public class FileBrowserPanel extends PetriNetBrowsePanel
{
	private static final long serialVersionUID = 1L;
	
	public FileBrowserPanel(String title, DataLayer currNet){
		super(title, currNet);
	}

	  JList placeList = null, transitionList = null;
	    
	  public void setPlaceList(JList listName)
	  {
		  placeList = listName;
		  updateUIList();
	  }
	  
	  public void setTransitionList(JList listName)
	  {
		  transitionList = listName;
		  updateUIList();
	  }
	  
	  // This method updates the JList
	  protected void updateUIList()
	  {
		  if (placeList != null)
		  {
			  // Load the list of place names
			  String[] names = getPlaceNames();
			  
			  if (names != null)
				  placeList.setListData(names);
			  else
			  	  placeList.removeAll();	  
		  }
		  
		  if (transitionList != null)
		  {
			  // Load the list of transition names
			  String[] names = getTransitionNames();
			  
			  if (names != null)
				  transitionList.setListData(names);
			  else
				  transitionList.removeAll();		  
		  }	
	  }
	  
	  public String[] getPlaceNames()
	  {
		  int i;
		  String[] names = null;
		  
		  if (selectedNet != null)
		  {
			  Place[] places = selectedNet.getPlaces();
			  int length = places.length;
			  
			  names = new String[length];			
				
			  for (i=0; i< length; i++)
			  		names[i] = places[i].getName();
		  }
		  	  
		  return names;
	  }
	  
	  public String[] getTransitionNames()
	  {
		  int i;
		  String[] names = null;
		  
		  if (selectedNet != null)
		  {
			  Transition[] transitions = selectedNet.getTransitions(); 

			  int length = transitions.length;			  
			  names = new String[length];			
				
			  for (i=0; i< length; i++)
				  names[i] = transitions[i].getName();

		  }
		  	  
		  return names;
	  }

}
