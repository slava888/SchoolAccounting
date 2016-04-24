package de.slava.schoolaccounting.util;

import org.junit.Test;
import static junit.framework.TestCase.*;

/**
 * @author by V.Sysoltsev
 */
public class SeqWatcherTest {
    @Test
    public void test_watch() {
        Integer status [] = {
            0
        };
        SeqWatcher<Integer> watcher = new SeqWatcher<>(
                new Integer[]{0,1,2,3}
            , (i) -> status[0] = 0
            , (i) -> status[0] = 1
            , () -> status[0] = 2);
        // start correctly
        watcher.update(0);
        assertEquals(1, (int)status[0]);
        // and then fail
        watcher.update(2);
        assertEquals(0, (int)status[0]);
        // start correctly
        for (int i : new Integer[]{0,1,2}) {
            watcher.update(i);
            assertEquals(1, (int)status[0]);
        }
        // and complete correctly
        watcher.update(3);
        assertEquals(2, (int)status[0]);
        // and now fail
        watcher.update(null); // should be accepted
        assertEquals(0, (int)status[0]);
    }
}
