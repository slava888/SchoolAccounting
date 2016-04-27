package de.slava.schoolaccounting.util;


import android.os.Handler;

import de.slava.schoolaccounting.functional.Consumer;

/**
 * Class to sync some value with another with some delay
 * @author by V.Sysoltsev
 */
public class DelayedSync<T> implements Runnable {
    private final Handler handler = new Handler();
    private final Consumer<T> consumer;
    private T current;

    public DelayedSync(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void syncDelayed(T value, long delayMillis) {
        handler.removeCallbacks(this);
        this.current = value;
        handler.postDelayed(this, delayMillis);
    }

    public void syncImmediately(T value) {
        handler.removeCallbacks(this);
        this.current = value;
        run();
    }

    @Override
    public void run() {
        consumer.accept(current);
    }
}
