package model.data_structures;

public class Nodo<T> {
	
	private T elemento;
	
	private Nodo<T> next; 
	
	public Nodo(T elemento)
	{
		this.elemento=elemento;
		
	}

	public T getInfo()
	{
		return elemento;
	}
	
	public Nodo<T> getNext()
	{
		return next;
	}
	
	public void setNext( Nodo<T> nodoNext)
	{
		next=nodoNext;
	}
	
	public void disconnectNext(Nodo<T> nodo)
	{
		if (next!=null && next.getNext()!=null)
		{
			next=next.getNext();
		}

		else if ( next!=null && next.getNext()==null)
		{
			next=null;
		}

	}
	
	public void change(T nuevoElemento)
	{
		elemento=nuevoElemento;
	}
}
