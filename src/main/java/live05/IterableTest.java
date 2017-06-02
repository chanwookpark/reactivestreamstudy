package live05;

import java.util.Iterator;

/**
 * @author chanwook
 */
public class IterableTest {

    public static void main(String[] args) {
        Iterable<Integer> iterable = () ->
                new Iterator<Integer>() {
                    int i = 0;
                    final static int MAX = 10;

                    public boolean hasNext() {
                        return i < MAX;
                    }

                    public Integer next() {
                        return ++i;
                    }
                };

        for (Integer i : iterable) {
            System.out.println(i);
        }
    }
}
