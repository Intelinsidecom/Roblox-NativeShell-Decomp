package com.roblox.roblox_client;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/* loaded from: classes.dex */
public class RbxKeyboard extends EditText {
    long currentTextBox;

    public RbxKeyboard(Context context) {
        super(context);
    }

    public RbxKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentTextBox(long newTextBox) {
        this.currentTextBox = newTextBox;
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 4 && event.getAction() == 1) {
            ActivityGlView.releaseFocus(this.currentTextBox);
            this.currentTextBox = 0L;
            setVisibility(8);
            setText("");
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService("input_method");
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        return super.dispatchKeyEvent(event);
    }
}
