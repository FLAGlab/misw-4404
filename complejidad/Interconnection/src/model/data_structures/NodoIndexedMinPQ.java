package model.data_structures;

public class NodoIndexedMinPQ<K extends Comparable<K>, IK extends Comparable<IK>, V extends Comparable <V>> extends NodoTS<K, V>
{
	private IK llaveIndexacion;
	
	public NodoIndexedMinPQ(K llave, IK llaveIndexacion, V valor )
	{
		super(llave, valor);
		this.llaveIndexacion= llaveIndexacion;
	}
	
	public IK getIndexedKey()
	{
		return llaveIndexacion;
	}
}

