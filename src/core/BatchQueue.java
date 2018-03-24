package core;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Colinhu on 3/24/18.
 */
public class BatchQueue<T> {
    private AtomicReference<Node> head;
    private AtomicReference<Node> tail;
    public BatchQueue(){
        Node sentinel = new Node(null);
        this.head = new AtomicReference<Node>(sentinel);
        this.tail = new AtomicReference<Node>(sentinel);
    }
    /**
     * Append item to end of queue.
     * @param item
     */
    public void enq(T item) {
        if (item == null) throw new NullPointerException();
        Node node = new Node(item); // allocate & initialize new node
        while (true) {		 // keep trying
            Node last = tail.get();    // read tail
            Node next = last.next.get(); // read next
            if (last == tail.get()) { // are they consistent?
                if(next == null){
                    if(last.next.compareAndSet(next, node)){
                        tail.compareAndSet(last, node);
                        return;
                    }
                }
                else{
                    Node lastl = next;
                    next = next.next.get();
                    if(next == null){
                        if(lastl.next.compareAndSet(next, node)){
                            tail.compareAndSet(last, node);
                            return;
                        }
                    }
                    else
                        tail.compareAndSet(last, next);
                }
            }
        }
    }
    /**
     * Remove and return head of queue.
     * @return remove first item in queue
     * @throws NoSuchElementException
     */
    public T deq() throws NoSuchElementException {
        while (true) {
            Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();
            if (first == head.get()) {// are they consistent?
                if (first == last) {    // is queue empty or tail falling behind?
                    if (next == null) {    // is queue empty?
                        throw new NoSuchElementException();
                    }
                    // tail is behind, try to advance
                    tail.compareAndSet(last, next);
                } else {
                    T value = next.value; // read value before dequeuing
                    if (head.compareAndSet(first, next))
                        return value;
                }
            }
        }
    }

    public class Node {
        public T value;
        public AtomicReference<Node> next;

        public Node(T value) {
            this.value = value;
            this.next  = new AtomicReference<Node>(null);
        }
    }
}
