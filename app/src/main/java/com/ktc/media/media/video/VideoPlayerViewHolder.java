package com.ktc.media.media.video;

import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.media.business.video.VideoPlayView;
import com.ktc.media.media.view.MediaControllerView;


public class VideoPlayerViewHolder {

    protected RelativeLayout mVideoPlayLayout;
    public VideoPlayView mVideoPlayView;
    protected MediaControllerView mMediaControllerView;
    private VideoPlayerActivity mVideoPlayerActivity;

    public VideoPlayerViewHolder(VideoPlayerActivity videoPlayerActivity) {
        mVideoPlayerActivity = videoPlayerActivity;
        findView();
    }

    private void findView() {
        mVideoPlayLayout = (RelativeLayout) mVideoPlayerActivity.findViewById(R.id.video_play_layout);
        mVideoPlayView = (VideoPlayView) mVideoPlayerActivity.findViewById(R.id.video_play_view);
        mMediaControllerView = (MediaControllerView) mVideoPlayerActivity.findViewById(R.id.video_play_controller);
    }

}

