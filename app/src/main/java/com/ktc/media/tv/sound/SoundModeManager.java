package com.ktc.media.tv.sound;

import android.content.Context;
import android.provider.Settings;

import com.ktc.media.tv.util.SharePreferenceUtil;
import com.mediatek.twoworlds.factory.MtkTvFApiAudio;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_10KHZ;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_120HZ;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_1_5_KHZ;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_500HZ;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_5KHZ;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_BALANCE;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_DIGITAL_OUTPUT;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_SPDIF_DELAY;
import static com.ktc.media.tv.sound.SoundConstant.KEY_AN_SOUND_SURROUND;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_10KHZ;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_120HZ;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_1_5_KHZ;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_500HZ;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_5KHZ;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_BALANCE;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_DIGITAL_OUTPUT;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_SPDIF_DELAY;
import static com.ktc.media.tv.sound.SoundConstant.SOUND_SURROUND;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MOVIE_VALUE_10KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MOVIE_VALUE_120HZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MOVIE_VALUE_1_5KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MOVIE_VALUE_500HZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MOVIE_VALUE_5KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MUSIC_VALUE_10KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MUSIC_VALUE_120HZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MUSIC_VALUE_1_5KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MUSIC_VALUE_500HZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_MUSIC_VALUE_5KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_STANDAER_VALUE_10KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_STANDAER_VALUE_120HZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_STANDAER_VALUE_1_5KHZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_STANDAER_VALUE_500HZ;
import static com.ktc.media.tv.sound.SoundDefault.DEFAULT_SOUND_MODE_STANDAER_VALUE_5KHZ;
import static com.ktc.media.tv.util.SharePreferenceUtil.KEY_USER_10KHZ;
import static com.ktc.media.tv.util.SharePreferenceUtil.KEY_USER_120HZ;
import static com.ktc.media.tv.util.SharePreferenceUtil.KEY_USER_1_5KHZ;
import static com.ktc.media.tv.util.SharePreferenceUtil.KEY_USER_500HZ;
import static com.ktc.media.tv.util.SharePreferenceUtil.KEY_USER_5KHZ;

public class SoundModeManager {

    private static final String KEY_AN_SOUND_STYLE = "sound_style";
    private static final String SOUND_STYLE = MtkTvConfigType.CFG_AUD_SOUND_MODE;

    /**
     * User 模式，传入的值为 0 ，
     * Standard 模式，传入的值为 1 ，
     * 以此类推
     */
    public static final int VALUE_SOUND_MODE_User = 0;
    public static final int VALUE_SOUND_MODE_Standard = 1;
    public static final int VALUE_SOUND_MODE_Vivid = 2;
    public static final int VALUE_SOUND_MODE_Sports = 3;
    public static final int VALUE_SOUND_MODE_Movie = 4;
    public static final int VALUE_SOUND_MODE_Music = 5;
    public static final int VALUE_SOUND_MODE_News = 6;
    public static final int VALUE_SOUND_MODE_Auto = 7;

    public static final int KTC_SOUND_MODE_STANDARD = VALUE_SOUND_MODE_Standard;
    public static final int KTC_SOUND_MODE_MUSIC = VALUE_SOUND_MODE_Music;
    public static final int KTC_SOUND_MODE_MOVIE = VALUE_SOUND_MODE_Movie;
    public static final int KTC_SOUND_MODE_USER = VALUE_SOUND_MODE_User;

    /**
     * 如果是 KTC 规格外的声音模式，定义为标准
     */
    private static final int INDEX_KTC_SOUND_MODE_UNKNOWN = 0;
    /**
     * 顺序写死，和 arrays.xml 里的 array_sound_mode 保持一致
     */
    private static final int[] KtcSoundModeArray = new int[]{
            KTC_SOUND_MODE_STANDARD,
            KTC_SOUND_MODE_MUSIC,
            KTC_SOUND_MODE_MOVIE,
            KTC_SOUND_MODE_USER,
    };

    private Context mContext;
    private static SoundModeManager soundModeManager;
    private MtkTvFApiAudio mTvFApiAudio;

    private static final int KTC_DIGITAL_OUTPUT_OFF = 7;//user auto for off
    private static final int KTC_DIGITAL_OUTPUT_PCM = 2;
    private static final int DEFAULT_DIGITAL_OUTPUT_INDEX = 0;
    private static int[] KtcDigitalOuputArray = new int[]{
            KTC_DIGITAL_OUTPUT_OFF,
            KTC_DIGITAL_OUTPUT_PCM,
    };

    private SoundModeManager(Context context) {
        super();
        mContext = context.getApplicationContext();
        mTvFApiAudio = MtkTvFApiAudio.getInstance();
    }

    public static SoundModeManager getInstance(Context context) {
        if (soundModeManager == null) {
            synchronized (SoundModeManager.class) {
                if (soundModeManager == null) {
                    soundModeManager = new SoundModeManager(context);
                }
            }
        }
        return soundModeManager;
    }

    public void setKtcSoundMode(int ktcSoundMode) {
        switch (ktcSoundMode) {
            case 0:
                //standard
                setSoundMode(KTC_SOUND_MODE_STANDARD);
                updateSoundModeMemberStandard();
                break;
            case 1:
                //music
                setSoundMode(KTC_SOUND_MODE_MUSIC);
                updateSoundModeMemberMusic();
                break;
            case 2:
                //movie
                setSoundMode(KTC_SOUND_MODE_MOVIE);
                updateSoundModeMemberMovie();
                break;
            case 3:
                //user
                setSoundMode(KTC_SOUND_MODE_USER);
                updateSoundModeMemberUser();
                break;
            default:
                break;
        }
    }

    /**
     * 直接返回下标
     */
    public int getKtcSoundMode() {
        int ktcSoundMode = getSoundMode();
        for (int sou = 0; sou < KtcSoundModeArray.length; sou++) {
            if (ktcSoundMode == KtcSoundModeArray[sou]) {
                return sou;
            }
        }
        return INDEX_KTC_SOUND_MODE_UNKNOWN;
    }

    private int getSoundMode() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_STYLE);
    }

    private void setSoundMode(int value) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_STYLE, value);
        MtkTvConfig.getInstance().setConfigValue(SOUND_STYLE, value);
    }

    /**
     * 更新图像模式相关（120Hz等）的值
     */
    private void updateSoundModeMemberStandard() {
        set120Hz(DEFAULT_SOUND_MODE_STANDAER_VALUE_120HZ);
        set500Hz(DEFAULT_SOUND_MODE_STANDAER_VALUE_500HZ);
        set1_5KHz(DEFAULT_SOUND_MODE_STANDAER_VALUE_1_5KHZ);
        set5KHz(DEFAULT_SOUND_MODE_STANDAER_VALUE_5KHZ);
        set10KHz(DEFAULT_SOUND_MODE_STANDAER_VALUE_10KHZ);
    }

    private void updateSoundModeMemberMusic() {
        set120Hz(DEFAULT_SOUND_MODE_MUSIC_VALUE_120HZ);
        set500Hz(DEFAULT_SOUND_MODE_MUSIC_VALUE_500HZ);
        set1_5KHz(DEFAULT_SOUND_MODE_MUSIC_VALUE_1_5KHZ);
        set5KHz(DEFAULT_SOUND_MODE_MUSIC_VALUE_5KHZ);
        set10KHz(DEFAULT_SOUND_MODE_MUSIC_VALUE_10KHZ);
    }

    private void updateSoundModeMemberMovie() {
        set120Hz(DEFAULT_SOUND_MODE_MOVIE_VALUE_120HZ);
        set500Hz(DEFAULT_SOUND_MODE_MOVIE_VALUE_500HZ);
        set1_5KHz(DEFAULT_SOUND_MODE_MOVIE_VALUE_1_5KHZ);
        set5KHz(DEFAULT_SOUND_MODE_MOVIE_VALUE_5KHZ);
        set10KHz(DEFAULT_SOUND_MODE_MOVIE_VALUE_10KHZ);
    }

    private void updateSoundModeMemberUser() {
        set120Hz(getUser120Hz());
        set500Hz(getUser500Hz());
        set1_5KHz(getUser1_5KHz());
        set5KHz(getUser5KHz());
        set10KHz(getUser10KHz());
    }

    private void set120Hz(int m120hz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_120HZ, m120hz);
        MtkTvConfig.getInstance().setConfigValue(SOUND_120HZ, m120hz);
    }

    public int get120Hz() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_120HZ);
    }

    private void set500Hz(int m500hz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_500HZ, m500hz);
        MtkTvConfig.getInstance().setConfigValue(SOUND_500HZ, m500hz);
    }

    public int get500Hz() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_500HZ);
    }

    private void set1_5KHz(int m1_5khz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_1_5_KHZ, m1_5khz);
        MtkTvConfig.getInstance().setConfigValue(SOUND_1_5_KHZ, m1_5khz);
    }

    public int get1_5KHz() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_1_5_KHZ);
    }

    private void set5KHz(int m5khz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_5KHZ, m5khz);
        MtkTvConfig.getInstance().setConfigValue(SOUND_5KHZ, m5khz);
    }

    public int get5KHz() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_5KHZ);
    }

    private void set10KHz(int m10khz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_10KHZ, m10khz);
        MtkTvConfig.getInstance().setConfigValue(SOUND_10KHZ, m10khz);
    }

    public int get10KHz() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_10KHZ);
    }

    public void setUser120Hz(int m120hz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_120HZ, m120hz - 50);
        MtkTvConfig.getInstance().setConfigValue(SOUND_120HZ, m120hz - 50);
        SharePreferenceUtil.setValue(mContext, KEY_USER_120HZ, m120hz);
    }

    public int getUser120Hz() {
        return SharePreferenceUtil.getValue(mContext, KEY_USER_120HZ, 50);
        //return MtkTvConfig.getInstance().getConfigValue(SOUND_120HZ);
    }

    public void setUser500Hz(int m500hz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_500HZ, m500hz - 50);
        MtkTvConfig.getInstance().setConfigValue(SOUND_500HZ, m500hz - 50);
        SharePreferenceUtil.setValue(mContext, KEY_USER_500HZ, m500hz);
    }

    public int getUser500Hz() {
        return SharePreferenceUtil.getValue(mContext, KEY_USER_500HZ, 50);
        //return MtkTvConfig.getInstance().getConfigValue(SOUND_500HZ);
    }

    public void setUser1_5KHz(int m1_5khz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_1_5_KHZ, m1_5khz - 50);
        MtkTvConfig.getInstance().setConfigValue(SOUND_1_5_KHZ, m1_5khz - 50);
        SharePreferenceUtil.setValue(mContext, KEY_USER_1_5KHZ, m1_5khz);
    }

    public int getUser1_5KHz() {
        return SharePreferenceUtil.getValue(mContext, KEY_USER_1_5KHZ, 50);
        //return MtkTvConfig.getInstance().getConfigValue(SOUND_1_5_KHZ);
    }

    public void setUser5KHz(int m5khz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_5KHZ, m5khz - 50);
        MtkTvConfig.getInstance().setConfigValue(SOUND_5KHZ, m5khz - 50);
        SharePreferenceUtil.setValue(mContext, KEY_USER_5KHZ, m5khz);
    }

    public int getUser5KHz() {
        return SharePreferenceUtil.getValue(mContext, KEY_USER_5KHZ, 50);
        //return MtkTvConfig.getInstance().getConfigValue(SOUND_5KHZ);
    }

    public void setUser10KHz(int m10khz) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_10KHZ, m10khz - 50);
        MtkTvConfig.getInstance().setConfigValue(SOUND_10KHZ, m10khz - 50);
        SharePreferenceUtil.setValue(mContext, KEY_USER_10KHZ, m10khz);
    }

    public int getUser10KHz() {
        return SharePreferenceUtil.getValue(mContext, KEY_USER_10KHZ, 50);
        //return MtkTvConfig.getInstance().getConfigValue(SOUND_10KHZ);
    }

    public void setSurround(int surround) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_SURROUND, surround);
        MtkTvConfig.getInstance().setConfigValue(SOUND_SURROUND, surround);
    }

    public int getSurroundValueInt() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_SURROUND);
    }

    public void setSpdifDelay(int spdifDelay) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_SPDIF_DELAY, spdifDelay);
        MtkTvConfig.getInstance().setConfigValue(SOUND_SPDIF_DELAY, spdifDelay);
    }

    public int getSpdifDelay() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_SPDIF_DELAY);
    }

    public void setSpeakerDelay(int speakerDelay) {
        mTvFApiAudio.setSoundSpeakerDelay(speakerDelay);
        mTvFApiAudio.saveAudioIni();
    }

    public int getSpeakerDelay() {
        return mTvFApiAudio.getSoundSpeakerDelay();
    }

    public void setBalance(int balanceBalue) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_BALANCE, balanceBalue);
        MtkTvConfig.getInstance().setConfigValue(SOUND_BALANCE, balanceBalue);
    }

    public int getBalance() {
        return MtkTvConfig.getInstance().getConfigValue(SOUND_BALANCE);
    }

    public void setDigitalOutputType(int digitalOutputType) {
        Settings.Global.putInt(mContext.getContentResolver(), KEY_AN_SOUND_DIGITAL_OUTPUT, digitalOutputType);
        MtkTvConfig.getInstance().setConfigValue(SOUND_DIGITAL_OUTPUT, digitalOutputType);
    }

    /**
     * 直接返回下标，与 array.xml 的 array_coaxial_mode 对应
     */
    public int getDigitalOutput() {
        int digitalOutput = MtkTvConfig.getInstance().getConfigValue(SOUND_DIGITAL_OUTPUT);
        for (int d = 0; d < KtcDigitalOuputArray.length; d++) {
            if (digitalOutput == KtcDigitalOuputArray[d]) {
                return d;
            }
        }
        return DEFAULT_DIGITAL_OUTPUT_INDEX;
    }
}
