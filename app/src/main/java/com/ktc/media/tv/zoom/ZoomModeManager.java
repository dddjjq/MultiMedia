package com.ktc.media.tv.zoom;

import android.content.Context;
import android.provider.Settings;

import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;


public class ZoomModeManager {

    /**
     * 底层用的 key, TV 的
     */
    private static final String PICTURE_FORMAT = MtkTvConfigType.CFG_FISION_PIC_PICTRUE_FORMAT;

    /**
     * 上层用的 key, AN 的
     */
    private static final String KEY_AN_PICTURE_FORMAT = "picture_format";

    /**
     * Automatic 模式，传入的值为 7 ，
     * Super_Zoom 模式，传入的值为 3 ，
     * 以此类推
     */
    private static final int VALUE_DISPLAY_MODE_Automatic = 7;
    private static final int VALUE_DISPLAY_MODE_Super_Zoom = 3;
    private static final int VALUE_DISPLAY_MODE_4_3 = 8;
    private static final int VALUE_DISPLAY_MODE_Movie_expand_14_9 = 9;
    private static final int VALUE_DISPLAY_MODE_Movie_Expand_16_9 = 10;
    private static final int VALUE_DISPLAY_MODE_Wide_Screen = 5;
    private static final int VALUE_DISPLAY_MODE_Full = 1;
    private static final int VALUE_DISPLAY_MODE_Unscaled = 6;

    /**
     * KTC规格 和 MTK规格对应关系
     */
    public static final int KTC_ZOOM_MODE_AUTO = VALUE_DISPLAY_MODE_Automatic;
    public static final int KTC_ZOOM_MODE_16_9 = VALUE_DISPLAY_MODE_Full;
    public static final int KTC_ZOOM_MODE_4_3 = VALUE_DISPLAY_MODE_4_3;
    public static final int KTC_ZOOM_MODE_FILM = VALUE_DISPLAY_MODE_Super_Zoom;
    public static final int KTC_ZOOM_MODE_SUBTITLE = VALUE_DISPLAY_MODE_Movie_Expand_16_9;
    /**
     * 如果是 KTC 规格外的比例模式，定义为自动
     */
    private static final int INDEX_KTC_ZOOM_MODE_UNKNOWN = 0;

    private MtkTvAppTV mtkTvAppTv;

    private Context mContext;
    private static ZoomModeManager zoomModeManager;

    /**
     * 顺序写死, 和 arrays.xml 里 array_zoom_mode 的顺序保持一致
     * index - zoomMode
     * 0     - auto
     * 1     - 16:9
     * 2     - 4:3
     * 3     - film
     * 4     - subtitle
     */
    public static int[] KtcZoomModeArray = new int[]{
            KTC_ZOOM_MODE_AUTO,
            KTC_ZOOM_MODE_16_9,
            KTC_ZOOM_MODE_4_3,
            KTC_ZOOM_MODE_FILM,
            KTC_ZOOM_MODE_SUBTITLE,
    };

    private ZoomModeManager() {
        super();
    }

    private ZoomModeManager(Context context) {
        super();
        mContext = context.getApplicationContext();
        mtkTvAppTv = MtkTvAppTV.getInstance();
    }

    public static ZoomModeManager getInstance(Context context) {
        if (zoomModeManager == null) {
            synchronized (ZoomModeManager.class) {
                if (zoomModeManager == null) {
                    zoomModeManager = new ZoomModeManager(context);
                }
            }
        }
        return zoomModeManager;
    }

    public void finish() {
        if (zoomModeManager != null) {
            zoomModeManager = null;
        }

        if (mContext != null) {
            mContext = null;
        }
    }

    public void setKtcZoomMode(int zoomModeIndex) {
        switch (zoomModeIndex) {
            case 0:
                setZoomMode(KTC_ZOOM_MODE_AUTO);
                break;
            case 1:
                setZoomMode(KTC_ZOOM_MODE_16_9);
                break;
            case 2:
                setZoomMode(KTC_ZOOM_MODE_4_3);
                break;
            case 3:
                setZoomMode(KTC_ZOOM_MODE_FILM);
                break;
            case 4:
                setZoomMode(KTC_ZOOM_MODE_SUBTITLE);
                break;
            default:
                break;
        }
    }

    /**
     * 直接返回 arrays.xml 字段 array_zoom_mode 的下标
     */
    public int getKtcZoomModeValueInt() {
        int zoom = getZoomModeValueInt();
        for (int z = 0; z < KtcZoomModeArray.length; z++) {
            if (KtcZoomModeArray[z] == zoom) {
                return z;
            }
        }
        /**
         * 默认返回自动
         * */
        return INDEX_KTC_ZOOM_MODE_UNKNOWN;
    }

    /**
     * 设置比例模式
     */
    private void setZoomMode(int value) {
        mtkTvAppTv.setVideoMute("main", true);

        /**
         * 上层调用
         * */
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_PICTURE_FORMAT, value);

        /**
         * 传给底层
         * */
        MtkTvConfig.getInstance().setConfigValue(PICTURE_FORMAT, value);

        mtkTvAppTv.setVideoMute("main", false);
    }

    public String getZoomMode() {
        return MtkTvConfig.getInstance().getConfigString(PICTURE_FORMAT);
    }

    public int getZoomModeValueInt() {
        return Settings.Global.getInt(mContext.getContentResolver(), KEY_AN_PICTURE_FORMAT, VALUE_DISPLAY_MODE_Automatic);
    }
}
