package com.ktc.media;

import android.app.Application;


public class MainApplication extends Application {

    private static MainApplication instance;

    private int[] panelSize = {0, 0};

    private int[] osdSize = {0, 0};

    private long totalMem = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * @return singleton instance.
     */
    public static MainApplication getInstance() {
        return instance;
    }

    public final void setPanelSize(final int[] config) {
        panelSize = config;
    }

    public final int[] getPanelSize() {
        return panelSize;
    }

    public final void setOsdSize(final int[] config) {
        osdSize = config;
    }

    public final int[] getOsdSize() {
        return osdSize;
    }

    public final void setTotalMem(final long total) {
        totalMem = total;
    }

    public final long getTotalMem() {
        return totalMem;
    }
}
