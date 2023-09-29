package model.data_structures;

import java.util.Comparator;

public class Vertex<K extends Comparable<K>,V  extends Comparable <V>> implements Comparable<Vertex<K, V>>
{
	private K key;
	private V value;
	private ILista<Edge<K, V>> arcos;
	private boolean marked;
	
	public Vertex(K id, V value)
	{
		this.key=id;
		this.value=value;
		this.arcos= new ArregloDinamico<Edge<K, V>>(1);
	}

	
	public K getId()
	{
		return key;
	}
	
	public V getInfo()
	{
		return value;
	}
	
	public boolean getMark()
	{
		return marked;
	}
	
	public void addEdge( Edge<K,V> edge )
	{
		try {
			arcos.insertElement(edge, arcos.size() +1);
		} catch (PosException | NullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void mark()
	{
		marked=true;
	}
	
	public void unmark()
	{
		marked=false;
	}
	
	public int outdegree()
	{
		return arcos.size();
	}
	
	public int indegree() 
	{
		return arcos.size();
	}
	
	public Edge<K,V> getEdge(K vertex)
	{
		Edge<K,V> retorno=null;
		for(int i=1; i<=arcos.size(); i++)
		{
			try 
			{
				if(arcos.getElement(i).getDestination().getId().compareTo(vertex)==0)
				{
					retorno= arcos.getElement(i);
				}
			} 
			catch (PosException | VacioException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return retorno;
	
	}
	
	public ILista<Vertex<K,V>> vertices()
	{
		ILista<Vertex<K,V>> retorno=new ArregloDinamico<>(1);
		for(int i=1; i<=arcos.size(); i++)
		{
			try {
				retorno.insertElement(arcos.getElement(i).getDestination(), retorno.size()+1);
			} catch (PosException | NullException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return retorno; 
	}
	
	public ILista<Edge<K,V>> edges()
	{
		return arcos;
	}
	
	public void bfs()
	{
		ColaEncadenada<Vertex<K, V>> cola= new ColaEncadenada<Vertex<K, V>>();
		mark();
		cola.enqueue(this);
		while(cola.peek() !=null)
		{
			Vertex<K, V> actual= cola.dequeue();
			for(int i=1; i<=actual.arcos.size(); i++)
			{
				Vertex<K, V> dest;
				try 
				{
					dest = actual.edges().getElement(i).getDestination();
					if(dest.marked)
					{
						mark();
						cola.enqueue(dest);
					}
				} 
				catch (PosException | VacioException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void dfs(Edge<K, V> edgeTo)
	{
		mark();
		for(int i=1; i<=arcos.size(); i++)
		{
			Vertex<K, V> dest;
			try 
			{
				dest = arcos.getElement(i).getDestination();
				if(!dest.marked)
				{
					dest.dfs(arcos.getElement(i));
				}
			} 
			catch (PosException | VacioException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void topologicalOrder( ColaEncadenada<Vertex<K, V>> pre, ColaEncadenada<Vertex<K, V>> post, PilaEncadenada<Vertex<K, V>> reversePost )
	{
		mark();
		pre.enqueue(this);
		
		for(int i=1; i<= arcos.size(); i++ )
		{
			Vertex<K, V> destino;
			try {
				destino = arcos.getElement(i).getDestination();
				if(!destino.getMark())
				{
					destino.topologicalOrder(pre, post, reversePost);
				}
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		post.enqueue(this);
		reversePost.push(this);
		
	}



	@Override
	public int compareTo(Vertex<K, V> o) 
	{
		return key.compareTo(o.getId());
	}
	
	public void getSCC(ITablaSimbolos<K, Integer> tabla, int idComponente)
	{
		mark();
		tabla.put(key, idComponente);
		for(int i=1; i<= arcos.size(); i++)
		{
			Vertex<K, V> actual;
			try 
			{
				actual = arcos.getElement(i).getDestination();
				if(!actual.getMark())
				{
					actual.getSCC(tabla, idComponente);
				}
			} 
			catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ILista<Edge<K, V>> mstPrimLazy()
	{
		ILista<Edge<K, V>> mst= new ArregloDinamico<Edge<K, V>>(1);
		MinPQ<Float, Edge<K, V>> cola= new MinPQ<Float, Edge<K, V>>(1);
		
		addEdgesToMinPQ(cola, this);
		
		while(!cola.isEmpty())
		{
			Edge<K, V> actual= cola.delMin(). getValue();
			Vertex<K, V> dest= actual.getDestination();
			if(!dest.marked)
			{
				try {
					mst.insertElement(actual, mst.size()+1);
				} catch (PosException | NullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addEdgesToMinPQ(cola, dest);
			}
		}
		return mst;
		
	}
	
	private void addEdgesToMinPQ(MinPQ<Float, Edge<K, V>> cola, Vertex<K, V> inicio)
	{
		inicio.mark();
		
		for(int i=1; i<= inicio.edges().size(); i++)
		{
			Edge<K, V> actual=null;
			try {
				actual = inicio.edges().getElement(i);
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cola.insert(actual.getWeight(), actual);
		}
	}
	
	 public static class ComparadorXKey implements Comparator<Vertex<String, Landing>>
	 {

		/** Comparador alterno de acuerdo al número de likes
		* @return valor 0 si video1 y video2 tiene los mismos likes.
		 valor negativo si video1 tiene menos likes que video2.
		 valor positivo si video1 tiene más likes que video2. */
		 public int compare(Vertex vertice1, Vertex vertice2) 
		 {
			 return ((String)vertice1.getId()).compareToIgnoreCase((String) vertice2.getId());
		 }

	}
	 
	public ITablaSimbolos<K, NodoTS<Float, Edge<K, V>>> minPathTree()
	{
		 ITablaSimbolos<K, NodoTS<Float, Edge<K, V>>> tablaResultado= new TablaHashLinearProbing<K, NodoTS<Float, Edge<K, V>>>(2);
		 MinPQIndexada<Float, K, Edge<K, V>> colaIndexada= new MinPQIndexada<Float, K, Edge<K, V>>(20);
		 
		 tablaResultado.put(this.key, new NodoTS<Float, Edge<K, V>>(0f, null));
		 
		 relaxDijkstra(tablaResultado, colaIndexada, this, 0);
		 
		 while(!colaIndexada.isEmpty())
		 {
			 NodoTS<Float, Edge<K, V>> actual= colaIndexada.delMin();
			 Edge<K, V> arcoActual= actual.getValue();
			 float pesoActual= actual.getKey();
			 relaxDijkstra(tablaResultado, colaIndexada, arcoActual.getDestination(), pesoActual);
		 }
		 
		 return tablaResultado;
	}
	
	public void relaxDijkstra(ITablaSimbolos<K, NodoTS<Float, Edge<K, V>>> tablaResultado, MinPQIndexada<Float, K, Edge<K, V>> colaIndexada, Vertex<K, V> actual, float pesoAcumulado)
	{
		actual.mark();
		for(int i=1; i<=actual.edges().size(); i++)
		{
			Edge<K, V> arcoActual;
			try 
			{
				arcoActual = actual.edges().getElement(i);
				Vertex<K, V> destino= arcoActual.getDestination();
				float peso= arcoActual.getWeight();
				if(!destino.getMark())
				{
					NodoTS<Float, Edge<K, V>>llegadaDestino= tablaResultado.get(destino.getId());
					
					if(llegadaDestino== null)
					{
						tablaResultado.put(destino.getId(), new NodoTS<Float, Edge<K, V>>(pesoAcumulado + peso, arcoActual));
						colaIndexada.insert(peso+ pesoAcumulado, destino.getId(), arcoActual);
						
					}
					else if(llegadaDestino.getKey()>(pesoAcumulado + peso))
					{
						llegadaDestino.setKey(pesoAcumulado + peso);
						llegadaDestino.setValue(arcoActual);
						colaIndexada.changePriority(destino.getId(), pesoAcumulado + peso, arcoActual);
						
					}
				}
			} 
			catch (PosException | VacioException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	
	
	
}
