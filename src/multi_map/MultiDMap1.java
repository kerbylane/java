package multi_map;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class MultiDMap1<K1,V> extends MultiDMap {

    @Override
    protected MultiDMap createInnerMap() {
        throw new RuntimeException("Inner-most map cannot create an innner map");
    }
	
    public MultiDMap1() {
	    super(1);
        _data = (Map<Object, Object>) new HashMap<K1,V>();
    }
    
    public V get(K1 k) { 
        Object[] keys = {k};
        return (V) get(keys);
    }
	
//    protected V get(Object... keys) { 
//		return (V) _data.get(keys[0]);
//	}
    
    public MultiDMap subset(Object... keys) {
        throw new UnsupportedOperationException("subset called with too many keys");
    }
    
    public void put(K1 k1, V v) {
        Object[] entries = {k1, v};
        put(entries);
    }

	protected void put(Object... keys) {
	    _data.put(keys[0], keys[1]);
	}
	
	/**
	 * Generate iterable of all keys.
	 * 
	 * @return
	 */
    Stream<K1> keys() {
	    return (Stream<K1>) _data.keySet().stream();
	}
	
    Stream<V> values() {
	    return (Stream<V>) _data.values().stream();
	}
    
    protected Stream<Object[]> entries() {
        return _data
                .entrySet()
                .stream()
                .map(entry -> new Object[] {entry.getKey(), entry.getValue()});
    }
}
