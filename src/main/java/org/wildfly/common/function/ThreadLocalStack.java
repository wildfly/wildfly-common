package org.wildfly.common.function;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A thread local stack data structure. In order to avoid memory churn the underlying
 * ArrayDeque is never freed. If we remove the deque when it is empty then this results
 * in excessive deque allocations.
 */
public class ThreadLocalStack<E> {

    private static final Object NULL_VALUE = new Object();

    private final ThreadLocal<Deque<Object>> deque = ThreadLocal.withInitial(ArrayDeque::new);

    public void push(E item) {
        Deque<Object> st = deque.get();
        if(item == null) {
            st.push(NULL_VALUE);
        } else {
            st.push(item);
        }
    }

    public E peek() {
        Deque<Object> st = deque.get();
        Object o =  st.peek();
        if(o == NULL_VALUE) {
            return null;
        } else {
            return (E) o;
        }
    }

    public E pop() {
        Deque<Object> st = deque.get();
        Object o =  st.pop();
        if(o == NULL_VALUE) {
            return null;
        } else {
            return (E) o;
        }
    }

    public boolean isEmpty() {
        return deque.get().isEmpty();
    }

}