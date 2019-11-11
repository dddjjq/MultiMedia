package com.ktc.media.util;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorUtil {

    private static final String TAG = ExecutorUtil.class.getSimpleName();

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(100, TimeUnit.MILLISECONDS))
                    Log.d(TAG, "pool not determined");
            }
        } catch (InterruptedException ie) {
            Log.d(TAG, "exception pool shutdownNow");
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
