package com.roblox.roblox_client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"Registered"})
/* loaded from: classes.dex */
public class RobloxServiceActivity extends Activity {
    private static String SERVICETAG = "robloxservice";
    protected static final String TAG = "RobloxServiceActivity";
    private Messenger mRobloxService = null;
    private Messenger mMessenger = new Messenger(new IncomingHandler(this, null));
    private List<Message> mPendingMessages = new ArrayList();
    boolean mGooglePlayServicesAvailable = false;
    private ServiceConnection mRobloxServiceConnection = new ServiceConnection() { // from class: com.roblox.roblox_client.RobloxServiceActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            RobloxServiceActivity.this.mRobloxService = new Messenger(service);
            try {
                Message msg = Message.obtain((Handler) null, 1);
                msg.replyTo = RobloxServiceActivity.this.mMessenger;
                RobloxServiceActivity.this.mRobloxService.send(msg);
                Log.i(RobloxServiceActivity.SERVICETAG, "RobloxServiceActivity.onServiceConnected success");
                RobloxServiceActivity.this.doNotifyService(1, 0);
                RobloxServiceActivity.this.onServiceConnected();
            } catch (RemoteException e) {
                Log.e(RobloxServiceActivity.SERVICETAG, "RobloxServiceActivity.onServiceConnected failed");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            RobloxServiceActivity.this.mRobloxService = null;
            RobloxServiceActivity.this.onServiceDisconnected();
        }
    };

    protected void onServiceNotification(int notification, int extra) {
    }

    public void onServiceConnected() throws RemoteException {
        for (Message msg : this.mPendingMessages) {
            try {
                this.mRobloxService.send(msg);
            } catch (RemoteException e) {
                Log.e(SERVICETAG, Utils.format("RobloxActivity.doNotifyService failed service dead", new Object[0]));
            }
        }
        this.mPendingMessages.clear();
    }

    public void onServiceDisconnected() {
        Log.w(SERVICETAG, "RobloxServiceActivity: onServiceDisconnected");
    }

    @SuppressLint({"HandlerLeak"})
    private class IncomingHandler extends Handler {
        private IncomingHandler() {
        }

        /* synthetic */ IncomingHandler(RobloxServiceActivity robloxServiceActivity, IncomingHandler incomingHandler) {
            this();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    RobloxServiceActivity.this.onServiceNotification(msg.arg1, msg.arg2);
                    Log.i(RobloxServiceActivity.SERVICETAG, String.format("onServiceNotification %d %d", Integer.valueOf(msg.arg1), Integer.valueOf(msg.arg2)));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    protected void doBindService() {
        Intent intent = new Intent(this, (Class<?>) RobloxService.class);
        boolean debuggerAttached = Debug.isDebuggerConnected();
        intent.putExtra("roblox_launcher_debugger_attached", debuggerAttached);
        boolean isOk = bindService(intent, this.mRobloxServiceConnection, 1);
        if (!isOk) {
            Log.e(SERVICETAG, Utils.format("RobloxActivity.doBindService failed", new Object[0]));
        }
    }

    protected void doUnbindService() throws RemoteException {
        if (this.mRobloxService != null) {
            try {
                Message msg = Message.obtain((Handler) null, 2);
                msg.replyTo = this.mMessenger;
                this.mRobloxService.send(msg);
                stopService(new Intent(this, (Class<?>) RobloxService.class));
                unbindService(this.mRobloxServiceConnection);
                return;
            } catch (RemoteException e) {
                Log.e(SERVICETAG, Utils.format("RobloxActivity.doUnbindService wtf service already dead.", new Object[0]));
                return;
            }
        }
        Log.e(SERVICETAG, Utils.format("RobloxActivity.doUnbindService failed service already unbound", new Object[0]));
    }

    protected void doNotifyService(int notification, int extra) throws RemoteException {
        Message msg = Message.obtain(null, 3, notification, extra);
        if (this.mRobloxService == null) {
            this.mPendingMessages.add(msg);
            return;
        }
        try {
            this.mRobloxService.send(msg);
        } catch (RemoteException e) {
            Log.e(SERVICETAG, Utils.format("RobloxActivity.doNotifyService failed service dead", new Object[0]));
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        int res = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (res != 0) {
            Log.w(TAG, "isGooglePlayServicesAvailable: No");
        } else {
            this.mGooglePlayServicesAvailable = true;
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        try {
            doUnbindService();
        } catch (RemoteException e) {
        }
        super.onDestroy();
    }

    protected boolean isGooglePlayServicesAvailable() {
        return this.mGooglePlayServicesAvailable;
    }
}
