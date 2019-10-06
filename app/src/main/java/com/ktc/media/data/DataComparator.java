package com.ktc.media.data;

import com.ktc.media.model.BaseData;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class DataComparator<T extends BaseData> implements Comparator<T> {

    @Override
    public int compare(T t1, T t2) {
        String lName = t1.getName();
        String rName = t2.getName();
        File lFile = new File(t1.getPath());
        File rFile = new File(t2.getPath());
        if (lFile.exists() && rFile.exists()) {
            if (lFile.isDirectory() && !rFile.isDirectory()) {
                return -1;
            } else if (!lFile.isDirectory() && rFile.isDirectory()) {
                return 1;
            }
        }
        if (lName != null && rName != null) {
            Collator collator = Collator.getInstance(Locale.CHINA);
            return collator.compare(lName.toLowerCase(),
                    rName.toLowerCase());
        } else {
            return 0;
        }
    }
}
