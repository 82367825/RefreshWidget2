package com.zero.refreshwidget2;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author linzewu
 * @date 16-7-26
 */
public class RefreshConstant {
    
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_REFRESH = 1;
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    public static final int STATUS_REFRESH_ING = 3;
    public static final int STATUS_LOAD_MORE = 4;
    public static final int STATUS_RELEASE_TO_LOAD_MORE = 5;
    public static final int STATUS_LOAD_MORE_ING = 6;


    /**
     * 测量view的大小.
     */
    public static void measureView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
    }
    
}
