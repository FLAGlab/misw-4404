package cars;

public class Dashboard {

	Motor motor;
	
	public Dashboard(Motor motor) {
		this.motor = motor;
	}
	
	public void printDashboard() {
		System.out.println("--------------------------------");
		System.out.println("DASHBOARD:");
		System.out.println("\t RPM: " + this.motor.rpm);
		System.out.println("\t Speed: " + this.motor.speed);
		System.out.println("\t Oil level: " + this.motor.oilLevel);
		System.out.println("\t Gas level: " + this.motor.gasLevel);
	}
}
