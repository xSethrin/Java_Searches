package Search;


public class HashChain<K, V >{
	
	private SLList<HashNode<K,V>>[] hashTable;
	private int numberOfItems;
	
	
	
	/** Constructor
	* @param size initial size of hashtable
	**/
	public HashChain(int size){
		hashTable = new SLList[size];
		// initialize hashtable with empty linked lists
		for (int i = 0; i < hashTable.length; i++){
			hashTable[i] = new SLList<HashNode<K, V>>();
		}
		numberOfItems = 0;
	}
	
	/** load balance - have it return a double in case you want to use
	 * use percentages later
	 */
	public double calcLoad(){
		return (numberOfItems + 0.0) / hashTable.length;
	}
	
	
	
	/**
	* insert the key value pair into the hash table
	*/
	public void insert(HashNode<K,V> hn){
		//for separate chaining we want to keep load around 10 or less
		if (calcLoad() > 10){
			rehash();
		}
		int postion = hashCode(hn.getKey(), hashTable.length);
		SLList<HashNode<K, V>> l = hashTable[postion];
		l.add(hn);
		numberOfItems++;
	}
	/**
	* insert the key into the given hash table - this is for rehashing!
	*/
	private void insert (HashNode<K,V> hn, SLList<HashNode<K,V>>[]table){
		int postion = hashCode(hn.getKey(), table.length);
		SLList<HashNode<K, V>> l = table[postion];
		l.add(hn);
		numberOfItems++;
	}
	/**
	* make a bigger table and rehash contents of old table
	*/
	public void rehash(){
		//System.out.println("Rehashing...");
		numberOfItems = 0;
		SLList<HashNode<K, V>>[] bigger = new SLList[hashTable.length * 2 + 1];
		// initialize hashtable with empty linked lists
		for (int i = 0; i < bigger.length; i++){
			bigger[i] = new SLList<HashNode<K, V>>();
		}
		//take everything from the old hashtable and rehash it into bigger
		for (int i = 0; i < hashTable.length; i++){
			SLList<HashNode<K, V>> l = hashTable[i];
			Node<HashNode<K, V>> n = l.getHead();
			while(n != null){
				insert(n.getElement(), bigger);
				n = n.getNext();
			}
		}
		// reassign reference to bigger
		hashTable = bigger;
	}
	
	/** simple hash function from slides 
	* uses Horner's rule with radix of 37
	*/
	public int hashCode(K key, int tableSize) {
		String k = key.toString();
		int hashValue = 0;
		for (int i = 0; i < k.length(); i++) {
			hashValue = 97 * hashValue + k.charAt(i);
		}
		hashValue %= tableSize;//take the mod of the tableSize
		if (hashValue < 0) {
			hashValue += tableSize;
		}
		//System.out.println(key +" has a hash value of "+ hashValue);//for debugging
		return hashValue;
	}
	
	
	/**
	* @return HashNode with key value you are looking for
	* @param key - class used for key
	* @param value - class you are storing
	*/
	public HashNode<K,V> search(K key, V value){
		int postion = hashCode(key, hashTable.length);
		SLList<HashNode<K, V>> l = hashTable[postion];
		//System.out.println(value + " should be at position " + postion);
		int itemNumber = 0;
		Node<HashNode<K, V>> n = l.getHead();
		while(n != null){
			if(n.getElement().getValue().equals(value)){
				//System.out.println("The number of places in the list searched before finding the value of " + value + " is " + itemNumber);
				//System.out.println();
				return n.getElement();
			}
			n = n.getNext();
			itemNumber++;
		}
		//System.out.println("The number of places in the list searched before not finding the value of " + value + " is " + itemNumber);
		//System.out.println();
		return null;
	}
	
	/**
	 * removing the key value pair for the table if it is there
	 */
	public void remove(K key, V value){
		int postion = hashCode(key, hashTable.length);
		SLList<HashNode<K, V>> l = hashTable[postion];
		System.out.println("Removing: " + value + " should be at position " + postion);
		int itemNumber = 0;
		Node<HashNode<K, V>> n = l.getHead();
		while(n != null){
			if(n.getElement().getValue().equals(value)){
				l.remove(itemNumber);
				return;
			}
			n = n.getNext();
			itemNumber++;
		}
		System.out.println(value + " is not in table, cannot remove it");
		System.out.println();
	}
			
	//for debugging - this is nice
	public SLList[] getChainTable(){
		return hashTable;
	}
}
	
	
	
			
