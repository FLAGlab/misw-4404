package model.data_structures;

public interface ITablaSimbolos <K extends Comparable<K>,V extends Comparable <V>> 
{
	public void put(K key , V value);
	
	public V get (K key);
	
	public V remove(K key);
	
	public boolean contains (K key );
	
	public boolean isEmpty ( );
	
	public int size ( ) ;
	
	public ILista<K> keySet();
	
	public ILista<V> valueSet();
	
	public ILista<NodoTS<K, V>> darListaNodos();
	
	public int hash(K key);
	
}
