package com.ktc.media.menu.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.menu.manager.MenuManager;


public abstract class BaseMenuActivity extends Activity {

    public MenuFragmentController mController;
    public MenuManager mMenuManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mController = new MenuFragmentController(this);
        initView();
        mMenuManager = new MenuManager(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mController.removeFragment(mController.peekFragment());
        if (!isMenuShow()) {
            setAllViewCanFocus();
        }
    }

    public void exitMenu() {
        mController.clearFragments();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            boolean result = false;
            for (BaseMenuFragment fragment : mController.getFragments()) {
                result |= fragment.onKeyDown(event);
            }
            if (result) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void startMenu() {
        mMenuManager.startMajorView();
    }

    public boolean isMenuShow() {
        return mController.getFragments().size() != 0;
    }

    public abstract RelativeLayout getMainContainer();

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract BaseEntityHolder getBaseEntityHolder();

    public abstract void setAllViewCanFocus();

}
