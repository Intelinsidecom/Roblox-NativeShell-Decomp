package com.roblox.roblox_client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.fmod.FMODAudioDevice;

/* loaded from: classes.dex */
public class ActivityGlView extends RobloxServiceActivity implements SurfaceHolder.Callback {
    public static final int SURFACE_CREATED = 2;
    public static final int SURFACE_DESTROYED = 4;
    public static final int SURFACE_INVALID = -1;
    public static final int SURFACE_NOT_READY = 0;
    public static final int SURFACE_READY_NOT_CREATED = 1;
    public static final int SURFACE_READY_TO_DESTROY = 3;
    private static final String TAG = "ActivityGlView";
    private static RbxKeyboard mGlEditTextView = null;
    private static SurfaceView mSurfaceView = null;
    private static Handler mUIThreadHandler = null;
    private static long currentTextBox = 0;
    private static int mSurfaceState = -1;
    private Surface mSurface = null;
    private boolean mDifferentProcess = false;
    private final FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeCallMessagesFromMainThread();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativePassInput(Surface surface, int i, int i2, int i3, int i4, float f);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativePassText(long j, String str);

    private static native void nativeReleaseFocus(long j);

    private static native void nativeStartGame(Surface surface, String str, String str2, float f);

    private static native void nativeStopGame();

    @Override // com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "ActivityGlView onCreate()");
        Intent incomingIntent = getIntent();
        int launcherPid = incomingIntent.getIntExtra("roblox_launcher_pid", -1);
        boolean launcherDebuggerAttached = incomingIntent.getBooleanExtra("roblox_launcher_debugger_attached", false);
        int myPid = Process.myPid();
        this.mDifferentProcess = myPid != launcherPid;
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(launcherPid);
        objArr[1] = launcherDebuggerAttached ? "attached" : "none";
        String msg = Utils.format("Incoming Intent ActivityGlView Pid:%d Debuger:%s", objArr);
        Log.i(TAG, msg);
        if (launcherDebuggerAttached && this.mDifferentProcess) {
            Log.w(TAG, "ActivityGlView waiting for debugger");
            Debug.waitForDebugger();
        }
        try {
            System.loadLibrary("fmodex");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, e.getLocalizedMessage());
            System.loadLibrary("fmodexL");
        }
        System.loadLibrary("curl");
        System.loadLibrary("roblox");
        super.onCreate(savedInstanceState);
        RobloxApplication app = (RobloxApplication) getApplication();
        if (app.checkShowCriticalError()) {
            Log.e(TAG, "Trying to create GLView after critcal error.");
            return;
        }
        setContentView(R.layout.activity_glview);
        HttpAgent.onCreate(this);
        RobloxSettings.setCacheDirectory(getCacheDir().getPath());
        RobloxSettings.updateNativeSettings();
        mUIThreadHandler = new Handler(Looper.getMainLooper());
        mGlEditTextView = (RbxKeyboard) findViewById(R.id.gl_edit_text);
        initGlEditTextView();
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        initSurfaceView();
        mSurfaceState = 0;
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        this.mFMODAudioDevice.start();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        HttpAgent.onResume();
        if (mSurfaceState >= 3) {
            mUIThreadHandler.postDelayed(new Runnable() { // from class: com.roblox.roblox_client.ActivityGlView.1
                @Override // java.lang.Runnable
                public void run() {
                    ActivityGlView.this.onBackPressed();
                }
            }, 50L);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        HttpAgent.onPause(getCacheDir(), null);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        if (mSurfaceState < 2) {
            mSurfaceState = 4;
        } else if (mSurfaceState == 2) {
            mSurfaceState = 3;
        }
        this.mFMODAudioDevice.stop();
    }

    @Override // com.roblox.roblox_client.RobloxServiceActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        try {
            doNotifyService(2, 0);
        } catch (RemoteException e) {
        }
        if (this.mDifferentProcess) {
            Process.killProcess(Process.myPid());
        } else {
            super.onDestroy();
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Intent intent = new Intent(this, (Class<?>) ActivityWebView.class);
        intent.putExtra("roblox_back", true);
        startActivity(intent);
        finish();
    }

    private void initSurfaceView() {
        mSurfaceView.setFocusable(true);
        mSurfaceView.setFocusableInTouchMode(true);
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() { // from class: com.roblox.roblox_client.ActivityGlView.2
            float displayDensity;
            SparseArray<TouchInfo> mActivePointers = new SparseArray<>();

            {
                this.displayDensity = ActivityGlView.this.getResources().getDisplayMetrics().density;
            }

            /* renamed from: com.roblox.roblox_client.ActivityGlView$2$TouchInfo */
            class TouchInfo {
                private int eventType;
                int lastEventType;
                int lastX;
                int lastY;
                private int x;
                private int y;

                TouchInfo() {
                }

                public int getX() {
                    return this.x;
                }

                public void setX(int X) {
                    this.lastX = this.x;
                    this.x = X;
                }

                public int getY() {
                    return this.y;
                }

                public void setY(int Y) {
                    this.lastY = this.y;
                    this.y = Y;
                }

                public int getEventType() {
                    return this.eventType;
                }

                public void setEventType(int newEventType) {
                    this.lastEventType = this.eventType;
                    this.eventType = newEventType;
                }
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);
                int maskedAction = event.getActionMasked();
                switch (maskedAction) {
                    case 0:
                    case 5:
                        TouchInfo info = new TouchInfo();
                        info.setX((int) (event.getX(pointerIndex) / this.displayDensity));
                        info.setY((int) (event.getY(pointerIndex) / this.displayDensity));
                        info.setEventType(0);
                        this.mActivePointers.put(pointerId, info);
                        break;
                    case 1:
                    case 3:
                    case 6:
                        this.mActivePointers.get(pointerId).setEventType(2);
                        break;
                    case 2:
                        int size = event.getPointerCount();
                        for (int i = 0; i < size; i++) {
                            TouchInfo info2 = this.mActivePointers.get(event.getPointerId(i));
                            if (info2 != null) {
                                info2.setX((int) (event.getX(i) / this.displayDensity));
                                info2.setY((int) (event.getY(i) / this.displayDensity));
                                info2.setEventType(1);
                            }
                        }
                        break;
                }
                List<Integer> idsToRemove = new ArrayList<>();
                int size2 = this.mActivePointers.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    int touchId = this.mActivePointers.keyAt(i2);
                    TouchInfo point = this.mActivePointers.valueAt(i2);
                    if (point.getEventType() == 2) {
                        idsToRemove.add(Integer.valueOf(touchId));
                    } else {
                        if (point.getEventType() != 1 || point.lastEventType != 1 || point.getX() != point.lastX || point.getY() != point.lastY) {
                        }
                    }
                    if (ActivityGlView.mSurfaceState != 2) {
                        Log.w(ActivityGlView.TAG, "nativePassInput not ready");
                    } else {
                        ActivityGlView.nativePassInput(ActivityGlView.this.mSurface, touchId, point.getX(), point.getY(), point.getEventType(), this.displayDensity);
                    }
                }
                for (Integer key : idsToRemove) {
                    this.mActivePointers.remove(key.intValue());
                }
                return true;
            }
        });
    }

    private void initGlEditTextView() {
        mGlEditTextView.setVisibility(8);
        mGlEditTextView.setImeOptions(2);
        mGlEditTextView.setSingleLine(true);
        mGlEditTextView.setText("");
        mGlEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.ActivityGlView.3
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                String text = v.getText().toString();
                if (ActivityGlView.mSurfaceState != 2) {
                    Log.w(ActivityGlView.TAG, "nativePassText not ready");
                } else {
                    ActivityGlView.nativePassText(ActivityGlView.currentTextBox, text);
                }
                ActivityGlView.mGlEditTextView.setCurrentTextBox(0L);
                v.setVisibility(8);
                InputMethodManager imm = (InputMethodManager) ActivityGlView.mSurfaceView.getContext().getSystemService("input_method");
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        if (mSurfaceState == 0) {
            mSurfaceState = 1;
            try {
                doNotifyService(4, 0);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        RobloxApplication app = (RobloxApplication) getApplication();
        if (app.checkShowCriticalError()) {
            Log.e(TAG, "Trying to shut down GL surface after critcal error.");
            return;
        }
        if (mSurfaceState == 2 || mSurfaceState == 3) {
            Log.i(TAG, ">>> Calling nativeStopGame");
            nativeStopGame();
            Log.i(TAG, "<<< Returned from nativeStopGame");
            mSurfaceState = 4;
        }
    }

    @Override // com.roblox.roblox_client.RobloxServiceActivity
    protected void onServiceNotification(int notification, int extra) {
        if (notification == 3) {
            if (mSurfaceState != 1) {
                Log.w(TAG, "surfaceCreated->onServiceNotification unexpectedly: " + mSurfaceState);
                return;
            }
            mSurfaceState = 2;
            SurfaceHolder holder = mSurfaceView.getHolder();
            RobloxApplication app = (RobloxApplication) getApplication();
            if (app.checkShowCriticalError()) {
                Log.e(TAG, "Trying to create GL surface after critcal error.");
                return;
            }
            String placeId = String.valueOf(extra);
            Log.i(TAG, "surfaceCreated->onServiceNotification: Place ID: " + placeId);
            this.mSurface = holder.getSurface();
            XAPKManager.unpackAssetsAsync(this);
            String assetPath = XAPKManager.unpackAssets(this);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            nativeStartGame(this.mSurface, placeId, assetPath, metrics.density);
        }
    }

    public static void releaseFocus(long textBoxInFocus) {
        if (mSurfaceState != 2) {
            Log.w(TAG, "releaseFocus() called unexpectedly: " + mSurfaceState);
        } else {
            nativeReleaseFocus(textBoxInFocus);
        }
    }

    public static class showEditText implements Runnable {
        String param;

        public showEditText(String parameter) {
            this.param = parameter;
        }

        @Override // java.lang.Runnable
        public void run() {
            ActivityGlView.mGlEditTextView.setVisibility(0);
            CharSequence charText = this.param;
            ActivityGlView.mGlEditTextView.setText(charText);
            ActivityGlView.mGlEditTextView.requestFocus();
            InputMethodManager imm = (InputMethodManager) ActivityGlView.mSurfaceView.getContext().getSystemService("input_method");
            imm.showSoftInput(ActivityGlView.mGlEditTextView, 1);
        }
    }

    public static void showKeyboard(long textBox, String textFromTextBox) {
        currentTextBox = textBox;
        mGlEditTextView.setCurrentTextBox(textBox);
        Runnable showKeyBoardRunnable = new showEditText(textFromTextBox);
        mUIThreadHandler.post(showKeyBoardRunnable);
    }

    static void SendAppEvent(final boolean blockUntilSent) throws InterruptedException {
        final CountDownLatch appEventSentSignal = blockUntilSent ? new CountDownLatch(1) : null;
        mUIThreadHandler.post(new Runnable() { // from class: com.roblox.roblox_client.ActivityGlView.4
            @Override // java.lang.Runnable
            public void run() {
                ActivityGlView.nativeCallMessagesFromMainThread();
                if (blockUntilSent) {
                    appEventSentSignal.countDown();
                }
            }
        });
        if (blockUntilSent) {
            appEventSentSignal.await();
        }
    }

    static void PostAppEvent() {
        mUIThreadHandler.post(new Runnable() { // from class: com.roblox.roblox_client.ActivityGlView.5
            @Override // java.lang.Runnable
            public void run() {
                ActivityGlView.nativeCallMessagesFromMainThread();
            }
        });
    }
}
