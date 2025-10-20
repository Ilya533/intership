public class Node <E>{
    E data;
    Node <E> next;
    Node <E> prev;

    Node (E data, Node<E> next, Node<E> prev){
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

}
