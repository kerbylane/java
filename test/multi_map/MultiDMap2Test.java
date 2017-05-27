package multi_map;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

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
            (Object[]) results[0], expectedResult 
        );
    }

}