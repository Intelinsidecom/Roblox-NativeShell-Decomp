package com.roblox.roblox_client;

import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.roblox.roblox_client.AnimatedExpandableListView;
import com.roblox.roblox_client.SidebarItem;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SidebarAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private static final String TAG = "roblox.sidebar";
    private ActivityNativeSidebar mActivity;
    private LayoutInflater mInflater;
    private SparseArray<SidebarItem> mItems;

    public SidebarAdapter(ActivityNativeSidebar act) throws JSONException, Resources.NotFoundException {
        this.mActivity = null;
        this.mInflater = null;
        this.mItems = null;
        this.mActivity = act;
        this.mItems = new SparseArray<>();
        this.mInflater = act.getLayoutInflater();
        LoadData();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int groupPosition, int childPosition) {
        return this.mItems.get(groupPosition).mDetails.get(childPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int groupPosition, int childPosition) {
        return 0L;
    }

    @Override // com.roblox.roblox_client.AnimatedExpandableListView.AnimatedExpandableListAdapter
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.sidebar_details, (ViewGroup) null);
        }
        final SidebarItem.Detail item = (SidebarItem.Detail) getChild(groupPosition, childPosition);
        TextView label = (TextView) convertView.findViewById(R.id.sidebar_detail_label);
        label.setText(item.mLabel);
        convertView.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.SidebarAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SidebarAdapter.this.mActivity.onPageSelected(item.mLabel);
            }
        });
        return convertView;
    }

    @Override // com.roblox.roblox_client.AnimatedExpandableListView.AnimatedExpandableListAdapter
    public int getRealChildrenCount(int groupPosition) {
        return this.mItems.get(groupPosition).mDetails.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int groupPosition) {
        return this.mItems.get(groupPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.mItems.size();
    }

    @Override // android.widget.BaseExpandableListAdapter, android.widget.ExpandableListAdapter
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        SidebarItem item = this.mItems.get(groupPosition);
        if (item.mDetails.size() == 0) {
            this.mActivity.onPageSelected(item.mLabel);
        }
    }

    @Override // android.widget.BaseExpandableListAdapter, android.widget.ExpandableListAdapter
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        SidebarItem item = this.mItems.get(groupPosition);
        if (item.mDetails.size() == 0) {
            this.mActivity.onPageSelected(item.mLabel);
        }
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int groupPosition) {
        return 0L;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.sidebar_item, (ViewGroup) null);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.sidebar_item_image);
        TextView label = (TextView) convertView.findViewById(R.id.sidebar_item_label);
        ImageView indicator = (ImageView) convertView.findViewById(R.id.sidebar_item_indicator);
        SidebarItem item = this.mItems.get(groupPosition);
        image.setImageDrawable(item.mImage);
        label.setText(item.mLabel);
        if (item.mDetails.isEmpty()) {
            indicator.setImageResource(R.drawable.sb_indicator_none);
        } else if (isExpanded) {
            indicator.setImageResource(R.drawable.sb_indicator_open);
        } else {
            indicator.setImageResource(R.drawable.sb_indicator_closed);
        }
        return convertView;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public void LoadData() throws JSONException, Resources.NotFoundException {
        boolean isOk = true;
        try {
            JSONObject json = Utils.loadJson(this.mActivity, R.raw.sidebar_json);
            JSONArray items = json.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                this.mItems.append(i, new SidebarItem(this.mActivity, item));
            }
        } catch (IOException e) {
            isOk = false;
        } catch (JSONException e2) {
            Log.e(TAG, "JSONException");
            isOk = false;
        }
        if (!isOk) {
            RobloxApplication app = (RobloxApplication) this.mActivity.getApplication();
            app.setCriticalErrorOccured("Unable to load res/raw/sidebar_json");
        }
    }
}
