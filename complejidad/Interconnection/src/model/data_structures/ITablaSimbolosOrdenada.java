package model.data_structures;

public interface ITablaSimbolosOrdenada<K extends Comparable <K>, V extends Comparable<V>> extends ITablaSimbolos<K, V>
{
	public K min();
	
	public K max();
	
	public int height();
	
	public int getHeight();
	
	public ILista<K> keysInRange(K init, K end);
	
	public ILista<V> valuesInRange(K init, K end);

}
