// IFileScanManager.aidl
package com.ktc.media;

import com.ktc.media.IFileScanUpdateListener;

interface IFileScanManager {
    void registerListener(IFileScanUpdateListener listener);

    void unregisterListener(IFileScanUpdateListener listener);
}
