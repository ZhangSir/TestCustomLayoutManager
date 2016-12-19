package com.itzs.testcustomlayoutmanager;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class FABRecyclerView extends RecyclerView {

    /**
     * 总移动的距离
     */
    private int mTotalMoveDelay = 0;

    private int lastScrolly = 0;

    /**
     * 控制X方法fling速度因子
     */
    private float flingScaleX = 1f;
    /**
     * 控制Y方法fling速度因子
     */
    private float flingScaleY = 1f;

    private FloatingActionButton floatingActionButton;

    private OnScrollStatusEffectListener onScrollStatusEffectListener;

    public FABRecyclerView(Context context) {
        super(context);
        initListener();
    }

    public FABRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initListener();
    }

    public FABRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListener();
    }

    public void setFlingScaleX(float flingScaleX) {
        this.flingScaleX = flingScaleX;
    }

    public void setFlingScaleY(float flingScaleY) {
        this.flingScaleY = flingScaleY;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= flingScaleX;
        velocityY *= flingScaleY;
        return super.fling(velocityX, velocityY);
    }

    private void initListener() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onScroll(dy);
            }
        });
    }


    private void onScroll(int dy) {
        //延迟视差滚动
        if (dy > 0 && mTotalMoveDelay < 0) {
            mTotalMoveDelay = 0;
        } else if (dy < 0 && mTotalMoveDelay > 0) {
            mTotalMoveDelay = 0;
        }
        mTotalMoveDelay += dy;
        if (mTotalMoveDelay > 300) {
            hideFloatButton();
            if (null != onScrollStatusEffectListener) {
                onScrollStatusEffectListener.onScrollUpEffect();
            }
        } else if (mTotalMoveDelay < -300) {
            showFloatButton();
            if (null != onScrollStatusEffectListener) {
                onScrollStatusEffectListener.onScrollDownEffect();
            }
        }
    }

    public void setFloatingActionButton(FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;
    }


    public void showFloatButton() {
        if (floatingActionButton != null && floatingActionButton.getVisibility() != View.VISIBLE) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    public void hideFloatButton() {
        if (floatingActionButton != null && floatingActionButton.getVisibility() == View.VISIBLE) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    /**
     * 监听滚动状态（向上或向下）监听器
     *
     * @param onScrollStatusEffectListener
     */
    public void setOnScrollStatusEffectListener(OnScrollStatusEffectListener onScrollStatusEffectListener) {
        this.onScrollStatusEffectListener = onScrollStatusEffectListener;
    }

    /**
     * 监听滚动状态（向上或向下）监听器
     */
    public interface OnScrollStatusEffectListener {
        /**
         * 向上滚动生效
         */
        void onScrollUpEffect();

        /**
         * 向下滚动生效
         */
        void onScrollDownEffect();
    }

}
