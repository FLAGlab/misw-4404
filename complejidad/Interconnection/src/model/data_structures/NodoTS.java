package model.data_structures;

import java.util.Comparator;

public class NodoTS<K extends Comparable<K>, V extends Comparable <V>> implements Comparable<NodoTS<K,V>>
{
	protected K llave;
	protected V valor;
	
	public NodoTS(K llave, V valor)
	{
		this.llave= llave;
		this.valor=valor; 
		
	}

	public int compareTo(NodoTS<K, V> o) 
	{
		return this.llave.compareTo(o.getKey());
	}
	
	public K getKey()
	{
		return llave;
	}
	
	public V getValue()
	{
		return valor;
	}
	
	public void setKey(K key)
	{
		llave=key;
	}
	
	public void setValue(V value)
	{
		valor=value;
	}

	public void setEmpty() 
	{
		this.llave=null;
	}
	
	public boolean isEmpty()
	{
		return this.llave==null;
	}
	

}
