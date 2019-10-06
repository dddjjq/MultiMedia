package com.ktc.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import java.lang.reflect.Method;

public class KeyboardEditText extends EditText {


    public KeyboardEditText(Context context) {
        super(context);
    }

    public KeyboardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void disableShowSoftInput() {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableShowSoftInput() {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
