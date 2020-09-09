package Search;

import java.util.ArrayList;

public class MaxHeap{

    private ArrayList<TNode> array;
    private int numOfItems;

    /**Constructor
     */
    public MaxHeap()
    {
        array = new ArrayList<TNode>();
        numOfItems = 0;


    }
    /** returns true of the heap is empty
     */
    public boolean isEmpty(){
        return numOfItems == 0;
    }
    /**
     * Swap two locations i and j in ArrayList a.
     * @param a the arrayList
     * @param i first position
     * @param j second position
     */
    private static <E> void swap(ArrayList<E> a, int i, int j){
        E temp = a.get(i);
        a.set(i,a.get(j));
        a.set(j,temp);
    }

    /**
     * Return the index of the left child of node i.
     * @param i index of the parent node
     * @return index of the left child of node i
     */
    private static int leftChild(int i) {
        return 2 * i + 1;
    }


    /**
     * Return the index of the right child of node i.
     * @param i index of parent
     * @return the index of the right child of node i
     */
    private static int rightChild(int i) {
        return 2 * i + 2;
    }

    /**
     * Return the index of the parent of node i
     * (Parent of root will be -1)
     * @param i index of the child
     * @return index of the parent of node i
     */
    private static int parent(int i) {
        return (i - 1) / 2;
    }

    /**
     * Insert an element into the heap.
     * Keep in heap order
     * @param element the element to insert
     */
    public void insert(TNode element) {

        array.add(element);  //last free spot in the array
        int loc = numOfItems;//current location
        //determine if we need to swap - if parent is not smallar and not root
        while(loc > 0 && array.get(loc).compare(array.get(parent(loc))) > 0){
            swap(array, loc, parent(loc));
            loc = parent(loc);
        }
        numOfItems++;

    }


    /**
     * Return the element with the maximum key, and remove it from the heap.
     * @return the element with the maximum key, or null if heap empty.
     */
    public TNode extractMax() {
        if (isEmpty()){
            return null;
        }
        else{
            //get the root
            TNode maxVal = array.get(0);
            //move the last item to the root
            array.set(0, array.get(array.size() - 1));
            //remove the last item
            array.remove(array.size() - 1);
            maxHeapify(array, 0);//sift down the new root
            numOfItems--;
            return maxVal;
        }

    }

    /**
     * Restore the max-heap property.  When this method is called, the max-heap
     * property holds everywhere, except possibly at node i and its children.
     * When this method returns, the max-heap property holds everywhere.
     * @param a the list to sort
     * @param i the position of the possibly bad spot in the heap
     */
    private static void maxHeapify(ArrayList<TNode> a, int i) {
        int left = leftChild(i);
        int right = rightChild(i);
        int biggest;
        //among node i and left and right - find the biggest element
        //is there a left child and if so, does the left child has an element bigger than i?
        if (left < a.size() - 1 && a.get(left).compare(a.get(i)) > 0){
            biggest = left;
        }
        else {
            biggest = i;
        }
        //is there a right child and if so, does the right child has an element bigger than i or the left child?
        if (right < a.size() - 1 && a.get(right).compare(a.get(biggest)) > 0){
            biggest =right;
        }
        //if node i holds an element that is biggest than both the left and right children,
        //then the max-heap property already held, and we don't need the do anything
        // Otherwise, we need to swap node i with the smallest and then recurse down the heap
        //from the biggest children
        if (biggest != i){
            swap(a, i, biggest);
            maxHeapify(a, biggest);
        }
    }

    /**
     * Return the element with the maximum key, without removing it from the heap.
     * @return the element with the maximum key, or null if heap empty.
     */
    public TNode maximum() {
        if (isEmpty()){
            return null;
        }
        else{
            return array.get(0);
        }
    }

    public int size() {
        return numOfItems;
    }


}

