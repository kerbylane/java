package multi_map;

import org.junit.Assert;
import org.junit.Test;

public class MultiDFillerTest {

    @Test
    public void completeFillTest() {
        MultiDMap2<Integer, Integer, Integer> md2 = new MultiDMap2<>();
        MultiDFiller.completeFill(md2, 2);

        Assert.assertEquals(new Integer(11), md2.get(1,1));
        Assert.assertEquals(new Integer(12), md2.get(1,2));
        Assert.assertEquals(new Integer(21), md2.get(2,1));
        Assert.assertEquals(new Integer(22), md2.get(2,2));
        
        Assert.assertEquals(4, md2.getSize());
    }

    @Test
    public void fillTests() {
        MultiDMap2<Integer, Integer, Integer> md2 = new MultiDMap2<>();
        MultiDFiller.completeFill(md2, 3);

        Assert.assertEquals(9, md2.getSize());
        Assert.assertEquals(new Integer(11), md2.get(1,1));
        Assert.assertEquals(new Integer(13), md2.get(1,3));
    }
}
