package view;

import model.data_structures.TablaSimbolos;
import model.logic.Modelo;

public class View 
{
	    /**
	     * Metodo constructor
	     */
	    public View()
	    {
	    	
	    }
	    
		public void printMenu()
		{
			System.out.println("1. Cargar datos");
			System.out.println("2. Componentes conectados");
			System.out.println("3. Encontrar landings interconexión");
			System.out.println("4. Ruta mínima");
			System.out.println("5. Red de expansión mínima");
			System.out.println("6. Fallas en conexión");
			System.out.println("7. Exit");
			System.out.println("Dar el numero de opcion a resolver, luego oprimir tecla Return: (e.g., 1):");
		}

		public void printMessage(String mensaje) {

			System.out.println(mensaje);
		}		
		
		public void printModelo(Modelo modelo)
		{
			System.out.println(modelo);
		}
}
