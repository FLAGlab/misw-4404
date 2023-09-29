package model.data_structures;

public class Edge <K extends Comparable<K>, V extends Comparable <V>> implements Comparable <Edge<K, V>>
{
	private Vertex<K, V> source;
	private Vertex<K, V> destination;
	private float weight;
	public Edge(Vertex<K,V> source, Vertex<K,V>destination, float weight)
	{
		this.source= source;
		this.destination= destination;
		this.weight= weight;
	}
	
	public Vertex<K,V> getSource()
	{
		return source;
	}
	
	public Vertex<K,V> getDestination()
	{
		return destination;
	}
	
	public float weight()
	{
		return weight;
	}
	
	public void setWeight(float weight)
	{
		this.weight= weight;
	}
	
	public float getWeight()
	{
		return weight;
	}

	@Override
	public int compareTo(Edge o) 
	{
		// TODO Auto-generated method stub
		return 0;
	}
}

