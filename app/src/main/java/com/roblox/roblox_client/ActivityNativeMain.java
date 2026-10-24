package com.roblox.roblox_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.roblox.roblox_client.FragmentSignUp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ActivityNativeMain extends RobloxActivity implements FragmentSignUp.SignUpCallbacks {
    private static final String TAG = "roblox.nativemain";
    Activity mActivity = null;
    ViewFlipper mViewFlipper = null;
    FragmentSignUp mFragmentSignUp = null;
    EditText mUsernameEditText = null;
    EditText mPasswordEditText = null;
    CheckBox mRememberPasswordCheckBox = null;
    AlertDialog mAlertDialog = null;
    View mFocusableView = null;
    ViewFlipper mSidebarViewFlipper = null;
    ImageView mThumbnailUrlImageView = null;
    TextView mUsernameTextView = null;
    TextView mRobuxBalanceTextView = null;
    TextView mTicketsTextView = null;
    EditText mSearchEditText = null;
    ImageView mLogoutImageView = null;
    Button mSidebarSignUp = null;
    Button mSidebarLogin = null;

    @Override // com.roblox.roblox_client.RobloxActivity, com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_main);
        this.mActivity = this;
        this.mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        this.mUsernameEditText = (EditText) findViewById(R.id.username);
        this.mUsernameEditText.setText(this.mUsername);
        this.mPasswordEditText = (EditText) findViewById(R.id.password);
        this.mPasswordEditText.setText(this.mPassword);
        this.mRememberPasswordCheckBox = (CheckBox) findViewById(R.id.remember_password);
        this.mRememberPasswordCheckBox.setChecked(this.mRememberPassword);
        this.mFocusableView = findViewById(R.id.focusable_view);
        this.mSidebarViewFlipper = (ViewFlipper) findViewById(R.id.sidebar_flipper);
        this.mThumbnailUrlImageView = (ImageView) findViewById(R.id.thumbnail_url);
        this.mUsernameTextView = (TextView) findViewById(R.id.username_textview);
        this.mRobuxBalanceTextView = (TextView) findViewById(R.id.robux_balance);
        this.mTicketsTextView = (TextView) findViewById(R.id.tickets_balance);
        this.mSearchEditText = (EditText) findViewById(R.id.search);
        this.mLogoutImageView = (ImageView) findViewById(R.id.logout);
        this.mSidebarSignUp = (Button) findViewById(R.id.sidebar_sign_up);
        this.mSidebarLogin = (Button) findViewById(R.id.sidebar_login);
        UpgradeCheckHelper.checkForUpdate((RobloxActivity) this.mActivity);
        this.mThumbnailUrlImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_thumbnail));
        this.mUsernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityNativeMain.1
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 5) {
                    return false;
                }
                ActivityNativeMain.this.mPasswordEditText.requestFocus();
                return true;
            }
        });
        this.mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityNativeMain.2
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                ActivityNativeMain.this.onExecuteLogin(null);
                return true;
            }
        });
        this.mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityNativeMain.3
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                String t = "";
                try {
                    t = URLEncoder.encode(v.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                Intent intent = new Intent(ActivityNativeMain.this, (Class<?>) ActivityWebView.class);
                intent.putExtra("roblox_url", RobloxSettings.searchGamesUrl(t));
                intent.putExtra("roblox_back_activity", "ActivityNativeMain");
                ActivityNativeMain.this.startActivity(intent);
                return true;
            }
        });
        this.mLogoutImageView.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.ActivityNativeMain.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActivityNativeMain.this.onLogout();
                ActivityNativeMain.this.mViewFlipper.setDisplayedChild(0);
                ActivityNativeMain.this.mSidebarViewFlipper.setDisplayedChild(0);
            }
        });
        this.mSidebarSignUp.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.ActivityNativeMain.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActivityNativeMain.this.mViewFlipper.setDisplayedChild(0);
                ActivityNativeMain.this.onSignUp(null);
            }
        });
        this.mSidebarLogin.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.ActivityNativeMain.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ActivityNativeMain.this.mViewFlipper.setDisplayedChild(0);
            }
        });
        onUserInfoChanged();
    }

    @Override // com.roblox.roblox_client.RobloxActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        Utils.killBackgroundProcesses(this);
    }

    @Override // com.roblox.roblox_client.RobloxActivity
    public void onUserInfoChanged() {
        if (this.mLoggedIn) {
            this.mUsernameEditText.setText(this.mUsername);
            if (this.mThumbnailUrl != null && !this.mThumbnailUrl.equals("")) {
                new ImageViewHttpLoader((ImageView) findViewById(R.id.thumbnail_url), null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mThumbnailUrl);
            }
            this.mUsernameTextView.setText(this.mUsername);
            this.mRobuxBalanceTextView.setText(" " + this.mRobuxBalance);
            this.mTicketsTextView.setText(" " + this.mTicketsBalance);
            this.mSidebarViewFlipper.setDisplayedChild(1);
            this.mViewFlipper.setDisplayedChild(1);
        } else {
            this.mSidebarViewFlipper.setDisplayedChild(0);
        }
        hideKeyboard();
    }

    public void hideKeyboard() {
        this.mFocusableView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService("input_method");
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
    }

    @Override // com.roblox.roblox_client.FragmentSignUp.SignUpCallbacks
    public void onFragmentSignUpCancel() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this.mFragmentSignUp);
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override // com.roblox.roblox_client.FragmentSignUp.SignUpCallbacks
    public void onFragmentSignUpOk(JSONObject response) {
        onFragmentSignUpCancel();
        onLogin(response);
    }

    private void propigateLoginKeyValues() {
        this.mUsername = this.mUsernameEditText.getText().toString();
        this.mPassword = this.mPasswordEditText.getText().toString();
        this.mRememberPassword = this.mRememberPasswordCheckBox.isChecked();
        writeLoginKeyValues();
    }

    @Override // com.roblox.roblox_client.RobloxActivity
    protected void onLogout() {
        super.onLogout();
        Utils.sendAnalytics(this, "ActivityNativeMain", "logout");
        this.mPasswordEditText.setText("");
    }

    public void onNop(View view) {
    }

    public void onPlayNow(View view) {
        if (this.mLoggedIn) {
            onLogout();
        }
        this.mSidebarViewFlipper.setDisplayedChild(0);
        this.mViewFlipper.setDisplayedChild(1);
        hideKeyboard();
        Utils.sendAnalytics(this, "ActivityNativeMain", "playNow");
    }

    public void onRememberPassword(View view) {
        propigateLoginKeyValues();
    }

    public void onExecuteLogin(View ignored) {
        propigateLoginKeyValues();
        if (this.mUsername.isEmpty()) {
            Utils.alertExclusively(this, R.string.NoSignupUsernameError);
            return;
        }
        if (this.mPassword.isEmpty()) {
            Utils.alertExclusively(this, R.string.NoSignupPasswordError);
            return;
        }
        new LoginAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        this.mAlertDialog = Utils.alert(this, R.string.LoggingIn);
        this.mAlertDialog.setCanceledOnTouchOutside(false);
        this.mAlertDialog.setCancelable(false);
        this.mAlertDialog.show();
    }

    public void onSignUp(View view) {
        propigateLoginKeyValues();
        FragmentSignUp f = new FragmentSignUp();
        f.setSignUpCallbacks(this);
        this.mFragmentSignUp = f;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_root, f);
        ft.addToBackStack(null);
        ft.commit();
        fm.executePendingTransactions();
        Utils.sendAnalytics(this, "ActivityNativeMain", "signup");
    }

    public void onAbout(View view) {
        FragmentAbout f = new FragmentAbout();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_root, f);
        ft.addToBackStack(null);
        ft.commit();
        Utils.sendAnalytics(this, "ActivityNativeMain", "about");
    }

    private String getUrl(View view) {
        switch (view.getId()) {
            case R.id.games /* 2131230750 */:
                return RobloxSettings.gamesUrl();
            case R.id.native_main_resize_text_1 /* 2131230751 */:
            case R.id.native_main_resize_text_2 /* 2131230753 */:
            case R.id.native_main_resize_text_3 /* 2131230755 */:
            case R.id.native_main_resize_text_4 /* 2131230757 */:
            case R.id.native_main_resize_text_5 /* 2131230759 */:
            default:
                return null;
            case R.id.builders_club /* 2131230752 */:
                return RobloxSettings.buildersClubUrl();
            case R.id.catalog /* 2131230754 */:
                return RobloxSettings.catalogUrl();
            case R.id.profile /* 2131230756 */:
                return RobloxSettings.profileUrl();
            case R.id.inventory /* 2131230758 */:
                return RobloxSettings.inventoryUrl();
            case R.id.messages /* 2131230760 */:
                return RobloxSettings.messagesUrl();
        }
    }

    public void onMainImageButton(View view) {
        String url = getUrl(view);
        if (!this.mLoggedIn && view.getId() != R.id.games) {
            Utils.sendAnalytics(this, "MainImageButtonNotLoggedIn", url);
            Utils.alertExclusively(this, R.string.YouMustLogin);
            return;
        }
        Utils.sendAnalytics(this, "ActivityNativeMain", url);
        Intent intent = new Intent(this, (Class<?>) ActivityWebView.class);
        intent.putExtra("roblox_url", url);
        intent.putExtra("roblox_back_activity", "ActivityNativeMain");
        startActivity(intent);
    }

    class LoginAsyncTask extends AsyncTask<Void, Void, Void> {
        String mResponse = null;
        JSONObject mJson = null;

        LoginAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0) {
            String encodedUsername = "";
            String encodedPassword = "";
            try {
                encodedUsername = URLEncoder.encode(ActivityNativeMain.this.mUsername, "UTF-8");
                encodedPassword = URLEncoder.encode(ActivityNativeMain.this.mPassword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.loginUrl(), Utils.format("username=%s&password=%s", encodedUsername, encodedPassword));
            Log.i(ActivityNativeMain.TAG, "Login: " + this.mResponse);
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                    return null;
                } catch (JSONException e2) {
                    return null;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (this.mResponse == null) {
                if (!Utils.alertIfNetworkNotConnected(ActivityNativeMain.this)) {
                    Utils.alertUnexpectedError(ActivityNativeMain.this, "Login cannot get response");
                }
            } else if (this.mJson == null) {
                Utils.alertUnexpectedError(ActivityNativeMain.this, "Login cannot parse JSON");
            } else {
                try {
                    String status = this.mJson.getString("Status");
                    if (status.equals("OK")) {
                        if (ActivityNativeMain.this.onLogin(this.mJson)) {
                            Utils.sendAnalytics(ActivityNativeMain.this, "LoginAsyncTask", "OK");
                        }
                    } else if (status.equals("InvalidUsername") || status.equals("InvalidPassword") || status.equals("MissingRequiredField")) {
                        Utils.alertExclusively(ActivityNativeMain.this, R.string.InvalidUsernameOrPw);
                    } else if (status.equals("SuccessfulLoginFloodcheck") || status.equals("FailedLoginFloodcheck") || status.equals("FailedLoginPerUserFloodcheck")) {
                        Utils.alertExclusively(ActivityNativeMain.this, R.string.TooManyAttempts);
                    } else if (status.equals("AccountNotApproved")) {
                        Utils.alertExclusively(ActivityNativeMain.this, R.string.NeedsExternalLogin);
                    } else {
                        Utils.alertUnexpectedError(ActivityNativeMain.this, status);
                    }
                } catch (JSONException e) {
                    Utils.alertUnexpectedError(ActivityNativeMain.this, "Login incomplete JSON");
                }
            }
            ActivityNativeMain.this.mAlertDialog.cancel();
            ActivityNativeMain.this.mAlertDialog = null;
        }
    }
}
