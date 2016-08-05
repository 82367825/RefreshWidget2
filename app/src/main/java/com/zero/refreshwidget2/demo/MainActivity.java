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

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzewu
 * @date 16-7-27
 */
public class MainActivity extends Activity {

    private RefreshListViewWidget mRefreshListViewWidget;
    
    private List<String> mRefreshList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mRefreshListViewWidget = (RefreshListViewWidget) findViewById(R.id.refresh_list);
    }
    
    
    private void init() {
        mRefreshList = new ArrayList<>();
    }
    
    private void refreshData() {
        
    }
    
    private void loadMoreData() {
        
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
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.refresh_item, null);
                TextView textView = (TextView) convertView.findViewById(R.id.text);
                textView.setText(mRefreshList.get(position));
            }
            return convertView;
        }
    }
    
    private class RefreshHolder {
        
    }
}
