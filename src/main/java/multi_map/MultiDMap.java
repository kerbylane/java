package multi_map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public abstract class MultiDMap {

    // TODO: add remove() (it will need to reduce the size value)
    
    protected int dimensions;
    protected Map<Object, Object> _data;
    private int size = 0;
    
    /**
     * Creates an 'inner' map suitable for use as the value entry in _data.
     * 
     * @return MultiDMap
     */
    protected abstract MultiDMap createInnerMap();
    
    protected MultiDMap(int dimensions) {
        this.dimensions = dimensions;
        _data = new HashMap<>();
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

        MultiDMap sub = (MultiDMap) _data.get(keys[0]);
        if (sub == null || keys.length == 1)
            return sub;

        return sub.get(Arrays.copyOfRange(keys, 1, keys.length));
    }

    /**
     * Implementation of internal put logic.  Note that there is not type checking, hence its 'protected' status.
     * @param o Array containing keys for each level of the map and the relevant value.
     */
    protected Object put(Object... o) {
        MultiDMap inner = (MultiDMap) _data.computeIfAbsent(o[0], val -> createInnerMap());

        Object value = inner.put(Arrays.copyOfRange(o, 1, o.length));
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

    protected Stream<Object[]> constructiveEntries(int depth) {
        // Builds up the arrays by creating them only at the value level and then populating
        // the keys of the arrays in reverse order.  This is intended to eliminate the use
        // of temporary, intermediate arrays.
        return _data.entrySet().stream().flatMap(
                entry -> ((MultiDMap)entry.getValue()).constructiveEntries(depth).map(
                        array -> {
                            array[depth - dimensions] = entry.getKey();
                            return array;
                        }
                )
            );
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MultiDMap)) return false;

        MultiDMap that = (MultiDMap) obj;
        if (that.dimensions != this.dimensions || that.size != this.size) return false;

        return this.entries().allMatch(e -> that.get(Arrays.copyOf(e, e.length - 1)) == e[e.length - 1]);
    }

    public void remove(Object... keys) {
        // Goals:
        // - remove node identified by keys
        // - if any maps/submaps which contained keys are left empty remove those from their parent
        // - reduce size value as appropriate.
    }
}
