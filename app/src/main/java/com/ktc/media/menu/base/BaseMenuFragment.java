package com.ktc.media.menu.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseMenuFragment extends Fragment {

    public BaseMenuActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initFocus();
        addListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseMenuActivity) context;
    }

    public boolean onKeyDown(KeyEvent event) {
        ViewGroup container = getContainerView();
        if (container == null) return false;
        View focusView = container.findFocus();
        if (focusView != null && container.indexOfChild(focusView) != -1) {
            View focusUp = focusView.focusSearch(View.FOCUS_UP);
            View focusDown = focusView.focusSearch(View.FOCUS_DOWN);
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                return focusUp == null
                        || container.indexOfChild(focusUp) == -1;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                return focusDown == null
                        || container.indexOfChild(focusDown) == -1;
            }
        }
        return false;
    }

    public abstract int getLayoutId();

    public abstract void initView(View view);

    public abstract void initData();

    public abstract void addListener();

    public abstract void initFocus();

    public abstract ViewGroup getContainerView();
}
