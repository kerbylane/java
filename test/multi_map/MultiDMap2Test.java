package multi_map;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiDMap2Test {

    @Test
    public void testGet() {
        MultiDMap2<Integer, Integer, Integer> md2 = new MultiDMap2<>();
        
        md2.put(1, 2, 3);
        Integer value = md2.get(1, 2);
        Assert.assertEquals(new Integer(3), value);
        
        Stream<Object[]> entries = md2.entries();
        Object[] results = entries.toArray();
        Object[] expectedResult = new Object[] { new Integer(1), new Integer(2), new Integer(3) };
        Assert.assertArrayEquals(
                expectedResult, (Object[]) results[0]
        );

        // Size works for submaps too
        Assert.assertEquals(1, md2.get(1).getSize());

        try {
            md2.get(1, 2, 3);
            Assert.fail("MultiDMap2.get(1) should fail as it has too few keys");
        } catch (IllegalArgumentException ex) {}
    }

    @Test
    public void testSubset() {
        MultiDMap2<Integer, Integer, Integer> md2 = new MultiDMap2<>();

        md2.put(1, 2, 3);
        MultiDMap1<Integer,Integer> submap = md2.get(1);
        Assert.assertEquals(3, (Object) submap.get(2));

        Assert.assertTrue(submap instanceof MultiDMap1);

        try {
            Integer result = md2.get(1, 2);
            Assert.assertEquals(3, result.intValue());
        } catch (IllegalArgumentException ex) {}
    }

    @Test
    public void testPut() {
        MultiDMap2<Integer, Integer, Integer> md2 = new MultiDMap2<>();

        Object array[] = {new Integer(1), new Integer(2), new Integer(3)};
        // TODO: Should this work?  There's no type checking with this.
        md2.put(array);
        Assert.assertEquals((Object) 3, (Object) md2.get(1,2));
    }

    @Test
    public void testEntries() {
        MultiDMap2<Integer, Integer, Integer> md2 = new MultiDMap2<>();

        md2.put(1, 1, 11);
        md2.put(1, 2, 12);

        Set<Object[]> entries = md2.entries().collect(Collectors.toSet());
        Assert.assertEquals(2, entries.size());

        Assert.assertEquals(
                1,
                entries.
                        stream().
                        filter(entry -> Arrays.equals(entry, new Integer[]{1,1,11})).
                        collect(Collectors.toList()).
                        size()
        );
        Assert.assertEquals(
                1,
                entries.
                        stream().
                        filter(entry -> Arrays.equals(entry, new Integer[]{1,2,12})).
                        collect(Collectors.toList()).
                        size()
        );
    }

    @Test
    public void testEquality() {
        MultiDMap2<Integer, Integer, Integer> md21 = new MultiDMap2<>();

        md21.put(1, 1, 11);
        md21.put(1, 2, 12);

        MultiDMap2<Integer, Integer, Integer> md22 = new MultiDMap2<>();

        md22.put(1, 1, 11);
        md22.put(1, 2, 12);

        Assert.assertEquals(md21, md22);

        md22.put(1,3, 13);
        Assert.assertNotEquals(md21, md22);
    }
}
