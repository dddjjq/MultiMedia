package com.ktc.media.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ktc.media.R;
import com.ktc.media.util.DestinyUtil;

import java.util.ArrayList;
import java.util.List;

public class KeyboardPopDialog extends Dialog implements OnItemClickListener {

    private Context mContext;
    private LinearLayout itemContainer;
    private List<KeyboardPopItemView> mKeyboardPopItemViews;
    private OnPopItemClickListener mOnPopItemClickListener;

    public KeyboardPopDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mKeyboardPopItemViews = new ArrayList<>();
        itemContainer = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        itemContainer.setLayoutParams(layoutParams);
        itemContainer.setOrientation(LinearLayout.HORIZONTAL);
        itemContainer.setBackgroundResource(R.drawable.keyboard_pop_background);
        setContentView(itemContainer);
        setWindowLayoutParam();
    }

    private void setWindowLayoutParam() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0f;
        getWindow().setAttributes(lp);
    }

    public void prepareData(String[] data) {
        for (int i = 0; i < data.length; i++) {
            KeyboardPopItemView keyboardPopItemView = new KeyboardPopItemView(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DestinyUtil.dp2px(mContext, 53.3f)
                    , DestinyUtil.dp2px(mContext, 53.3f));
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            keyboardPopItemView.setText(data[i]);
            keyboardPopItemView.setLayoutParams(layoutParams);
            keyboardPopItemView.setId(keyboardPopItemView.hashCode());
            keyboardPopItemView.setOnItemClickListener(this);
            itemContainer.addView(keyboardPopItemView);
            mKeyboardPopItemViews.add(keyboardPopItemView);
        }
    }

    @Override
    public void onItemClick(View view) {
        for (KeyboardPopItemView keyboardPopItemView : mKeyboardPopItemViews) {
            if (keyboardPopItemView.getId() == view.getId()) {
                if (mOnPopItemClickListener != null) {
                    mOnPopItemClickListener.onPopItemClick(keyboardPopItemView.getTextString());
                }
                dismiss();
            }
        }
    }

    public void setOnPopItemClickListener(OnPopItemClickListener onPopItemClickListener) {
        mOnPopItemClickListener = onPopItemClickListener;
    }

    public interface OnPopItemClickListener {
        void onPopItemClick(String s);
    }

}
