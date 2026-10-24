package com.roblox.roblox_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
public class ActivityWebView extends RobloxActivity {
    private static final String TAG = "webview";
    private ImageButton mGridImageButton = null;
    private TextView mUrlBar = null;
    private LinearLayout mRobuxLayout = null;
    private LinearLayout mTicketsLayout = null;
    private TextView mRobuxBalanceTextView = null;
    private TextView mTicketsTextView = null;
    private WebView mWebView = null;
    private String mWebViewUrl = null;
    private String mPlaceId = null;

    private class WebViewClientEmbedded extends WebViewClient {
        private WebViewClientEmbedded() {
        }

        /* synthetic */ WebViewClientEmbedded(ActivityWebView activityWebView, WebViewClientEmbedded webViewClientEmbedded) {
            this();
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String urlString) {
            Log.i(ActivityWebView.TAG, urlString);
            if (!Utils.alertIfNetworkNotConnected(ActivityWebView.this)) {
                RobloxApplication app = (RobloxApplication) ActivityWebView.this.getApplication();
                if (app.checkShowCriticalError()) {
                    Log.i(ActivityWebView.TAG, "Trying to use WebView after critcal error.");
                } else {
                    Utils.sendAnalytics(ActivityWebView.this, "WebView", urlString);
                    if (urlString.indexOf("/games/start?") <= -1) {
                        ActivityWebView.this.mUrlBar.setText(urlString);
                        view.loadUrl(urlString);
                        ActivityWebView.this.mWebViewUrl = urlString;
                        try {
                            ActivityWebView.this.writeWebViewUrl();
                        } catch (Throwable t) {
                        }
                    } else {
                        Uri uriObject = Uri.parse(urlString);
                        ActivityWebView.this.mPlaceId = uriObject.getQueryParameter("placeid");
                        Log.i(ActivityWebView.TAG, Utils.format("Signalling Service PlaceId:%s", ActivityWebView.this.mPlaceId));
                        try {
                            ActivityWebView.this.doNotifyService(3, Integer.parseInt(ActivityWebView.this.mPlaceId));
                            HttpAgent.flushToDisk(ActivityWebView.this.getCacheDir(), urlString);
                        } catch (Throwable t) {
                        }
                    }
                }
            }
            return true;
        }
    }

    @Override // com.roblox.roblox_client.RobloxActivity, com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        RobloxApplication app = (RobloxApplication) getApplication();
        if (app.checkShowCriticalError()) {
            Log.i(TAG, "Trying to create WebView after critcal error.");
            return;
        }
        this.mGridImageButton = (ImageButton) findViewById(R.id.webview_grid);
        this.mUrlBar = (TextView) findViewById(R.id.webview_urlbar);
        this.mRobuxLayout = (LinearLayout) findViewById(R.id.webview_tickets_layout);
        this.mTicketsLayout = (LinearLayout) findViewById(R.id.webview_robux_layout);
        this.mRobuxBalanceTextView = (TextView) findViewById(R.id.robux_balance);
        this.mTicketsTextView = (TextView) findViewById(R.id.tickets_balance);
        this.mGridImageButton.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.ActivityWebView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActivityWebView.this.goBackToShell();
            }
        });
        Utils.alertIfNetworkNotConnected(this);
        this.mWebView = (WebView) findViewById(R.id.web_view);
        this.mWebView.setWebViewClient(new WebViewClientEmbedded(this, null));
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setUserAgentString(RobloxSettings.userAgent());
        this.mWebView.getSettings().setCacheMode(-1);
        this.mUrlBar.setVisibility(RobloxSettings.isInternalBuild() ? 0 : 4);
        if (RobloxSettings.isInternalBuild()) {
            InternalBuildLayout layout = new InternalBuildLayout(this);
            addContentView(layout, new ViewGroup.LayoutParams(-1, -1));
        }
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity
    protected void onResume() {
        Utils.killBackgroundProcesses(this);
        Intent incomingIntent = getIntent();
        if (incomingIntent == null) {
            super.onResume();
            return;
        }
        String url = incomingIntent.getStringExtra("roblox_url");
        if (url == null) {
            this.mWebViewUrl = this.mKeyValues.getString("webview_url", "");
            url = this.mWebViewUrl;
        } else {
            this.mWebViewUrl = url;
            writeWebViewUrl();
        }
        Utils.sendAnalytics(this, "WebView", url);
        this.mUrlBar.setText(url);
        String displayedUrl = this.mWebView.getUrl();
        if (displayedUrl == null || !displayedUrl.equals(url)) {
            this.mWebView.loadUrl(url);
        }
        super.onResume();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            switch (keyCode) {
                case 4:
                    if (this.mWebView.canGoBack()) {
                        this.mWebView.goBack();
                    } else {
                        goBackToShell();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.roblox.roblox_client.RobloxServiceActivity
    protected void onServiceNotification(int notification, int extra) {
        if (notification == 3) {
            String extraStr = String.valueOf(extra);
            if (this.mPlaceId == null || !this.mPlaceId.equals(extraStr)) {
                Log.i(TAG, Utils.format("Mismatched place ID %s != %s ", this.mPlaceId, extraStr));
                return;
            }
            int pid = Process.myPid();
            boolean debuggerAttached = Debug.isDebuggerConnected();
            Object[] objArr = new Object[3];
            objArr[0] = this.mPlaceId;
            objArr[1] = Integer.valueOf(pid);
            objArr[2] = debuggerAttached ? "attached" : "none";
            Log.i(TAG, Utils.format("Launching PlaceId:%s Pid:%d Debuger:%s", objArr));
            Intent intent = new Intent(this, (Class<?>) ActivityGlView.class);
            intent.setFlags(335544320);
            intent.putExtra("roblox_placeid", this.mPlaceId);
            intent.putExtra("roblox_launcher_pid", pid);
            intent.putExtra("roblox_launcher_debugger_attached", debuggerAttached);
            intent.putExtra("roblox_back_activity", "ActivityWebView");
            startActivity(intent);
        }
    }

    @Override // com.roblox.roblox_client.RobloxActivity
    public void onUserInfoChanged() {
        this.mRobuxLayout.setVisibility(this.mLoggedIn ? 0 : 4);
        this.mTicketsLayout.setVisibility(this.mLoggedIn ? 0 : 4);
        this.mRobuxBalanceTextView.setText(" " + this.mRobuxBalance);
        this.mTicketsTextView.setText(" " + this.mTicketsBalance);
    }

    protected void writeWebViewUrl() {
        SharedPreferences.Editor editor = this.mKeyValues.edit();
        editor.putString("webview_url", this.mWebViewUrl);
        editor.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goBackToShell() {
        Intent intent = new Intent(this, (Class<?>) ActivityNativeMain.class);
        startActivity(intent);
        finish();
    }
}

