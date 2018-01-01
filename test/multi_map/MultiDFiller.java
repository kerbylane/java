package multi_map;

import java.util.Arrays;
import java.util.Deque;
import java.util.stream.IntStream;

/**
 * Provides methods to populate MultiDMaps with data for perf testing.
 */
public class MultiDFiller {

    /**
     * Fills a {@link MultiDMap} with values from the range [1, width] at each level of the map.
     * The values will be an integer whose digits are those from each level leading to the value.
     * For instance, if the keys for a value are 1, 2, 3, the value will be 123.  When called
     * with a {@link MultiDMap2} and width 2 the entries will be [0,
     *
     * @param mdm       MultiDMap to be filled
     * @param width     number of keys each level will have
     */
    public static void completeFill(MultiDMap mdm, int width) {
        fillArray(mdm, width);
    }

    public static void fillStreams(MultiDMap mdm, int width, Deque<Integer> parents) {
        if (parents.size() == mdm.getDimensions()) {
            Integer[] entry = new Integer[mdm.getDimensions() + 1];
            parents.toArray(entry);

            Integer[] keys = new Integer[parents.size()];
            parents.toArray(keys);

            entry[mdm.getDimensions()] = digitArrayToInteger(keys);
            mdm.put(entry);

        } else {
            IntStream.range(1, width+1).forEach(
                    w -> {
                        parents.addLast(w);
                        fillStreams(mdm, width, parents);
                        parents.removeLast();
                    }
            );
        }
    }

    public static Integer digitArrayToInteger(Integer[] digits) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(digits).forEach(builder::append);
        return Integer.parseInt(builder.toString());
    }
    
    public static void fillArray(MultiDMap mdm, int width) {
        Integer[] keys = new Integer[mdm.getDimensions()];
        Object[] entry = new Object[mdm.getDimensions() + 1];
        int valueIndex = mdm.getDimensions();
        
        for (int i=0; i<mdm.getDimensions(); ++i) {
            keys[i] = 1;
            entry[i] = 1;
        }
        entry[valueIndex] = digitArrayToInteger(keys);
        
        /*
        If the key != width then increment it.
        else, reset key at level, then decrease level
        repeate
         */
        
        while (true) {
            // put the current entry
            entry[valueIndex] = digitArrayToInteger(keys);
            mdm.put(entry);
            
            // advance to next entry
            int level = mdm.getDimensions() - 1;
            while (level != -1 && keys[level] == width) {
                keys[level] = 1;
                entry[level] = 1;
                --level;
            }
            if (level == -1)
                break;
            
            ++keys[level];
            entry[level] = keys[level];
        }
    }
}
