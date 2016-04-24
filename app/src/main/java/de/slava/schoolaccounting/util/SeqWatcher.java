package de.slava.schoolaccounting.util;

import de.slava.schoolaccounting.AccessRight;
import de.slava.schoolaccounting.functional.Consumer;

/**
 * @author by V.Sysoltsev
 */
public class SeqWatcher<T> {
    private final T[] sequence;
    private int idx = 0;
    private final Consumer<Integer> onFail;
    private final Consumer<Integer> onStepCorrect;
    private final Runnable onComplete;

    public SeqWatcher(T[] sequence, Consumer<Integer> onFail, Consumer<Integer> onStepCorrect, Runnable onComplete) {
        this.sequence = sequence;
        this.onFail = onFail;
        this.onStepCorrect = onStepCorrect;
        this.onComplete = onComplete;
    }

    public void update(T value) {
        if (idx < sequence.length && value == sequence[idx]) {
            // advance
            idx ++;
            if (idx >= sequence.length) {
                // completed
                if (onComplete != null)
                    onComplete.run();
            } else {
                // correct step
                if (onStepCorrect != null)
                    onStepCorrect.accept(idx-1);
            }
        } else {
            // nope
            int oldIdx = idx;
            idx = 0;
            onFail.accept(oldIdx);
        }
    }
}
