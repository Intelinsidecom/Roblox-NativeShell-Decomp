package com.roblox.roblox_client;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.widget.TextView;

/* loaded from: classes.dex */
public class AutoResizeTextView extends TextView {
    private static final int NO_LINE_LIMIT = -1;
    private RectF mAvailableSpaceRect;
    private boolean mEnableSizeCache;
    private boolean mInitiallized;
    private int mMaxLines;
    private float mMaxTextSize;
    private float mMinTextSize;
    private TextPaint mPaint;
    private final SizeTester mSizeTester;
    private float mSpacingAdd;
    private float mSpacingMult;
    private SparseIntArray mTextCachedSizes;
    private RectF mTextRect;
    private int mWidthLimit;

    private interface SizeTester {
        int onTestSize(int i, RectF rectF);
    }

    public AutoResizeTextView(Context context) {
        super(context);
        this.mTextRect = new RectF();
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mMinTextSize = 20.0f;
        this.mEnableSizeCache = true;
        this.mSizeTester = new SizeTester() { // from class: com.roblox.roblox_client.AutoResizeTextView.1
            @Override // com.roblox.roblox_client.AutoResizeTextView.SizeTester
            public int onTestSize(int suggestedSize, RectF availableSPace) {
                AutoResizeTextView.this.mPaint.setTextSize(suggestedSize);
                String text = AutoResizeTextView.this.getText().toString();
                TransformationMethod trans = AutoResizeTextView.this.getTransformationMethod();
                boolean singleline = trans instanceof SingleLineTransformationMethod;
                if (singleline) {
                    AutoResizeTextView.this.mTextRect.bottom = AutoResizeTextView.this.mPaint.getFontSpacing();
                    AutoResizeTextView.this.mTextRect.right = AutoResizeTextView.this.mPaint.measureText(text);
                } else {
                    StaticLayout layout = new StaticLayout(text, AutoResizeTextView.this.mPaint, AutoResizeTextView.this.mWidthLimit, Layout.Alignment.ALIGN_NORMAL, AutoResizeTextView.this.mSpacingMult, AutoResizeTextView.this.mSpacingAdd, true);
                    AutoResizeTextView.this.mTextRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for (int i = 0; i < layout.getLineCount(); i++) {
                        if (maxWidth < layout.getLineWidth(i)) {
                            maxWidth = (int) layout.getLineWidth(i);
                        }
                    }
                    AutoResizeTextView.this.mTextRect.right = maxWidth;
                }
                AutoResizeTextView.this.mTextRect.offsetTo(0.0f, 0.0f);
                return availableSPace.contains(AutoResizeTextView.this.mTextRect) ? -1 : 1;
            }
        };
        initialize();
    }

    public AutoResizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTextRect = new RectF();
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mMinTextSize = 20.0f;
        this.mEnableSizeCache = true;
        this.mSizeTester = new SizeTester() { // from class: com.roblox.roblox_client.AutoResizeTextView.1
            @Override // com.roblox.roblox_client.AutoResizeTextView.SizeTester
            public int onTestSize(int suggestedSize, RectF availableSPace) {
                AutoResizeTextView.this.mPaint.setTextSize(suggestedSize);
                String text = AutoResizeTextView.this.getText().toString();
                TransformationMethod trans = AutoResizeTextView.this.getTransformationMethod();
                boolean singleline = trans instanceof SingleLineTransformationMethod;
                if (singleline) {
                    AutoResizeTextView.this.mTextRect.bottom = AutoResizeTextView.this.mPaint.getFontSpacing();
                    AutoResizeTextView.this.mTextRect.right = AutoResizeTextView.this.mPaint.measureText(text);
                } else {
                    StaticLayout layout = new StaticLayout(text, AutoResizeTextView.this.mPaint, AutoResizeTextView.this.mWidthLimit, Layout.Alignment.ALIGN_NORMAL, AutoResizeTextView.this.mSpacingMult, AutoResizeTextView.this.mSpacingAdd, true);
                    AutoResizeTextView.this.mTextRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for (int i = 0; i < layout.getLineCount(); i++) {
                        if (maxWidth < layout.getLineWidth(i)) {
                            maxWidth = (int) layout.getLineWidth(i);
                        }
                    }
                    AutoResizeTextView.this.mTextRect.right = maxWidth;
                }
                AutoResizeTextView.this.mTextRect.offsetTo(0.0f, 0.0f);
                return availableSPace.contains(AutoResizeTextView.this.mTextRect) ? -1 : 1;
            }
        };
        initialize();
    }

    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTextRect = new RectF();
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mMinTextSize = 20.0f;
        this.mEnableSizeCache = true;
        this.mSizeTester = new SizeTester() { // from class: com.roblox.roblox_client.AutoResizeTextView.1
            @Override // com.roblox.roblox_client.AutoResizeTextView.SizeTester
            public int onTestSize(int suggestedSize, RectF availableSPace) {
                AutoResizeTextView.this.mPaint.setTextSize(suggestedSize);
                String text = AutoResizeTextView.this.getText().toString();
                TransformationMethod trans = AutoResizeTextView.this.getTransformationMethod();
                boolean singleline = trans instanceof SingleLineTransformationMethod;
                if (singleline) {
                    AutoResizeTextView.this.mTextRect.bottom = AutoResizeTextView.this.mPaint.getFontSpacing();
                    AutoResizeTextView.this.mTextRect.right = AutoResizeTextView.this.mPaint.measureText(text);
                } else {
                    StaticLayout layout = new StaticLayout(text, AutoResizeTextView.this.mPaint, AutoResizeTextView.this.mWidthLimit, Layout.Alignment.ALIGN_NORMAL, AutoResizeTextView.this.mSpacingMult, AutoResizeTextView.this.mSpacingAdd, true);
                    AutoResizeTextView.this.mTextRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for (int i = 0; i < layout.getLineCount(); i++) {
                        if (maxWidth < layout.getLineWidth(i)) {
                            maxWidth = (int) layout.getLineWidth(i);
                        }
                    }
                    AutoResizeTextView.this.mTextRect.right = maxWidth;
                }
                AutoResizeTextView.this.mTextRect.offsetTo(0.0f, 0.0f);
                return availableSPace.contains(AutoResizeTextView.this.mTextRect) ? -1 : 1;
            }
        };
        initialize();
    }

    private void initialize() {
        this.mPaint = new TextPaint(getPaint());
        this.mMaxTextSize = getTextSize();
        this.mAvailableSpaceRect = new RectF();
        this.mTextCachedSizes = new SparseIntArray();
        if (this.mMaxLines == 0) {
            this.mMaxLines = -1;
        }
        this.mInitiallized = true;
    }

    @Override // android.widget.TextView
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        adjustTextSize(text.toString());
    }

    @Override // android.widget.TextView
    public void setTextSize(float size) {
        this.mMaxTextSize = size;
        this.mTextCachedSizes.clear();
        adjustTextSize(getText().toString());
    }

    @Override // android.widget.TextView
    public void setSingleLine() {
        super.setSingleLine();
        this.mMaxLines = 1;
        reAdjust();
    }

    @Override // android.widget.TextView
    public void setSingleLine(boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine) {
            this.mMaxLines = 1;
        } else {
            this.mMaxLines = -1;
        }
        reAdjust();
    }

    @Override // android.widget.TextView
    public void setLines(int lines) {
        super.setLines(lines);
        this.mMaxLines = lines;
        reAdjust();
    }

    @Override // android.widget.TextView
    public void setTextSize(int unit, float size) {
        Resources r;
        Context c = getContext();
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        this.mMaxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        this.mTextCachedSizes.clear();
        adjustTextSize(getText().toString());
    }

    @Override // android.widget.TextView
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        this.mSpacingMult = mult;
        this.mSpacingAdd = add;
    }

    public void setMinTextSize(float minTextSize) {
        this.mMinTextSize = minTextSize;
        reAdjust();
    }

    private void reAdjust() {
        adjustTextSize(getText().toString());
    }

    private void adjustTextSize(String string) {
        if (this.mInitiallized) {
            int startSize = (int) this.mMinTextSize;
            int heightLimit = (getMeasuredHeight() - getCompoundPaddingBottom()) - getCompoundPaddingTop();
            this.mWidthLimit = (getMeasuredWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
            this.mAvailableSpaceRect.right = this.mWidthLimit;
            this.mAvailableSpaceRect.bottom = heightLimit;
            super.setTextSize(0, efficientTextSizeSearch(startSize, (int) this.mMaxTextSize, this.mSizeTester, this.mAvailableSpaceRect));
        }
    }

    public void enableSizeCache(boolean enable) {
        this.mEnableSizeCache = enable;
        this.mTextCachedSizes.clear();
        adjustTextSize(getText().toString());
    }

    private int efficientTextSizeSearch(int start, int end, SizeTester sizeTester, RectF availableSpace) {
        if (!this.mEnableSizeCache) {
            return binarySearch(start, end, sizeTester, availableSpace);
        }
        String text = getText().toString();
        int key = text == null ? 0 : text.length();
        int size = this.mTextCachedSizes.get(key);
        if (size == 0) {
            int size2 = binarySearch(start, end, sizeTester, availableSpace);
            this.mTextCachedSizes.put(key, size2);
            return size2;
        }
        return size;
    }

    private static int binarySearch(int start, int end, SizeTester sizeTester, RectF availableSpace) {
        int lastBest = start;
        int lo = start;
        int hi = end - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midValCmp = sizeTester.onTestSize(mid, availableSpace);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else {
                if (midValCmp <= 0) {
                    return mid;
                }
                hi = mid - 1;
                lastBest = hi;
            }
        }
        return lastBest;
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        reAdjust();
    }

    @Override // android.view.View
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        this.mTextCachedSizes.clear();
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight) {
            reAdjust();
        }
    }
}
