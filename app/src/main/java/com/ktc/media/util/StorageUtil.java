package com.ktc.media.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.ktc.media.R;
import com.ktc.media.model.DiskData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StorageUtil {

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;

    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0";

    public static List<DiskData> getMountedDisksList(Context context) {
        List<DiskData> diskDataList = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = storageManager.getVolumeList();
        for (StorageVolume sv : volumes) {
            String path = sv.getPath();
            DiskData diskData = new DiskData();
            String state = sv.getState();
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;
            }
            String volumeLabel = sv.getDescription(context);
            if (StringEncoder.isUTF8(path)) {
                path = StringEncoder.convertString(path, "UTF-8");
            } else {
                path = StringEncoder.convertString(path, "GBK");
            }
            diskData.setPath(path);
            /*diskData.setTotalSpace(*//*getTotalSpace(path)*//*0);
            if (diskData.getTotalSpace() != 0) {
                diskData.setAvailableSpace(*//*getAvailSpace(diskData.getPath())*//*0);
            }*/
            if (path.equals(INTERNAL_STORAGE_PATH)) {
                diskData.setName(context.getString(R.string.str_universal_storage_internal));
            } else {
                if (volumeLabel == null) {
                    diskData.setName(context.getString(R.string.str_universal_storage_default));
                } else {
                    diskData.setName(volumeLabel);
                }
            }
            diskDataList.add(diskData);
        }
        return diskDataList;
    }

    public static long getTotalSpace(CharSequence path) {
        try {
            StatFs sf = new StatFs((String) path);
            return sf.getBlockSizeLong() * sf.getBlockCountLong();
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getAvailSpace(CharSequence path) {
        try {
            StatFs sf = new StatFs((String) path);
            return sf.getBlockSizeLong() * sf.getAvailableBlocksLong();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * * @param 获取显示的存储空间大小
     */
    public static String getFileSizeDescription(long size) {
        String fileSizeString = "0KB";
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        float resultSize;
        if (size < KB) {
            fileSizeString = String.valueOf(size) + "B";
        } else if (size > KB && size <= MB) {
            resultSize = size / KB;
            fileSizeString = String.valueOf(resultSize) + "KB";
        } else if (size > MB && size <= GB) {
            resultSize = size / MB;
            fileSizeString = String.valueOf(resultSize) + "MB";
        } else {
            resultSize = size * 1.0f / GB;
            fileSizeString = decimalFormat.format(resultSize) + "G";
        }
        return fileSizeString;
    }

}
