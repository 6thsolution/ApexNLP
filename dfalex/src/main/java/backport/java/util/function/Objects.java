package backport.java.util.function;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class Objects {

    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
