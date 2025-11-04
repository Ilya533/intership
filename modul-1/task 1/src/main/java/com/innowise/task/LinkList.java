package com.innowise.task;

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
        final Node<E> newNode = new Node<E>(el, temp,null);
        first =newNode;
        if (temp == null) last = newNode;
        else temp.prev = newNode;
        size++;
    }

    public void addLast(E el){
        Node<E> temp = last;
        final Node<E> newNode = new Node<E>(el,null,temp);
        last = newNode;
        if (temp == null) first = newNode;
        else temp.next = newNode;
        size++;
    }

    public void add(int index, E el) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();

        if (index == 0) {
            addFirst(el);
        } else if (index == size) {
            addLast(el);
        } else {
            Node<E> temp = getNode(index);
            Node<E> newNode = new Node<>(el, temp, temp.prev);
            temp.prev.next = newNode;
            temp.prev = newNode;
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

    public Node<E> remove(int index) {
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

    public Node<E> getNode(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();

        if (index < size / 2) {
            Node<E> x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }

}
