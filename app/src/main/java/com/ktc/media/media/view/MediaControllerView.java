package com.ktc.media.media.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.util.DestinyUtil;

public class MediaControllerView extends RelativeLayout implements View.OnClickListener
        , View.OnFocusChangeListener, MediaSeekBar.OnSeekBarDrawListener {

    private RelativeLayout bottomContainer;
    private RelativeLayout seekBarContainer;
    private RelativeLayout topContainer;
    public MediaSeekBar mediaSeekBar;
    private TextView titleText;
    private TextView currentTime;
    private TextView durationTime;
    private ImageView previousBtn;
    private ImageView playPauseBtn;
    private ImageView nextBtn;
    private ImageView zoomOutBtn;
    private ImageView zoomInBtn;
    private ImageView rotateBtn;
    private ImageView settingBtn;
    private OnControllerClickListener mOnControllerClickListener;
    private MediaSeekPopWindow mMediaSeekPopWindow;
    public boolean mIsControllerShow = true;
    private OnMediaEventRefreshListener mOnMediaEventRefreshListener;

    public MediaControllerView(Context context) {
        super(context);
        init(context, null);
    }

    public MediaControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_media_controller, this, true);
        findView();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MediaControllerView);
            boolean isPicture = ta.getBoolean(R.styleable.MediaControllerView_isPicture, false);
            boolean isNeedTop = ta.getBoolean(R.styleable.MediaControllerView_needTop, true);
            if (!isNeedTop) {
                topContainer.setVisibility(GONE);
            }
            changeGroup(isPicture);
            ta.recycle();
        }
        addListener();
    }

    private void findView() {
        bottomContainer = (RelativeLayout) findViewById(R.id.media_play_bottom_container);
        topContainer = (RelativeLayout) findViewById(R.id.media_play_top_container);
        seekBarContainer = (RelativeLayout) findViewById(R.id.media_play_seek_bar_container);
        titleText = (TextView) findViewById(R.id.media_play_title_text);
        mediaSeekBar = (MediaSeekBar) findViewById(R.id.media_play_seek_bar);
        currentTime = (TextView) findViewById(R.id.media_play_current_time_text);
        durationTime = (TextView) findViewById(R.id.media_play_duration_time_text);
        previousBtn = (ImageView) findViewById(R.id.media_previous_btn);
        playPauseBtn = (ImageView) findViewById(R.id.media_play_pause_btn);
        nextBtn = (ImageView) findViewById(R.id.media_next_btn);
        zoomOutBtn = (ImageView) findViewById(R.id.media_zoom_out_btn);
        zoomInBtn = (ImageView) findViewById(R.id.media_zoom_in_btn);
        rotateBtn = (ImageView) findViewById(R.id.media_rotate_btn);
        settingBtn = (ImageView) findViewById(R.id.media_setting_btn);
    }

    private void changeGroup(boolean isPicture) {
        if (!isPicture) {
            zoomOutBtn.setVisibility(GONE);
            zoomInBtn.setVisibility(GONE);
            rotateBtn.setVisibility(GONE);
        } else {
            seekBarContainer.setVisibility(GONE);
        }
    }

    private void addListener() {
        previousBtn.setOnClickListener(this);
        playPauseBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        zoomOutBtn.setOnClickListener(this);
        zoomInBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
        previousBtn.setOnFocusChangeListener(this);
        playPauseBtn.setOnFocusChangeListener(this);
        nextBtn.setOnFocusChangeListener(this);
        zoomOutBtn.setOnFocusChangeListener(this);
        zoomInBtn.setOnFocusChangeListener(this);
        rotateBtn.setOnFocusChangeListener(this);
        settingBtn.setOnFocusChangeListener(this);
        mMediaSeekPopWindow = new MediaSeekPopWindow(getContext());
        mediaSeekBar.setOnSeekBarDrawListener(this);
        mediaSeekBar.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mMediaSeekPopWindow.dismiss();
                } else {
                    showAndUpdatePopWindow(mediaSeekBar.getProgress());
                }
            }
        });
        getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (newFocus == mediaSeekBar
                        || newFocus == previousBtn
                        || newFocus == playPauseBtn
                        || newFocus == nextBtn
                        || newFocus == zoomOutBtn
                        || newFocus == zoomInBtn
                        || newFocus == rotateBtn
                        || newFocus == settingBtn) {
                    if (mOnMediaEventRefreshListener != null) {
                        mOnMediaEventRefreshListener.onEventRefresh();
                    }
                }
            }
        });
    }

    private int[] getPopWindowLocation() {
        int[] location = new int[2];
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int[] seekBarLocation = new int[2];
        mediaSeekBar.getLocationOnScreen(seekBarLocation);
        int position = mediaSeekBar.getThumb().getBounds().left + DestinyUtil.dp2px(getContext(), 16.5f);
        location[0] = seekBarLocation[0]
                + position
                - width / 2;
        location[1] = bottomContainer.getTop()
                - DestinyUtil.dp2px(getContext(), 13);
        return location;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.media_previous_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onPreClick();
                }
                break;
            case R.id.media_play_pause_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onPlayPauseClick();
                }
                break;
            case R.id.media_next_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onNextClick();
                }
                break;
            case R.id.media_zoom_out_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onZoomOutClick();
                }
                break;
            case R.id.media_zoom_in_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onZoomInClick();
                }
                break;
            case R.id.media_rotate_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onRotateClick();
                }
                break;
            case R.id.media_setting_btn:
                if (mOnControllerClickListener != null) {
                    mOnControllerClickListener.onSettingClick();
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v instanceof ImageView) {
            if (hasFocus) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (mIsControllerShow) {
            showAndUpdatePopWindow(progress);
        }
    }

    private void showAndUpdatePopWindow(int progress) {
        if (mediaSeekBar.hasFocus()
                && progress != 0
                && mIsControllerShow) {
            int[] location = getPopWindowLocation();
            mMediaSeekPopWindow.showAtLocation(mediaSeekBar, Gravity.TOP, location[0], location[1]);
            mMediaSeekPopWindow.update(location[0], location[1], mMediaSeekPopWindow.getWidth()
                    , mMediaSeekPopWindow.getHeight(), true);
        }
    }

    public void setTitleText(String text) {
        titleText.setText(text);
    }

    public void setCurrentTime(String time) {
        currentTime.setText(time);
        mMediaSeekPopWindow.setPopText(time);
    }

    public void setDurationTime(String time) {
        durationTime.setText(time);
    }

    public void setPlayPauseStatus(boolean isPlay) {
        if (!isPlay) {
            playPauseBtn.setImageResource(R.drawable.media_play_image);
        } else {
            playPauseBtn.setImageResource(R.drawable.media_pause_image);
        }
    }

    public void setSeekBarMax(int max) {
        mediaSeekBar.setMax(max);
    }

    public void setSeekBarProgress(int progress) {
        mediaSeekBar.setProgress(progress);
    }

    public boolean isPlayPauseFocus() {
        return playPauseBtn.hasFocus();
    }

    public void setPlayPauseFocus() {
        playPauseBtn.requestFocus();
    }

    public void setSettingFocus() {
        settingBtn.requestFocus();
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mediaSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    public void setOnControllerClickListener(OnControllerClickListener onControllerClickListener) {
        mOnControllerClickListener = onControllerClickListener;
    }

    public void setAllCanFocus(boolean canFocus) {
        mediaSeekBar.setFocusable(canFocus);
        mediaSeekBar.setFocusableInTouchMode(canFocus);
        previousBtn.setFocusable(canFocus);
        previousBtn.setFocusableInTouchMode(canFocus);
        playPauseBtn.setFocusable(canFocus);
        playPauseBtn.setFocusableInTouchMode(canFocus);
        nextBtn.setFocusable(canFocus);
        nextBtn.setFocusableInTouchMode(canFocus);
        zoomOutBtn.setFocusable(canFocus);
        zoomOutBtn.setFocusableInTouchMode(canFocus);
        zoomInBtn.setFocusable(canFocus);
        zoomInBtn.setFocusableInTouchMode(canFocus);
        rotateBtn.setFocusable(canFocus);
        rotateBtn.setFocusableInTouchMode(canFocus);
        settingBtn.setFocusable(canFocus);
        settingBtn.setFocusableInTouchMode(canFocus);
    }

    public void showController() {
        if (!mIsControllerShow) {
            mediaSeekBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaSeekBar.hasFocus()) {
                        showAndUpdatePopWindow(mediaSeekBar.getProgress());
                    }
                }
            }, 300);
            mIsControllerShow = true;
            ObjectAnimator topAnim = ObjectAnimator.ofFloat(topContainer, "translationY"
                    , 0);
            ObjectAnimator bottomAnim = ObjectAnimator.ofFloat(bottomContainer, "translationY"
                    , 0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.play(topAnim).with(bottomAnim);
            animatorSet.start();
        }
    }

    public void hideController() {
        if (mIsControllerShow) {
            if (mMediaSeekPopWindow.isShowing()) {
                mMediaSeekPopWindow.dismiss();
            }
            mIsControllerShow = false;
            ObjectAnimator topAnim = ObjectAnimator.ofFloat(topContainer, "translationY"
                    , -topContainer.getHeight());
            ObjectAnimator bottomAnim = ObjectAnimator.ofFloat(bottomContainer, "translationY"
                    , bottomContainer.getHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.play(topAnim).with(bottomAnim);
            animatorSet.start();
        }
    }

    @Override
    public void onSeekBarDraw() {
        if (mIsControllerShow) {
            showAndUpdatePopWindow(mediaSeekBar.getProgress());
        }
    }

    public void setOnMediaEventRefreshListener(OnMediaEventRefreshListener onMediaEventRefreshListener) {
        mOnMediaEventRefreshListener = onMediaEventRefreshListener;
    }

    public interface OnMediaEventRefreshListener {
        void onEventRefresh();
    }

    public interface OnControllerClickListener {

        void onPreClick();

        void onPlayPauseClick();

        void onNextClick();

        void onZoomOutClick();

        void onZoomInClick();

        void onRotateClick();

        void onSettingClick();
    }
}
