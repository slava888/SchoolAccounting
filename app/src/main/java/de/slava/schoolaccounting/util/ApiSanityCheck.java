package de.slava.schoolaccounting.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * This utility class checks that Java runtime is sane in regard of using constructs, that may not be available for the platform.
 * @author by V.Sysoltsev
 */
public class ApiSanityCheck {
    private static interface NoThrowCloseable extends AutoCloseable {
        @Override
        public void close();
    }

    public static class SanityCheckFailedException extends Exception {
        public SanityCheckFailedException(String detailMessage) {
            super(detailMessage);
        }
    };

    private final Context context;

    public ApiSanityCheck(Context context) {
        this.context = context;
    }

    public void check() throws SanityCheckFailedException {
        boolean closed[] = { false };
        NoThrowCloseable closeable = () -> {
            closed[0] = true;
        };
        try (NoThrowCloseable c = closeable){
            // nothing
        }
        assertTrue(closed[0], "Autocloseable is not closed");
    }

    private void assertTrue(boolean condition, String message) throws SanityCheckFailedException {
        if (condition != true)
            fail(message);
    }

    private void fail(String message) throws SanityCheckFailedException {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Sanity check failed");
        dlgAlert.setPositiveButton("Exit", (dialog, which) -> {
            System.exit(-1);
        });
        dlgAlert.create().show();
        throw new SanityCheckFailedException(message);
    }
}
