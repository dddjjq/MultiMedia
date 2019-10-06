package com.ktc.media.menu.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.media.business.video.VideoPlayView;
import com.ktc.media.media.photo.ImagePlayerSurfaceView;
import com.ktc.media.menu.base.BaseMenuFragment;
import com.ktc.media.menu.entity.MajorMenuEntity;
import com.ktc.media.menu.view.MajorMenuView;
import com.ktc.media.menu.view.MenuViewContainer;
import com.ktc.media.menu.view.MinorMenuView;
import com.ktc.media.util.DestinyUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MajorMenuFragment extends BaseMenuFragment {

    public MenuViewContainer majorMenuContainer;
    private List<MajorMenuEntity> mMenuEntities;
    private Map<MajorMenuView, MajorMenuEntity> mMajorMenuEntityHashMap;
    private List<MajorMenuView> mMajorMenuViews;
    private static final int majorAnimTime = 400;
    private boolean isInit = false;

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimatorInflater.loadAnimator(getContext(), R.animator.major_menu_left_in);
        } else {
            return AnimatorInflater.loadAnimator(getContext(), R.animator.major_menu_left_out);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_major_menu_layout;
    }

    @Override
    public void initView(View view) {
        majorMenuContainer = (MenuViewContainer) view.findViewById(R.id.major_menu_container);
    }

    @Override
    public void initData() {
        if (mMenuEntities == null) {
            throw new RuntimeException("Entities must prepared before load fragment!");
        }
        mMajorMenuEntityHashMap = new LinkedHashMap<>();
        mMajorMenuViews = new ArrayList<>();
        for (MajorMenuEntity majorMenuEntity : mMenuEntities) {
            MajorMenuView menuView = new MajorMenuView(getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    DestinyUtil.dp2px(getContext(), 40));
            if (mMenuEntities.indexOf(majorMenuEntity) != (mMenuEntities.size() - 1)) {
                layoutParams.bottomMargin = DestinyUtil.dp2px(getContext(), 23);
            }
            if (mMajorMenuViews.size() > 0) {
                layoutParams.addRule(RelativeLayout.BELOW, mMajorMenuViews.get(mMajorMenuViews.size() - 1).getId());
            }
            menuView.setLayoutParams(layoutParams);
            menuView.setImageView(majorMenuEntity.getImageRes());
            menuView.setTextView(majorMenuEntity.getTextString());
            menuView.setId(menuView.hashCode());
            majorMenuContainer.addView(menuView);
            mMajorMenuViews.add(menuView);
            mMajorMenuEntityHashMap.put(menuView, majorMenuEntity);
        }
        if (mMajorMenuViews.size() > 0) {
            mMajorMenuViews.get(0).requestFocus();
            mMajorMenuViews.get(0).setSelected(true);
            majorMenuContainer.setNewFocus(mMajorMenuViews.get(0));
        }
        majorMenuContainer.setFlyBorderViewDuration(250);
    }

    @Override
    public void addListener() {
        majorMenuContainer.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (!isInit) return;
                if (newFocus == null
                        || majorMenuContainer.indexOfChild(newFocus) == -1
                        || oldFocus instanceof ImagePlayerSurfaceView
                        || oldFocus instanceof VideoPlayView) {
                    return;
                }
                if (oldFocus instanceof MinorMenuView
                        && newFocus instanceof MajorMenuView) {
                    MinorMenuView minorMenuView = (MinorMenuView) oldFocus;
                    MajorMenuView majorMenuView = (MajorMenuView) newFocus;
                    if (minorMenuView.getNextFocusLeftId() == majorMenuView.getId()) {
                        //avoid key left refresh minor view
                        return;
                    }
                }
                for (MajorMenuView majorMenuView : mMajorMenuEntityHashMap.keySet()) {
                    if (majorMenuView == null) return;
                    if (majorMenuView.getId() == newFocus.getId()) {
                        majorMenuView.setSelected(true);
                        if (mActivity.mController.peekFragment() instanceof MinorMenuFragment) {
                            ((MinorMenuFragment) mActivity.mController.peekFragment())
                                    .setMajor(majorMenuView, mMajorMenuViews.indexOf(majorMenuView)
                                            , mMajorMenuEntityHashMap.get(mMajorMenuViews.get(mMajorMenuViews.indexOf(majorMenuView)))
                                                    .getMinorMenuEntities());
                        } else {
                            startMinorFragment(mMajorMenuViews.indexOf(majorMenuView));
                        }
                    } else {
                        majorMenuView.setSelected(false);
                    }
                }
            }
        });
    }

    @Override
    public void initFocus() {
        majorMenuContainer.getChildAt(0).requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initMinorView();
            }
        }, majorAnimTime);
    }

    @Override
    public ViewGroup getContainerView() {
        return majorMenuContainer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.mController.clearFragments();
    }

    public void prepareEntities(List<MajorMenuEntity> menuEntities) {
        mMenuEntities = menuEntities;
    }

    private void initMinorView() {
        isInit = true;
        startMinorFragment(0);
    }

    private void startMinorFragment(int index) {
        MinorMenuFragment minorMenuFragment = new MinorMenuFragment();
        minorMenuFragment.setMajor(mMajorMenuViews.get(index), index
                , mMajorMenuEntityHashMap.get(mMajorMenuViews.get(index)).getMinorMenuEntities());
        mActivity.mController.newMinorFragment(mMajorMenuEntityHashMap.get(mMajorMenuViews.get(index)).getMinorMenuEntities(),
                minorMenuFragment);
    }
}
