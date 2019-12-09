package com.python.pythonator.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to handle list diffs
 */
public class ListUtil {

    /**
     * @param old The old state of a collection
     * @param cur The new state of the collection
     * @param <T> The type of the collection
     * @return All removed items (in old collection but not in new collection)
     */
    public static @NonNull <T> List<T> getRemoved(@Nullable Collection<T> old, @Nullable Collection<T> cur) {
        if (old == null)
            return new ArrayList<>();
        if (cur == null)
            return new ArrayList<>(old);
        List<T> retList = new ArrayList<>();
        for (T t : old)
            if (!cur.contains(t))
                retList.add(t);
        return retList;
    }

    /**
     * @param old The old state of a collection
     * @param cur The new state of the collection
     * @param <T> The type of the collection
     * @return All added items (in new collection but not in old collection)
     */
    public static @NonNull <T> List<T> getAdded(@Nullable Collection<T> old, @Nullable Collection<T> cur) {
        if (cur == null)
            return new ArrayList<>();
        if (old == null) {
            return new ArrayList<>(cur);
        }
        List<T> retList = new ArrayList<>();
        for (T t : cur)
            if (!old.contains(t))
                retList.add(t);
        return retList;
    }
}
