package com.zero.refreshwidget2.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zero.refreshwidget2.R;
import com.zero.refreshwidget2.RefreshListViewWidget;
import com.zero.refreshwidget2.RefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzewu
 * @date 16-7-27
 */
public class MainActivity extends Activity {

    private RefreshListViewWidget mRefreshListViewWidget;
    private RefreshAdapter mRefreshAdapter;
    
    private List<String> mRefreshList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
        
        mRefreshListViewWidget = (RefreshListViewWidget) findViewById(R.id.refresh_list);
        mRefreshAdapter = new RefreshAdapter();
        mRefreshListViewWidget.setAdapter(mRefreshAdapter);
        mRefreshListViewWidget.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }

            @Override
            public void onLoadMore() {
                loadMoreData();
            }

            @Override
            public void onScrollStateChange(int state) {

            }
        });
    }
    
    
    private void init() {
        mRefreshList = new ArrayList<>();
        for (int i = 0 ; i < 16; i++) {
            mRefreshList.add("text item" + (i + 1));
        }
    }
    
    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRefreshListViewWidget.refreshComplete();
            }
        }).start();
    }
    
    private void loadMoreData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRefreshListViewWidget.loadMoreComplete();
            }
        }).start();
    }
    
    private class RefreshAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRefreshList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRefreshList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RefreshHolder refreshHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.refresh_item, null);
                refreshHolder = new RefreshHolder();
                refreshHolder.mTextView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(refreshHolder);
            } else {
                refreshHolder = (RefreshHolder) convertView.getTag();
            }
            refreshHolder.mTextView.setText(mRefreshList.get(position));
            return convertView;
        }
    }
    
    private class RefreshHolder {
        public TextView mTextView;
    }
}
