package com.ktc.media.menu.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.menu.base.BaseMenuFragment;
import com.ktc.media.menu.entity.MinorMenuEntity;
import com.ktc.media.menu.entity.MinorType;
import com.ktc.media.menu.view.MajorMenuView;
import com.ktc.media.menu.view.MenuViewContainer;
import com.ktc.media.menu.view.MinorMenuView;
import com.ktc.media.util.DestinyUtil;

import java.util.ArrayList;
import java.util.List;

public class MinorMenuFragment extends BaseMenuFragment {

    private RelativeLayout minorMenuLayout;
    private MenuViewContainer minorMenuContainer;
    private List<MinorMenuEntity> mEntities;
    private MajorMenuView mMajorMenuView = null;
    private int mMajorIndex = 0;
    private List<MinorMenuView> mMenuViews;

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimatorInflater.loadAnimator(getContext(), R.animator.minor_menu_left_in);
        } else {
            return AnimatorInflater.loadAnimator(getContext(), R.animator.minor_menu_left_out);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_minor_menu_layout;
    }

    @Override
    public void initView(View view) {
        minorMenuLayout = (RelativeLayout) view.findViewById(R.id.minor_menu_layout);
    }

    @Override
    public void initData() {
        if (mEntities == null) {
            throw new RuntimeException("Entities must prepared before load fragment!");
        }
        mMenuViews = new ArrayList<>();
        initContainerLocation();
        initMinorView();
    }

    @Override
    public void addListener() {

    }

    @Override
    public void initFocus() {

    }

    @Override
    public ViewGroup getContainerView() {
        return minorMenuContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMajorMenuView.requestFocus();
    }

    //初始化Container位置
    private void initContainerLocation() {
        if (mMajorMenuView == null) return;
        minorMenuContainer = new MenuViewContainer(getContext());
        minorMenuLayout.addView(minorMenuContainer);
        if (mMajorIndex < 2) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                    , RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (mMajorMenuView.getParent() instanceof View) {
                layoutParams.topMargin = mMajorMenuView.getTop()
                        + ((View) mMajorMenuView.getParent()).getTop();
            } else {
                layoutParams.topMargin = mMajorMenuView.getTop();
            }
            minorMenuContainer.setLayoutParams(layoutParams);
        } else {
            int height = getContainerHeight();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                    , RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (mMajorMenuView.getParent() instanceof View) {
                layoutParams.topMargin = mMajorMenuView.getTop() + ((View) mMajorMenuView.getParent()).getTop()
                        + mMajorMenuView.getHeight() / 2 - height / 2;
            } else {
                layoutParams.topMargin = mMajorMenuView.getTop() + mMajorMenuView.getHeight() / 2 - height / 2;
            }
            minorMenuContainer.setLayoutParams(layoutParams);
        }
        startAnim(minorMenuContainer);
    }

    private void initMinorView() {
        mMenuViews.clear();
        for (MinorMenuEntity entity : mEntities) {
            MinorMenuView minorMenuView = new MinorMenuView(getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    DestinyUtil.dp2px(getContext(), 38.7f));
            if (mEntities.indexOf(entity) != (mEntities.size() - 1)) {
                layoutParams.bottomMargin = DestinyUtil.dp2px(getContext(), 6.7f);
            }
            if (mMenuViews.size() > 0) {
                layoutParams.addRule(RelativeLayout.BELOW, mMenuViews.get(mMenuViews.size() - 1).getId());
            }
            minorMenuView.setLayoutParams(layoutParams);
            minorMenuView.setTextView(entity.getTextString());
            minorMenuView.setIsPoint(entity.getType() == MinorType.TYPE_POINT);
            if (entity.isSelected()) {
                minorMenuView.setSelectStatus(true);
            } else {
                minorMenuView.setSelectStatus(false);
            }
            minorMenuView.setId(minorMenuView.hashCode());
            minorMenuView.setOnItemClickListener(entity.getListener());
            minorMenuContainer.addView(minorMenuView);
            mMenuViews.add(minorMenuView);
            minorMenuView.setNextFocusLeftId(mMajorMenuView.getId());
        }
        if (mMenuViews.size() > 0) {
            mMajorMenuView.setNextFocusRightId(mMenuViews.get(0).getId());
        }

    }

    private int getContainerHeight() {
        if (mMajorMenuView == null) return 0;
        return DestinyUtil.dp2px(getContext(), 38.7f) * mEntities.size()
                + DestinyUtil.dp2px(getContext(), 6.7f) * (mEntities.size() - 1);
    }

    public void setMajor(MajorMenuView majorMenuView, int index, List<MinorMenuEntity> minorMenuEntities) {
        mMajorMenuView = majorMenuView;
        mMajorIndex = index;
        mEntities = minorMenuEntities;
        if (minorMenuContainer != null) {
            clearOtherView();
            initContainerLocation();
            initMinorView();
        }
    }

    private void clearOtherView() {
        minorMenuLayout.removeAllViews();
    }

    private void startAnim(View view) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        alphaAnim.setDuration(400);
        alphaAnim.start();
    }
}
