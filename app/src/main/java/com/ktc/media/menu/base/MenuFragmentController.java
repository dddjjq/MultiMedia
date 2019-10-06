package com.ktc.media.menu.base;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import com.ktc.media.R;
import com.ktc.media.menu.fragment.MajorMenuFragment;
import com.ktc.media.menu.fragment.MinorMenuFragment;

import java.util.ArrayList;
import java.util.List;

public class MenuFragmentController implements IController {

    private List<BaseMenuFragment> mBaseMenuFragments;
    private FragmentManager mFragmentManager;

    public MenuFragmentController(Context context) {
        mBaseMenuFragments = new ArrayList<>();
        mFragmentManager = ((Activity) (context)).getFragmentManager();
    }

    @Override
    public void newMajorFragment(List list, BaseMenuFragment baseMenuFragment) {
        if (mBaseMenuFragments.size() > 0) return;
        if (baseMenuFragment instanceof MajorMenuFragment) {
            ((MajorMenuFragment) baseMenuFragment).prepareEntities(list);
        } else {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.first_menu_frame, baseMenuFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        mBaseMenuFragments.add(baseMenuFragment);
    }

    @Override
    public void newMinorFragment(List list, BaseMenuFragment baseMenuFragment) {
        if (!(baseMenuFragment instanceof MinorMenuFragment)) {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.second_menu_frame, baseMenuFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        mBaseMenuFragments.add(baseMenuFragment);
    }

    @Override
    public void newFragment(int layoutId, BaseMenuFragment baseMenuFragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(layoutId, baseMenuFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        mBaseMenuFragments.add(baseMenuFragment);
    }

    @Override
    public void removeFragment(BaseMenuFragment baseMenuFragment) {
        if (baseMenuFragment == null) return;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.remove(baseMenuFragment);
        if (mBaseMenuFragments.contains(baseMenuFragment)) {
            mBaseMenuFragments.remove(baseMenuFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void clearFragments() {
        for (BaseMenuFragment baseMenuFragment : mBaseMenuFragments) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.remove(baseMenuFragment).commitAllowingStateLoss();
        }
        mBaseMenuFragments.clear();
    }

    @Override
    public List<BaseMenuFragment> getFragments() {
        return mBaseMenuFragments;
    }

    @Override
    public BaseMenuFragment peekFragment() {
        if (mBaseMenuFragments.size() > 0) {
            return mBaseMenuFragments.get(mBaseMenuFragments.size() - 1);
        }
        return null;
    }
}
