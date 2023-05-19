package lib.dependencies;

/**
 * 
 *
 */
public class BoundedQueue<E> {
    final E [] data;
    int head=0;
    int size = 0;

    @SuppressWarnings("unchecked")
    public BoundedQueue(int maxSize) {
        data = (E[]) new Object[maxSize];
    }

    private boolean isFull() {
        return size == data.length;
    }

    /**
     * Adds an element to the queue's tail.
     * @throws IllegalStateException
     */
    public void push(E e) throws IllegalStateException {
        if (isFull())
            throw new IllegalStateException("The queue is full");

        data[(head+size) % data.length] = e;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return An elements from the queue's head.
     */
    public E pop() {
        if (isEmpty())
            throw new IllegalStateException("can not pop fron an empty queue");

        E e = peek();
        head = (head+1) % data.length;
        size--;
        return e;
    }

    @SuppressWarnings ("Unchecked")
    public E peek() {
        return isEmpty() ? null: (E) data[head];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (int i=0; i<size; i++) {
            str.append(data[i].toString());
            if (i<size-1)
                str.append(", ");
        }
        str.append(']');
        return str.toString();
    }
}
