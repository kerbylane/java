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
	
    protected V get(Object... keys) {
		return (V) _data.get(keys[0]);
	}
    
    public MultiDMap subset(Object... keys) {
        throw new UnsupportedOperationException("subset called with too many keys");
    }

	protected Object put(Object... keys) {
	    return _data.put(keys[0], keys[1]);
	}
    
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
