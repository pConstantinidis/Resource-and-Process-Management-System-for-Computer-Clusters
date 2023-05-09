package lib.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * 
 *
 */
public class BoundedQueue<E> {
    final Object [] data;
    int head=0;
    int size = 0;

    public BoundedQueue(int maxSize) {
        data = new Object[maxSize];
    }

    private boolean isFull() {
        return size == data.length;
    }

    private void push(E e) throws IllegalStateException {
        if (isFull())
            throw new IllegalStateException("The queue is full");

        data[(head+size) % data.length] = e;
        size++;
    }

    private boolean isEmpty() {
        return size == 0;
    }

    private E pop() {
        if (isEmpty())
            throw new IllegalStateException("can not pop fron an empty queue");

        E e = peek();
        head = (head+1) % data.length;
        size--;
        return e;
    }

    @SuppressWarnings ("unchecked")
    private E peek() {
        return isEmpty() ? null: (E) data[head];
    }
}
