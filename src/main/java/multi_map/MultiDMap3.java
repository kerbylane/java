package multi_map;

@SuppressWarnings("unchecked")
public class MultiDMap3<K1,K2,K3,V> extends MultiDMap {
    
    public MultiDMap3() {
        super(3);
    }

    @Override
    protected MultiDMap createInnerMap() {
        return new MultiDMap2<K2,K3,V>();
    }
	
	public V get(K1 k1, K2 k2, K3 k3) {
	    return (V) super.get(k1, k2, k3);
	}

	public void put(K1 k1, K2 k2, K3 k3, V v) {
	    super.put(k1, k2, k3, v);
	}
}
