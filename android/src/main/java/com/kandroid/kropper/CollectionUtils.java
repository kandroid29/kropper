package com.kandroid.kropper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class CollectionUtils {

    static <T> List<T> dropLastWhile(List<T> source, ContentUtils.Predicate<T> predicate) {
        if (!source.isEmpty()) {
            ListIterator<T> iterator = source.listIterator(source.size());
            while (iterator.hasPrevious()) {
                if (!predicate.test(iterator.previous())) {
                    return take(source, iterator.nextIndex() + 1);
                }
            }
        }
        return new ArrayList<>();
    }

    static <T> List<T> take(Iterable<T> iterable, int n) {
        if (n < 0) return null;
        if (n == 0) return new ArrayList<>();
        if (iterable instanceof Collection) {
            Collection<T> collection = (Collection<T>) iterable;
            if (n >= collection.size()) {
                return toList(collection);
            }
            if (n == 1) {
                List<T> list = new ArrayList<>();
                if (collection.size() >= 1) {
                    list.add(iterable.iterator().next());
                }
                return list;
            }
        }

        int count = 0;
        List<T> list = new ArrayList<>(n);
        for (T item : iterable) {
            if (count++ == n)
                break;
            list.add(item);
        }
        return list;
    }

    static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            Collection<T> collection = (Collection<T>) iterable;
            switch (collection.size()) {
                case 0:
                    return new ArrayList<>();
                default:
                    List<T> list = new ArrayList<>();
                    for (T item : iterable) {
                        list.add(item);
                    }
                    return list;
            }
        }

        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }
}
