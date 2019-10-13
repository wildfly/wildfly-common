package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

/**
 */
final class SkippingCodePointIterator extends CodePointIterator {
    private final CodePointIterator iter;
    private final IntPredicate predicate;

    SkippingCodePointIterator(final CodePointIterator iter, final IntPredicate predicate) {
        this.iter = iter;
        this.predicate = predicate;
    }

    public boolean hasNext() {
        return iter.hasNext() && ! skip(peekNext());
    }

    public boolean hasPrevious() {
        return iter.hasPrevious() && ! skip(peekPrevious());
    }

    public int next() {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }

        return iter.next();
    }

    public int peekNext() throws NoSuchElementException {
        if (! iter.hasNext()) {
            throw new NoSuchElementException();
        }

        int next = seekNext(iter.peekNext());

        if (! skip(next)) {
            return next;
        }

        return next;
    }

    private int seekNext(int next) throws NoSuchElementException {
        if (! iter.hasNext()) {
            return next;
        }

        next = iter.next();

        if (skip(next)) {
            return seekNext(next);
        }

        iter.previous();

        return next;
    }

    public int previous() {
        if (! hasPrevious()) {
            throw new NoSuchElementException();
        }

        return iter.previous();
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! iter.hasPrevious()) {
            throw new NoSuchElementException();
        }

        int prev = seekPrev(iter.peekPrevious());

        if (! skip(prev)) {
            return prev;
        }

        return prev;
    }

    private int seekPrev(int prev) throws NoSuchElementException {
        if (! iter.hasPrevious()) {
            return prev;
        }

        prev = iter.previous();

        if (skip(prev)) {
            return seekPrev(prev);
        }

        iter.next();

        return prev;
    }

    public long getIndex() {
        return iter.getIndex();
    }

    private boolean skip(int c) {
        return predicate.test(c);
    }
}
