/*
 * Created on 01-Jul-2005
 */
package pipe.dataLayer.calculations;

/**
 * @author Nadeem
 *
 * This class is used for recording vanishing states while
 * generating the state space of a GSPN
 * 
 */
public class VanishingState extends State {
	
	double rate;
	
	VanishingState(int[] new_state, double initial_rate){
		super(new_state);
		setRate(initial_rate);
	}
	
	VanishingState(State new_state, double initial_rate){
		super(new_state);
		setRate(initial_rate);
	}
	
	public void setRate(double new_rate){
		rate = new_rate;
	}
	
	public double getRate(){
		return rate;
	}

}
