package com.roblox.roblox_client;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UpgradeCheckHelper {
    private static final String TAG = "UpgradeCheckHelper";

    public static void checkForUpdate(RobloxActivity activity) {
        new ForceUpgradeCheck(activity).execute(new Void[0]);
    }

    static class ForceUpgradeCheck extends AsyncTask<Void, Void, Void> {
        RobloxActivity mActivity;
        String mStatus;
        String mResponse = null;
        JSONObject mJson = null;
        AlertDialog mAlertDialog = null;
        String upgradeCheckUrl = RobloxSettings.upgradeCheckUrl();

        ForceUpgradeCheck(RobloxActivity activity) {
            this.mActivity = null;
            this.mActivity = activity;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... uri) {
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.upgradeCheckUrl(), null);
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                } catch (JSONException e) {
                }
            }
            Log.i(UpgradeCheckHelper.TAG, "JSON: " + this.mResponse);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result)  {
            super.onPostExecute(result);
            if (this.mJson == null) {
                if (!Utils.alertIfNetworkNotConnected(this.mActivity)) {
                    Utils.alertUnexpectedError(this.mActivity, "UpgradeCheckHelper failed");
                    return;
                }
                return;
            }
            try {
                JSONObject dataJsonObj = this.mJson.getJSONObject("data");
                if (dataJsonObj != null) {
                    String status = dataJsonObj.getString("UpgradeAction");
                    if (status.equals("Required")) {
                        new AlertDialog.Builder(this.mActivity).setTitle("ROBLOX Upgrade").setMessage(R.string.UpgradeBodyString).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.roblox.roblox_client.UpgradeCheckHelper.ForceUpgradeCheck.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                String appPackageName = ForceUpgradeCheck.this.mActivity.getPackageName();
                                try {
                                    ForceUpgradeCheck.this.mActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
                                } catch (ActivityNotFoundException e) {
                                    ForceUpgradeCheck.this.mActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        }).show();
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
