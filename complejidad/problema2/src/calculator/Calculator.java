package calculator;

import java.util.ArrayList;
import java.util.Random;

public class Calculator {

	class Course {

		String name;
		int credits;
		double mark;

		public Course(String name, int credits, double mark) {
			this.name = name;
			this.credits = credits;
			this.mark = mark;
		}

		public int getCredits() {
			return this.credits;
		}

		public double courseMark() {
			return this.mark;
		}

		public String courseName() {
			return this.name;
		}
	}



	public String getAverage(ArrayList<Course> courses) {
		int sum = 0;
		int credits = 0;

		for (int i = 0; i < courses.size(); i++) {
			Course c = courses.get(i);
			sum += courses.get(i).courseMark() * courses.get(i).getCredits();
			credits += courses.get(i).getCredits();
		}

		double average = sum / credits;
		String result = "";
		//todo cambiar los ifs
		if (average < 3) {
			result = "No aprobado";
			if (average == 1.5) {
				result += ", nota minima";
			}
			else {
				result += ", insuficiente";
			}
		}
		else {
			result = "Aprobado";
			if (average < 3.5) {
				result += ", suficiente";
			}
			else if (average < 4) {
				result += ", satisfactorio";
			}
			else if (average < 4.5) {
				result += ", bueno";
			}
			else {
				result += ", excelente";
			}
		}

		return result;
	}	

	public static void main(String[] args) {
		Calculator calculator = new Calculator();
		Calculator.Course math = calculator.new Course("Matematicas", 3, 5.0);
		Calculator.Course physics = calculator.new Course("Fisica", 3, 4.5);
		Calculator.Course chem = calculator.new Course("Quimica", 2, 3.0);
		Calculator.Course programming = calculator.new Course("Programacion", 3, 5.0);   
		ArrayList<Calculator.Course> courses = new ArrayList<Calculator.Course>();
		Random rand = new Random();
		for(int i=0; i < rand.nextInt(10); i++) {
			switch(i) {
			case 1: courses.add(math); break;
			case 2: courses.add(physics); break;
			case 3: courses.add(chem); break;
			case 4: courses.add(programming); break;
			default: courses.add(calculator.new Course("CBU", 2, rand.nextDouble(5.0)));
			}
			
		}
		System.out.println(calculator.getAverage(courses));

	}
}
