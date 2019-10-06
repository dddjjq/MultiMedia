package com.ktc.media.menu.holder;


import com.ktc.media.menu.entity.MajorMenuEntity;

import java.util.List;

public abstract class BaseEntityHolder {

    public abstract List<MajorMenuEntity> getMajorMenuEntities();

    public abstract void init();
}
