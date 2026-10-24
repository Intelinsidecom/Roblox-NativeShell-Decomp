package com.roblox.roblox_client;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public class SidebarExpandableListView extends AnimatedExpandableListView {
    public SidebarExpandableListView(Context context) {
        super(context);
    }

    public SidebarExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SidebarExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0) {
            return true;
        }
        super.onTouchEvent(ev);
        ev.setAction(1);
        return super.onTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean postDelayed(Runnable action, long delayMillis) {
        action.run();
        return true;
    }
}
