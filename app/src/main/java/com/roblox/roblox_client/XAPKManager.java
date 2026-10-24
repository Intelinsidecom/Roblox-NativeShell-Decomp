package com.roblox.roblox_client;

import android.content.Context;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes.dex */
public class XAPKManager {
    private static String TAG = "roblox.xapkmanager";
    private static String ASSET_CONTAINER_FILENAME = "main.1.com.roblox.roblox_client.obb";
    private static String FINGERPRINT_FILENAME = "fingerprint.txt";
    private static int BUFFER_SIZE = 8192;
    private static final Lock mLock = new ReentrantLock();
    private static String mCachedAssetPath = null;

    public static void unpackAssetsAsync(final Context context) {
        Runnable runnable = new Runnable() { // from class: com.roblox.roblox_client.XAPKManager.1
            @Override // java.lang.Runnable
            public void run() {
                XAPKManager.unpackAssets(context);
            }
        };
        new Thread(runnable).start();
    }

    public static String unpackAssets(Context context) {
        mLock.lock();
        try {
            if (mCachedAssetPath == null) {
                File baseAssetPath = context.getDir("assets", 0);
                String obbFingerprint = readOBBFingerprint(context);
                String assetsFingerprint = readAssetsFingerprint(baseAssetPath.toString());
                if (assetsFingerprint.isEmpty() || !obbFingerprint.equals(assetsFingerprint)) {
                    if (!obbFingerprint.isEmpty()) {
                        baseAssetPath.delete();
                        baseAssetPath.mkdirs();
                        unzipContainer(context, baseAssetPath.toString());
                        writeFingerprint(obbFingerprint, baseAssetPath.toString());
                    } else if (assetsFingerprint.isEmpty()) {
                        Log.e(TAG, "Unable to reach a valid OBB file. Assets are not available");
                    }
                } else {
                    Log.v(TAG, "Assets are up to date");
                }
                mCachedAssetPath = baseAssetPath + "/content";
            }
            mLock.unlock();
            return mCachedAssetPath;
        } catch (Throwable th) {
            mLock.unlock();
            return null;
        }
    }

    private static String readOBBFingerprint(Context context) throws Throwable {
        BufferedReader bufferReader = null;
        String fingerprint = null;
        InputStream inputStream = null;
        BufferedReader bufferReader2 = null;
        try {
            inputStream = context.getAssets().open(FINGERPRINT_FILENAME);
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException e) {
        }
        try {
            fingerprint = bufferReader.readLine();
            bufferReader.close();
            inputStream.close();
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e2) {
                }
            }
            if (inputStream != null) {
                inputStream.close();
            }
            bufferReader2 = null;
            inputStream = null;
        } catch (IOException e3) {
            bufferReader2 = bufferReader;
            Log.e(TAG, "Exception: " + e3.getMessage());
            if (bufferReader2 != null) {
                try {
                    bufferReader2.close();
                } catch (IOException e4) {
                    bufferReader2 = null;
                    inputStream = null;
                    return fingerprint;
                }
            }
            if (inputStream != null) {
                inputStream.close();
            }
            bufferReader2 = null;
            inputStream = null;
            return fingerprint;
        } catch (Throwable th2) {
            bufferReader2 = bufferReader;
            if (bufferReader2 != null) {
                try {
                    bufferReader2.close();
                } catch (IOException e5) {
                    throw th2;
                }
            }
            if (inputStream != null) {
                inputStream.close();
            }
            throw th2;
        }
        return fingerprint;
    }

    private static String readAssetsFingerprint(String baseAssetPath) throws Throwable {
        String fingerprint = "";
        File file = new File(baseAssetPath, FINGERPRINT_FILENAME);
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new FileReader(file));
            try {
                fingerprint = reader2.readLine();
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e2) {
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                    }
                }
                return fingerprint;
            } catch (Throwable th) {
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (IOException e5) {
        } catch (Throwable th2) {
        }
        return fingerprint;
    }

    private static void unzipContainer(Context context, String baseAssetPath) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            InputStream inputStream = new BufferedInputStream(context.getAssets().open(ASSET_CONTAINER_FILENAME));
            ZipInputStream zin = new ZipInputStream(inputStream);
            while (true) {
                try {
                    ZipEntry ze = zin.getNextEntry();
                    if (ze != null) {
                        String path = String.valueOf(baseAssetPath) + "/" + ze.getName();
                        File unzipFile = new File(path);
                        if (ze.isDirectory()) {
                            if (!unzipFile.isDirectory()) {
                                unzipFile.mkdirs();
                            }
                        } else {
                            File parentDir = unzipFile.getParentFile();
                            if (parentDir != null && !parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                            FileOutputStream out = new FileOutputStream(unzipFile, false);
                            BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                            while (true) {
                                try {
                                    int size = zin.read(buffer, 0, BUFFER_SIZE);
                                    if (size == -1) {
                                        break;
                                    } else {
                                        fout.write(buffer, 0, size);
                                    }
                                } catch (Throwable th) {
                                    fout.flush();
                                    fout.close();
                                    throw th;
                                }
                            }
                            zin.closeEntry();
                            fout.flush();
                            fout.close();
                        }
                    } else {
                        return;
                    }
                } finally {
                    zin.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    private static void writeFingerprint(String fingerprint, String baseAssetPath) throws Throwable {
        OutputStream out = null;
        OutputStream out2 = null;
        try {
            try {
                out = new FileOutputStream(new File(String.valueOf(baseAssetPath) + "/" + FINGERPRINT_FILENAME));
            } catch (Throwable th) {
            }
        } catch (Exception e) {
        }
        try {
            out.write(fingerprint.getBytes());
            out.close();
            if (out != null) {
                try {
                    out.close();
                    out2 = out;
                } catch (Exception e2) {
                    out2 = out;
                }
            } else {
                out2 = out;
            }
        } catch (Exception e3) {
            out2 = out;
            Log.e(TAG, "Exception " + e3.getMessage());
            if (out2 != null) {
                try {
                    out2.close();
                } catch (Exception e4) {
                }
            }
        } catch (Throwable th2) {
            out2 = out;
            if (out2 != null) {
                try {
                    out2.close();
                } catch (Exception e5) {
                }
            }
            throw th2;
        }
    }
}
