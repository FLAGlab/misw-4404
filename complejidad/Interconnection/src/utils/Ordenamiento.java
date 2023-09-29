package utils;

import java.util.Comparator;

import model.data_structures.ILista;
import model.data_structures.NullException;
import model.data_structures.PosException;
import model.data_structures.VacioException;

public final class Ordenamiento <T extends Comparable <T>>
{

	public void ordenarSeleccion(ILista<T> lista, Comparator<T> criterio, boolean ascendente ) throws PosException, VacioException
	{
		for (int i=1; i<=lista.size(); i++)
		{
			int posMayorMenor= i;
			
			for (int j=i+1; j<=lista.size(); j++)
			{
				int factorComparacion= (ascendente ? 1:-1)* criterio.compare(lista.getElement(posMayorMenor), lista.getElement(j));
				if (factorComparacion > 0)
				{
					posMayorMenor=j;
				}
			}
			lista.exchange(posMayorMenor, i);
		}
	}
	
	public void ordenarInsercion(ILista<T> lista, Comparator<T> criterio, boolean ascendente ) throws PosException, VacioException
	{
		
		for (int i=2; i<= lista.size(); i++)
		{
			boolean enPosicion=false;
			
			for (int j=i; j>1 && !enPosicion; j--)
			{
				int factorComparacion= (ascendente ?1:-1) * criterio.compare(lista.getElement(j), lista.getElement(j-1));
				if (factorComparacion<0)
				{
					lista.exchange(j, j-1);
				}
				else
				{
					enPosicion=true;
				}
			}
		}
	
	}
	
	public void ordenarShell (ILista<T> lista, Comparator<T> criterio, boolean ascendente ) throws PosException, VacioException
	{
		int n=lista.size();
		int h=1;
		
		while(h<(n/3))
		{
			h=3*h +1;
		}
		
		while(h>=1)
		{
			for(int i=h+1; i<=n; i++)
			{
				boolean enPosicion= false;
				
				for(int j=i; j>h && !enPosicion; j-=h)
				{
					int factorComparacion= (ascendente ?1:-1)*criterio.compare(lista.getElement(j), lista.getElement(j-h));
					
					if (factorComparacion<0)
					{
						lista.exchange(j, j-h);
					}
					else
					{
						enPosicion=true;
					}
				}
			}
			
			h/=3;
		}
	}
	
	public void sort (ILista<T> lista, Comparator<T> criterio, boolean ascendente, int lo, int hi) throws PosException, VacioException
	{
		if(lo>=hi)
			return;
		int pivot= partition(lista, criterio, ascendente, lo, hi);
		sort(lista, criterio, ascendente, lo, pivot-1);
		sort(lista, criterio, ascendente, pivot +1, hi);
	}
	
	public void ordenarQuickSort(ILista<T> lista, Comparator<T> criterio, boolean ascendente) throws PosException, VacioException
	{
		sort(lista, criterio, ascendente, 1, lista.size());
	}
	
	public int partition(ILista<T> lista, Comparator<T> criterio, boolean ascendente, int lo, int hi) throws PosException, VacioException
	{
		int follower, leader;
		follower=leader=lo;
		
		while(leader<hi)
		{
			int factorComparacion=(ascendente?1:-1)*criterio.compare(lista.getElement(leader), lista.getElement(hi));
			if (factorComparacion<0)
			{
				lista.exchange(follower, leader);
				follower++;
			}
			leader++;
		}
		
		lista.exchange(follower, hi);
		
		return follower;
	}
	
	public final void ordenarMergeSort(ILista<T> lista, Comparator<T> criterio, boolean ascendente) throws PosException, VacioException, NullException
	{
		int size = lista.size();
		if(size > 1)
		{
			int mid = size/2;
			//Se divide la lista original en dos partes, izquierda y derecha, desde el punto mid.
			ILista<T> leftList = lista.sublista(1, mid);
			ILista<T> rightList = lista.sublista(mid+1, size - mid);

			//Se hace el llamado recursivo con la lista izquierda y derecha.
			ordenarMergeSort(leftList, criterio, ascendente);
			ordenarMergeSort(rightList, criterio, ascendente);
			
			//i recorre la lista de la izquierda, j la derecha y k la lista original.
			int i,j,k;
			i=j=k= 1;
			
			int leftelements = leftList.size();
			int rightelements = rightList.size();
			
			while(i <= leftelements && j <= rightelements)
			{
				T elemi = leftList.getElement(i);
				T elemj = rightList.getElement(j);
				//Compara y ordena los elementos
				int factorComparacion = (ascendente?1:-1) * criterio.compare(elemi, elemj);
				
				if(factorComparacion <= 0) 
				{
					lista.changeInfo(k, elemi);
					i++;
				}
				else
				{
					lista.changeInfo(k, elemj);
					j++;
				}
				k++;
			}
			
			//Agrega los elementos que no se compararon y están ordenados
			while(i <= leftelements)
			{
				lista.changeInfo(k, leftList.getElement(i));
				i++;
				k++;
			}
			
			while(j <= rightelements)
			{
				lista.changeInfo(k, rightList.getElement(j));
				j++;
				k++;
			}
		}
	}

	
}
