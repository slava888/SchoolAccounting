package de.slava.schoolaccounting.util;

import java.text.Collator;
import java.util.Comparator;

import de.slava.schoolaccounting.functional.Function;

/**
 * NullSafe comparator with extraction function. Uses default locale collator for strnig comparison.
 *
 * @author by V.Sysoltsev
 */
public class NullSafeCompare<T> implements Comparator<T> {
    private final Function<T, String> textExtractor;
    private final Collator collator = Collator.getInstance();

    public NullSafeCompare() {
        this(Object::toString);
    }

    public NullSafeCompare(Function<T, String> textExtractor) {
        this.textExtractor = textExtractor;
    }

    @Override
    public int compare(T lhs, T rhs) {
        if (lhs == rhs)
            return 0;
        if (lhs == null)
            return -1;
        if (rhs == null)
            return 1;
        String s1 = textExtractor.apply(lhs);
        String s2 = textExtractor.apply(rhs);
        if (s1 == s2)
            return 0;
        if (s1 == null)
            return -1;
        if (s2 == null)
            return 1;
        return collator.compare(s1, s2);
    }
}
