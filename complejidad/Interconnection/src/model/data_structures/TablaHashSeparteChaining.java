package model.data_structures;

import java.text.DecimalFormat;

import sun.security.util.Debug;

public class TablaHashSeparteChaining <K extends Comparable<K>, V extends Comparable <V>> implements ITablaSimbolos<K, V >
{
	
	private ILista<ILista<NodoTS<K,V>>> listaNodos;
	private int tamanoAct;
	private int tamanoTabla;
	private double minicial;
	private double cantidadRehash;
	
	public TablaHashSeparteChaining(int tamInicial)
	{
		int m = nextPrime(tamInicial);
		minicial=m;
		listaNodos=new ArregloDinamico<>(m);
		tamanoAct=0;
		tamanoTabla=m;
		
		for(int i=1; i<=tamanoTabla; i++)
		{
			try 
			{
				listaNodos.insertElement(null, i);
			} 
			catch (PosException | NullException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void put(K key, V value) 
	{
		int posicion=hash(key);
		try 
		{
			ILista<NodoTS<K,V>> listasc=listaNodos.getElement(posicion);
			
			if(listasc!=null && !contains(key))
			{
				listasc.insertElement(new NodoTS<K,V>(key, value), listasc.size()+1);
			}
			else
			{
				listaNodos.changeInfo(posicion, new ArregloDinamico<NodoTS<K,V>>(1));
				listasc=listaNodos.getElement(posicion);
				listasc.insertElement(new NodoTS<K,V>(key, value), listasc.size()+1);
			}
		} 
		catch (PosException | VacioException | NullException e) 
		{
			e.printStackTrace();
		}
		
		tamanoAct++;
		
		double tam= tamanoAct;
		double tam2=tamanoTabla;
		double tamanoCarga= tam/tam2;
		
		if (tamanoCarga > 5)
		{
			rehash();
		}
		
	}

	@Override
	public V get(K key)
	{
		V retornar=null;
		int posicion=hash(key);
		try 
		{
			ILista<NodoTS<K,V>> listasc= listaNodos.getElement(posicion);
			if(listasc!=null)
			{
				for(int i=1; i<=listasc.size() && retornar==null; i++)
				{
					if(listasc.getElement(i).getKey().compareTo(key)==0)
					{
						retornar=listasc.getElement(i).getValue();
					}
				}
			}
		} 
		catch (PosException | VacioException e) 
		{
			e.printStackTrace();
		}
		
		return retornar;
	}

	@Override
	public V remove(K key) 
	{
		V retornar=null;
		int posicion=hash(key);
		try 
		{
			ILista<NodoTS<K,V>> listasc= listaNodos.getElement(posicion);
			if(listasc!=null)
			{
				for(int i=1; i<=listasc.size() && retornar==null; i++)
				{
					if(listasc.getElement(i).getKey().compareTo(key)==0)
					{
						retornar=listasc.getElement(i).getValue();
						listasc.deleteElement(i);
					}
				}
			}
		} 
		catch (PosException | VacioException e) 
		{
			e.printStackTrace();
		}
		tamanoAct--;
		return retornar;
		
		
	}

	@Override
	public boolean contains(K key) 
	{
		V valor=get(key);
		boolean retorno=false;
		if(valor!=null)
		{
			retorno=true;
		}
		
		return retorno;
	}

	@Override
	public boolean isEmpty() 
	{
		if (size()==0)
		{
			return true;
		}
		return false;
	}

	@Override
	public int size() 
	{
		return tamanoAct;
	}

	@Override
	public ILista<K> keySet() 
	{
		ILista<K> lista= new ArregloDinamico(1);
		try
		{
			for (int i=1; i<= tamanoTabla; i++)
			{
				if (listaNodos.getElement(i) !=null)
				{
					for (int j=1; j<=listaNodos.getElement(i).size(); j++)
					{
						if (listaNodos.getElement(i).getElement(j)!= null)
						{
							lista.insertElement(listaNodos.getElement(i).getElement(j).getKey(), lista.size()+1);
						}
					}
				}
			}
		}
		catch (PosException | NullException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lista;
	}

	@Override
	public ILista<V> valueSet() 
	{
		ILista<V> lista= new ArregloDinamico(1);
		
		try 
		{
			for (int i=1; i<= tamanoTabla; i++)
			{
				if (listaNodos.getElement(i)!=null)
				{
					for (int j=1; j<= listaNodos.getElement(i).size(); j++)
					{
						if (listaNodos.getElement(i).getElement(j)!= null)
						{
							lista.insertElement(listaNodos.getElement(i).getElement(j).getValue(), lista.size()+1);
						}
					}
				}
				
			}
		}
		catch (PosException | NullException | VacioException e) 
		{
			e.printStackTrace();
		}
		
		return lista;
	}

	@Override
	public ILista<NodoTS<K, V>> darListaNodos() 
	{
		ILista<NodoTS<K, V>> nodos= new ArregloDinamico<NodoTS<K, V>>(1);
		try 
		{
			for (int i=1; i<= tamanoTabla; i++)
			{
				ILista<NodoTS<K,V>> elemento= listaNodos.getElement(i);
				if(elemento!= null && !elemento.isEmpty())
				{
					for(int j=1; j<= elemento.size(); j++)
					{
						NodoTS<K,V> elemento2= elemento.getElement(j);
						if(elemento2 != null && !elemento.isEmpty())
						{
							nodos.insertElement(elemento2, nodos.size()+1);
						}
					}
				}
			}
		}
		catch (PosException | NullException | VacioException e) 
		{
			e.printStackTrace();
		}
		
		return nodos;
		
	}
	@Override
	public int hash(K key) 
	{
		return Math.abs(key.hashCode()% tamanoTabla) +1;
	}
	
	public void rehash()
	{
		try
		{
			ILista<NodoTS<K,V>> nodos= darListaNodos();
			
			tamanoAct=0;
			tamanoTabla*=2;
			int m = nextPrime(tamanoTabla);
			tamanoTabla=m;
			listaNodos=new ArregloDinamico<>(tamanoTabla);
			
			for(int i=1; i<=tamanoTabla; i++)
			{
				listaNodos.insertElement(null, size()+1);
			}
			
			NodoTS<K,V> actual= null;
			for(int i=1; i<= nodos.size();i++)
			{
				actual=nodos.getElement(i);
				put(actual.getKey(), actual.getValue());
			}
		}
		catch (NullException| VacioException| PosException e)
		{
			e.printStackTrace();
		}
		
		cantidadRehash++;
		
	}
	
	static boolean isPrime(int n)
    {

        if (n <= 1) return false;

        if (n > 1 && n <= 3) return true;


        if (n % 2 == 0 || n % 3 == 0) return false;

        for (int i = 5; i * i <= n; i = i + 6)

            if (n % i == 0 || n % (i + 2) == 0)

            return false;

        return true;
    }

    static int nextPrime(int N)

    {
        if (N <= 1)

            return 2;

        int prime = N;

        boolean found = false;


        while (!found)

        {
            prime++;

            if (isPrime(prime))

                found = true;

        }
        return prime;

    }
    
    public String toString()
    {
    	String retorno="";
    	retorno+= "La cantidad de duplas: " + keySet().size();
    	retorno+="\nEl m inicial es: " + minicial;
    	retorno+="\nEl m final es: " + tamanoTabla;
    	double tam= tamanoAct;
		double tam2=tamanoTabla;
		DecimalFormat df= new DecimalFormat("###.##");
		double tamañoCarga= tam/tam2;
		retorno+="\nEl factor de carga es: " + df.format(tamañoCarga);
    	retorno+="\nLa cantidad de rehash es: " + cantidadRehash;
    	
    	return retorno;
    }

}
