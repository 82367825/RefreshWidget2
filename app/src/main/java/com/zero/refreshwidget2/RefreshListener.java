package com.zero.refreshwidget2;

/**
 * @author linzewu
 * @date 16-7-26
 */
public interface RefreshListener {
    
    void onRefresh();
    
    void onLoadMore();
    
    void onScrollStateChange(int state);
    
}
