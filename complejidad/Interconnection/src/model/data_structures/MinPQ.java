package model.data_structures;

public class MinPQ<K extends Comparable<K> ,V extends Comparable <V>>
{
	protected ILista<NodoTS<K, V>> arbol;
	
	protected int tamano;
	
	public MinPQ(int inicial)
	{
		arbol= new ArregloDinamico<NodoTS<K, V>>(inicial);
		tamano=0;
	}
	
	public void swim(ILista<NodoTS<K, V>> lista, int pos)
	{
		boolean swimTerminado=false;
		while(pos>1 && !swimTerminado)
		{
			try {
				NodoTS<K, V> papa= lista.getElement(pos/2);
				NodoTS<K, V> actual= lista.getElement(pos);
				if(papa.getKey().compareTo(actual.getKey())>0)
				{
					lista.exchange(pos/2, pos);
				}
				else
				{
					swimTerminado=true;
				}
				pos=pos/2;
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void insert(K key, V value)
	{
		try 
		{
			arbol.insertElement(new NodoTS<K, V>(key, value), arbol.size()+1);
			tamano++;
			swim(arbol, tamano);
		} 
		catch (PosException | NullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public int size()
	{
		return tamano;
	}
	
	public NodoTS<K, V> min()
	{
		try {
			if(tamano > 1) 
			{
				return arbol.getElement(1);
			}
			else 
			{
				return null;
			}
		} catch (PosException | VacioException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void sink(ILista<NodoTS<K, V>> lista, int pos)
	{
		int size= lista.size();
		
		boolean sinkCompleto=false;
		while(2*pos<=size && sinkCompleto==false)
		{
			int hizq=2*pos;
			int hder=hizq +1;
			
			int posMenor=pos;
			
			try 
			{
				if(lista.getElement(posMenor).compareTo(lista.getElement(hizq))>0)
				{
					posMenor=hizq;
					
				}
				if(hder<=size && lista.getElement(posMenor).compareTo(lista.getElement(hder))>0)
				{
					posMenor=hder;
				}
				if(posMenor==pos)
				{
					sinkCompleto=true;
				}
				else
				{
					lista.exchange(pos, posMenor);
					pos=posMenor;
				}
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public NodoTS<K, V> delMin()
	{
		NodoTS<K, V> retornar=null;
		if(tamano>1)
		{
			try {
				arbol.exchange(1, tamano);
				retornar=arbol.removeLast();
				tamano--;
				sink(arbol, 1);

			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(tamano>0)
		{
			retornar=arbol.removeLast();
			tamano--;
		}
		
		return retornar;
	}
	
	public boolean isEmpty()
	{
		return tamano==0;
	}
	
	
}
