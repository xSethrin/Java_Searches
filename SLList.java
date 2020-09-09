package Search;

public class SLList<E> implements List<E> {
    private Node<E> head;
    private int size = 0;

    /**
     * Constructors
     */
    public SLList(E item){
        head = new Node<E>(item);
        size++;
    }

    /**
     * Constructors
     */
    public SLList(){
        head = null;
        size = 0;
    }

    /** Remove all contents from the list, so it is once again
     empty. */
    public void clear(){
        head = null;
        size = 0;
    }

    /** Insert an element at the given location.
     * allows you to insert after the tail
     * @param item The element to be inserted.
     */
    public void insert(int index, E item){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        if (index == 0){// a new head
            addFirst(item);
        }
        else {
            Node<E> node = getNode(index - 1);
            addAfter(node, item);
        }
    }

    /**
     * return the node at a given index
     * @param index
     * @return
     */
    private Node<E> getNode(int index) {
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        Node<E> node = head;
        for (int i = 0; i < index; i++){
            node = node.getNext();
        }
        return node;
    }

    /**
     * Add a new element in the end
     * @param item the item to go into the new head
     */
    private void addAfter(Node<E> node, E item) {
        node.setNext(new Node<E>(item, node.getNext()));
        size++;
    }

    /**
     * Create a new head
     * @param item the item to go into the new head
     */
    private void addFirst(E item) {
        head = new Node<E>(item, head);
        size++;
    }

    /** Append an element at the end of the list.
     *  @param item The element to be appended.
     */
    public void add(E item){
        insert(size, item);
    }

    /**
     * Remove the  element at the given location.
     */
    public void remove(int index){
        Node<E> node = getNode(index - 1);
        Node<E> node1 = getNode(index + 1);
        node.setNext(node1);
        size--;
    }

    /**
     * Get the element in the position to one step left.
     * @return element in the node to the left of the node at the index,
     * null if at the head.
     */
    public E prev(int index){
        Node<E> node = getNode(index - 1);
        return node.getElement();
    }

    /** Get the element in the position one step right.
     * @return the element in the node to the right of
     * the node at the index, null if at the end.
     */
    public E next(int index){
        Node<E> node = getNode(index + 1);
        return node.getElement();
    }

    /**
     *  @return The number of elements in the list.
     */
    public int length(){
        return size;
    }

    /** Turn the contents of the Nodes to a string in order from head to end.
     * @return The String representation of the
     * elements in the list from head to end.
     */
    public String toString(){
        Node<E> node = head;
        String r = "";
        while(node != null){
            r += node.getElement().toString();
            if (node.getNext() != null){
                r += "==>";
            }
            node =node.getNext();
        }
        return r;
    }

    /** Reverse the content of the list.
     * if list is A => B => C it becomes C => B => A
     */
    public void reverse(){
        Node<E> node = head;
        if (node == null || node.getNext() == null){
            return;
        }
        Node<E> prev = node.getNext();
        Node<E> curr = prev.getNext();
        prev.setNext(node);
        node.setNext(null);

        while(curr != null){
            Node<E> next = curr.getNext();
            curr.setNext(prev);
            prev = curr;
            curr = next;
        }
        head = prev;

    }//---------------------------------------------------

    /**
     * @return The  element at given position.
     */
    public E getValue(int index){
        Node<E> node = getNode(index);
        return node.getElement();
    }

    /**
     * inserts the given list after the given index
     * @param list
     * @param index
     */
    public void insertList (SLList list, int index){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        if (list.size != 0){
            Node<E> node = getNode(index);
            Node<E> temp = node.getNext();
            node.setNext(list.getHead());
            list.getLast().setNext(temp);
            size += list.size;
        }
    }

    /**
     * @return the head of the list
     */
    public Node<E> getHead(){
        return head;
    }

    /**
     * @return the tail of the list
     */
    public Node<E> getLast(){
        return getNode(size - 1);
    }
}
