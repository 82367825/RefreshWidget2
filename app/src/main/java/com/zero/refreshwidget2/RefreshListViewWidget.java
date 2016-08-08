package com.zero.refreshwidget2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.test.FlakyTest;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * 下拉刷新
 * @author linzewu
 * @date 16-7-26
 */
public class RefreshListViewWidget extends ListView implements OnScrollListener, RefreshListWidgetInterface {

    private static final String TAG = "RefreshListViewWidget";
    
    private boolean mRefreshEnabled;
    private boolean mLoadMoreEnabled;

    private RefreshHeader mHeaderLayout;
    private RefreshFooter mFooterLayout;
    
    private int mHeaderLayoutHeight;
    private int mFooterLayoutHeight;

    /**
     * 下拉距离比例 （0-1）
     * 值越小，下拉距离越大
     */
    private static final float PULL_SCALE = 0.65f;

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
        mCurrentState = RefreshConstant.STATUS_NORMAL;
        initHeaderLayout();
        initFooterLayout();
    }
    
    private void initHeaderLayout() {
        this.mHeaderLayout = new RefreshHeader(getContext());
        RefreshConstant.measureView(mHeaderLayout);
        this.mHeaderLayoutHeight = mHeaderLayout.getMeasuredHeight();
        this.addHeaderView(mHeaderLayout);
        setHeaderViewTopPadding(-mHeaderLayoutHeight);
        Log.d(TAG, "HeaderLayout Height : " + mHeaderLayoutHeight);
    }
    
    private void initFooterLayout() {
        this.mFooterLayout = new RefreshFooter(getContext());
        RefreshConstant.measureView(mFooterLayout);
        this.mFooterLayoutHeight = mFooterLayout.getMeasuredHeight();
        this.addFooterView(mFooterLayout);
        setFooterViewBottomPadding(-mFooterLayoutHeight);
        Log.d(TAG, "FooterLayout Height : " + mFooterLayoutHeight);
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
    
    public void refreshComplete() {
        mCurrentState = RefreshConstant.STATUS_NORMAL;
        taskCancelRefreshMainThread();
        mHeaderLayout.onCancel();
    } 
    
    public void loadMoreComplete() {
        mCurrentState = RefreshConstant.STATUS_NORMAL;
        taskCancelLoadMoreMainThread();
        mHeaderLayout.onCancel();
    }
    
    private void setHeaderViewTopPadding(int topPadding) {
        if (mHeaderLayout != null) {
            mHeaderLayout.setPadding(mHeaderLayout.getPaddingLeft(), topPadding, 
                    mHeaderLayout.getPaddingRight(), mHeaderLayout.getPaddingBottom());
        }
    }
    
    private void setFooterViewBottomPadding(int bottomPadding) {
        if (mFooterLayout != null) {
            mFooterLayout.setPadding(mFooterLayout.getPaddingLeft(), mFooterLayout.getPaddingTop(), 
                    mFooterLayout.getPaddingRight(), bottomPadding);
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
        if (scrollState == SCROLL_STATE_FLING) {
            
        }
        if (mRefreshListener != null) {
            mRefreshListener.onScrollStateChange(mCurrentScrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        
    }

    private float mDownY;
    private float mMoveY;
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsAnimRunning) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = ev.getRawY();
                
                if (mCurrentState == RefreshConstant.STATUS_NORMAL && 
                        isReachHeader() && mMoveY - mDownY > 0) {
                    /* 进入下拉状态 */
                    mCurrentState = RefreshConstant.STATUS_REFRESH;
                } else if (mCurrentState == RefreshConstant.STATUS_NORMAL && isReachFooter() && 
                        mMoveY - mDownY <= 0) {
                    /* 进入上拉状态 */
                    mCurrentState = RefreshConstant.STATUS_LOAD_MORE;
                } else if (mCurrentState == RefreshConstant.STATUS_REFRESH || mCurrentState == 
                        RefreshConstant.STATUS_RELEASE_TO_REFRESH) {
                    /* 正在下拉状态 */
                    if (mMoveY - mDownY > 0) {
                        if ((mMoveY - mDownY) * PULL_SCALE > mHeaderLayoutHeight) {
                            mCurrentState = RefreshConstant.STATUS_RELEASE_TO_REFRESH;
                            mHeaderLayout.onReleaseToRefresh();
                            setHeaderViewTopPadding((int) 
                                    (((mMoveY - mDownY) * PULL_SCALE - mHeaderLayoutHeight) * PULL_SCALE));
                            Log.d(TAG, "Header Top : " + mHeaderLayout.getPaddingTop());
                            Log.d(TAG, "CurrentState : Release to Refresh");
                        } else {
                            mCurrentState = RefreshConstant.STATUS_REFRESH;
                            mHeaderLayout.onRefresh();
                            setHeaderViewTopPadding((int) (-mHeaderLayoutHeight + (mMoveY - mDownY) * PULL_SCALE));
                            Log.d(TAG, "Header Top : " + mHeaderLayout.getPaddingTop());
                            Log.d(TAG, "CurrentState : Refresh");
                        }
                    }
                } else if (mCurrentState == RefreshConstant.STATUS_LOAD_MORE || mCurrentState == 
                        RefreshConstant.STATUS_RELEASE_TO_LOAD_MORE) {
                    /* 正在上拉状态 */
                    if (mMoveY - mDownY <= 0) {
                        if ((mDownY - mMoveY) * PULL_SCALE > mFooterLayoutHeight) {
                            mCurrentState = RefreshConstant.STATUS_RELEASE_TO_LOAD_MORE;
                            mFooterLayout.onReleaseToLoadMore();
                            setFooterViewBottomPadding((int)
                                    (((mDownY - mMoveY) * PULL_SCALE - mFooterLayoutHeight) * PULL_SCALE));
                            Log.d(TAG, "CurrentState : Load more");
                        } else {
                            mCurrentState = RefreshConstant.STATUS_LOAD_MORE;
                            mFooterLayout.onLoadMore();
                            setFooterViewBottomPadding((int) (-mFooterLayoutHeight + (mDownY - mMoveY) * PULL_SCALE));
                            Log.d(TAG, "CurrentState : Release to load more");
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentState == RefreshConstant.STATUS_REFRESH) {
                    mCurrentState = RefreshConstant.STATUS_NORMAL;
                    taskCancelRefreshMainThread();
                } else if (mCurrentState == RefreshConstant.STATUS_RELEASE_TO_REFRESH) {
                    mCurrentState = RefreshConstant.STATUS_REFRESH_ING;
                    mHeaderLayout.onRefreshIng();
                    taskRefreshMainThread();
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                } else if (mCurrentState == RefreshConstant.STATUS_LOAD_MORE) {
                    mCurrentState = RefreshConstant.STATUS_NORMAL;
                    taskCancelLoadMoreMainThread();
                } else if (mCurrentState == RefreshConstant.STATUS_RELEASE_TO_LOAD_MORE) {
                    mCurrentState = RefreshConstant.STATUS_LOAD_MORE_ING;
                    mFooterLayout.onLoadMoreIng();
                    taskLoadMoreMainThread();
                    if (mRefreshListener != null) {
                        mRefreshListener.onLoadMore();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    
    private Handler mMainThreadHandler = new Handler();
    
    private boolean mIsAnimRunning = false;
    private static final long ANIM_RUNNING_DURATION = 500;
    
    private void taskRefreshMainThread() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                taskRefresh();
            }
        });
    }
    
    private void taskRefresh() {
        mIsAnimRunning = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mHeaderLayout.getPaddingTop(), 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setHeaderViewTopPadding((int) value);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimRunning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(ANIM_RUNNING_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }
    
    private void taskCancelRefreshMainThread() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                taskCancelRefresh();     
            }
        });
    }
    
    private void taskCancelRefresh() {
        mIsAnimRunning = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mHeaderLayout.getPaddingTop(), 
                -mHeaderLayoutHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setHeaderViewTopPadding((int) value);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimRunning = false;
                if (mHeaderLayout != null) {
                    setHeaderViewTopPadding(-mHeaderLayoutHeight);
                    /* 刷新完成或者取消后，会有一定距离的偏移，设置位置使之复位 */
                    setSelection(1);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(ANIM_RUNNING_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }
    
    private void taskLoadMoreMainThread() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                taskLoadMore();   
            }
        });
    }
    
    private void taskLoadMore() {
        mIsAnimRunning = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mFooterLayout.getPaddingBottom(), 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setFooterViewBottomPadding((int) value);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimRunning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(ANIM_RUNNING_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }
    
    private void taskCancelLoadMoreMainThread() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                taskCancelLoadMore();
            }
        });
    }
    
    private void taskCancelLoadMore() {
        mIsAnimRunning = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mFooterLayout.getPaddingBottom(),
                -mFooterLayoutHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setFooterViewBottomPadding((int) value);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimRunning = false;
                mCurrentState = RefreshConstant.STATUS_NORMAL;
                if (mFooterLayout != null) {
                    setFooterViewBottomPadding(-mFooterLayoutHeight);
                    /* 刷新完成或者取消后，会有一定距离的偏移，设置位置使之复位 */
                    setSelection(getLastVisiblePosition());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(ANIM_RUNNING_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
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
