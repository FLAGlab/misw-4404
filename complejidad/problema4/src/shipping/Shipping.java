package shipping;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shipping {
	
	public Shipping() {
		super();
	}
	
	public int calcularCostoEnvio(String pais, int peso) {
		int costo = -1;
		if(pais.equalsIgnoreCase("Argentina")) {
			if(peso <= 5)
				costo = 100;
			else
				costo = 100 + (peso - 5) * 10;
		} else if(pais.equalsIgnoreCase("Brasil")) {
			if(peso <= 5) {
				costo = 200;
			} else {
				costo = 200 + (peso - 5) * 20;
			}
		} else if(pais.equalsIgnoreCase("Chile")) {
			if(peso <= 5) {
				costo = 300;
			} else {
				costo = 300 + (peso - 5) * 30;
			}
		} else if(pais.equalsIgnoreCase("Uruguay")) {
			if(peso <= 5) 
				costo = 400;
			else 
				costo = 400 + (peso - 5) * 40;
		}
		
		return costo;
	}
	
	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			int peso = Integer.parseInt(in.readLine());
			String pais = in.readLine();
			
			Shipping ship = new Shipping();
			int costo = ship.calcularCostoEnvio(pais, peso);
			if(costo == -1) {
				System.out.println("El pais ingresado no es valido");
			} else {
				System.out.println("El costo del envio es: " + costo);
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
 }
