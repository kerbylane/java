package multi_map;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class MultiDMap1<K1,V> extends MultiDMap {

    @Override
    protected MultiDMap createInnerMap() {
        throw new RuntimeException("Inner-most map cannot create an inner map");
    }
	
    public MultiDMap1() {
	    super(1);
    }

    public int getSize() { return data.size(); }

    /**
     * Overrides @{@link MultiDMap} implementation.  Not to be used by other callers.
     * @param keys  Array of keys, one for each dimension of the instance
     * @return
     */
    protected Object getInner(int maxDimensions, Object... keys) {
		return (V) data.get(keys[keys.length - 1]);
	}

    public V get(K1 k1) { return (V) data.get(k1); }

    /**
     * Overrides @{@link MultiDMap} implementation.  Not to be used by other callers.
     * @param keys  Array of keys, one for each dimension of the instance
     * @return
     */
	protected Object putInner(Object... keys) {
	    return data.put(keys[keys.length - 2], keys[keys.length - 1]);
	}

	public Object put(K1 k1, V v) {
        return data.put(k1, v);
    }

    public int remove(K1 k1) {
	    if (data.containsKey(k1)) {
	        data.remove(k1);
	        return 1;
        }
        return 0;
    }

    protected int remove(Object... keys) {
	    return remove((K1) keys[0]);
    }

    /**
     * Overrides @{@link MultiDMap} implementation.  Not to be used by other callers.
     * @return
     */
    protected Stream<Object[]> entries() {
        return data
                .entrySet()
                .stream()
                .map(entry -> new Object[] {entry.getKey(), entry.getValue()});
    }

    protected Stream<Object[]> constructiveEntries(int maxDimensions) {
        return data.entrySet().stream().map(
                entry -> {
                    Object[] result = new Object[maxDimensions + 1];
                    result[maxDimensions] = entry.getValue();
                    result[maxDimensions -1] = entry.getKey();
                    return result;
                }
        );
    }

    @Override
    protected Stream<Object[]> filteredEntries(NestedMapFilter filter, int maxDimensions, Object[] parentKeys) {
        return data.
                entrySet().
                stream().
                filter(
                    entry -> {
                        Object[] keys = Arrays.copyOf(parentKeys, parentKeys.length + 2);
                        keys[keys.length-2] = entry.getKey();
                        keys[keys.length-1] = entry.getValue();
                        return filter.test(keys);
                    }
                ).map(
                    entry -> {
                        Object[] result = new Object[maxDimensions + 1];
                        result[maxDimensions] = entry.getValue();
                        result[maxDimensions -1] = entry.getKey();
                        return result;
                    }
                );
    }
}
