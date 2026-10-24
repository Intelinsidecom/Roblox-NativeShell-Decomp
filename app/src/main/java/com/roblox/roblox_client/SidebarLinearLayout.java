package com.roblox.roblox_client;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/* loaded from: classes.dex */
public class SidebarLinearLayout extends LinearLayout {
    public SidebarLinearLayout(Context context) {
        super(context);
    }

    public SidebarLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SidebarLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || !super.onTouchEvent(ev)) {
            return super.onTouchEvent(ev);
        }
        ev.setAction(1);
        super.onTouchEvent(ev);
        return true;
    }

    @Override // android.view.View
    public boolean postDelayed(Runnable action, long delayMillis) {
        action.run();
        return true;
    }
}
