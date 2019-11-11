package com.ktc.media.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.util.DestinyUtil;

public class KeyboardView extends RelativeLayout implements OnItemClickListener
        , KeyboardPopDialog.OnPopItemClickListener {

    private Context mContext;
    private KeyboardEditText keyboardEditText;
    private KeyboardItemView clearKey;
    private KeyboardItemView backSpaceKey;
    private KeyboardItemView key0;
    private KeyboardItemView key1;
    private KeyboardItemView key2;
    private KeyboardItemView key3;
    private KeyboardItemView key4;
    private KeyboardItemView key5;
    private KeyboardItemView key6;
    private KeyboardItemView key7;
    private KeyboardItemView key8;
    private KeyboardItemView key9;
    private KeyboardItemView t9Keyboard;
    private KeyboardItemView systemKeyboard;
    private RelativeLayout keyboardGridLayout;
    private OnTextChangeListener mOnTextChangeListener;

    private static final String[] ABC = {"2", "A", "B", "C"};
    private static final String[] DEF = {"3", "D", "E", "F"};
    private static final String[] GHI = {"4", "G", "H", "I"};
    private static final String[] JKL = {"5", "J", "K", "L"};
    private static final String[] MNO = {"6", "M", "N", "O"};
    private static final String[] PQRS = {"7", "P", "Q", "R", "S"};
    private static final String[] TUV = {"8", "T", "U", "V"};
    private static final String[] WXYZ = {"9", "W", "X", "Y", "Z"};

    public KeyboardView(Context context) {
        super(context);
        init(context);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_keyboard_layout, this, true);
        findView();
        addListener();
    }

    private void findView() {
        keyboardEditText = (KeyboardEditText) findViewById(R.id.keyboard_edit);
        clearKey = (KeyboardItemView) findViewById(R.id.keyboard_clear);
        backSpaceKey = (KeyboardItemView) findViewById(R.id.keyboard_backspace);
        key0 = (KeyboardItemView) findViewById(R.id.keyboard_num_0);
        key1 = (KeyboardItemView) findViewById(R.id.keyboard_num_1);
        key2 = (KeyboardItemView) findViewById(R.id.keyboard_num_2);
        key3 = (KeyboardItemView) findViewById(R.id.keyboard_num_3);
        key4 = (KeyboardItemView) findViewById(R.id.keyboard_num_4);
        key5 = (KeyboardItemView) findViewById(R.id.keyboard_num_5);
        key6 = (KeyboardItemView) findViewById(R.id.keyboard_num_6);
        key7 = (KeyboardItemView) findViewById(R.id.keyboard_num_7);
        key8 = (KeyboardItemView) findViewById(R.id.keyboard_num_8);
        key9 = (KeyboardItemView) findViewById(R.id.keyboard_num_9);
        t9Keyboard = (KeyboardItemView) findViewById(R.id.keyboard_t9);
        systemKeyboard = (KeyboardItemView) findViewById(R.id.keyboard_system);
        keyboardGridLayout = (RelativeLayout) findViewById(R.id.keyboard_grid_layout);
    }

    private void addListener() {
        clearKey.setOnItemClickListener(this);
        backSpaceKey.setOnItemClickListener(this);
        key0.setOnItemClickListener(this);
        key1.setOnItemClickListener(this);
        key2.setOnItemClickListener(this);
        key3.setOnItemClickListener(this);
        key4.setOnItemClickListener(this);
        key5.setOnItemClickListener(this);
        key6.setOnItemClickListener(this);
        key7.setOnItemClickListener(this);
        key8.setOnItemClickListener(this);
        key9.setOnItemClickListener(this);
        t9Keyboard.setOnItemClickListener(this);
        systemKeyboard.setOnItemClickListener(this);
        switchKeyboard(true);
        addEditTextListener();
    }

    private void addEditTextListener() {
        keyboardEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mOnTextChangeListener != null) {
                    mOnTextChangeListener.onTextChange(keyboardEditText.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.keyboard_clear:
                keyboardEditText.setText(null);
                break;
            case R.id.keyboard_backspace:
                if (keyboardEditText.getText().toString().length() > 0) {
                    keyboardEditText.setText(keyboardEditText.getText().toString()
                            .substring(0, keyboardEditText.getText().toString().length() - 1));
                } else {
                    keyboardEditText.setText(null);
                }
                break;
            case R.id.keyboard_num_0:
                keyboardEditText.setText(getAppendString(0));
                break;
            case R.id.keyboard_num_1:
                keyboardEditText.setText(getAppendString(1));
                break;
            case R.id.keyboard_num_2:
                startMoreDialog(ABC);
                break;
            case R.id.keyboard_num_3:
                startMoreDialog(DEF);
                break;
            case R.id.keyboard_num_4:
                startMoreDialog(GHI);
                break;
            case R.id.keyboard_num_5:
                startMoreDialog(JKL);
                break;
            case R.id.keyboard_num_6:
                startMoreDialog(MNO);
                break;
            case R.id.keyboard_num_7:
                startMoreDialog(PQRS);
                break;
            case R.id.keyboard_num_8:
                startMoreDialog(TUV);
                break;
            case R.id.keyboard_num_9:
                startMoreDialog(WXYZ);
                break;
            case R.id.keyboard_t9:
                switchKeyboard(true);
                break;
            case R.id.keyboard_system:
                switchKeyboard(false);
                break;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        keyboardEditText.setFocusable(enabled);
        keyboardEditText.setFocusableInTouchMode(enabled);
        clearKey.setFocusable(enabled);
        clearKey.setFocusableInTouchMode(enabled);
        backSpaceKey.setFocusable(enabled);
        backSpaceKey.setFocusableInTouchMode(enabled);
        key0.setFocusable(enabled);
        key0.setFocusableInTouchMode(enabled);
        key1.setFocusable(enabled);
        key1.setFocusableInTouchMode(enabled);
        key2.setFocusable(enabled);
        key2.setFocusableInTouchMode(enabled);
        key3.setFocusable(enabled);
        key3.setFocusableInTouchMode(enabled);
        key4.setFocusable(enabled);
        key4.setFocusableInTouchMode(enabled);
        key5.setFocusable(enabled);
        key5.setFocusableInTouchMode(enabled);
        key6.setFocusable(enabled);
        key6.setFocusableInTouchMode(enabled);
        key7.setFocusable(enabled);
        key7.setFocusableInTouchMode(enabled);
        key8.setFocusable(enabled);
        key8.setFocusableInTouchMode(enabled);
        key9.setFocusable(enabled);
        key9.setFocusableInTouchMode(enabled);
        t9Keyboard.setFocusable(enabled);
        t9Keyboard.setFocusableInTouchMode(enabled);
        systemKeyboard.setFocusable(enabled);
        systemKeyboard.setFocusableInTouchMode(enabled);
    }

    public void switchKeyboard(boolean isT9) {
        if (isT9) {
            t9Keyboard.setPointSelect(true);
            systemKeyboard.setPointSelect(false);
            keyboardEditText.disableShowSoftInput();
            enableT9Keyboard(true);
        } else {
            t9Keyboard.setPointSelect(false);
            systemKeyboard.setPointSelect(true);
            keyboardEditText.enableShowSoftInput();
            enableT9Keyboard(false);
            keyboardEditText.requestFocus();
            showOrHideIme();
        }
    }

    @SuppressWarnings("all")
    public void showOrHideIme() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void enableT9Keyboard(boolean enable) {
        key0.setEnabled(enable);
        key1.setEnabled(enable);
        key2.setEnabled(enable);
        key3.setEnabled(enable);
        key4.setEnabled(enable);
        key5.setEnabled(enable);
        key6.setEnabled(enable);
        key7.setEnabled(enable);
        key8.setEnabled(enable);
        key9.setEnabled(enable);
        if (!enable) {
            systemKeyboard.setNextFocusUpId(backSpaceKey.getId());
            t9Keyboard.setNextFocusUpId(clearKey.getId());
            clearKey.setNextFocusDownId(t9Keyboard.getId());
            backSpaceKey.setNextFocusDownId(systemKeyboard.getId());
        } else {
            systemKeyboard.setNextFocusUpId(key9.getId());
            t9Keyboard.setNextFocusUpId(key7.getId());
            clearKey.setNextFocusDownId(key1.getId());
            backSpaceKey.setNextFocusDownId(key3.getId());
        }
    }

    private void startMoreDialog(String[] data) {
        KeyboardPopDialog keyboardPopDialog = new KeyboardPopDialog(getContext());
        keyboardPopDialog.prepareData(data);
        setDialogLayoutParam(keyboardPopDialog, data.length);
        keyboardPopDialog.setOnPopItemClickListener(this);
        keyboardPopDialog.show();
    }

    private void setDialogLayoutParam(KeyboardPopDialog keyboardPopDialog, int size) {
        int width = DestinyUtil.dp2px(getContext(), 14.7f) * 2
                + DestinyUtil.dp2px(getContext(), 53.3f) * size;
        int height = DestinyUtil.dp2px(getContext(), 93.3f);
        int left = (getWidth() - width) / 2;
        int top = keyboardGridLayout.getTop() + (keyboardGridLayout.getTop() - height) / 2
                + DestinyUtil.dp2px(getContext(), 36.7f);
        WindowManager.LayoutParams lp = keyboardPopDialog.getWindow().getAttributes();
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = left;
        lp.y = top;
        keyboardPopDialog.getWindow().setAttributes(lp);
    }

    private String getAppendString(int i) {
        return keyboardEditText.getText().toString() + String.valueOf(i);
    }

    private String getAppendString(String s) {
        return keyboardEditText.getText().toString() + s;
    }

    public void editTextRequestFocus() {
        keyboardEditText.requestFocus();
    }

    public void clearKeyboard() {
        keyboardEditText.setText(null);
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        mOnTextChangeListener = onTextChangeListener;
    }

    @Override
    public void onPopItemClick(String s) {
        keyboardEditText.setText(getAppendString(s));
    }

    public interface OnTextChangeListener {
        void onTextChange(String s);
    }
}
