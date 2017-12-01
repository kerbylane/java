package multi_map;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.IntStream;

/**
 * Provides methods to populate MultiDMaps with data for perf testing.
 */
public class MultiDFiller {

    /**
     * Fills a {@link MultiDMap} with values from the range [0, width) at each level of the map.
     * The values will be an integer whose digits are those from each level leading to the value.
     * For instance, if the keys for a value are 1, 2, 3, the value will be 123.
     *
     * @param mdm       MultiDMap to be filled
     * @param width     number of keys each level will have
     */
    public static void completeFill(MultiDMap mdm, int width) {
        fillStreams(mdm, width, new ArrayDeque<>());
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
            IntStream.range(0, width).forEach(
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
}
