package com.roblox.roblox_client;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

/* loaded from: classes.dex */
public class HttpAgent {
    private static final String TAG = "roblox.httpagent";

    public static void onCreate(Activity activity) {
        CookieSyncManager.createInstance(activity);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();
        WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(coreCookieManager);
    }

    public static void onPause(File cacheDir, String url) {
        CookieSyncManager.getInstance().stopSync();
        try {
            flushToDisk(cacheDir, url);
        } catch (Throwable t) {
        }
    }

    public static void onResume() {
        CookieSyncManager.getInstance().startSync();
    }

    public static void flushToDisk(File cacheDir, String url) throws Throwable {
        String cookies;
        if (url != null && !url.isEmpty()) {
            Uri uri = Uri.parse(url);
            if (uri.getHost() != null) {
                if ((uri.getHost().contains(".robloxlabs.com") || uri.getHost().contains(".roblox.com")) && (cookies = CookieManager.getInstance().getCookie(uri.getHost())) != null) {
                    Log.v(TAG, "Writing cookies for " + uri.getHost() + ": " + cookies);
                    File httpCacheDir = new File(cacheDir, RobloxSettings.httpCacheDir());
                    Utils.createDirectory(httpCacheDir);
                    File path = new File(httpCacheDir, RobloxSettings.httpRbxCookieFile());
                    Utils.writeToFile(path, cookies);
                }
            }
        }
    }

    public static HttpURLConnection urlToConnection(String url, String post) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection();
        httpConnection.setRequestProperty("User-Agent", RobloxSettings.userAgent());
        httpConnection.setConnectTimeout(60000);
        httpConnection.setReadTimeout(60000);
        if (post != null) {
            httpConnection.setDoOutput(true);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(post);
            out.close();
        }
        int status = httpConnection.getResponseCode();
        if (status != 200) {
            Log.i(TAG, "HTTP Status:" + status);
        }
        return httpConnection;
    }

    public static byte[] readUrlToBytes(String url, String post) {
        HttpURLConnection httpConnection = null;
        byte[] result = null;
        try {
            try {
                httpConnection = urlToConnection(url, post);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int length = httpConnection.getInputStream().read(buffer);
                    if (length == -1) {
                        break;
                    }
                    baos.write(buffer, 0, length);
                }
                result = baos.toByteArray();
            } catch (IOException e) {
                Log.i(TAG, "readUrlToBytes:" + e.toString());
                Log.i(TAG, "url:" + url);
                if (post != null) {
                    Log.i(TAG, "post:" + post);
                }
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
            }
            return result;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

    public static String readUrlToString(String url, String post) {
        byte[] b = readUrlToBytes(url, post);
        if (b == null) {
            return null;
        }
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, "readUrlToString:" + e.toString());
            return null;
        }
    }
}
