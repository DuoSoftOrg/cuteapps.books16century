package ru.cuteapps.books16century.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ObservableWebView extends WebView {

    private OnScrollChangeListener onScrollChangeListener;
    private Context context;
    private GestureDetector gd;

    private OnSwipeListener onSwipeListener;
    private boolean flinged;

    private static final int SWIPE_MIN_DISTANCE = 330;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    public ObservableWebView(Context context) {
        super(context);
    }

    public ObservableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gd = new GestureDetector(context, sogl);
        Log.d("tag", "gesture конструктор вызван");
    }

    public ObservableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    OnSizeChangeListener sizeChangeListener;
    public void setOnSizeChangeListener (OnSizeChangeListener listener) {
        this.sizeChangeListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w,h,ow,oh);
        if (sizeChangeListener != null)
            sizeChangeListener.onSizeChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        if (flinged) {
            flinged = false;
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
        public boolean onDown(MotionEvent event) {
            return true;
        }

        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (Math.abs(event1.getY() - event1.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if(event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (onSwipeListener != null) onSwipeListener.onSwipe("left");
                flinged = true;
            } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (onSwipeListener != null) onSwipeListener.onSwipe("right");
                flinged = true;
            }
            return true;
        }
    };

    public OnSwipeListener getOnSwipeListener() {
        return onSwipeListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public OnScrollChangeListener getOnScrollChangeListener() {
        return onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a mView changes.
         *
         * @param v          The mView whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(WebView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    public interface OnSwipeListener {
        void onSwipe(String direction);
    }

    public interface OnSizeChangeListener {
        void onSizeChanged();
    }
}