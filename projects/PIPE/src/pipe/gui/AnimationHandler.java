package pipe.gui;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import pipe.common.dataLayer.Transition;

/**
 * This class handles mouse clicks by the user. 
 * 
 * @author unknown 
 * @author David Patterson
 * 
 * Change by David Patterson was to fire the selected 
 * transition in the DataLayer, and then record the firing
 * in the animator.
 * 
 * @author Pere Bonet reverted the above change.
 */

public class AnimationHandler extends MouseInputAdapter {
   

   public void mouseClicked(MouseEvent e){
      Transition transition = (Transition)e.getComponent();
      if((e.getButton() == MouseEvent.BUTTON1) && (transition.isEnabled(true))) {
         CreateGui.getAnimationHistory().clearStepsForward();
         CreateGui.getAnimator().fireTransition(transition);
         CreateGui.getApp().setRandomAnimationMode(false);
      }
   }
}
