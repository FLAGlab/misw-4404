//package main;
//
//
//	import java.util.Comparator;
//	import java.util.concurrent.ThreadLocalRandom;
//
//	import model.data_structures.ArregloDinamico;
//	import model.data_structures.ILista;
//import model.data_structures.NullException;
//import model.data_structures.PosException;
//import model.data_structures.VacioException;
//import utils.Ordenamiento;
//
//	public class Prueba 
//	{
//		public static void main(String[] args) throws PosException, VacioException, NullException 
//		{
//			ArregloDinamico<Integer> arr = new ArregloDinamico<Integer>(200);
//			for(int i = 0; i < 350000; i++)
//			{
//				arr.addLast(ThreadLocalRandom.current().nextInt(0, 200000000));
//			}
//			//System.out.println(arr);
//			System.out.println(arr.size());
//			ILista<Integer> copy = arr.sublista(1);
//			Ordenamiento<Integer> ord = new Ordenamiento<Integer>();
//			Comparator<Integer> comp = new ComparadorInteger();
//			ord.ordenarShell(arr, comp, true);
//			//System.out.println(arr);
//			System.out.println(arr.size());
//			boolean funciono = true;
//			for(int i = 1; i < arr.size() && funciono; i++)
//			{
//				int factor = comp.compare(arr.getElement(i), arr.getElement(i+1) );
//				if(factor > 0)
//					funciono = false;
//				
//				int countCopy = cantidad(copy, arr.getElement(i), comp);
//				int countActual = cantidad(arr, arr.getElement(i), comp);
//				if(countCopy != countActual)
//				{
//					funciono = false;
//					System.out.println(arr.getElement(i) + ": " + countActual + " - " + countCopy);
//				}
//					
//			}
//			
//			int countCopy = cantidad(copy, arr.getElement(arr.size()), comp);
//			int countActual = cantidad(arr, arr.getElement(arr.size()), comp);
//			if(countCopy != countActual)
//			{
//				funciono = false;
//				System.out.println(arr.getElement(arr.size()) + ": " + countActual + " - " + countCopy);
//			}
//			
//			System.out.println(funciono? "Funcionó el experimento": "Falló");
//		}
//		
//		public final static int cantidad(ILista<Integer> lista, Integer probar, Comparator<Integer> comp) throws PosException, VacioException
//		{
//			int count = 0;
//			for(int i = 1; i <= lista.size(); i++)
//			{
//				if(comp.compare(probar, lista.getElement(i)) == 0)
//					count ++;
//			}
//			return count;
//		}
//		
//		
//		public static class ComparadorInteger implements Comparator<Integer>
//		{
//
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				return o1.compareTo(o2);
//			}
//			
//		}
//		
//		
//	}

