package task;

public class Node <E>{
    public E data;
    public Node <E> next;
    public Node <E> prev;

    public Node(E data, Node<E> next, Node<E> prev){
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

}
