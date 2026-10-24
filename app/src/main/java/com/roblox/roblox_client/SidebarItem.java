package com.roblox.roblox_client;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SidebarItem {
    public List<Detail> mDetails;
    public Drawable mImage;
    public String mLabel;

    public class Detail {
        String mLabel;

        public Detail() {
        }
    }

    public SidebarItem(Activity act, JSONObject item) throws JSONException {
        this.mImage = null;
        this.mLabel = "";
        this.mDetails = null;
        int imageId = act.getResources().getIdentifier(item.getString("image"), "drawable", act.getPackageName());
        this.mImage = act.getResources().getDrawable(imageId);
        this.mLabel = item.getString(PlusShare.KEY_CALL_TO_ACTION_LABEL);
        this.mDetails = new ArrayList();
        JSONArray detailsJson = item.optJSONArray("details");
        if (detailsJson != null) {
            for (int i = 0; i < detailsJson.length(); i++) {
                JSONObject detailJson = detailsJson.getJSONObject(i);
                Detail detail = new Detail();
                detail.mLabel = detailJson.getString(PlusShare.KEY_CALL_TO_ACTION_LABEL);
                this.mDetails.add(detail);
            }
        }
    }
}
