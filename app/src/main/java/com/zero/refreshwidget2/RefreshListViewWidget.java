package com.zero.refreshwidget2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
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
    
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    
    private int mHeaderViewCount = 1;
    private int mFooterViewCount = 1;
    
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

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    private float mDownY;
    private float mMoveY;
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public void setRefreshEnabled(boolean enabled) {
        
    }

    @Override
    public void setLoadMoreEnabled(boolean enabled) {

    }
}
