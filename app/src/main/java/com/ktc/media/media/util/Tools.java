//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>
package com.ktc.media.media.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.ktc.media.MainApplication;
import com.ktc.media.R;
import com.ktc.media.constant.Constants;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;


public class Tools {

    private static final String TAG = "Tools";

    private static final String FILE_SIZE_B = "B";
    private static final String FILE_SIZE_KB = "KB";
    private static final String FILE_SIZE_MB = "MB";
    private static final String FILE_SIZE_GB = "GB";
    private static final String FILE_SIZE_TB = "TB";
    private static final String FILE_SIZE_NA = "N/A";

    private static String FILE_SYS_CONFIG = "vendor/config/sys.ini";
    private static String FILE_SYS_TVCONFIG = "vendor/tvconfig/config/sys.ini";

    private static final String LOCALMM_SETTINGS = "localmm_settings";

    private static final String SHARED_PREFERENCE_ROTATE = "rotate_mode_on";
    private static final String SHARED_PREFERENCE_ROTATE_DEGREE = "rotate_mode_degree";
    private static final String SHARED_PREFERENCE_DIVX_PRESENT_FILE_NAME = "divx_present_file_name";
    private static final String SHARED_PREFERENCE_4K2K_MODE_ON = "4k2k_mode_on";
    private static final String SHARED_PREFERENCE_NATIVE_AUDIO_CHECKED = "native_audio_checked";
    private static final String SHARED_PREFERENCE_NATIVE_AUDIO_ON = "native_audio_on";
    private static final String SHARED_PREFERENCE_SEAMLESS_MODE_ON = "seamless_mode_on";
    private static final String SHARED_PREFERENCE_SAMBA_PLAYBACK_ON = "samba_playback_on";
    private static final String SHARED_PREFERENCE_FREEZE_LAST_FRAME_ON = "freeze_last_frame_on";

    /**
     * judgment file exists.
     *
     * @param path file path.
     * @return when the parameters specified file exists return true, otherwise
     * returns false.
     */
    public static boolean isFileExist(String path) {
        return isFileExist(new File(path));
    }

    /**
     * judgment file exists.
     *
     * @param file {@link File}.
     * @return when the parameters specified file exists return true, otherwise
     * returns false.
     */
    public static boolean isFileExist(File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * will millisecond number formatting 00:00:00.
     *
     * @param duration time value to seconds for the unit.
     * @return format for 00:00:00 forms of string or '-- : -- : -- ".
     */
    public static String formatDuration(long duration) {
        long time = duration / 1000;
        if (time <= 0) {
            return "--:--:--";
        }
        long min = time / 60 % 60;
        long hour = time / 60 / 60;
        long second = time % 60;
        return String.format("%02d:%02d:%02d", hour, min, second);
    }

    public static String formatISODuration(long duration) {
        long time = duration / 90000;
        if (time <= 0) {
            return "--:--:--";
        }
        long min = time / 60 % 60;
        long hour = time / 3600;
        long second = time % 60;
        return String.format("%02d:%02d:%02d", hour, min, second);
    }

    /**
     * will millisecond number formatting 00:00.
     *
     * @ param duration time value to seconds for the unit. @ return format for
     * 00:00 forms of string.
     */
    @SuppressLint("all")
    public static String formatDuration(int duration) {
        int time = duration / 1000;
        if (time == 0 && duration > 0) {
            time = 1;
        }

        int second = time % 60;
        int min = time / 60;
        long hour = 0;
        if (min > 60) {
            hour = time / 3600;
            min = time / 60 % 60;
        }
        return String.format("%02d:%02d:%02d", hour, min, second);
    }

    /**
     * will file size into human form, the biggest said 1023 TB.
     *
     * @ param size file size. @ return file size minimum unit for B string.
     */
    public static String formatSize(BigInteger size) {
        // Less than
        if (size.compareTo(BigInteger.valueOf(1024)) == -1) {
            return (size.toString() + FILE_SIZE_B);
        } else if (size.compareTo(BigInteger.valueOf(1024 * 1024)) == -1) {
            return (size.divide(BigInteger.valueOf(1024)).toString() + FILE_SIZE_KB);
        } else if (size.compareTo(BigInteger.valueOf(1024 * 1024 * 1024)) == -1) {
            return (size.divide(BigInteger.valueOf(1024 * 1024)).toString() + FILE_SIZE_MB);
        } else if (size.compareTo(BigInteger
                .valueOf(1024 * 1024 * 1024 * 1024L)) == -1) {
            return (size.divide(BigInteger.valueOf(1024 * 1024 * 1024))
                    .toString() + FILE_SIZE_GB);
        } else if (size.compareTo(BigInteger
                .valueOf(1024 * 1024 * 1024 * 1024L).multiply(
                        BigInteger.valueOf(1024))) == -1) {
            return (size.divide(BigInteger.valueOf(1024 * 1024 * 1024 * 1024L))
                    .toString() + FILE_SIZE_TB);
        }
        return FILE_SIZE_NA;
    }

    /**
     * @return The current usb disk equipment loading position.
     */
    public static String getUSBMountedPath() {
        return Environment.getExternalStorageDirectory().getParent();
    }

    // Play Settings option value
    private static String[] playSettingOptTextOne = null;
    private static String[] playSettingOptTextTwo = null;

    /**
     * @return Initialization play Settings related option value
     */
    public static String[] initPlaySettingOpt(final Context context, int id, final int total) {
        final String closeStr = context.getString(R.string.play_setting_0_value_2);

        if (playSettingOptTextOne == null) {
            playSettingOptTextOne = new String[total];
            for (int idx = 0; idx < total; idx++) {
                playSettingOptTextOne[idx] = new String(closeStr);
            }
        }
        if (playSettingOptTextTwo == null) {
            playSettingOptTextTwo = new String[total];
            for (int idx = 0; idx < total; idx++) {
                playSettingOptTextTwo[idx] = new String(closeStr);
            }
        }
        if (id == 1) {
            return playSettingOptTextOne;
        } else {
            return playSettingOptTextTwo;
        }
    }

    /**
     * @param index initPlaySettingOpt
     */
    public static String getPlaySettingOpt(int index, int id) {
        if (id == 1) {
            final int len = playSettingOptTextOne.length;
            if (index >= 0 && index < len) {
                if (playSettingOptTextOne != null) {
                    return playSettingOptTextOne[index];
                }
                return null;
            }
        } else {
            final int len = playSettingOptTextTwo.length;
            if (index >= 0 && index < len) {
                if (playSettingOptTextTwo != null) {
                    return playSettingOptTextTwo[index];
                }
                return null;
            }
        }
        return null;
    }

    /**
     * @ param index subscript index value. @ param value to set to value.
     */
    public static void setPlaySettingOpt(int index, final String value, int id) {
        if (id == 1) {
            final int len = playSettingOptTextOne.length;
            if (index >= 0 && index < len) {
                if (playSettingOptTextOne != null) {
                    playSettingOptTextOne[index] = value;
                }
            }
        } else {
            final int len = playSettingOptTextTwo.length;
            if (index >= 0 && index < len) {
                if (playSettingOptTextTwo != null) {
                    playSettingOptTextTwo[index] = value;
                }
            }
        }
        if (index == 2) {
            if (playSettingOptTextOne != null) {
                playSettingOptTextOne[index] = value;
            }
            if (playSettingOptTextTwo != null) {
                playSettingOptTextTwo[index] = value;
            }
        }
    }

    /**
     * Exit video player when eliminate all Settings option value.
     */
    public static void destroyAllSettingOpt() {
        playSettingOptTextOne = null;
        playSettingOptTextTwo = null;
    }

    /**
     * Stop media scanning.
     */
    public static void stopMediascanner(Context context) {
        Intent intent = new Intent();
        intent.setAction("action_media_scanner_stop");
        context.sendBroadcast(intent);
        Log.d(TAG, "stopMediascanner");
    }

    /**
     * Start media scanning.
     */
    public static void startMediascanner(Context context) {
        Intent intent = new Intent();
        intent.setAction("action_media_scanner_start");
        context.sendBroadcast(intent);
        Log.d(TAG, "startMediascanner");
    }

    /**
     * The size of file is whether larger than the specified size.
     *
     * @param path absolute path of file.
     * @param size the specified size of file.
     * @return true if the file is larger than size, otherwise false.
     */
    public static boolean isLargeFile(final String path, final long size) {
        File file = new File(path);
        // file does not exist
        if (!isFileExist(file)) {
            Log.d(TAG, "file does not exist");
            return true;
        }
        long length = file.length();
        Log.d(TAG, "size of file : " + length);
        // file bigger than size
        if (length > size) {
            return true;
        }
        return false;
    }

    private static String getConfigName(String file, String index) {
        String str = null;
        String gConfigName = null;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index) >= 0) {
                    break;
                }
            }
            if (str != null) {
                int begin = str.indexOf("\"") + 1;
                int end = str.lastIndexOf("\"");
                if ((begin > 0) && (end > 0)) {
                    gConfigName = str.substring(begin, end);
                    str = null;
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
            return null;
        }
        Log.i(TAG, "gConfigName:" + gConfigName + "; file: " + file + "; index: " + index);
        return gConfigName;
    }

    private static String getbEnabled(String file, String index) {
        String str = null;
        String bEnabled = null;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index) >= 0 && str.indexOf(index) < 9) {
                    break;
                }
            }
            Log.i(TAG, "str:" + str);
            if (str != null) {
                int begin = str.indexOf("=") + 1;
                int end = str.lastIndexOf(";");
                if ((begin > 0) && (end > 0)) {
                    bEnabled = str.substring(begin, end);
                    str = null;
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
            return null;
        }
        Log.i(TAG, "bEnabled:" + bEnabled + "; file: " + file + "; index: " + index);
        return bEnabled;
    }

    private static int[] parsePanelOsdSize(String index) {
        String str = null;
        String wPanelWidth = null;
        String wPanelHeight = null;
        int[] config = {0, 0};
        try {
            FileReader reader;
            String modelName = getConfigName(FILE_SYS_CONFIG, "gModelName");
            if (modelName == null || modelName.equals("")) {
                modelName = getConfigName(FILE_SYS_TVCONFIG, "gModelName");
            }
            reader = new FileReader(getConfigName(modelName, "m_pPanelName"));
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index + "Width") >= 0) {
                    Log.i(TAG, index + "Width:" + str);
                    int begin = str.indexOf("=") + 1;
                    int end = str.lastIndexOf(";");
                    if ((begin > 0) && (end > 0)) {
                        wPanelWidth = str.substring(begin, end).trim();
                        Log.i(TAG, index + "Width:" + wPanelWidth);
                        config[0] = Integer.parseInt(wPanelWidth);
                    }
                } else if (str.indexOf(index + "Height") >= 0) {
                    Log.i(TAG, index + "Height:" + str);
                    int begin = str.indexOf("=") + 1;
                    int end = str.lastIndexOf(";");
                    if ((begin > 0) && (end > 0)) {
                        wPanelHeight = str.substring(begin, end).trim();
                        Log.i(TAG, index + "Height:" + wPanelHeight);
                        config[1] = Integer.parseInt(wPanelHeight);
                    }
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return config;
        }
        return config;
    }

    public static int[] getPanelSize() {
        int[] config = MainApplication.getInstance().getPanelSize();
        if (config[0] == 0 || config[1] == 0) {
            config = parsePanelOsdSize("m_wPanel");
            MainApplication.getInstance().setPanelSize(config);
        } else
            Log.i(TAG, "config!=0");
        return config;
    }

    public static int[] getOsdSize() {
        int[] config = MainApplication.getInstance().getOsdSize();
        if (config[0] == 0 || config[1] == 0) {
            config = parsePanelOsdSize("osd");
            MainApplication.getInstance().setOsdSize(config);
        }
        return config;
    }

    private static long parseMem(String index) {
        String str = null;
        long mTotal = 0;
        try {
            FileReader reader = new FileReader("/proc/meminfo");
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index) >= 0) {
                    break;
                }
            }
            if (str != null) {
                int begin = str.indexOf(':');
                int end = str.indexOf('k');
                str = str.substring(begin + 1, end).trim();
                mTotal = Integer.parseInt(str);
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "parse " + index + " : " + mTotal + " kB");
        return mTotal;
    }

    public static long getTotalMem() {
        long total = MainApplication.getInstance().getTotalMem();
        if (total == -1) {
            total = parseMem("MemTotal");
            MainApplication.getInstance().setTotalMem(total);
        }
        return total;
    }

    public static long getFreeMem() {
        return parseMem("MemFree");
    }

    public static String getFileName(String sPath) {
        int pos = sPath.lastIndexOf("/");
        String name = sPath.substring(pos + 1, sPath.length());
        return name;
    }

    public static String parseUri(Uri intent) {
        if (intent != null) {
            return intent.getPath();
        }
        return null;
    }

    public static int getThumbnailNumber() {
        return 5;
    }

    public static boolean isThumbnailModeOn() {
        return false;
    }

    public static void setThumbnailMode(String value) {
        if (!isSupportDualDecode()) {
            return;
        }
    }

    public static void setRotateMode(Context context, boolean flag) {
        saveSettingsAsBoolean(context, SHARED_PREFERENCE_ROTATE, flag);
    }

    public static boolean isRotateModeOn(Context context) {
        String value = loadSettings(context, SHARED_PREFERENCE_ROTATE);
        boolean status = Boolean.valueOf(value);
        Log.i(TAG, "isRotateModeOn:" + status);
        return status;
    }

    public static void setRotateDegrees(Context context, String value) {
        saveSettings(context, SHARED_PREFERENCE_ROTATE_DEGREE, value);
    }

    public static int getRotateDegrees(Context context) {
        String value = loadSettings(context, SHARED_PREFERENCE_ROTATE_DEGREE);
        if (value == null) {
            return 0;
        }
        return Integer.valueOf(value);
    }

    public static boolean isRotate90OR270Degree(Context context) {
        if (!isRotateModeOn(context)) {
            return false;
        }
        if (90 == getRotateDegrees(context)
                || 270 == getRotateDegrees(context)) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isSupportDualDecode() {
        return false;
    }

    public static boolean isSupportMWPlayBack() {
        return false;
    }

    public static String getDivxPresentFileName(Context context) {
        return loadSettings(context, SHARED_PREFERENCE_DIVX_PRESENT_FILE_NAME);
    }

    public static void setDivxPresentFileName(Context context, String fileName) {
        saveSettings(context, SHARED_PREFERENCE_DIVX_PRESENT_FILE_NAME, fileName);
    }

    private static int curLangID = 1;

    public static void setCurrLangID(int nID) {
        curLangID = nID;
    }

    public static int getCurrLangID() {
        return curLangID;
    }

    public static boolean isNetPlayback(String path) {
        String ret = null;
        if (path != null && path.length() != 0) {
            ret = path.substring(0, 4);
        }
        if (ret != null && ret.length() != 0) {
            if (ret.equals("http") || ret.equals("rtsp")) {
                return true;
            }
        }
        return false;
    }

    public static boolean getSubtitleAPI() {
        return false;
    }

    public static void setMainPlay4K2KModeOn(Context context, boolean flag) {
        saveSettings(context, SHARED_PREFERENCE_4K2K_MODE_ON, String.valueOf(flag));
    }

    public static boolean isMainPlay4K2KModeOn(Context context) {
        return loadSettingsAsBoolean(context, SHARED_PREFERENCE_4K2K_MODE_ON);
    }

    public static void setVideoStreamlessModeOn(Context context, boolean streamlessOn) {
        saveSettingsAsBoolean(context, SHARED_PREFERENCE_SEAMLESS_MODE_ON, streamlessOn);
    }

    public static boolean isVideoStreamlessModeOn(Context context) {
        return loadSettingsAsBoolean(context, SHARED_PREFERENCE_SEAMLESS_MODE_ON);
    }

    public static void setVideoFreezeLastFrameOn(Context context, boolean streamlessOn) {
        saveSettingsAsBoolean(context, SHARED_PREFERENCE_FREEZE_LAST_FRAME_ON, streamlessOn);
    }

    public static boolean isVideoFreezeLastFrameOn(Context context) {
        return loadSettingsAsBoolean(context, SHARED_PREFERENCE_FREEZE_LAST_FRAME_ON);
    }

    public static boolean isPhotoStreamlessModeOn() {
        return true;
    }

    public static void setNativeAudioModeOn(Context context, boolean flag) {
        saveSettings(context, SHARED_PREFERENCE_NATIVE_AUDIO_ON, String.valueOf(flag));
    }

    public static boolean isNativeAudioModeOn(Context context) {
        return loadSettingsAsBoolean(context, SHARED_PREFERENCE_NATIVE_AUDIO_ON);
    }

    public static boolean isElderPlatformForStreamLessMode() {
        return false;
    }

    // there are two ways to playback samba's resource
    // one way uses mount/unmount (in sdk which less than 23)
    // another way uses http server to send data to player who request data.(in sdk which is 23 or more than 23)
    public static boolean isUseHttpSambaModeOn() {
        return true;
    }

    public static boolean isSambaPlaybackUrl(String path) {
        String ret = null;
        if (path != null && path.length() != 0) {
            ret = path.substring(0, 3);
        }
        if (ret != null && ret.length() != 0) {
            if (ret.equals("smb")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSambaVideoPlayBack(Context context) {
        boolean ret = loadSettingsAsBoolean(context, SHARED_PREFERENCE_SAMBA_PLAYBACK_ON);
        Log.i(TAG, "isSambaVideoPlayBack:" + ret);
        return ret;
    }

    public static void setSambaVideoPlayBack(Context context, boolean flag) {
        Log.i(TAG, "setSambaVideoPlayBack:" + flag);
        saveSettings(context, SHARED_PREFERENCE_SAMBA_PLAYBACK_ON, String.valueOf(flag));
    }

    public static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            if (!f1.exists()) {
                return;
            }
            File f2 = new File(dtFile);
            if (!f2.exists()) {
                f2.createNewFile();
            }
            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            Log.d(TAG, "the file cannot be found");
        } catch (IOException e) {
            Log.i(TAG, "---IOException---");
        }
    }

    public static String fixPath(String path) {
        // lollipop5.1 corrsponding to 22, don't need to handle the sign of '#' and '%' after lollipop5.1
        // mantis:0893065,0870320
        if (Build.VERSION.SDK_INT < 22) {
            if (path.indexOf("%") != -1) {
                path = path.replaceAll("%", "%25");
            }
            if (path.indexOf("#") != -1) {
                path = path.replaceAll("#", "%23");
            }
        }
        return path;
    }

    public static int getSdkVersion() {
        int ret = Build.VERSION.SDK_INT;
        return ret;
    }

    // print the utf-8 encoding hex string
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    // print the byte array(every encoding) to hex string
    public static String byte2hex(byte[] buffer) {
        String h = "";
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }

    // divx subtitle request
    public static byte[] cutForThreeRowSubtitle(byte[] buffer) {
        int cnt = 0;
        Log.i(TAG, "cutForThreeRowSubtitle");
        byte result[] = new byte[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            Log.i(TAG, "buffer[i]:" + i + "," + buffer[i]);
            result[i] = buffer[i];
            if (buffer[i] == 0x0A) {
                cnt++;
                if (cnt == 3) break;
            }
        }
        return result;
    }

    public static boolean is4K2KPlatForm() {
        int[] config = getPanelSize();
        int mPanelWidth = config[0];
        int mPanelHeight = config[1];
        Log.i(TAG, "is4K2KPlatForm:" + String.valueOf(mPanelWidth) + " " + String.valueOf(mPanelHeight));
        if (mPanelWidth == 3840 && mPanelHeight == 2160) {
            return true;
        }
        return false;
    }

    public static int getAndroidSDKVersion() {
        Log.i(TAG, "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
        return Build.VERSION.SDK_INT;
    }

    /**
     * judge file chaset,use opensource solution from UniversalDetector
     *
     * @param filePath the file need to judge chaset
     * @return file chaset
     */
    public static String getFileEncoding(String filePath) {
        String encoding = "utf-8";

        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        InputStream fs = null;
        BufferedInputStream bis = null;
        try {
            if (isNetPlayback(filePath)) {
                try {
                    fs = new URL(filePath).openStream();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fs == null) {
                    Log.i(TAG, "fs is null");
                    return null;
                }
                bis = new BufferedInputStream(fs);
            } else {
                fis = new FileInputStream(filePath);
                bis = new BufferedInputStream(fis);
            }
            // (1)
            UniversalDetector detector = new UniversalDetector(null);
            // (2)
            int nread;
            while ((nread = bis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();
            // (4)
            encoding = detector.getDetectedCharset();
            if (encoding != null) {
                Log.i(TAG, "Detected encoding = " + encoding);
            } else {
                Log.i(TAG, "No encoding detected.");
                //defualt UTF-8
                encoding = "utf-8";
            }
            // (5)
            detector.reset();
            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fs != null) {
                    fs.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return encoding;
    }

    public static String getUSBNameFromPath(String path) {
        if (path == null) {
            return null;
        }
        String sStart = "/mnt/usb/";
        String usbName = null;
        int start = path.indexOf(sStart);
        if (start >= 0) {
            start += sStart.length();
            int end = path.indexOf('/', start);
            if (start >= 0 && start < path.length()
                    && end >= start && end < path.length()) {
                usbName = path.substring(start, end);
            }
        }
        return usbName;
    }

    public static void saveSettings(Context context, String name, String value) {
        SharedPreferences preference = context.getSharedPreferences(
                LOCALMM_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static String loadSettings(Context context, String name) {
        if (context == null) {
            return null;
        }
        SharedPreferences preference = context.getSharedPreferences(
                LOCALMM_SETTINGS, Context.MODE_PRIVATE);
        String value = preference.getString(name, null);
        return value;
    }

    public static void saveSettingsAsBoolean(Context context, String name, boolean value) {
        if (value) {
            saveSettings(context, name, "true");
        } else {
            saveSettings(context, name, "false");
        }
    }

    public static boolean loadSettingsAsBoolean(Context context, String name) {
        final String value = loadSettings(context, name);
        if (null == value) {
            return false;
        }
        return "true".equalsIgnoreCase(value);
    }
}
