package com.roblox.roblox_client;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class RobloxService extends Service {
    public static final int MSG_NOTIFICATION = 3;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int NOTIFICATION_CONNECTED = 1;
    public static final int NOTIFICATION_GET_PLACEID = 4;
    public static final int NOTIFICATION_GLSHUTDOWN = 2;
    public static final int NOTIFICATION_SET_PLACEID = 3;
    private static String SERVICETAG = "robloxservice";
    private Messenger mMessenger = new Messenger(new IncomingHandler(this, null));
    private ArrayList<Messenger> mClients = new ArrayList<>();
    private boolean mWaitForDebugger = false;
    private boolean mAlreadyWaitedForDebugger = false;
    private int mCurrentPlaceId = -1;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        boolean launcherDebuggerAttached = intent.getBooleanExtra("roblox_launcher_debugger_attached", false);
        if (launcherDebuggerAttached) {
            this.mWaitForDebugger = true;
        }
        return this.mMessenger.getBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(SERVICETAG, "RobloxService onCreate");
        XAPKManager.unpackAssetsAsync(this);
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.w(SERVICETAG, "RobloxService onDestroy");
        super.onDestroy();
    }

    @SuppressLint({"HandlerLeak"})
    private class IncomingHandler extends Handler {
        private IncomingHandler() {
        }

        /* synthetic */ IncomingHandler(RobloxService robloxService, IncomingHandler incomingHandler) {
            this();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (RobloxService.this.mWaitForDebugger && !RobloxService.this.mAlreadyWaitedForDebugger) {
                Log.w(RobloxService.SERVICETAG, "RobloxService waiting for debugger");
                Debug.waitForDebugger();
                RobloxService.this.mAlreadyWaitedForDebugger = true;
            }
            switch (msg.what) {
                case 1:
                    RobloxService.this.mClients.add(msg.replyTo);
                    break;
                case 2:
                    RobloxService.this.mClients.remove(msg.replyTo);
                    break;
                case 3:
                    int notification = msg.arg1;
                    int extra = msg.arg2;
                    switch (notification) {
                        case 2:
                            RobloxService.this.onGlShutdown(msg);
                            break;
                        case 3:
                            RobloxService.this.mCurrentPlaceId = msg.arg2;
                            try {
                                RobloxService.this.doNotifyBroadcast(3, RobloxService.this.mCurrentPlaceId);
                            } catch (RemoteException e) {
                            }
                            Log.i(RobloxService.SERVICETAG, "Setting CurrentPlaceId " + RobloxService.this.mCurrentPlaceId);
                            break;
                        case 4:
                            Log.i(RobloxService.SERVICETAG, "Sending CurrentPlaceId " + msg.arg2);
                            try {
                                RobloxService.this.doNotifyBroadcast(3, RobloxService.this.mCurrentPlaceId);
                            } catch (RemoteException e) {
                            }
                            break;
                        default:
                            try {
                                RobloxService.this.doNotifyBroadcast(notification, extra);
                            } catch (RemoteException e) {
                            }
                            break;
                    }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doNotifyBroadcast(int notification, int extra) throws RemoteException {
        for (int i = this.mClients.size() - 1; i >= 0; i--) {
            try {
                this.mClients.get(i).send(Message.obtain(null, 3, notification, extra));
            } catch (RemoteException e) {
                Log.e(SERVICETAG, Utils.format("Notifying dead activity that did not unregister.", new Object[0]));
                this.mClients.remove(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGlShutdown(Message msg) {
        Log.i(SERVICETAG, "onGlShutdown");
        this.mCurrentPlaceId = -1;
    }
}
