package multi_map;

@SuppressWarnings("unchecked")
public class MultiDMap2<K1,K2,V> extends MultiDMap {
    
    public MultiDMap2() {
        super(2);
    }

    @Override
    protected MultiDMap createInnerMap() {
        return new MultiDMap1<K2,V>();
    }
	
	public V get(K1 k1, K2 k2) { return (V) super.get(k1, k2); }

    public MultiDMap1<K2,V> get(K1 k1) { return (MultiDMap1<K2, V>) super.get(k1); }

	public void put(K1 k1, K2 k2, V v) {
	    super.put(k1, k2, v);
	}
}
