package com.ktc.media.tv.sound;

import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

public class SoundConstant {

    /**
     * Balance ,
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100
     */
    public static final String KEY_AN_SOUND_BALANCE = "sound_balance";
    public static final String SOUND_BALANCE = "g_audio__aud_balance";

    /**
     * BaSS , 0 ~ 100
     */
    public static final String KEY_AN_SOUND_BASS = "sound_bass";
    public static final String SOUND_BASS = "g_audio__aud_bass";

    /**
     * Treble , 0 ~ 100
     */
    public static final String KEY_AN_SOUND_TREBLE = "sound_treble";
    public static final String SOUND_TREBLE = "g_audio__aud_treble";

    /**
     * Sound Surround
     * off : 0
     * on  : 1
     */
    public static final String KEY_AN_SOUND_SURROUND = "sound_sound_surround";
    public static final String SOUND_SURROUND = "g_audio__aud_surround";

    /**
     * 120HZ
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100
     */
    public static final String KEY_AN_SOUND_120HZ = "sound_equalizer_detail_120hz";
    public static final String SOUND_120HZ = "g_fusion_sound__equalizer_120hz";

    /**
     * 500HZ
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100
     */
    public static final String KEY_AN_SOUND_500HZ = "sound_equalizer_detail_500hz";
    public static final String SOUND_500HZ = "g_fusion_sound__equalizer_500hz";

    /**
     * 1.5KHZ
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100
     */
    public static final String KEY_AN_SOUND_1_5_KHZ = "sound_equalizer_detail_1500hz";
    public static final String SOUND_1_5_KHZ = "g_fusion_sound__equalizer_1.5khz";

    /**
     * 5KHZ
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100
     */
    public static final String KEY_AN_SOUND_5KHZ = "sound_equalizer_detail_5000hz";
    public static final String SOUND_5KHZ = "g_fusion_sound__equalizer_5khz";

    /**
     * 10KHZ
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100
     */
    public static final String KEY_AN_SOUND_10KHZ = "sound_equalizer_detail_10000hz";
    public static final String SOUND_10KHZ = "g_fusion_sound__equalizer_10khz.5khz";

    /**
     * Digital Output
     * Auto : 7
     * Bypass : 4
     * PCM : 2
     * Dolby Digital Plus : 5
     * Dolby Digital : 1
     */
    public static final String KEY_AN_SOUND_DIGITAL_OUTPUT = "sound_spdif_type";
    public static final String SOUND_DIGITAL_OUTPUT = "g_audio__spdif";

    /**
     * SPDIF Delay , 0 ~ 250 ,调节一次加 10
     */
    public static final String KEY_AN_SOUND_SPDIF_DELAY = "sound_spdif_delay";
    public static final String SOUND_SPDIF_DELAY = "g_audio__spdif_delay";

    /**
     * Subtitle Type
     * Normal
     * Hearing Impaired
     * Visually Impaired
     */
    public static final String CFG_MENU_AUDIO_AD_TYP = MtkTvConfigType.CFG_MENU_AUDIO_AD_TYP;
}
