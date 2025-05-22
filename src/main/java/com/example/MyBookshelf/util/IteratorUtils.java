package com.example.MyBookshelf.util;

import com.example.MyBookshelf.iterator.FilterIterator;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class IteratorUtils {

    public static <T> Iterator<T> filterIterator(List<T> list, Predicate<T> predicate) {
        return new FilterIterator<>(list.iterator(), predicate);
    }
}
