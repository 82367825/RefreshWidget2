package com.zero.refreshwidget2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author linzewu
 * @date 16-7-26
 */
public class RefreshListViewWidget extends ListView implements AbsListView.OnScrollListener, 
        RefreshListWidgetInterface {
    
    private boolean mRefreshEnabled;
    private boolean mLoadMoreEnabled;
    
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    
    private int mHeaderViewCount = 1;
    private int mFooterViewCount = 1;

    /**
     * 当前状态
     */
    private int mCurrentState = RefreshConstant.STATUS_NORMAL;
    
    
    public RefreshListViewWidget(Context context) {
        super(context);
        init();
    }

    public RefreshListViewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListViewWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        setOnScrollListener(this);
        mHeaderLayout = new LinearLayout(getContext());
        mFooterLayout = new LinearLayout(getContext());
        
    }
    
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }
    
    public void addHeaderView(View headerView) {
        super.addHeaderView(headerView);
    }
    
    public void addFooterView(View footerView) {
        super.addFooterView(footerView);
    }
    
    private void setHeaderLayoutTopPadding(int topPadding) {
        mHeaderLayout.setPadding(
                mHeaderLayout.getLeft(), topPadding, mHeaderLayout.getRight(), mHeaderLayout.getBottom()
        );
    }
    
    private void setFooterLayoutBottomPadding(int bottomPadding) {
        mFooterLayout.setPadding(
                mFooterLayout.getLeft(), mFooterLayout.getTop(), mFooterLayout.getRight(), bottomPadding
        );
    }

    /**
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular, it will be called before any calls to
     * {@link Adapter#getView(int, View, ViewGroup)}.
     * @param absListView
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.mCurrentScrollState = scrollState;
    }
    
    private int mCurrentScrollState;
    
    /**
     * The view is not scrolling. Note navigating the list using the trackball counts as
     * being in the idle state since these transitions are not animated.
     */
    public static int SCROLL_STATE_IDLE = 0;

    /**
     * The user is scrolling using touch, and their finger is still on the screen
     */
    public static int SCROLL_STATE_TOUCH_SCROLL = 1;

    /**
     * The user had previously been scrolling using touch and had performed a fling. The
     * animation is now coasting to a stop
     */
    public static int SCROLL_STATE_FLING = 2;


    /**
     * Callback method to be invoked when the list or grid has been scrolled. This will be
     * called after the scroll has completed
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCurrentScrollState == SCROLL_STATE_IDLE) {
            
        } else if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if (isReachHeader()) {
                mCurrentState = RefreshConstant.STATUS_REFRESH;
            } else if (isReachFooter()) {
                mCurrentState = RefreshConstant.STATUS_LOAD_MORE;
            }
        } else if (mCurrentScrollState == SCROLL_STATE_FLING) {
            /* 高速滑动的时候，不下拉刷新也不上拉加载 */
            
        }
    }

    private float mDownY;
    private float mMoveY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = ev.getRawY();
                
                if (mMoveY - mDownY > 0 && (mCurrentState == RefreshConstant.STATUS_REFRESH || mCurrentState == 
                                RefreshConstant.STATUS_RELEASE_TO_REFRESH)) {
                    /* 正在下拉刷新 */
                    setHeaderLayoutTopPadding((int) (mMoveY - mDownY));
                } else if (mMoveY - mDownY <= 0 && (mCurrentState == RefreshConstant
                        .STATUS_LOAD_MORE || mCurrentState == RefreshConstant.STATUS_RELEASE_TO_LOAD_MORE)) {
                    /* 正在上拉加载 */
                    setFooterLayoutBottomPadding((int) (mDownY - mMoveY));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentState == RefreshConstant.STATUS_REFRESH) {
                    
                } else if (mCurrentState == RefreshConstant.STATUS_RELEASE_TO_REFRESH) {
                    
                } else if (mCurrentState == RefreshConstant.STATUS_LOAD_MORE) {
                    
                } else if (mCurrentState == RefreshConstant.STATUS_RELEASE_TO_LOAD_MORE) {
                    
                }
                break;
                
        }
        
        return super.onTouchEvent(ev);
    }
    
    private boolean isReachHeader() {
        return getFirstVisiblePosition() == 0;
    }
    
    private boolean isReachFooter() {
        return getLastVisiblePosition() == getCount() - 1;
    }
    

    @Override
    public void setRefreshEnabled(boolean enabled) {
        this.mRefreshEnabled = enabled;
    }

    @Override
    public void setLoadMoreEnabled(boolean enabled) {
        this.mLoadMoreEnabled = enabled;
    }
}
