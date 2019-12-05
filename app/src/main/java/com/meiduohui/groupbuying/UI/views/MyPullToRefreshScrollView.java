package com.meiduohui.groupbuying.UI.views;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class MyPullToRefreshScrollView extends PullToRefreshScrollView {

    private NorthernScrollViewListener scrollViewListener = null;

    public void setScrollViewListener(NorthernScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public MyPullToRefreshScrollView(Context context) {
        super(context);
    }

    public MyPullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public MyPullToRefreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public interface NorthernScrollViewListener {
        void onScrollChanged(PullToRefreshScrollView scrollView, int x, int y, int oldx, int oldy);
    }

}
