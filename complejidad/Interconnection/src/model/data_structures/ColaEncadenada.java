package model.data_structures;

public class ColaEncadenada<T extends Comparable <T>> extends ListaEncadenada<T>
{
	public void enqueue(T element)
	{
		try {
			this.addLastCola(element);
		} catch (NullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public T dequeue()
	{
		T retorno=null;
		try 
		{
			retorno= this.deleteElement(1);
		} catch (PosException | VacioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retorno;
	}
	
	public T peek()
	{
		T retorno=null;
		try {
			retorno = this.getElement(1);
		} catch (PosException | VacioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retorno;
	}
}
