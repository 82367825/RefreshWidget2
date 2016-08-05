package com.zero.refreshwidget2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
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
public class RefreshListViewWidget extends ListView implements AbsListView.OnScrollListener, RefreshListWidgetInterface {

    private boolean mRefreshEnabled;
    private boolean mLoadMoreEnabled;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;

    private int mHeaderViewCount = 1;
    private int mFooterViewCount = 1;


    private int mCurrentState;
    private int mCurrentScrollState;
    
    private RefreshListener mRefreshListener;
    
    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

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
    
    
    private void setHeaderViewTopPadding(int topPadding) {
        if (mHeaderLayout != null) {
            mHeaderLayout.setPadding(mHeaderLayout.getLeft(), topPadding, 
                    mHeaderLayout.getRight(), mHeaderLayout.getBottom());
        }
    }
    
    private void setFooterViewBottomPadding(int bottomPadding) {
        if (mFooterLayout != null) {
            mFooterLayout.setPadding(mFooterLayout.getLeft(), mFooterLayout.getTop(), 
                    mFooterLayout.getRight(), bottomPadding);
        }
    }

    /**
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular, it will be called before any calls to
     * {@link Adapter#getView(int, View, ViewGroup)}.
     * @param absListView
     * @param scrollState
     * {@link android.widget.Adapter#getView(int, View, ViewGroup)}.
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.mCurrentScrollState = scrollState;
        if (mRefreshListener != null) {
            mRefreshListener.onScrollStateChange(scrollState);
        }
    }
    
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

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (mCurrentScrollState == SCROLL_STATE_FLING) {
            /* 快速滑动的时候,不进入任何状态 */
        } else if (mCurrentScrollState == SCROLL_STATE_IDLE) {
            if (mCurrentState == RefreshConstant.STATUS_NORMAL && isReachHeader()) {
               mCurrentState = RefreshConstant.STATUS_REFRESH; 
            } else if (mCurrentState == RefreshConstant.STATUS_NORMAL && isReachFooter()) {
                
            }
            /* 处于手指滑动状态 */
        } else if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
            /* 处于无手指无滑动状态 */}
    }

    private float mDownY;
    private float mMoveY;
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = ev.getRawY();

                if (mCurrentState == RefreshConstant.STATUS_NORMAL && 
                        isReachHeader() && mMoveY - mDownY > 0) {
                    /* 进入下拉状态 */
                    setHeaderViewTopPadding((int) (mMoveY - mDownY));
                } else {
                    /* 上拉状态 */
                    setFooterViewBottomPadding((int) (mDownY - mMoveY));
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
        if (mHeaderLayout != null && !enabled) {
            mHeaderLayout.setVisibility(GONE);
        } else if (mHeaderLayout != null && enabled) {
            mHeaderLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setLoadMoreEnabled(boolean enabled) {
        this.mLoadMoreEnabled = enabled;
        if (mFooterLayout != null && !enabled) {
            mFooterLayout.setVisibility(GONE);
        } else if (mFooterLayout != null && enabled) {
            mFooterLayout.setVisibility(VISIBLE);
        }
    }
}
