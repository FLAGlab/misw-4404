package pipe.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;

/**
 * This class is used to process clicks by the user to manually step 
 * through enabled transitions in the net. 
 * 
 * @author unspecified 	wrote this code
 * @author David Patterson fixed a bug with double-firing transitions
 *         in the doRandomFiring method. I also renamed the fireTransition
 *         method to recordFiredTransition to better describe what it does.
 *
 * @author Pere Bonet modified the recordFiredTransition method to
 * fix the unexpected behaviour observed during animation playback.
 * The method is renamed back to fireTransition. 
 * 
 * @author Edwin Chung fixed the bug where users can still step forward to previous
 * firing sequence even though it has been reset. The issue where an unexpected behaviour 
 * will occur when the firing sequence has been altered has been resolved. The problem where
 * animation will freeze halfway while stepping back a firing sequence has also been fixed (Feb 2007)  
 * 
 * @author Dave Patterson The code now outputs an error message in the status bar
 * if there is no transition to be found when picking a random transition to fire. This
 * is related to the problem described in bug 1699546. 
 */


public class Animator implements Constants{

  Timer timer;
  int numberSequences;
  
  public static ArrayList firedTransitions;
  public static int count= 0;
  
  public Animator(){
    firedTransitions = new ArrayList();
  
    
    timer = new Timer(0, new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if ((getNumberSequences()<1) ||
            !CreateGui.getView().animationmode) {
          timer.stop();
          CreateGui.getApp().setRandomAnimationMode(false);
          return;
        }
        doRandomFiring();
        setNumberSequences(getNumberSequences()-1);
      }    
    });
  }

  /* highlights enabled transitions*/
  
  public void highlightEnabledTransitions(){

	/* rewritten by wjk 03/10/2007 */
	  DataLayer current = CreateGui.currentPNMLData();
		current.setEnabledTransitions();
			 
    Iterator transitionIterator = current.returnTransitions();
    while(transitionIterator.hasNext()){
      Transition tempTransition =(Transition) transitionIterator.next(); 
      if(tempTransition.isEnabled(true)==true ){
      	current.notifyObservers();
        tempTransition.repaint();
      }
      
      if(tempTransition.isTaggedEnabled(true)==true){
    	  
    	 // System.out.println("check");
    	  
      }
    }
    
  }

    /* called during animation to unhighlight previously highlighted transitions*/
  public void unhighlightDisabledTransitions(){
	DataLayer current = CreateGui.currentPNMLData();
	current.setEnabledTransitions();
			  	  
    Iterator transitionIterator = current.returnTransitions();
    while(transitionIterator.hasNext()){
      Transition tempTransition =(Transition) transitionIterator.next();
      if(tempTransition.isEnabled(true)==false){
      	current.notifyObservers();
        tempTransition.repaint();
      }
      /*
      if(tempTransition.isTaggedEnabled(false)==false){
        	current.notifyObservers();
            tempTransition.repaint();
          }*/
    }
  }

    /* called at end of animation and resets all Transitions to false and unhighlighted*/
  public void disableTransitions(){
    Iterator transitionIterator = CreateGui.currentPNMLData().returnTransitions();
    while(transitionIterator.hasNext()){
      Transition tempTransition =(Transition) transitionIterator.next();
      tempTransition.setEnabledFalse();
      tempTransition.setTaggedEngabledFalse();
      CreateGui.currentPNMLData().notifyObservers();
      tempTransition.repaint();
    }
  }

    /* stores model at start of animation*/
  public void storeModel(){
  	CreateGui.currentPNMLData().storeState();
  }

    /*restores model at end of animation and sets all transitions to false and unhighlighted*/
  public void restoreModel(){
  	CreateGui.currentPNMLData().restoreState();
    disableTransitions();
  }

  public void startRandomFiring(){
    if(getNumberSequences()>0) {
      // stop animation
      setNumberSequences(0);
      return;
    } else try {
      String s = JOptionPane.showInputDialog("Enter number of firings to perform", "1");
      this.numberSequences=Integer.parseInt(s);
      s = JOptionPane.showInputDialog("Enter time delay between firing /ms", "500");
      timer.setDelay(Integer.parseInt(s));
      timer.start();
    } catch (NumberFormatException e) {
      CreateGui.getApp().setRandomAnimationMode(false);
    }
  }
  
  public void stopRandomFiring() {
    numberSequences=0;
  }
  
  /**
   * This method randomly fires one of the
   * enabled transitions. It then records the
   * information about this by calling the 
   * recordFiredTransition method.
   * 
   * @author Dave Patterson Apr 29, 2007
   * I changed the code to keep the random transition found by the DataLayer.
   * If it is not null, I call the fireTransition method, otherwise I put 
   * out an error message in the status bar. 
   * 
   * 
   * June 20, 2008
   * Fixed the bug with double firing and program freezes when there
   * is no enabled transition. random firing with tagged token is also integrated
   */

 public void doRandomFiring() {
    //DataLayer data=CreateGui.currentPNMLData();
    // data.setEnabledTransitions();
    Transition t=CreateGui.currentPNMLData().fireRandomTransition();
    
    /*do clearStepsForward so that when it revisits original marking place, 
     * the history starts over
     */
    CreateGui.getAnimationHistory().clearStepsForward();
    //removeStoredTransitions();
    if(t!=null)
   	{
		// determine whether to fire tagged or nontagged token
		if(t.isEnabled() && !t.isTaggedTokenEnabled())
			this.fireTransition(t);
		
		else if(!t.isEnabled() && t.isTaggedTokenEnabled())
			this.fireTaggedTransition(t);
		
		else if(t.isEnabled() && t.isTaggedTokenEnabled())
		{
			//final double tagged_rate = t.getRateTagged();
			//final double untagged_rate = t.getRate();
			
			
			final double untagged_rate = t.getRate();
			double max = 0;
			
			Iterator arcsFrom = t.getConnectToIterator();
			while(arcsFrom.hasNext()){
				
				
				final Arc arc = (Arc)arcsFrom.next();
				System.out.println("\n arc source "+arc.getSource().getId());
				final Place place = (Place)arc.getSource();
				if(place.isTagged())
				{	
					System.out.println("weight tagged arc="+arc.getWeight()
							+" current marking="+place.getCurrentMarking());
					
					max = (double)arc.getWeight() / (double)place.getCurrentMarking();
				
				}
				 System.out.println("\n***"+place.getId()+"  max = "+max);
				
			}
			
			
			final double rateOfTaggedMode = max*untagged_rate;
			final double rateOfUntaggedMode = untagged_rate - rateOfTaggedMode;
			
			final double prob_t = rateOfTaggedMode/(rateOfTaggedMode + rateOfUntaggedMode);
			final double prob_u = rateOfUntaggedMode /(rateOfTaggedMode + rateOfUntaggedMode);
			
			System.out.println("\n probt = "+prob_t+" probu="+prob_u);
			
			//final double sum = (tagged_rate+untagged_rate);
			//final double prob_t = (tagged_rate/sum);
			//final double prob_u = untagged_rate/sum;
			
			double random = DataLayer.randomNumber.nextDouble();
	
			System.out.println("\n random ="+ random);
			
			/*
			while(random > 1 ){
				random/=sum;
				System.out.println("\n ="+ random);
			}*/

			if(random<prob_t)this.fireTaggedTransition(t);
			else this.fireTransition(t);

		}
   	}//end if(t!=null)
    else
    {
    	CreateGui.getApp().getStatusBar().changeText( 
    			"ERROR: No transition to fire." );
    }//end else
  }//end method
  

  /*steps back through previously fired transitions*/
  public void stepBack(){
    if(count>0){
    	
    	System.out.println("\ncount = "+count+" firedTransitions size = "+this.firedTransitions.size());
    	
    	Transition lastTransition = (Transition)firedTransitions.get(count-1);
    	count--;
    	
    	
    	CreateGui.currentPNMLData().fireTransitionBackwards(lastTransition);
    	CreateGui.currentPNMLData().setEnabledTransitions();
    	unhighlightDisabledTransitions();
      	highlightEnabledTransitions();
    }
    else return;
  }

    /* steps forward through previously fired transitions*/
  public void stepForward(){
    if(count < firedTransitions.size()){
    	
    	System.out.println("\n****");
    	for(int i=0;i<firedTransitions.size();i++)
    		System.out.println( ( (Transition) firedTransitions.get(i)).getId() );
    	
    	System.out.println("\ncount = "+count+" firedTransitions size = "+this.firedTransitions.size());
    	
      Transition nextTransition = (Transition)firedTransitions.get(count);
      count++;
      
      System.out.println("\nnextTransition = "+nextTransition.getId());
      
      CreateGui.currentPNMLData().fireTransitionForward(nextTransition);
      CreateGui.currentPNMLData().setEnabledTransitions();
      unhighlightDisabledTransitions();
      highlightEnabledTransitions();
    }
    return;
  }

  /* This method keeps track of a fired transition in the 
   * AnimationHistory object, enables transitions after the
   * recent firing, and properly displays the transitions.
   * 
   * @author David Patterson renamed this method and changed
   * the AnimationHandler to make it fire the transition before
   * calling this method. This prevents double-firing a 
   * transition.
   * 
   * @author Pere Bonet modified this method so that it now stores transitions 
   * that has just been fired in an array so that it can be accessed during backwards and 
   * forwards stepping to fix the unexcepted behaviour observed during animation playback
   * The method is renamed back to fireTransition.
   * 
   *  
   */
  public void fireTransition(Transition transition){
	    Animator animator = CreateGui.getAnimator();
    CreateGui.getAnimationHistory().addHistoryItem(transition.getName());
    CreateGui.currentPNMLData().fireTransition(transition);
    CreateGui.currentPNMLData().setEnabledTransitions();
    animator.highlightEnabledTransitions();
    animator.unhighlightDisabledTransitions();
    
    System.out.println("\ncount = "+ count);
    
    if (count == firedTransitions.size()) {
        firedTransitions.add(transition);
        count++;
     } else { 
    	  firedTransitions.set(count, transition);
    	  count++;
       	  removeStoredTransitions();
     }  
    
    //count++;
     }		
 /*
  * method to fire tagged token on the enabled taggedTokenTransition
  * 
  */
  public void fireTaggedTransition(Transition transition){
	    Animator animator = CreateGui.getAnimator();
  CreateGui.getAnimationHistory().addHistoryItem(transition.getName()+" fires TAG");
  CreateGui.currentPNMLData().fireTaggedTransition(transition);
  CreateGui.currentPNMLData().setEnabledTransitions();
  animator.highlightEnabledTransitions();
  animator.unhighlightDisabledTransitions();
  
  
  if (count == firedTransitions.size()) {
      firedTransitions.add(transition);
      count++;
   } else { 
	   
	   
  	  firedTransitions.set(count, transition);
  	      count++;
     	  removeStoredTransitions();
   }  
  
   //count++;

   
   
   }		
  
  
  /*
   * modified so that it completely removes all the entries except
   * the current one
   */
  private void removeStoredTransitions() {
	  
	  while( firedTransitions.size()>count){
  	  for (int count1 = count ; count1 < firedTransitions.size(); count1++)
   		  firedTransitions.remove(count1);
  	  
  	  for(int i=0;i<firedTransitions.size();i++)
  		  System.out.println( ( (Transition) firedTransitions.get(i)).getId() );
  	  
	  }
  	  
  	  
	  
  }
  
  public synchronized int getNumberSequences() {
    return numberSequences;
  }
  public synchronized void setNumberSequences(int numberSequences) {
    this.numberSequences = numberSequences;
  }
}
