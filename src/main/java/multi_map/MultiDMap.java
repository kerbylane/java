package multi_map;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A multi-level map.
 */
public abstract class MultiDMap {
    
    protected Map<Object, Object> data;
    private int dimensions;
    private int size = 0;
    
    /**
     * Creates an 'inner' map suitable for use as the value entry of data.
     * 
     * @return MultiDMap
     */
    protected abstract MultiDMap createInnerMap();
    
    protected MultiDMap(int dimensions) {
        this.dimensions = dimensions;
        data = new HashMap<>();
    }

    public int getDimensions() { return dimensions; }

    /**
     * The number of values with distinct keys in the instance.
     * @return  The number of values contained
     */
    public int getSize() { return size; }

    /**
     * Implementation of internal get logic.  Note that there is no type checking, hence its 'protected' status.
     * @param keys  Array of keys, one for each level of the instance
     * @return      Value found in innermost map
     */
    protected Object get(Object... keys) {
        if (keys.length > dimensions)
            throw new IllegalArgumentException(
                    "incorrect number of keys, accepts at most " + dimensions + ", got " + keys.length);
        return getInner(dimensions, keys);
    }

    protected Object getInner(int maxDimensions, Object... keys) {
        int keyPos = maxDimensions - dimensions;
        MultiDMap sub = (MultiDMap) data.get(keys[keyPos]);
        if (sub == null || keyPos == keys.length - 1)
            return sub;

        return sub.getInner(maxDimensions, keys);
    }

    /**
     * Implementation of internal put logic.  Note that there is no type checking, hence its 'protected' status.
     * This is done without testing the length to avoid testing at every level.
     *
     * @param   o Array containing keys for each level of the map and the relevant value.
     * @return  if there was already a value at the specified key, that value is returned, otherwise null
     */
    protected Object put(Object... o) {
        if (o.length > dimensions + 1)
            throw new IllegalArgumentException(
                    "incorrect number of arguments, must be " + (dimensions+1) + ", got " + o.length);

        return putInner(o);
	}

    /**
     * Implements the putting operation given an array which contains the complete argument
     * to the public put statement that was originally called.
     *
     * @param   o Array containing keys for each level of the map and the relevant value.
     * @return  if there was already a value at the specified key, that value is returned, otherwise null
     */
    protected Object putInner(Object... o) {
        int keyPos = o.length - 1 - dimensions;
        MultiDMap inner = (MultiDMap) data.computeIfAbsent(o[keyPos], val -> createInnerMap());

        Object value = inner.putInner(o);
        if (value == null)
            ++size;

        return value;
    }
	
	/**
	 * Generate iterable of all key-values, similar to @{@link Map}'s entries method.
	 * 
	 * @return  Stream of arrays in which each position holds the value for the relevant dimension
	 */
    protected Stream<Object[]> entries() {
        return constructiveEntries(dimensions);
    }

    protected Stream<Object[]> constructiveEntries(int maxDimensions) {
        // Builds up the arrays by creating them only at the value level and then populating
        // the keys of the arrays in reverse order.  This is intended to eliminate the use
        // of temporary, intermediate arrays, or a single Deque that is updated by multiple
        // processes (if we're running in parallel, is this valid).
        return data.entrySet().stream().flatMap(
                entry -> ((MultiDMap)entry.getValue()).constructiveEntries(maxDimensions).map(
                        array -> {
                            array[maxDimensions - dimensions] = entry.getKey();
                            return array;
                        }
                )
            );
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MultiDMap)) return false;

        MultiDMap that = (MultiDMap) obj;
        return
                that.dimensions == this.dimensions &&
                that.size == this.size &&
                this.entries().allMatch(e -> that.get(Arrays.copyOf(e, e.length - 1)) == e[e.length - 1]);
    }

    interface NestedMapFilter extends Predicate<Object[]> {
        @Override
        boolean test(Object[] entry);
    }

    /**
     * Provides a way to apply a filter when generating entries which avoids generation of subtrees
     * of entries.  This could be faster than iterating over all entries and filtering externally in
     * some cases.
     *
     * The filter will be called repeatedly during the execution of this method. It will be called
     * once for every entry in each MultiDMap contained in the instance. The argument passed will
     * be an array whose length matches the depth of the MultiDMap being evaluated. So, for instance,
     * every key in the top level MultiDMap will be passed to the filter in an array of size 1.
     *
     * Therefore the filter must be able to process arrays of varying length and respond appropriately.
     *
     * @param filter    {@link NestedMapFilter} that will determine if submaps should be included or excluded
     * @return          Stream of arrays which satisfy the conditions of the filter
     */
    protected Stream<Object[]> filteredEntries(NestedMapFilter filter) {
        return filteredEntries(filter, dimensions, new Object[0]);
    }

    protected Stream<Object[]> filteredEntries(NestedMapFilter filter, int maxDimensions, Object[] parentKeys) {
        return data.entrySet().stream().flatMap(
                entry -> {
                    Object[] keys = Arrays.copyOf(parentKeys, parentKeys.length + 1);
                    keys[keys.length-1] = entry.getKey();

                    if (!filter.test(keys))
                        return Stream.empty();

                    return ((MultiDMap) entry.getValue()).filteredEntries(filter, maxDimensions, keys).map(
                            array -> {
                                array[maxDimensions - dimensions] = entry.getKey();
                                return array;
                            }
                    );
                }
        );
    }

    /**
     * Removes a value or an entire submap of this instance.  If the number of keys supplied
     * is less than the number of dimensions the submap found with those keys will be removed.
     *
     * @param keys  Array of keys identifying what is to be deleted
     * @return      Number of values removed
     */
    protected int remove(Object... keys) {
        if (keys.length > dimensions)
            throw new IllegalArgumentException(
                    "incorrect number of keys, accepts at most " + dimensions + ", got " + keys.length);

        return removeInner(dimensions, keys);
    }

    /**
     * Internal implementation of removal.  This approach avoids the need to create new arrays for each level.
     *
     * @param maxDimensions Number of dimensions of the top level MultiDMap
     * @param keys          Array of keys identifying what is to be deleted
     * @return              Number of values removed
     */
    private int removeInner(int maxDimensions, Object... keys) {
        int pos = maxDimensions - dimensions;
        Object inner = data.get(keys[pos]);
        if (inner == null)
            return 0;

        int removed;
        if (keys.length - 1 == pos) {
            // Either we are removing a single value or an entire submap
            removed = dimensions == 1 ? 1 : ((MultiDMap) inner).getSize();
            data.remove(keys[pos]);
        } else {
            MultiDMap innerMap = (MultiDMap) inner;
            removed = innerMap.removeInner(maxDimensions, keys);
            if (innerMap.getSize() == 0) {
                // innerMap is now empty, so we can remove it too
                data.remove(keys[pos]);
            }
        }

        size -= removed;
        return removed;
    }
}
