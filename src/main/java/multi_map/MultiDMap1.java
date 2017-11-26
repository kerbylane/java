package multi_map;

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

    public int getSize() { return _data.size(); }

    /**
     * Overrides @{@link MultiDMap} implementation.  Not to be used by other callers.
     * @param keys  Array of keys, one for each dimension of the instance
     * @return
     */
    protected V get(Object... keys) {
		return (V) _data.get(keys[0]);
	}

    public V get(K1 k1) { return (V) _data.get(k1); }

    /**
     * Overrides @{@link MultiDMap} implementation.  Not to be used by other callers.
     * @param keys  Array of keys, one for each dimension of the instance
     * @return
     */
	protected Object put(Object... keys) {
	    return _data.put(keys[0], keys[1]);
	}

	public Object put(K1 k1, V v) {
        return _data.put(k1, v);
    }

    public int remove(K1 k1) {
	    if (_data.containsKey(k1)) {
	        _data.remove(k1);
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
        return _data
                .entrySet()
                .stream()
                .map(entry -> new Object[] {entry.getKey(), entry.getValue()});
    }

    protected Stream<Object[]> constructiveEntries(int depth) {
        return _data.entrySet().stream().map(
                entry -> {
                    Object[] result = new Object[depth + 1];
                    result[depth] = entry.getValue();
                    result[depth-1] = entry.getKey();
                    return result;
                }
        );
    }
}
