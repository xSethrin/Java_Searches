package Search;

import java.util.Arrays;
import java.util.Random;
import java.lang.*;

public class TNode<E>  {
	private E element;//an array to store state
	private TNode<E> parent = null;// reference to next node in list
	private char move; //movement of the state compare to the last step
	private int[] moves = new int[4];//possible movements this state could make
	private int i = 0;//pointer to the position of the moves of the array
	private int heu = 0;//heuristic value
	private int level = 0;

	/** Constructor
	* @param item the element to be stored in Node
	* @param move the next Node that this is pointing to
	*/
	public TNode(E item, TNode<E> parentValue, char move){ 
		element = item;  
		parent = parentValue; 
		this.move = move;
	}

	 /** Constructor
	 * @param item
	 * @param possibleMoves
	 */
	public TNode(E item, int[]possibleMoves){ 
		element = item;  
		System.arraycopy(possibleMoves, 0, moves, 0, 4);
	}

	 /** Constructor
	 * @param item
	 * @param parentValue
	 * @param move
	 * @param possibleMoves
	 */
	public TNode(E item, TNode<E> parentValue, char move, int[]possibleMoves){ 
		element = item;  
		parent = parentValue; 
		this.move = move;
		System.arraycopy(possibleMoves, 0, moves, 0, 4);
	}

	/** Constructor
	* @param item the element to be stored in Node
	*/
	public TNode(E item){
		element = item;
		parent = null;
	}

	/** Constructor
	*/
	 public TNode(){
		element = null;
		parent = null;
	}

	 /** Constructor
	 * @param parentValue
	 */
	public TNode(TNode<E> parentValue) { 
		parent = parentValue; 
	}
 
	/**
	 *@return the Node that is next to this
	 */
	public TNode<E> getParent() { 
		return parent; 
	}  

	/**
	 * Sets this next to Node(){
		element = null;
		parenthe given Node
	 * @param parent the Node that is to be set to this Node's next
	 */
	public void setParent(TNode<E> parent){
		this.parent = parent; 
	}     
	
	/** 
	 * returns the element in the Node
	 *@return element in the Node
	 */  
	public E getElement() { 
		return element; 
	}

	/**
	 * set the movement of the node
	 * @param move
	 */
	public void setMove(char move) {
		this.move = move; 
	}

	/**
	 * get the movement of the node
	 * @return
	 */
	public char getMove() {
		return move;
	}
	
	/**
	 * sets the element stored in Node to the element given
	 *@param item the element to be stored in Node.
	 */
	public E setElement(E item) {
		return element = item; 
	}

	/**
	 * know the position of the pointer to the moves array
	 * @return
	 */
	public int getI(){
		return i;
	}


	/**
	 * set the heuristic value
	 * @param h
	 */
	public void setHeu(int h){
		heu = h;
	}

	/**
	 * get the heuristic value
	 * @return heu
	 */
	public int getLevel(){
		return level;
	}

	/**
	 * set the heuristic value
	 * @param level
	 */
	public void setLevel(int level){
		this.level = level;
	}

	/**
	 * get the heuristic value
	 * @return heu
	 */
	public int getHeu(){
		return heu;
	}

	/**
	 * for comparision in the heap tree
	 * @param o
	 * @return
	 */
	public int compare(TNode o) {
		if(this.heu > o.heu) return 1;
		else if (this.heu == o.heu) return 0;
		else return -1;
	}
	
}