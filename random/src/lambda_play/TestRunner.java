package lambda_play;

import java.util.Arrays;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Here are the arguments:");
        StringDoubler doubler = new StringDoubler();
        List<String> words = Arrays.asList(args);
        words.stream()
            .map(s -> doubler.doubleIt(s) )
            .forEach(s -> System.out.println(s));
    }
}
