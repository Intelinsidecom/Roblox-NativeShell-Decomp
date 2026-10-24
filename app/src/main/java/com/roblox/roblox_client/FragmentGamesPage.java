package com.roblox.roblox_client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.roblox.roblox_client.ImageViewHttpLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FragmentGamesPage extends Fragment implements AbsListView.OnScrollListener, AbsListView.RecyclerListener {
    private static final int CORE_POOL_SIZE = 12;
    private static final int KEEP_ALIVE = 1;
    private static final int MAXIMUM_POOL_SIZE = 12;
    private static final int MAX_ASYNC_TASK = 256;
    private static final int NUM_FRAMES_PER_ROW = 4;
    private static final String TAG = "roblox_client";
    private static Handler mUIThreadHandler = new Handler(Looper.getMainLooper());
    private static final ThreadFactory sThreadFactory = new ThreadFactory() { // from class: com.roblox.roblox_client.FragmentGamesPage.1
        private final AtomicInteger mCount = new AtomicInteger(0);

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            return new Thread(r, "GamesPage " + this.mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(256);
    public static final Executor sThreadPoolExecutor = new ThreadPoolExecutor(12, 12, 1, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    private ActivityNativeSidebar mActivity = null;
    private TextView mPageStatus = null;
    private ListView mListView = null;
    private GameRowListAdapter mGameRowListAdapter = null;
    private SparseArray<GameData> mGameData = new SparseArray<>();
    FrameLayout[] mCurrentFrames = new FrameLayout[4];
    private Map<ImageView, ImageViewStateTracking> mImageViewStateTracker = null;
    private GamesPageAsyncTask mGamesPageAsyncTask = null;
    int mFirstVisibleRow = 0;
    int mLastVisibleRow = 0;
    int mScrollState = 0;

    private static class GameData {
        public String mCreatorName;
        public String mName;
        public String mPlaceId;
        public String mPlayerCount;

        private GameData() {
        }

        /* synthetic */ GameData(GameData gameData) {
            this();
        }
    }

    private static class ImageViewStateTracking implements ImageViewHttpLoader.TaskTracking {
        private AsyncTask mAsyncTaskInternal;
        private int mCurrentIndexInternal;
        public View mListViewConvertView;
        public boolean mListViewIsScrapInternal;
        public int mListViewPosition;

        private ImageViewStateTracking() {
            this.mCurrentIndexInternal = -1;
            this.mAsyncTaskInternal = null;
            this.mListViewIsScrapInternal = false;
            this.mListViewPosition = -1;
            this.mListViewConvertView = null;
        }

        /* synthetic */ ImageViewStateTracking(ImageViewStateTracking imageViewStateTracking) {
            this();
        }

        @Override // com.roblox.roblox_client.ImageViewHttpLoader.TaskTracking
        public int getCurrentIndex() {
            return this.mCurrentIndexInternal;
        }

        @Override // com.roblox.roblox_client.ImageViewHttpLoader.TaskTracking
        public void setDone(AsyncTask task) {
            if (task != this.mAsyncTaskInternal) {
                Log.w(FragmentGamesPage.TAG, "ImageViewStateTracking done task mismatch");
            }
            this.mAsyncTaskInternal = null;
            if (this.mCurrentIndexInternal == -1) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking done but no index");
            }
        }

        public void setStarting(AsyncTask task, int newIndex, boolean continuing) {
            if (this.mAsyncTaskInternal != null) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking starting task without finishing");
            }
            this.mAsyncTaskInternal = task;
            if (continuing && this.mCurrentIndexInternal != newIndex) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking continuing task not started");
            }
            this.mCurrentIndexInternal = newIndex;
        }

        public void setCancel() {
            if (this.mAsyncTaskInternal == null) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking scrapping non-existant task");
            }
            this.mAsyncTaskInternal = null;
            this.mCurrentIndexInternal = -1;
        }

        public void setSwitchTo(AsyncTask newTask, int newIndex) {
            if (this.mAsyncTaskInternal == null || this.mCurrentIndexInternal == -1) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking switching from non-existant task");
            }
            if (this.mCurrentIndexInternal == newIndex) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking switching to same index");
            }
            this.mAsyncTaskInternal = newTask;
            this.mCurrentIndexInternal = newIndex;
        }

        public void setSwitchToNoTask(int newIndex) {
            if (this.mAsyncTaskInternal != null) {
                setCancel();
            }
            this.mCurrentIndexInternal = newIndex;
        }

        public boolean isStarted() {
            boolean hasTask = this.mAsyncTaskInternal != null;
            if (hasTask && this.mCurrentIndexInternal == -1) {
                Log.e(FragmentGamesPage.TAG, "ImageViewStateTracking started but no index");
            }
            return hasTask;
        }

        public void setScrapped() {
            this.mAsyncTaskInternal = null;
            this.mCurrentIndexInternal = -1;
            this.mListViewIsScrapInternal = true;
        }

        public boolean getClearScrapped() {
            boolean wasScrapped = this.mListViewIsScrapInternal;
            this.mListViewIsScrapInternal = false;
            return wasScrapped;
        }
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_page, container, false);
        this.mPageStatus = (TextView) view.findViewById(R.id.games_page_status);
        this.mListView = (ListView) view.findViewById(R.id.games_page_listview);
        this.mGameRowListAdapter = new GameRowListAdapter(this, null);
        this.mListView.setAdapter((ListAdapter) this.mGameRowListAdapter);
        this.mListView.setOnScrollListener(this);
        this.mListView.setRecyclerListener(this);
        this.mListView.setOverScrollMode(2);
        this.mListView.setFriction(ViewConfiguration.getScrollFriction() * 2.0f);
        this.mListView.setChoiceMode(0);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.roblox.roblox_client.FragmentGamesPage.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                String err = Utils.format("OnItemClick: %d..%d", Integer.valueOf(position * 4), Integer.valueOf(((position * 4) + 4) - 1));
                Log.i(FragmentGamesPage.TAG, err);
            }
        });
        if (this.mGamesPageAsyncTask != null) {
            this.mGamesPageAsyncTask.cancel(false);
            this.mGamesPageAsyncTask = null;
        }
        this.mImageViewStateTracker = new HashMap();
        this.mFirstVisibleRow = 0;
        this.mLastVisibleRow = 5;
        updateGameDataFetching();
        return view;
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mListView = null;
        this.mGameRowListAdapter = null;
        this.mImageViewStateTracker = null;
    }

    @Override // android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (ActivityNativeSidebar) activity;
    }

    @Override // android.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    public boolean isGameDataFetched(int rowOffset) {
        for (int i = rowOffset; i < rowOffset + 4; i++) {
            if (this.mGameData.get(rowOffset + 1) == null) {
                return false;
            }
        }
        return true;
    }

    public void updateGameDataFetching() {
        GamesPageAsyncTask gamesPageAsyncTask = null;
        if (this.mScrollState != 2 && this.mGamesPageAsyncTask == null) {
            int startRange = (this.mFirstVisibleRow - 3) * 4;
            if (startRange < 0) {
                startRange = 0;
            }
            int lastRange = ((this.mLastVisibleRow + 3) * 4) + 3;
            while (startRange <= lastRange && this.mGameData.get(startRange) != null) {
                startRange++;
            }
            while (startRange <= lastRange && this.mGameData.get(lastRange) != null) {
                lastRange--;
            }
            if (startRange <= lastRange) {
                this.mGamesPageAsyncTask = new GamesPageAsyncTask(this, gamesPageAsyncTask);
                this.mGamesPageAsyncTask.mStart = startRange;
                this.mGamesPageAsyncTask.mLength = (lastRange - startRange) + 1;
                try {
                    this.mGamesPageAsyncTask.executeOnExecutor(sThreadPoolExecutor, null);
                } catch (RejectedExecutionException e) {
                    String err = Utils.format("Flooding GamesPageAsyncTask: %d..%d", Integer.valueOf(startRange), Integer.valueOf(lastRange));
                    Log.e(TAG, err);
                }
            }
        }
    }

    void openFindFramesScope(View row) {
        this.mCurrentFrames[0] = (FrameLayout) row.findViewById(R.id.fragment_games_page_item_1);
        this.mCurrentFrames[1] = (FrameLayout) row.findViewById(R.id.fragment_games_page_item_2);
        this.mCurrentFrames[2] = (FrameLayout) row.findViewById(R.id.fragment_games_page_item_3);
        this.mCurrentFrames[3] = (FrameLayout) row.findViewById(R.id.fragment_games_page_item_4);
    }

    void closeFindFramesScope() {
        for (int i = 0; i < 4; i++) {
            this.mCurrentFrames[i] = null;
        }
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.mScrollState = scrollState;
        updateGameDataFetching();
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mFirstVisibleRow = firstVisibleItem;
        this.mLastVisibleRow = (firstVisibleItem + visibleItemCount) - 1;
        updateGameDataFetching();
        mUIThreadHandler.post(new Runnable() { // from class: com.roblox.roblox_client.FragmentGamesPage.3
            @Override // java.lang.Runnable
            public void run() {
                FragmentGamesPage.this.mPageStatus.setText(Utils.format(" %d .. %d", Integer.valueOf((FragmentGamesPage.this.mFirstVisibleRow * 4) + 1), Integer.valueOf((FragmentGamesPage.this.mLastVisibleRow + 1) * 4)));
            }
        });
    }

    @Override // android.widget.AbsListView.RecyclerListener
    public void onMovedToScrapHeap(View view) {
        openFindFramesScope(view);
        for (int i = 0; i < 4; i++) {
            ImageView image = (ImageView) this.mCurrentFrames[i].findViewById(R.id.fragment_games_page_image);
            image.setImageDrawable(null);
            ImageViewStateTracking v = this.mImageViewStateTracker.get(image);
            v.setScrapped();
        }
        closeFindFramesScope();
    }

    private class GameRowListAdapter extends BaseAdapter {
        private GameRowListAdapter() {
        }

        /* synthetic */ GameRowListAdapter(FragmentGamesPage fragmentGamesPage, GameRowListAdapter gameRowListAdapter) {
            this();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return 5120;
        }

        @Override // android.widget.Adapter
        public Object getItem(int arg0) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            return getOrUpdateView(position, convertView, parent, false);
        }

        public View getOrUpdateView(int position, View convertView, ViewGroup parent, boolean isUpdating) {
            LayoutInflater inflater = null;
            View row = convertView;
            if (convertView == null) {
                inflater = LayoutInflater.from(parent.getContext());
                row = inflater.inflate(R.layout.fragment_games_page_row, parent, false);
            }
            try {
                FragmentGamesPage.this.openFindFramesScope(row);
                if (convertView == null) {
                    for (int i = 0; i < 4; i++) {
                        View view = inflater.inflate(R.layout.fragment_games_page_item, parent, false);
                        FragmentGamesPage.this.mCurrentFrames[i].addView(view);
                        ImageView imageView = (ImageView) FragmentGamesPage.this.mCurrentFrames[i].findViewById(R.id.fragment_games_page_image);
                        ImageViewStateTracking ce = new ImageViewStateTracking(null);
                        FragmentGamesPage.this.mImageViewStateTracker.put(imageView, ce);
                        if (i == 0) {
                            ce.mListViewPosition = position;
                            ce.mListViewConvertView = row;
                        }
                    }
                }
                int rowOffset = position * 4;
                boolean haveGameData = FragmentGamesPage.this.isGameDataFetched(rowOffset);
                if (!haveGameData) {
                    if (!isUpdating && convertView != null) {
                        blankFramesInScope(position, row);
                    }
                } else {
                    for (int i2 = 0; i2 < 4; i2++) {
                        FrameLayout frame = FragmentGamesPage.this.mCurrentFrames[i2];
                        ImageView imageView2 = (ImageView) frame.findViewById(R.id.fragment_games_page_image);
                        ImageViewStateTracking ce2 = (ImageViewStateTracking) FragmentGamesPage.this.mImageViewStateTracker.get(imageView2);
                        int index = rowOffset + i2;
                        if (!isUpdating || ce2.getCurrentIndex() != index) {
                            GameData game = (GameData) FragmentGamesPage.this.mGameData.get(index);
                            Bitmap bm = ImageViewHttpLoader.checkBitmapCache(game.mPlaceId);
                            if (bm != null) {
                                imageView2.setImageBitmap(bm);
                                ce2.setSwitchToNoTask(index);
                                ce2.getClearScrapped();
                            } else if (ce2.getCurrentIndex() != index) {
                                if (ce2.getClearScrapped()) {
                                    imageView2.setImageDrawable(FragmentGamesPage.this.getResources().getDrawable(R.drawable.grey));
                                }
                                GamesPageThumbnailAsyncTask task = new GamesPageThumbnailAsyncTask(FragmentGamesPage.this, null);
                                task.mImageView = imageView2;
                                task.mPlaceId = game.mPlaceId;
                                task.mTracking = ce2;
                                task.mIndex = index;
                                if (ce2.isStarted()) {
                                    ce2.setSwitchTo(task, index);
                                } else {
                                    ce2.setStarting(task, index, false);
                                }
                                try {
                                    task.executeOnExecutor(FragmentGamesPage.sThreadPoolExecutor, null);
                                } catch (RejectedExecutionException e) {
                                    String err = Utils.format("Flooding GamesPageThumbnailAsyncTask: %d : %s", Integer.valueOf(index), task.mPlaceId);
                                    Log.e(FragmentGamesPage.TAG, err);
                                }
                            }
                            if (i2 == 0) {
                                ce2.mListViewPosition = position;
                                ce2.mListViewConvertView = row;
                            }
                            TextView link = (TextView) frame.findViewById(R.id.fragment_games_page_link);
                            link.setText(game.mName);
                            TextView creator = (TextView) frame.findViewById(R.id.fragment_games_page_creator);
                            creator.setText(game.mCreatorName);
                            TextView playerNumber = (TextView) frame.findViewById(R.id.fragment_games_page_player_number);
                            playerNumber.setText(game.mPlayerCount);
                        }
                    }
                }
                return row;
            } finally {
                FragmentGamesPage.this.closeFindFramesScope();
            }
        }

        public void blankFramesInScope(int position, View row) {
            for (int i = 0; i < 4; i++) {
                FrameLayout frame = FragmentGamesPage.this.mCurrentFrames[i];
                ImageView imageView = (ImageView) FragmentGamesPage.this.mCurrentFrames[i].findViewById(R.id.fragment_games_page_image);
                ImageViewStateTracking ce = (ImageViewStateTracking) FragmentGamesPage.this.mImageViewStateTracker.get(imageView);
                if (ce.isStarted()) {
                    ce.setCancel();
                }
                if (i == 0) {
                    ce.mListViewPosition = position;
                    ce.mListViewConvertView = row;
                }
                imageView.setImageDrawable(FragmentGamesPage.this.getResources().getDrawable(R.drawable.grey));
                TextView link = (TextView) frame.findViewById(R.id.fragment_games_page_link);
                link.setText("");
                TextView creator = (TextView) frame.findViewById(R.id.fragment_games_page_creator);
                creator.setText("");
                TextView playerNumber = (TextView) frame.findViewById(R.id.fragment_games_page_player_number);
                playerNumber.setText("");
            }
        }
    }

    private class GamesPageThumbnailAsyncTask extends AsyncTask<Void, Void, Void> {
        public ImageView mImageView;
        public int mIndex;
        private JSONObject mJson;
        public String mPlaceId;
        private String mResponse;
        public ImageViewStateTracking mTracking;

        private GamesPageThumbnailAsyncTask() {
            this.mImageView = null;
            this.mPlaceId = null;
            this.mTracking = null;
            this.mIndex = -1;
            this.mResponse = null;
            this.mJson = null;
        }

        /* synthetic */ GamesPageThumbnailAsyncTask(FragmentGamesPage fragmentGamesPage, GamesPageThumbnailAsyncTask gamesPageThumbnailAsyncTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... placeId) {
            String request = Utils.format("http://www.roblox.com/asset-thumbnail/json?assetId=%s&width=396&height=216&format=jpeg", this.mPlaceId);
            this.mResponse = HttpAgent.readUrlToString(request, null);
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                } catch (JSONException e) {
                    Log.e(FragmentGamesPage.TAG, "Games page thumbnail JSON fail");
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result)  {
            super.onPostExecute(result);
            if (FragmentGamesPage.this.getActivity() != null) {
                if (this.mJson == null) {
                    Log.e(FragmentGamesPage.TAG, "Cannot connect to games page icon JSON.");
                    return;
                }
                if (this.mTracking.getCurrentIndex() == this.mIndex) {
                    this.mTracking.setDone(this);
                    try {
                        String url = this.mJson.getString("Url");
                        ImageViewHttpLoader task = new ImageViewHttpLoader(this.mImageView, this.mPlaceId, this.mTracking, this.mIndex);
                        this.mTracking.setStarting(task, this.mIndex, true);
                        task.executeOnExecutor(FragmentGamesPage.sThreadPoolExecutor, url);
                    } catch (RejectedExecutionException e) {
                        Log.e(FragmentGamesPage.TAG, "Flooding ImageViewHttpLoader");
                    } catch (JSONException e2) {
                        Log.e(FragmentGamesPage.TAG, "Games page icon incomplete JSON.");
                    }
                }
            }
        }
    }

    private class GamesPageAsyncTask extends AsyncTask<Void, Void, Void> {
        private JSONArray mJson;
        public int mLength;
        private GameData[] mNewGameData;
        private String mResponse;
        public int mStart;

        private GamesPageAsyncTask() {
            this.mStart = -1;
            this.mLength = -1;
            this.mResponse = null;
            this.mJson = null;
            this.mNewGameData = null;
        }

        /* synthetic */ GamesPageAsyncTask(FragmentGamesPage fragmentGamesPage, GamesPageAsyncTask gamesPageAsyncTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        @SuppressLint({"DefaultLocale"})
        public Void doInBackground(Void... arg0)  {
            String request = String.format("http://www.roblox.com/Games/List-Json?sortFilter=0&startRows=%d&maxRows=%d&genreFilter=1", Integer.valueOf(this.mStart), Integer.valueOf(this.mLength));
            this.mResponse = HttpAgent.readUrlToString(request, null);
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(Utils.format("{\"j\":%s}", this.mResponse)).getJSONArray("j");
                    this.mNewGameData = new GameData[this.mLength];
                    for (int i = 0; i < this.mLength; i++) {
                        JSONObject game = this.mJson.getJSONObject(i);
                        GameData[] gameDataArr = this.mNewGameData;
                        GameData gd = new GameData(null);
                        gameDataArr[i] = gd;
                        gd.mPlaceId = game.getString("PlaceID");
                        gd.mName = game.getString("Name");
                        gd.mCreatorName = game.getString("CreatorName");
                        gd.mPlayerCount = game.getString("PlayerCount");
                    }
                } catch (JSONException e) {
                    Log.e(FragmentGamesPage.TAG, "Games page JSON fail");
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (FragmentGamesPage.this.getActivity() != null) {
                if (this.mJson == null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(FragmentGamesPage.this.mActivity).create();
                    alertDialog.setTitle("Cannot connect to games page");
                    alertDialog.setIcon(R.drawable.crate);
                    alertDialog.show();
                    return;
                }
                for (int i = 0; i < this.mLength; i++) {
                    FragmentGamesPage.this.mGameData.put(this.mStart + i, this.mNewGameData[i]);
                }
                for (Map.Entry<ImageView, ImageViewStateTracking> i2 : FragmentGamesPage.this.mImageViewStateTracker.entrySet()) {
                    ImageViewStateTracking v = i2.getValue();
                    if (v.mListViewPosition != -1 && v.getCurrentIndex() == -1) {
                        FragmentGamesPage.this.mGameRowListAdapter.getOrUpdateView(v.mListViewPosition, v.mListViewConvertView, null, true);
                    }
                }
                FragmentGamesPage.this.mGamesPageAsyncTask = null;
                FragmentGamesPage.this.updateGameDataFetching();
            }
        }
    }
}

