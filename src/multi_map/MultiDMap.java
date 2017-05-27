package multi_map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class MultiDMap {
    
    // protected int size;
    protected Map<Object, Object> _data;
    
    /**
     * Creates an 'inner' map suitable for use as the value entry in _data.
     * 
     * @return MultiDMap
     */
    protected abstract MultiDMap createInnerMap();
    
    protected MultiDMap() {
        // this.size = size;  // TODO: why would we need size?
        _data = new HashMap<Object, Object>();
    }
	
    // TODO: if keys shorter than length/size return inner map (should this be a new method?)
    protected Object get(Object... keys) {
        MultiDMap sub = (MultiDMap) _data.get(keys[0]);
        return sub == null ? null : sub.get(Arrays.copyOfRange(keys, 1, keys.length));
	}

    protected void put(Object... o) {
        MultiDMap inner = (MultiDMap) _data.computeIfAbsent(o[0], val -> createInnerMap());
	    
        inner.put(Arrays.copyOfRange(o, 1, o.length));
	}
	
	/**
	 * Generate iterable of all keys.
	 * 
	 * @return
	 */
	protected Stream<Object[]> entries() {
        return _data.entrySet().stream()
                .flatMap(
                        entry -> ((MultiDMap) entry.getValue())
                        .entries()
                        .map(innerEntry -> concat(new Object[] {entry.getKey()}, innerEntry))
                 );
    }
    
    protected Object[] concat(Object[] first, Object[] second) {
        Object[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
