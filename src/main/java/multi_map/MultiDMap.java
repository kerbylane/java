package multi_map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    public int getSize() { return size; }

    /**
     * Implementation of internal get logic.  Note that there is not type checking, hence its 'protected' status.
     * @param keys  Array of keys, one for each level of the instance
     * @return      Value found in innermost map
     */
    protected Object get(Object... keys) {
        if (keys.length > dimensions)
            throw new IllegalArgumentException("incorrect number of keys, accepts at most " + dimensions + ", got " + keys.length);
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
     * Implementation of internal put logic.  Note that there is not type checking, hence its 'protected' status.
     *
     * @param o Array containing keys for each level of the map and the relevant value.
     */
    protected Object put(Object... o) {
        if (o.length > dimensions + 1)
            throw new IllegalArgumentException("incorrect number of arguments, must be " + (dimensions+1) + ", got " + o.length);

        return putInner(o);
	}

    protected Object putInner(Object... o) {
        int keyPos = o.length - 1 - dimensions;
        MultiDMap inner = (MultiDMap) data.computeIfAbsent(o[keyPos], val -> createInnerMap());

        Object value = inner.putInner(o);
        if (value == null)
            ++size;

        return value;
    }
	
	/**
	 * Generate iterable of all key-values, similar to @{@link Map}'s entries method..
	 * 
	 * @return  Stream of arrays in which each position holds the value for the relevant dimension
	 */
    protected Stream<Object[]> entries() {
        return constructiveEntries(dimensions);
    }

    protected Stream<Object[]> constructiveEntries(int maxDimensions) {
        // Builds up the arrays by creating them only at the value level and then populating
        // the keys of the arrays in reverse order.  This is intended to eliminate the use
        // of temporary, intermediate arrays.
        return data.entrySet().stream().flatMap(
                entry -> ((MultiDMap)entry.getValue()).constructiveEntries(maxDimensions).peek(
                        array -> array[maxDimensions - dimensions] = entry.getKey()
                )
            );
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MultiDMap)) return false;

        MultiDMap that = (MultiDMap) obj;
        return
                that.dimensions == this.dimensions &&
                that.size == this.size &&
                this.entries().allMatch(e -> that.get(Arrays.copyOf(e, e.length - 1)) == e[e.length - 1]);
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
            throw new IllegalArgumentException("incorrect number of keys, accepts at most " + dimensions + ", got " + keys.length);

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
