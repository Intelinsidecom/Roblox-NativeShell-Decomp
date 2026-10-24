package com.roblox.roblox_client;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Utils {
    private static final String TAG = "roblox.utils";
    static AlertDialog mExclusiveDialog = null;

    /* JADX WARN: Removed duplicated region for block: B:19:0x0026  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0016 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getDeviceTotalMegabytes() throws Throwable {
        RandomAccessFile reader = null;
        RandomAccessFile reader2 = null;
        String s = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
        } catch (IOException e) {
        } catch (Throwable th) {
            th = th;
        }
        try {
            s = reader.readLine();
            try {
                reader.close();
            } catch (IOException e2) {
            }
        } catch (IOException e3) {
            reader2 = reader;
            try {
                reader2.close();
            } catch (IOException e4) {
            }
            if (s != null) {
            }
        } catch (Throwable th2) {
            reader2 = reader;
            try {
                reader2.close();
            } catch (IOException e5) {
            }
            throw th2;
        }
        if (s != null) {
            return 0;
        }
        String[] tokens = s.split("\\s+");
        if (tokens.length < 2) {
            return 0;
        }
        long mb = 0;
        try {
            long kb = Long.parseLong(tokens[1]);
            mb = kb / 1024;
        } catch (NumberFormatException e6) {
        }
        return (int) mb;
    }

    public static Point getScreenSize(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getScreenDpi(Context c) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        Point dpi = new Point();
        dpi.x = (int) metrics.xdpi;
        dpi.y = (int) metrics.ydpi;
        return dpi;
    }

    public static String getDeviceName() {
        String name;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            name = model;
        } else {
            name = String.valueOf(manufacturer) + " " + model;
        }
        char capital = Character.toUpperCase(name.charAt(0));
        return String.valueOf(capital) + name.substring(1, name.length());
    }

    public static String getWebkitVersion(Context c) {
        String userAgent = new WebView(c).getSettings().getUserAgentString();
        String[] tokens = userAgent.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].startsWith("AppleWebKit")) {
                return tokens[i];
            }
        }
        return "AppleWebKit/Unknown";
    }

    public static void createDirectory(File path) {
        path.mkdirs();
    }

    public static void writeToFile(File path, String data) throws Throwable {
        BufferedWriter writer = null;
        BufferedWriter writer2 = null;
        try {
            try {
                writer = new BufferedWriter(new FileWriter(path));
            } catch (IOException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            writer.write(data);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                }
            }
        } catch (IOException e3) {
            writer2 = writer;
            Log.e("Exception", "File write failed: " + e3.toString());
            if (writer2 != null) {
                try {
                    writer2.close();
                } catch (IOException e4) {
                }
            }
        } catch (Throwable th2) {
            writer2 = writer;
            if (writer2 != null) {
                try {
                    writer2.close();
                } catch (IOException e5) {
                }
            }
            throw th2;
        }
    }

    static JSONObject loadJson(Context context, int res) throws Resources.NotFoundException, IOException {
        InputStream is = context.getResources().openRawResource(res);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while (true) {
                    int n = reader.read(buffer);
                    if (n == -1) {
                        break;
                    }
                    writer.write(buffer, 0, n);
                }
                String jsonString = writer.toString();
                JSONObject json = new JSONObject(jsonString);
                return json;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            Log.e(TAG, "IOException loading JSON.");
            throw new IOException("IOException loading JSON resource # " + res);
        } catch (JSONException e3) {
            Log.e(TAG, "Cannot parse JSON.");
            throw new IOException("Cannot parse JSON resource # " + res);
        }
    }

    public static void killBackgroundProcesses(Context c) {
        String packageName = c.getPackageName();
        ActivityManager activityManager = (ActivityManager) c.getSystemService("activity");
        activityManager.killBackgroundProcesses(packageName);
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService("connectivity");
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public static AlertDialog alert(Context c, int stringResource) throws Resources.NotFoundException {
        String s = c.getResources().getString(stringResource);
        return alert(c, s);
    }

    public static AlertDialog alert(Context c, String s) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextAppearance(c, android.R.attr.textAppearanceLarge);
        t.setTextSize(20.0f);
        t.setEllipsize(null);
        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setView(t, 150, 100, 150, 100);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog alertFormatted(Context c, int stringResource, Object... args) throws Resources.NotFoundException {
        String format = c.getResources().getString(stringResource);
        String s = String.format(Locale.ROOT, format, args);
        return alert(c, s);
    }

    public static synchronized AlertDialog alertExclusively(Context c, int stringResource) {
        AlertDialog alertDialog;
        if (mExclusiveDialog == null || !mExclusiveDialog.isShowing()) {
            mExclusiveDialog = alert(c, stringResource);
            alertDialog = mExclusiveDialog;
        } else {
            alertDialog = null;
        }
        return alertDialog;
    }

    public static synchronized AlertDialog alertExclusively(Context c, String s) {
        AlertDialog alertDialog;
        if (mExclusiveDialog == null || !mExclusiveDialog.isShowing()) {
            mExclusiveDialog = alert(c, s);
            alertDialog = mExclusiveDialog;
        } else {
            alertDialog = null;
        }
        return alertDialog;
    }

    public static synchronized AlertDialog alertExclusivelyFormatted(Context c, int stringResource, Object... args) {
        AlertDialog alertDialog;
        if (mExclusiveDialog == null || !mExclusiveDialog.isShowing()) {
            mExclusiveDialog = alertFormatted(c, stringResource, args);
            alertDialog = mExclusiveDialog;
        } else {
            alertDialog = null;
        }
        return alertDialog;
    }

    public static synchronized AlertDialog alertUnexpectedError(RobloxServiceActivity a, String err) {
        sendAnalytics(a, "UnexpectedError", err);
        return alertExclusivelyFormatted(a, R.string.UnexpectedError, err);
    }

    public static boolean alertIfNetworkNotConnected(Context c) {
        boolean isNotConnected = !isNetworkConnected(c);
        if (isNotConnected) {
            alertExclusively(c, R.string.ConnectionError);
        }
        return isNotConnected;
    }

    public static void sendAnalytics(RobloxServiceActivity a, String category, String action) {
        if (a.isGooglePlayServicesAvailable()) {
            Tracker t = ((RobloxApplication) a.getApplication()).getAndroidTracker();
            t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).build());
        }
    }
}
