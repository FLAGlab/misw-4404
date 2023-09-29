package model.data_structures;

public class GrafoListaAdyacencia <K extends Comparable<K> ,V extends Comparable <V>>
{
	private ITablaSimbolos<K, Vertex<K, V>> vertices; 
	
	private ILista<Edge<K, V>> arcos;
	
	private ILista<Vertex<K, V>> verticesLista;
	
	private int numEdges;
	
	public GrafoListaAdyacencia(int numVertices)
	{
		vertices= new TablaHashLinearProbing<K, Vertex<K, V>>(numVertices);	
		numEdges=0;
		arcos= new ArregloDinamico<>(1);
		verticesLista= new ArregloDinamico<>(1);
	}
	
	public boolean containsVertex(K id)
	{
		return vertices.contains(id);
	}
	
	public int numVertices()
	{
		return vertices.size();
	}
	
	public int numEdges()
	{
		return numEdges;
	}
	
	public void insertVertex(K id, V value)
	{
		vertices.put(id, new Vertex<K, V>(id, value));
		try {
			Vertex<K, V> vertice= getVertex(id);
			verticesLista.insertElement(vertice, verticesLista.size()+1);
		} catch (PosException | NullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void addEdge(K source, K dest, float weight)
	{
		Edge<K, V> existe= getEdge(source, dest);
		
		if(existe==null)
		{
			Vertex<K, V> origin= getVertex(source);
			
			Vertex<K, V> destination= getVertex(dest);
			Edge<K, V> arco1= new Edge<K, V>(origin, destination, weight);
			origin.addEdge(arco1);
			
			Edge<K, V> arco2=new Edge<K, V>(destination, origin, weight);
			destination.addEdge(arco2);
			numEdges++;
			try 
			{
				arcos.insertElement(arco1, arcos.size()+1);
			} catch (PosException | NullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public Vertex<K,V> getVertex(K id)
	{
		return vertices.get(id);
	}
	
	public Edge<K,V> getEdge(K idS, K idD)
	{
		Vertex<K,V> origen= vertices.get(idS);
		
		if(origen==null)
		{
			return null;
		}
		else
		{
			return origen.getEdge(idD);
		}
	}
	
	public ILista<Edge<K,V>> adjacentEdges(K id)
	{
		Vertex<K,V> origen= vertices.get(id);
		return origen.edges();
	}
	
	public ILista<Vertex<K, V>> adjacentVertex(K id)
	{
		Vertex<K,V> origen= vertices.get(id);
		
		return origen.vertices();
		
	}
	
	public int indegree(K vertex)
	{
		Vertex<K,V> origen= vertices.get(vertex);
		return origen.indegree();
	}
	
	public int outdegree(K vertex)
	{
		Vertex<K,V> origen= vertices.get(vertex);
		return origen.outdegree();
	}
	
	public ILista<Edge<K, V>> edges()
	{
		return arcos;
	}
	
	public ILista<Vertex<K,V>> vertices()
	{
		return verticesLista;
	}
	
	public void unmark()
	{
		ILista<Vertex<K, V>> vertices= vertices();
		for(int i=1; i<=vertices.size(); i++)
		{
			try {
				vertices.getElement(i).unmark();
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void dfs(K id)
	{
		Vertex<K, V> inicio= getVertex(id);
		inicio.dfs(null);
		unmark();
	}
	
	public void bfs(K id)
	{
		Vertex<K, V> inicio= getVertex(id);
		inicio.bfs();
		unmark();
	}
	
	public Edge<K, V>  arcoMin()
	{
		Edge<K, V> minimo=null;
		try 
		{
			minimo=arcos.getElement(1);
			float min=arcos.getElement(1).getWeight();
			for(int i=2; i<=arcos.size(); i++)
			{
				if(arcos.getElement(i).getWeight()< min)
				{
					minimo=arcos.getElement(i);
					min=arcos.getElement(i).getWeight();
				}
			}
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return minimo;
		
	}
	
	public Edge<K, V>  arcoMax()
	{
		Edge<K, V> maximo=null;
		try 
		{
			float max=0;
			for(int i=2; i<=arcos.size(); i++)
			{
				if(arcos.getElement(i).getWeight()> max)
				{
					maximo=arcos.getElement(i);
					max=arcos.getElement(i).getWeight();
				}
			}
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return maximo;
		
	}
	
	public GrafoListaAdyacencia<K, V> reverse()
	{
		GrafoListaAdyacencia<K, V> copia= new GrafoListaAdyacencia<K, V>(numVertices());
		ILista<Vertex<K, V>> vertices2= vertices();
		ILista<Edge<K, V>> arcos= edges();

		for(int i=1; i<= vertices2.size(); i++)
		{
		
			Vertex<K, V> actual;
			try {
				actual = vertices2.getElement(i);
				copia.insertVertex(actual.getId(), actual.getInfo());
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i=1; i<= arcos.size(); i++)
		{
			Edge<K, V> actual;
			try {
				actual = arcos.getElement(i);
				copia.addEdge(actual.getDestination().getId(), actual.getSource().getId(), actual.getWeight());
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return copia;
		
	}
	
	public ITablaSimbolos<K, Integer> getSSC()
	{
		PilaEncadenada<Vertex<K, V>> reverseTopological= reverse().topologicalOrder();
		ITablaSimbolos<K, Integer> tabla= new TablaHashLinearProbing<K, Integer>(numVertices());
		int idComponente=1;
		while(reverseTopological.top()!=null)
		{
			Vertex<K, V> actual= reverseTopological.pop();
			if(!actual.getMark())
			{
				actual.getSCC(tabla, idComponente);
				idComponente++;
			}
		}
		
		unmark();
		//Revisar
		return tabla;
	}
	
	public PilaEncadenada<Vertex<K, V>> topologicalOrder()
	{
		ColaEncadenada<Vertex<K, V>> pre= new ColaEncadenada<Vertex<K, V>>();
		ColaEncadenada<Vertex<K, V>> post= new ColaEncadenada<Vertex<K, V>>();
		PilaEncadenada<Vertex<K, V>> reversePost= new PilaEncadenada<Vertex<K, V>>();
		
		ILista<Vertex<K, V>> vertices= vertices();
		
		for(int i=1; i<= vertices.size(); i++)
		{
			try 
			{
				if(!vertices.getElement(i).getMark())
				{
					vertices.getElement(i).topologicalOrder(pre, post, reversePost);
				}
			} catch (PosException | VacioException e) {
				e.printStackTrace();
			}
		}
		
		unmark();
		return reversePost;
	}
	
	public ILista<Edge<K, V>> mstPrimLazy(K idOrigen)
	{
		ILista<Edge<K, V>> mst= getVertex(idOrigen).mstPrimLazy();
		unmark();
		return mst;
	}
	
	private ITablaSimbolos<K, NodoTS<Float, Edge<K, V>>> minPathTree(K idOrigen)
	{
		Vertex<K, V> inicio= getVertex(idOrigen);
		return inicio.minPathTree();
		
	}
	
	public PilaEncadenada<Edge<K, V>> minPath(K idOrigen, K idDestino)
	{
		ITablaSimbolos<K, NodoTS<Float, Edge<K, V>>> tree= minPathTree(idOrigen);
		
		PilaEncadenada<Edge <K, V>> path= new PilaEncadenada<Edge<K, V>>();
		K idBusqueda= idDestino;
		NodoTS<Float, Edge<K, V>> actual;
		
		while( (actual=tree.get(idBusqueda))!=null && actual.getValue()!=null)
		{
			path.push(actual.getValue());
			idBusqueda=actual.getValue().getSource().getId();
		}
		
		unmark();
		return path;
	}
	
	

	
}
