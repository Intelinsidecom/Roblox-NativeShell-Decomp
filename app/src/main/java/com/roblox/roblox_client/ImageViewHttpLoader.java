package com.roblox.roblox_client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
class ImageViewHttpLoader extends AsyncTask<String, Void, Bitmap> {
    private static Map<String, Bitmap> mBitmapCache = new HashMap();
    public String mCacheKey;
    public ImageView mImageView;
    private TaskTracking mLateTaskCheck;
    public int mLateTaskCheckIndex;

    public interface TaskTracking {
        int getCurrentIndex();

        void setDone(AsyncTask asyncTask);
    }

    public ImageViewHttpLoader(ImageView v, String key) {
        this.mImageView = null;
        this.mCacheKey = null;
        this.mLateTaskCheck = null;
        this.mLateTaskCheckIndex = -1;
        this.mImageView = v;
        this.mCacheKey = key;
    }

    public ImageViewHttpLoader(ImageView v, String key, TaskTracking lateTaskCheck, int lateTaskCheckIndex) {
        this.mImageView = null;
        this.mCacheKey = null;
        this.mLateTaskCheck = null;
        this.mLateTaskCheckIndex = -1;
        this.mImageView = v;
        this.mCacheKey = key;
        this.mLateTaskCheck = lateTaskCheck;
        this.mLateTaskCheckIndex = lateTaskCheckIndex;
    }

    public static Bitmap checkBitmapCache(String key) {
        return mBitmapCache.get(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Bitmap doInBackground(String... urls) {
        try {
            InputStream s = new URL(urls[0]).openStream();
            Bitmap bm = BitmapFactory.decodeStream(s);
            return bm;
        } catch (Exception e) {
            Log.e("ImageViewHttpLoader", e.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Bitmap result) {
        if (this.mCacheKey != null) {
            mBitmapCache.put(this.mCacheKey, result);
        }
        if (this.mLateTaskCheck == null) {
            this.mImageView.setImageBitmap(result);
        } else if (this.mLateTaskCheck.getCurrentIndex() == this.mLateTaskCheckIndex) {
            this.mImageView.setImageBitmap(result);
            this.mLateTaskCheck.setDone(this);
        }
    }
}
