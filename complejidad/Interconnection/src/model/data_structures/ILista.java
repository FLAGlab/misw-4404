package model.data_structures;

public interface ILista<T extends Comparable <T>> extends Comparable <ILista<T>>{
	
	public void addFirst(T element);
	
	public void addLast(T element);
	
	public void insertElement(T elemento, int pos) throws PosException, NullException;
	
	public T removeFirst() throws VacioException;
	
	public T removeLast();
	
	public T deleteElement(int pos) throws PosException, VacioException;
	
	public T firstElement() throws VacioException;
	
	public T lastElement() throws VacioException;
	
	public T getElement(int pos) throws PosException, VacioException;
	
	public int size();
	
	public boolean isEmpty();
	
	public int isPresent(T element) throws VacioException, NullException, PosException;
	
	public void exchange(int pos1, int pos2) throws PosException, VacioException;
	
	public void changeInfo(int pos, T element) throws PosException, VacioException, NullException;
	
	/**
	 * Crear una sublista de la lista original (this).
	 * Los elementos se toman en el mismo orden como aparecen en la lista original (this).
	 * @param número de elementos que contendrá la sublista. Si el número es superior al tamaño
	 * original de la lista, se obtiene una copia de la lista original.
	* @return sublista creada con la misma representación de la lista original (this).
	 * @throws VacioException 
	 * @throws PosException 
	 * @throws NullException 
	 */
	public ILista<T> sublista(int pos, int numElementos) throws PosException, VacioException, NullException;

}
