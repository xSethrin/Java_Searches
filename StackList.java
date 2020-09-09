package Search;

import java.util.*;

public class StackList<E> implements Stack<E> {
	private Node<E> top; // Pointer to top of stack.
	private int size = 0;// Size of list
	
	public StackList(E it) {	
		top = new Node<E>(it); // Create top that stores it
		size++; 
	}
	public StackList() {	
		top = new Node<E>(); // Create an empty top
		size = 0; 
	}
	

/** Stack ADT */
	public interface Stack<E> {
	}
	
  /**	Returns true if the stack is empty; otherwise false
		@return true is stack is empty otherwise false
	*/
	public boolean isEmpty(){
		return size==0;
	}


	/** Returns the object at the top of the stack without removing it
		@return the object at the top of the stack
		@throws EmptyStackException
	*/		
	public E peek(){
		if(isEmpty()){
			System.out.println("Empty stack");
			throw new EmptyStackException();
		}
		else{
			return top.getElement();
		}
	}


	/** Returns the object at the top of the stack and removes it
		so stack is one smaller
		@return the object at the top of the stack
		@throws EmptyStackException
	*/
	public E pop(){
		if(isEmpty()){
			
			System.out.println("Empty stack");
			throw new EmptyStackException();
		}
		else{
			E topElement = top.getElement();
			top = top.getNext();
			size --;
			return topElement;
		}
	}
	
	


	/** Pushes an item onto the top of the stack
		@param it The object to be inserted at the top
	*/
	public void push(E it){
		top = new Node<E>(it, top);
		size++;
	}


	/** Dumps the stack - clears it of its contents 
	* I know that this should be clear - but I love alliteration
	*/
	public void puke(){
		top = null;
		size = 0;
	}




	/** Returns the number of elements on the stack
		@return size - the number of elements on the stack
	*/
	public int size(){
		return size;
	}


}



