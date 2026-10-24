package com.roblox.roblox_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ActivityNativeSidebar extends RobloxActivity {
    private static final String PASSWORD_KEY = "password";
    private static final String TAG = "roblox_client";
    private static final String USERNAME_KEY = "username";
    Activity mActivity;
    ViewFlipper mViewFlipper = null;
    SharedPreferences mKeyValues = null;
    EditText mUsernameEditText = null;
    EditText mPasswordEditText = null;
    String mUsername = null;
    String mPassword = null;
    AlertDialog mAlertDialog = null;
    DrawerLayout mDrawer = null;
    AnimatedExpandableListView mSidebar = null;
    ImageView mHamburger = null;
    ImageView mThumbnailUrlImageView = null;
    TextView mUsernameTextView = null;
    TextView mRobuxBalanceTextView = null;
    TextView mTicketsTextView = null;
    EditText mSearchEditText = null;
    TextView mMore = null;
    int mUserID = 0;
    String mThumbnailUrl = null;
    int mRobuxBalance = 0;
    int mTicketsBalance = 0;
    boolean mIsAnyBuildersClubMember = false;
    Map<Class, Fragment> mFragmentTable = null;

    @Override // com.roblox.roblox_client.RobloxActivity, com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_sidebar);
        this.mActivity = this;
        this.mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        HttpAgent.onCreate(this);
        this.mKeyValues = getPreferences(0);
        this.mUsernameEditText = (EditText) findViewById(R.id.username);
        this.mUsername = this.mKeyValues.getString(USERNAME_KEY, getResources().getString(R.string.UsernameWord));
        this.mUsernameEditText.setText(this.mUsername);
        this.mPasswordEditText = (EditText) findViewById(R.id.password);
        this.mPassword = this.mKeyValues.getString(PASSWORD_KEY, getResources().getString(R.string.PasswordWord));
        this.mPasswordEditText.setText(this.mPassword);
        this.mDrawer = (DrawerLayout) findViewById(R.id.sidebar_drawer);
        this.mSidebar = (AnimatedExpandableListView) findViewById(R.id.sidebar);
        try {
            try {
            this.mSidebar.setAdapter(new SidebarAdapter(this));
        } catch (Exception e) {
        }
        } catch (Exception e) {
        }
        this.mHamburger = (ImageView) findViewById(R.id.hamburger);
        this.mThumbnailUrlImageView = (ImageView) findViewById(R.id.thumbnail_url);
        this.mUsernameTextView = (TextView) findViewById(R.id.username_textview);
        this.mRobuxBalanceTextView = (TextView) findViewById(R.id.robux_balance);
        this.mTicketsTextView = (TextView) findViewById(R.id.tickets_balance);
        this.mSearchEditText = (EditText) findViewById(R.id.search);
        this.mMore = (TextView) findViewById(R.id.more);
        this.mFragmentTable = new HashMap();
        setPageFragment(FragmentGamesPage.class);
        this.mUsernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                ActivityNativeSidebar.this.onUsernameEditTextOnFocusChange(hasFocus);
            }
        });
        onUsernameEditTextOnFocusChange(false);
        this.mUsernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.2
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 5) {
                    return false;
                }
                ActivityNativeSidebar.this.mPasswordEditText.requestFocus();
                return true;
            }
        });
        this.mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                ActivityNativeSidebar.this.onPasswordEditTextFocusChange(hasFocus);
            }
        });
        onPasswordEditTextFocusChange(false);
        this.mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.4
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                ActivityNativeSidebar.this.onExecuteLogin(null);
                return true;
            }
        });
        this.mSidebar.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.5
            @Override // android.widget.ExpandableListView.OnGroupClickListener
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (ActivityNativeSidebar.this.mSidebar.isGroupExpanded(groupPosition)) {
                    ActivityNativeSidebar.this.mSidebar.collapseGroupWithAnimation(groupPosition);
                    return true;
                }
                ActivityNativeSidebar.this.mSidebar.expandGroupWithAnimation(groupPosition);
                return true;
            }
        });
        this.mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.6
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                ActivityNativeSidebar.this.mSearchEditText.setText("");
            }
        });
        this.mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.7
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                Log.i(ActivityNativeSidebar.TAG, "TODO: Search");
                return true;
            }
        });
        this.mHamburger.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!ActivityNativeSidebar.this.mDrawer.isDrawerOpen(3)) {
                    ActivityNativeSidebar.this.mDrawer.openDrawer(3);
                } else {
                    ActivityNativeSidebar.this.mDrawer.closeDrawer(3);
                }
            }
        });
        this.mMore.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.i(ActivityNativeSidebar.TAG, "onMainImageButton:" + view.getId());
                ActivityNativeSidebar.this.mAlertDialog = new AlertDialog.Builder(ActivityNativeSidebar.this.mActivity).create();
                ActivityNativeSidebar.this.mAlertDialog.setTitle("Opening Webpage.");
                ActivityNativeSidebar.this.mAlertDialog.setIcon(R.drawable.crate);
                ActivityNativeSidebar.this.mAlertDialog.setCanceledOnTouchOutside(false);
                ActivityNativeSidebar.this.mAlertDialog.setCancelable(false);
                ActivityNativeSidebar.this.mAlertDialog.show();
                Intent intent = new Intent(ActivityNativeSidebar.this, (Class<?>) ActivityWebView.class);
                ActivityNativeSidebar.this.startActivity(intent);
            }
        });
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        HttpAgent.onPause(getCacheDir(), null);
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        HttpAgent.onResume();
        if (this.mAlertDialog != null) {
            this.mAlertDialog.cancel();
            this.mAlertDialog = null;
        }
    }

    public void onPageSelected(String label) {
        if (label.equalsIgnoreCase("GAMES")) {
            setPageFragment(FragmentGamesPage.class);
        } else {
            FragmentWebView f = (FragmentWebView) setPageFragment(FragmentWebView.class);
            f.setText(label);
        }
        this.mDrawer.closeDrawer(3);
    }

    private Fragment setPageFragment(Class c) {
        Fragment f = this.mFragmentTable.get(c);
        if (f == null) {
            try {
                f = (Fragment) c.newInstance();
                this.mFragmentTable.put(c, f);
            } catch (Exception e) {
                Log.i(TAG, "setPageFragment failed");
            }
        }
        if (!f.isInLayout()) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.page_fragment, f);
            ft.addToBackStack(null);
            ft.commit();
            fm.executePendingTransactions();
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void propigateLoginKeyValues() {
        SharedPreferences.Editor editor = this.mKeyValues.edit();
        this.mUsername = this.mUsernameEditText.getText().toString();
        this.mPassword = this.mPasswordEditText.getText().toString();
        editor.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUsernameEditTextOnFocusChange(boolean hasFocus) {
        this.mUsername = this.mUsernameEditText.getText().toString();
        this.mUsernameEditText.setInputType(524289);
        if (hasFocus) {
            if (this.mUsername.equals(getResources().getString(R.string.UsernameWord))) {
                this.mUsernameEditText.setText("");
            }
        } else if (this.mUsername.equals("")) {
            this.mUsernameEditText.setText(getResources().getString(R.string.UsernameWord));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPasswordEditTextFocusChange(boolean hasFocus) {
        this.mPassword = this.mPasswordEditText.getText().toString();
        if (hasFocus) {
            if (this.mPassword.equals(getResources().getString(R.string.PasswordWord))) {
                this.mPasswordEditText.setText("");
            }
            this.mPasswordEditText.setInputType(524417);
        } else if (this.mPassword.equals("") || this.mPassword.equals(getResources().getString(R.string.PasswordWord))) {
            this.mPasswordEditText.setText(getResources().getString(R.string.PasswordWord));
            this.mPasswordEditText.setInputType(524433);
        } else {
            this.mPasswordEditText.setInputType(524417);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logout() {
        this.mPasswordEditText.setText("");
        onPasswordEditTextFocusChange(this.mPasswordEditText.hasFocus());
        propigateLoginKeyValues();
        this.mUserID = 0;
        this.mRobuxBalance = 0;
        this.mTicketsBalance = 0;
        this.mThumbnailUrl = null;
        this.mIsAnyBuildersClubMember = false;
    }

    public void onNop(View view) {
    }

    public void onPlayNow(View view) throws Resources.NotFoundException {
        propigateLoginKeyValues();
        Utils.alert(this, R.string.not_implemented);
    }

    public void onRememberPassword(View view) {
        propigateLoginKeyValues();
    }

    public void onExecuteLogin(View view) {
        propigateLoginKeyValues();
        this.mViewFlipper.setDisplayedChild(1);
    }

    public void onSignUp(View view) throws Resources.NotFoundException {
        propigateLoginKeyValues();
        Utils.alert(this, R.string.not_implemented);
    }

    class LoginAsyncTask extends AsyncTask<Void, Void, Void> {
        String mResponse = null;
        JSONObject mJson = null;

        LoginAsyncTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0)  {
            String encodedUsername = "";
            String encodedPassword = "";
            try {
                encodedUsername = URLEncoder.encode(ActivityNativeSidebar.this.mUsername, "UTF-8");
                encodedPassword = URLEncoder.encode(ActivityNativeSidebar.this.mPassword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.loginUrl(), Utils.format("username=%s&password=%s", encodedUsername, encodedPassword));
            Log.i(ActivityNativeSidebar.TAG, "Login: " + this.mResponse);
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                    return null;
                } catch (JSONException e2) {
                    Log.e(ActivityNativeSidebar.TAG, "Login cannot parse JSON.");
                    return null;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result)  {
            super.onPostExecute(result);
            if (this.mJson == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(ActivityNativeSidebar.this.mActivity).create();
                alertDialog.setTitle("Cannot connect");
                alertDialog.setIcon(R.drawable.crate);
                alertDialog.show();
                return;
            }
            boolean isOk = false;
            try {
                String status = this.mJson.getString("Status");
                if (status.equals("OK")) {
                    JSONObject userInfo = this.mJson.getJSONObject("UserInfo");
                    ActivityNativeSidebar.this.mUsername = userInfo.getString("UserName");
                    ActivityNativeSidebar.this.mUserID = userInfo.getInt("UserID");
                    ActivityNativeSidebar.this.mRobuxBalance = userInfo.getInt("RobuxBalance");
                    ActivityNativeSidebar.this.mTicketsBalance = userInfo.getInt("TicketsBalance");
                    ActivityNativeSidebar.this.mIsAnyBuildersClubMember = userInfo.getBoolean("IsAnyBuildersClubMember");
                    ActivityNativeSidebar.this.mUsernameEditText.setText(ActivityNativeSidebar.this.mUsername);
                    ActivityNativeSidebar.this.propigateLoginKeyValues();
                    String thumbnailUrl = userInfo.getString("ThumbnailUrl");
                    if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
                        new ImageViewHttpLoader((ImageView) ActivityNativeSidebar.this.findViewById(R.id.thumbnail_url), null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thumbnailUrl);
                    }
                    ActivityNativeSidebar.this.mUsernameTextView.setText(ActivityNativeSidebar.this.mUsername);
                    ActivityNativeSidebar.this.mRobuxBalanceTextView.setText(" " + ActivityNativeSidebar.this.mRobuxBalance);
                    ActivityNativeSidebar.this.mTicketsTextView.setText(" " + ActivityNativeSidebar.this.mTicketsBalance);
                    isOk = true;
                }
            } catch (JSONException e) {
                Log.i(ActivityNativeSidebar.TAG, "Login incomplete JSON.");
            }
            ActivityNativeSidebar.this.mAlertDialog.cancel();
            ActivityNativeSidebar.this.mAlertDialog = null;
            if (!isOk) {
                ActivityNativeSidebar.this.logout();
                AlertDialog alertDialog2 = new AlertDialog.Builder(ActivityNativeSidebar.this.mActivity).create();
                alertDialog2.setTitle("Login Failed");
                alertDialog2.setMessage(this.mResponse);
                alertDialog2.setIcon(R.drawable.crate);
                alertDialog2.setButton("OK", new DialogInterface.OnClickListener() { // from class: com.roblox.roblox_client.ActivityNativeSidebar.LoginAsyncTask.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog2.show();
                return;
            }
            ActivityNativeSidebar.this.mViewFlipper.setDisplayedChild(1);
        }
    }
}

