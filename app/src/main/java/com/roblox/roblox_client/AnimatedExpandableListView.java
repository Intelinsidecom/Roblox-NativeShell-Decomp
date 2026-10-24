package com.roblox.roblox_client;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AnimatedExpandableListView extends ExpandableListView {
    private static final int ANIMATION_DURATION = 150;
    private AnimatedExpandableListAdapter adapter;

    public AnimatedExpandableListView(Context context) {
        super(context);
    }

    public AnimatedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.ExpandableListView
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof AnimatedExpandableListAdapter) {
            this.adapter = (AnimatedExpandableListAdapter) adapter;
            this.adapter.setParent(this);
            return;
        }
        throw new ClassCastException(String.valueOf(adapter.toString()) + " must implement AnimatedExpandableListAdapter");
    }

    public boolean expandGroupWithAnimation(int groupPos) {
        int childIndex;
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1 && (childIndex = groupFlatPos - getFirstVisiblePosition()) < getChildCount()) {
            View v = getChildAt(childIndex);
            if (v.getBottom() >= getBottom()) {
                return expandGroup(groupPos);
            }
        }
        this.adapter.startExpandAnimation(groupPos, 0);
        return expandGroup(groupPos);
    }

    public boolean collapseGroupWithAnimation(int groupPos) {
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1) {
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            if (childIndex >= 0 && childIndex < getChildCount()) {
                View v = getChildAt(childIndex);
                if (v.getBottom() >= getBottom()) {
                    return collapseGroup(groupPos);
                }
            } else {
                return collapseGroup(groupPos);
            }
        }
        long packedPos = getExpandableListPosition(getFirstVisiblePosition());
        int firstChildPos = getPackedPositionChild(packedPos);
        int firstGroupPos = getPackedPositionGroup(packedPos);
        if (firstChildPos == -1 || firstGroupPos != groupPos) {
            firstChildPos = 0;
        }
        this.adapter.startCollapseAnimation(groupPos, firstChildPos);
        this.adapter.notifyDataSetChanged();
        return isGroupExpanded(groupPos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAnimationDuration() {
        return ANIMATION_DURATION;
    }

    private static class GroupInfo {
        boolean animating;
        int dummyHeight;
        boolean expanding;
        int firstChildPosition;

        private GroupInfo() {
            this.animating = false;
            this.expanding = false;
            this.dummyHeight = -1;
        }

        /* synthetic */ GroupInfo(GroupInfo groupInfo) {
            this();
        }
    }

    public static abstract class AnimatedExpandableListAdapter extends BaseExpandableListAdapter {
        private SparseArray<GroupInfo> groupInfo = new SparseArray<>();
        private AnimatedExpandableListView parent;

        public abstract View getRealChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup);

        public abstract int getRealChildrenCount(int i);

        /* JADX INFO: Access modifiers changed from: private */
        public void setParent(AnimatedExpandableListView parent) {
            this.parent = parent;
        }

        public int getRealChildType(int groupPosition, int childPosition) {
            return 0;
        }

        public int getRealChildTypeCount() {
            return 1;
        }

        private GroupInfo getGroupInfo(int groupPosition) {
            GroupInfo info = this.groupInfo.get(groupPosition);
            if (info == null) {
                GroupInfo info2 = new GroupInfo(null);
                this.groupInfo.put(groupPosition, info2);
                return info2;
            }
            return info;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startExpandAnimation(int groupPosition, int firstChildPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = true;
            info.firstChildPosition = firstChildPosition;
            info.expanding = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startCollapseAnimation(int groupPosition, int firstChildPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = true;
            info.firstChildPosition = firstChildPosition;
            info.expanding = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void stopAnimation(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = false;
        }

        @Override // android.widget.BaseExpandableListAdapter, android.widget.HeterogeneousExpandableList
        public final int getChildType(int groupPosition, int childPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            if (info.animating) {
                return 0;
            }
            return getRealChildType(groupPosition, childPosition) + 1;
        }

        @Override // android.widget.BaseExpandableListAdapter, android.widget.HeterogeneousExpandableList
        public final int getChildTypeCount() {
            return getRealChildTypeCount() + 1;
        }

        @Override // android.widget.ExpandableListAdapter
        public final View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final GroupInfo info = getGroupInfo(groupPosition);
            if (info.animating) {
                if (convertView == null) {
                    convertView = new DummyView(parent.getContext());
                    convertView.setLayoutParams(new ViewGroup.LayoutParams(-1, 0));
                }
                if (childPosition < info.firstChildPosition) {
                    convertView.getLayoutParams().height = 0;
                    return convertView;
                }
                final ExpandableListView listView = (ExpandableListView) parent;
                DummyView dummyView = (DummyView) convertView;
                dummyView.clearViews();
                dummyView.setDivider(listView.getDivider(), parent.getMeasuredWidth(), listView.getDividerHeight());
                int measureSpecW = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), 1073741824);
                int measureSpecH = View.MeasureSpec.makeMeasureSpec(0, 0);
                int totalHeight = 0;
                int clipHeight = parent.getHeight();
                int len = getRealChildrenCount(groupPosition);
                int i = info.firstChildPosition;
                while (true) {
                    if (i >= len) {
                        break;
                    }
                    View childView = getRealChildView(groupPosition, i, i == len + (-1), null, parent);
                    childView.measure(measureSpecW, measureSpecH);
                    totalHeight += childView.getMeasuredHeight();
                    if (totalHeight < clipHeight) {
                        dummyView.addFakeView(childView);
                        i++;
                    } else {
                        dummyView.addFakeView(childView);
                        int averageHeight = totalHeight / (i + 1);
                        totalHeight += ((len - i) - 1) * averageHeight;
                        break;
                    }
                }
                if (info.expanding) {
                    ExpandAnimation ani = new ExpandAnimation(dummyView, 0, totalHeight, info, null);
                    ani.setDuration(this.parent.getAnimationDuration());
                    ani.setAnimationListener(new Animation.AnimationListener() { // from class: com.roblox.roblox_client.AnimatedExpandableListView.AnimatedExpandableListAdapter.1
                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationEnd(Animation animation) {
                            AnimatedExpandableListAdapter.this.stopAnimation(groupPosition);
                            AnimatedExpandableListAdapter.this.notifyDataSetChanged();
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    dummyView.startAnimation(ani);
                } else {
                    if (info.dummyHeight == -1) {
                        info.dummyHeight = totalHeight;
                    }
                    ExpandAnimation ani2 = new ExpandAnimation(dummyView, info.dummyHeight, 0, info, null);
                    ani2.setDuration(this.parent.getAnimationDuration());
                    ani2.setAnimationListener(new Animation.AnimationListener() { // from class: com.roblox.roblox_client.AnimatedExpandableListView.AnimatedExpandableListAdapter.2
                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationEnd(Animation animation) {
                            AnimatedExpandableListAdapter.this.stopAnimation(groupPosition);
                            listView.collapseGroup(groupPosition);
                            AnimatedExpandableListAdapter.this.notifyDataSetChanged();
                            info.dummyHeight = -1;
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    dummyView.startAnimation(ani2);
                }
                return convertView;
            }
            return getRealChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        }

        @Override // android.widget.ExpandableListAdapter
        public final int getChildrenCount(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            return info.animating ? info.firstChildPosition + 1 : getRealChildrenCount(groupPosition);
        }
    }

    private static class DummyView extends View {
        private Drawable divider;
        private int dividerHeight;
        private int dividerWidth;
        private List<View> views;

        public DummyView(Context context) {
            super(context);
            this.views = new ArrayList();
        }

        public void setDivider(Drawable divider, int dividerWidth, int dividerHeight) {
            this.divider = divider;
            this.dividerWidth = dividerWidth;
            this.dividerHeight = dividerHeight;
            divider.setBounds(0, 0, dividerWidth, dividerHeight);
        }

        public void addFakeView(View childView) {
            childView.layout(0, 0, getWidth(), getHeight());
            this.views.add(childView);
        }

        @Override // android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int len = this.views.size();
            for (int i = 0; i < len; i++) {
                View v = this.views.get(i);
                v.layout(left, top, right, bottom);
            }
        }

        public void clearViews() {
            this.views.clear();
        }

        @Override // android.view.View
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            this.divider.setBounds(0, 0, this.dividerWidth, this.dividerHeight);
            int len = this.views.size();
            for (int i = 0; i < len; i++) {
                View v = this.views.get(i);
                v.draw(canvas);
                canvas.translate(0.0f, v.getMeasuredHeight());
                this.divider.draw(canvas);
                canvas.translate(0.0f, this.dividerHeight);
            }
            canvas.restore();
        }
    }

    private static class ExpandAnimation extends Animation {
        private int baseHeight;
        private int delta;
        private GroupInfo groupInfo;
        private View view;

        private ExpandAnimation(View v, int startHeight, int endHeight, GroupInfo info) {
            this.baseHeight = startHeight;
            this.delta = endHeight - startHeight;
            this.view = v;
            this.groupInfo = info;
            this.view.getLayoutParams().height = startHeight;
            this.view.requestLayout();
        }

        /* synthetic */ ExpandAnimation(View view, int i, int i2, GroupInfo groupInfo, ExpandAnimation expandAnimation) {
            this(view, i, i2, groupInfo);
        }

        @Override // android.view.animation.Animation
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                int val = this.baseHeight + ((int) (this.delta * interpolatedTime));
                this.view.getLayoutParams().height = val;
                this.groupInfo.dummyHeight = val;
                this.view.requestLayout();
                return;
            }
            int val2 = this.baseHeight + this.delta;
            this.view.getLayoutParams().height = val2;
            this.groupInfo.dummyHeight = val2;
            this.view.requestLayout();
        }
    }
}
