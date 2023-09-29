package model.data_structures;

public class TablaSimbolos <K extends Comparable<K>, V extends Comparable <V>> implements ITablaSimbolos<K, V>
{

	private ILista<NodoTS<K, V>> listaNodos; 

	public TablaSimbolos()
	{
		listaNodos= new ArregloDinamico(1);
	}

	public void put(K key, V value) 
	{
		NodoTS<K, V> agregar = new NodoTS<K, V>(key, value);
		try 
		{
			listaNodos.insertElement(agregar, size()+1);
		} 
		catch (PosException | NullException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public V get(K key) 
	{
		
		int i = 1;
		int f = keySet().size();
		while ( i <= f)
		{	
			int m = (i + f) / 2;
		
			try 
			{
				if ( keySet().getElement(m).compareTo(key)==0 )
				{
					return listaNodos.getElement(m).getValue();
				}
				else if (keySet().getElement(m).compareTo(key)>0 )
				{
					f = m - 1;
				}
				else
				{
					i = m +1;
				}
			} 
			catch (PosException | VacioException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public V remove(K key) 
	{
		V eliminado1=null;
		try 
		{
			int pos= listaNodos.isPresent((NodoTS<K, V>) get(key));
			eliminado1=listaNodos.getElement(pos).getValue();
			listaNodos.deleteElement(pos);
		} 
		catch (VacioException | NullException | PosException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return eliminado1; 
	}

	@Override
	public boolean contains(K key) 
	{
		boolean respuesta=false;
		int pos=-1;
		try 
		{
			pos = listaNodos.isPresent((NodoTS<K, V>) get(key));
		} 
		catch (VacioException | NullException | PosException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (pos>0)
		{
			respuesta=true;
		}
		return respuesta;
	}

	@Override
	public boolean isEmpty() 
	{
		return listaNodos.isEmpty();
	}

	@Override
	public int size() 
	{
		return listaNodos.size();
	}

	@Override
	public ILista<K> keySet() 
	{
		ILista<K> lista= new ArregloDinamico(1);
		for (int i=1; i<= size(); i++)
		{
			try 
			{
				lista.insertElement(listaNodos.getElement(i).getKey(), lista.size()+1);
			} 
			catch (PosException | NullException | VacioException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lista;
	}

	@Override
	public ILista<V> valueSet() 
	{
		ILista<V> lista= new ArregloDinamico(1);
		for (int i=1; i<= size(); i++)
		{
			try 
			{
				lista.insertElement(listaNodos.getElement(i).getValue(), lista.size()+1);
			} 
			catch (PosException | VacioException| NullException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lista;
	}

	public String toString()
	{
		return  "Cantidad de duplas: "+ size();
	}
	
	public ILista<NodoTS<K, V>> darListNodos()
	{
		return listaNodos;
	}

	@Override
	public ILista<NodoTS<K, V>> darListaNodos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hash(K key) {
		// TODO Auto-generated method stub
		return 0;
	}
}
