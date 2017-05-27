package lambda_play;

public class StringDoubler {
    // String (s) -> { ss; }
    String doubleIt(String s) {
        char[] originalLetters = s.toCharArray();
        char[] letters = new char[s.length() * 2];
        for (int i=0; i<s.length(); ++i) {
            letters[2 * i]      = originalLetters[i];
            letters[2 * i + 1]  = originalLetters[i];
        }
        return new String(letters);
    }
}
