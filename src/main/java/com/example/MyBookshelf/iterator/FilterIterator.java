package com.example.MyBookshelf.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FilterIterator<T> implements Iterator<T> {
    private final Iterator<T> inner;
    private final Predicate<T> predicate;
    private T nextMatch;
    private boolean hasNextMatchComputed = false;

    public FilterIterator(Iterator<T> inner, Predicate<T> predicate) {
        this.inner = inner;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        if (hasNextMatchComputed) {
            return nextMatch != null;
        }
        hasNextMatchComputed = true;
        while (inner.hasNext()) {
            T candidate = inner.next();
            if (predicate.test(candidate)) {
                nextMatch = candidate;
                return true;
            }
        }
        nextMatch = null;
        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        hasNextMatchComputed = false;
        return nextMatch;
    }
}

