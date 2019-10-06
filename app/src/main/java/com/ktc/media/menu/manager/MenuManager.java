package com.ktc.media.menu.manager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.menu.base.BaseMenuActivity;
import com.ktc.media.menu.fragment.MajorMenuFragment;
import com.ktc.media.menu.holder.BaseEntityHolder;

public class MenuManager {

    private BaseMenuActivity mBaseMenuActivity;
    private RelativeLayout mainContainer;

    public MenuManager(BaseMenuActivity baseMenuActivity) {
        mBaseMenuActivity = baseMenuActivity;
        mainContainer = baseMenuActivity.getMainContainer();
        View menuContainerLayout = LayoutInflater.from(mBaseMenuActivity).inflate(R.layout.menu_container_layout, null);
        mainContainer.addView(menuContainerLayout);
    }

    public void startMajorView() {
        BaseEntityHolder mBaseEntityHolder = mBaseMenuActivity.getBaseEntityHolder();
        MajorMenuFragment mMajorMenuFragment = new MajorMenuFragment();
        mBaseMenuActivity.mController.newMajorFragment(mBaseEntityHolder.getMajorMenuEntities(), mMajorMenuFragment);
    }
}
