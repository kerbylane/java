package multi_map;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

public class MultiDMap3Test {

    @Test
    public void testGet() {
        MultiDMap3<Integer, Integer, Integer, Integer> md3 = new MultiDMap3<>();
        
        md3.put(1, 1, 1, 111);
        Integer value = md3.get(1, 1, 1);
        Assert.assertEquals(new Integer(111), value);
        
        Stream<Object[]> entries = md3.entries();
        Object[] results = entries.toArray();
        Object[] expectedResult = new Object[] {1, 1, 1, 111};
        Assert.assertArrayEquals(
                expectedResult, (Object[]) results[0]
        );

        // Size works for submaps too
        Assert.assertEquals(1, md3.get(1).getSize());
    }

    @Test
    public void testSubset() {
        MultiDMap3<Integer, Integer, Integer, Integer> md3 = new MultiDMap3<>();

        md3.put(1, 1, 1, 111);
        MultiDMap2<Integer,Integer, Integer> submap = md3.get(1);
        Assert.assertEquals(111, (Object) submap.get(1, 1));

        Assert.assertTrue(submap instanceof MultiDMap2);

        try {
            Integer result = md3.get(1, 1, 1);
            Assert.assertEquals(111, result.intValue());
        } catch (IllegalArgumentException ex) {}
    }

    @Test
    public void testRemove() {
        MultiDMap3<Integer, Integer, Integer, Integer> md3 = new MultiDMap3<>();

        md3.put(1, 1, 1, 111);
        md3.put(1, 1, 2, 112);
        md3.put(2, 1, 1, 211);

        Assert.assertEquals(3, md3.getSize());

        // trying to remove a value that isn't in the map has no effect
        int removeCount = md3.remove(1,3);
        Assert.assertEquals(0, removeCount);
        Assert.assertEquals(3, md3.getSize());

        removeCount = md3.remove(2);
        Assert.assertEquals(1, removeCount);
        Assert.assertEquals(2, md3.getSize());

        removeCount = md3.remove(1,1, 1);
        // remove count is correct when all keys have been supplied
        Assert.assertEquals(1, removeCount);

        // new size is correct at all levels of the map
        Assert.assertEquals(1, md3.getSize());
        Assert.assertEquals(1, md3.get(1).getSize());

        removeCount = md3.remove(1);
        // removing a submap works
        Assert.assertEquals(1, removeCount);
        Assert.assertEquals(0, md3.getSize());

        // Does deletion of a submap with multiple entries work?
        md3.put(1, 1, 1, 111);
        md3.put(1, 1, 2, 112);
        removeCount = md3.remove(1);
        Assert.assertEquals(2, removeCount);
        Assert.assertEquals(0, md3.getSize());
    }
}
