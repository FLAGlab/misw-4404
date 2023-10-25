package cars;

public class Car {
	private Motor motor;
	private Dashboard dashboard;
	
	public Car(Motor motor, Dashboard dashboard) {
		this.motor = motor;
		this.dashboard = dashboard;
	}
	
	public void accelerate() {
		this.motor.rpm += 100;
		this.motor.speed += 10;
		this.motor.oilLevel -= 0.1;
		this.motor.gasLevel -= 0.5;
	}
	
	public void stop() {
		this.motor.rpm -= 0;
		this.motor.speed -= 0;
		this.motor.oilLevel -= 0.1;
		this.motor.gasLevel -= 0;
	}
	
	public static void main(String[] args) {
		Motor motor = new Motor();
		Dashboard dashboard = new Dashboard(motor);
		Car car = new Car(motor, dashboard);
		
		dashboard.printDashboard();
		car.accelerate();
		dashboard.printDashboard();
		car.stop();
		dashboard.printDashboard();
	}
}
