package com.ktc.media.menu.base;


import com.ktc.media.menu.entity.MajorMenuEntity;
import com.ktc.media.menu.entity.MinorMenuEntity;

import java.util.List;

public interface IController<T extends BaseMenuFragment> {

    void newMajorFragment(List<MajorMenuEntity> entities, T t);

    void newMinorFragment(List<MinorMenuEntity> entities, T t);

    void newFragment(int layoutId, T t);

    void removeFragment(T t);

    void clearFragments();

    List<T> getFragments();

    T peekFragment();
}
