//size() - returns the size of the list
//addFirst(el) - adds the element in the beginning of the list
//addLast(el) - adds the element in the end of the list
//add(index, el) - adds the element in the list by index
//getFirst() - returns the first element of the list
//getLast() - returns the last element of the list
//get(index) - returns the element by index
//removeFirst() - retrieve and remove the first element of the list
//removeLast() - retrieve and remove the last element of the list
//remove(index) - retrieve and remove the element of the list by index
//Cover all these operations with unit tests using JUnit 5

import java.util.NoSuchElementException;

public class LinkList<E> {
    int size =0;
    Node<E> first;
    Node<E> last;

    public int size(){
        return size;
    }

    public void addFirst(E el){
        Node<E> temp = first;
        final Node<E> newNode = new Node<>(el, temp,null);
        first =newNode;
        if (temp == null) last = newNode;
        else temp.prev = newNode;
        size++;
    }

    public void addLast(E el){
        Node<E> temp = last;
        final Node<E> newNode = new Node<>(el,null,temp);
        last = newNode;
        if (temp == null) first = newNode;
        else temp.next = newNode;
        size++;
    }

    public void add(int index, E el){
        if (index >= 0 && index <= size) {
            if (index == size) {
                addLast(el);
            } else {
                Node<E> temp = getNode(index);
                Node<E> newNode = new Node<E>(el,temp,temp.prev);
                if (temp.prev == null)
                    first = newNode;
                else temp.prev.next = newNode;

            }
            size++;
        }
    }

    public E getFirst(){
        return first.data;
    }

    public E getLast(){
        return last.data;
    }

    public Node<E> removeFirst() {
        if (first == null) throw new NoSuchElementException();
        Node<E> temp = first;
        first = first.next;
        if (first == null) {
            last = null;
        }
        else {
            first.prev = null;
        }
        size--;
        temp.next = null;
        temp.prev = null;
        return temp;
    }

    public Node<E> removeLast(){
        if (last == null) throw new NoSuchElementException();
        Node<E> temp = last;
        last= last.prev;
        if (last == null){
            first = null;
        }
        else{
            last.prev = null;
        }
        size --;
        temp.next = null;
        temp.prev = null;
        return temp;
    }

    Node<E> remove(int index) {
        if (index < 0 || index >= size) throw new NoSuchElementException();

        Node<E> temp = getNode(index);
        Node<E> prev = temp.prev;
        Node<E> next = temp.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            temp.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            temp.next = null;
        }

        size--;
        return temp;
    }

    Node<E> getNode(int index){
        if (size/2 > index){
            Node<E> x = first;
            for (int i = 0; i<index; i++){
                x = x.next;
            }
            return x;
        }
        else{
            Node<E> x = last;
            for (int i = size; i > index;i-- ){
                x =x.prev;
            }
            return x;
        }
    }
}
