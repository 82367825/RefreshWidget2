package com.zero.refreshwidget2;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author linzewu
 * @date 16/8/7
 */
public class RefreshHeader extends LinearLayout implements RefreshHeaderInterface {
    private static final String TEXT_REFRESH = "下拉刷新";
    private static final String TEXT_RELEASE_TO_REFRESH = "松手刷新";
    private static final String TEXT_REFRESH_ING = "正在刷新";
    
    private ImageView mImageView;
    private TextView mTextView;
    
    public RefreshHeader(Context context) {
        super(context);
        init();
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.refresh_header, this);
        mImageView = (ImageView) findViewById(R.id.image);
        mTextView = (TextView) findViewById(R.id.text);
    }
    
    private int mCurrentState = RefreshConstant.STATUS_NORMAL;

    @Override
    public void onRefresh() {
        if (mCurrentState != RefreshConstant.STATUS_REFRESH) {
            if (mValueAnimator != null && mValueAnimator.isRunning()) {
                mValueAnimator.cancel();
            }
            mTextView.setText(TEXT_REFRESH);
            mImageView.setImageResource(R.drawable.arrow);
            mImageView.setRotation(0);
            mCurrentState = RefreshConstant.STATUS_REFRESH;
        }
    }

    @Override
    public void onReleaseToRefresh() {
        if (mCurrentState != RefreshConstant.STATUS_RELEASE_TO_REFRESH) {
            if (mValueAnimator != null && mValueAnimator.isRunning()) {
                mValueAnimator.cancel();
            }
            mTextView.setText(TEXT_RELEASE_TO_REFRESH);
            mImageView.setImageResource(R.drawable.arrow);
            mImageView.setRotation(180);
            mCurrentState = RefreshConstant.STATUS_RELEASE_TO_REFRESH;
        }
    }

    @Override
    public void onRefreshIng() {
        if (mCurrentState != RefreshConstant.STATUS_REFRESH_ING) {
            mTextView.setText(TEXT_REFRESH_ING);
            mImageView.setImageResource(R.drawable.spinner_48_inner_holo);
            mValueAnimator = ValueAnimator.ofFloat(0, 360);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mImageView.setRotation((Float) animation.getAnimatedValue());
                }
            });
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.setDuration(1000);
            mValueAnimator.start();
        }
    }

    @Override
    public void onCancel() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }    
    }

    private ValueAnimator mValueAnimator;
}
