package com.ktc.media.scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ktc.media.db.DatabaseUtil;

public class DiskReceiver extends BroadcastReceiver {

    private static final String TAG = DiskReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Uri uri = intent.getData();
            String USBPath = uri.getPath();
            scanDisk(context, USBPath);
        } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            Uri uri = intent.getData();
            String exitUsbPath = uri.getPath();
            deletePathData(context, exitUsbPath);
        }
    }

    private void scanDisk(Context context, String diskPath) {
        Intent intent = new Intent(context, ScanService.class);
        intent.putExtra("diskPath", diskPath);
        context.startService(intent);
    }

    private void deletePathData(Context context, String usbPath) {
        DatabaseUtil.getInstance(context).deletePathData(usbPath, true);
    }
}
