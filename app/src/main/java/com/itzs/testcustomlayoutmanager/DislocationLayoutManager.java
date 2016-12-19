package com.itzs.testcustomlayoutmanager;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * 用于RecyclerView的错位排列LayoutManager<br>
 * <p>
 * 在RecyclerView中，有两个缓存：<br>
 * Scrap<br>
 * Recycle<br>
 * Scrap中文就是废料的意思，Recycle对应是回收的意思。<br>
 * 这两个缓存有什么作用呢？<br>
 * 首先Scrap缓存是指里面缓存的View是接下来需要用到的，即里面的绑定的数据无需更改，可以直接拿来用的，是一个轻量级的缓存集合；<br>
 * 而Recycle的缓存的View为里面的数据需要重新绑定，即需要通过Adapter重新绑定数据。<br>
 * 当我们去获取一个新的View时，RecyclerView首先去检查Scrap缓存是否有对应的position的View，如果有，则直接拿出来可以直接用，不用去重新绑定数据；如果没有，则从Recycle缓存中取，并且会回调Adapter的onBindViewHolder方法（当然了，如果Recycle缓存为空，还会调用onCreateViewHolder方法），最后再将绑定好新数据的View返回。<br>
 * <p>
 * Created by zhangshuo on 2016/12/6.
 */
public class DislocationLayoutManager extends RecyclerView.LayoutManager {

    private final String TAG = DislocationLayoutManager.class.getSimpleName();

//    //保存所有的Item的上下左右偏移量信息
//    private SparseArray<Rect> allItemFrames = new SparseArray<>();
//    private SparseArray<View> viewCache = new SparseArray<View>();
//
//    /**
//     * 用于缓存Scroll时，显示区域的坐标
//     */
//    private Rect displayRect = new Rect();
//    /**
//     * 用于回收item时，缓存当前item的坐标
//     */
//    private Rect itemRect = new Rect();
//
//    private int offsetLeft = 0;
//    private int offsetTop = 0;
//
//    private int verticalScrollOffset = 0;
//    private int totalHeight = 0;
//
//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        if (getItemCount() == 0) {
//            //没有数据需要展示时，移除所有的子View
//            detachAndScrapAttachedViews(recycler);
//            return;
//        }
//        long startTime = System.currentTimeMillis();
//        //跳过preLayout，preLayout主要用于支持动画
//        if (state.isPreLayout()) return;
//
//        //所有的子View先Detach掉，放入到Scrap缓存中，为什么要这样做呢？主要是考虑到，屏幕上可能还有一些ItemView是继续要留在屏幕上的，我们不直接Remove，而是选择Detach
//        detachAndScrapAttachedViews(recycler);
//
//        recycleAndLayoutItem(0, recycler, state);
//
//        LogUtils.d(TAG + "--onLayoutChildren used time:" + (System.currentTimeMillis() - startTime));
//    }
//
//    @Override
//    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        if (getChildCount() == 0) {
//            return 0;
//        }
//        long startTime = System.currentTimeMillis();
//        //实际要滑动的距离
//        int travel = dy;
//
//        //Take top measurements from the top-left child
//        final View topView = getChildAt(0);
//        //Take bottom measurements from the bottom-right child.
//        final View bottomView = getChildAt(getChildCount() - 1);
//
//        if (dy > 0) { // Contents are scrolling up
//            //Check against bottom bound
//            if (getPosition(bottomView) >= getItemCount() - 1) {
//                //如果滑动到最底部
//                travel = Math.min(travel, getDecoratedBottom(bottomView) - getVerticalSpace());
//            }
//        } else {
//            if (verticalScrollOffset + dy < 0) {
//                //如果滑动到最顶部
//                travel = -verticalScrollOffset;
//            }
//
//        }
//
//        //将竖直方向的偏移量+travel
//        verticalScrollOffset += travel;
//        //平移容器内的item
//        offsetChildrenVertical(-travel);
//
//        LogUtils.d(TAG + "--scrollVerticallyBy-use time:" + (System.currentTimeMillis() - startTime));
//
//        recycleAndLayoutItem(dy, recycler, state);
//
//        LogUtils.d(TAG + "--childView count:" + getChildCount());
//        LogUtils.d(TAG + "--Scrap pool size:" + recycler.getScrapList().size());
//
//        return travel;
//    }
//
//    public void scrollToPosition(int position) {
//        if (getItemCount() == 0) return;
//        if (position >= getItemCount()) {
//            position = getItemCount() - 1;
//        }
//        if (position < 0) {
//            position = 0;
//        }
//
//        verticalScrollOffset = allItemFrames.get(position).top;
//
//        if (verticalScrollOffset < 0) {
//            //如果滑动到最顶部
//            verticalScrollOffset = 0;
//        } else if (verticalScrollOffset > totalHeight - getVerticalSpace()) {
//            //如果滑动到最底部
//            verticalScrollOffset = totalHeight - getVerticalSpace();
//        }
//
//        //Toss all existing views away
//        removeAllViews();
//        //Trigger a new view layout
//        requestLayout();
//    }
//
//    private void recycleAndLayoutItem(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        if (state.isPreLayout()) return;
//        long startTime = System.currentTimeMillis();
//        int firstVisiblePosition = getFirstVisiblePosition();
//        //当前scroll offset状态的显示区域
//        displayRect.set(getPaddingLeft(), getPaddingTop() + verticalScrollOffset, getPaddingLeft() + getHorizontalSpace(), getPaddingTop() + verticalScrollOffset + getVerticalSpace());
//        //先把所有的子View都分离开，通常会被缓存仅scrap Pool中
////        detachAndScrapAttachedViews(recycler);
//        viewCache.clear();
//        for (int i = 0; i < getChildCount(); i++) {
//            final View child = getChildAt(i);
//            viewCache.put(i, child);
//        }
//        for (int i = 0; i < viewCache.size(); i++) {
//            View view = viewCache.valueAt(i);
//            itemRect = allItemFrames.get(getPosition(view));
////            itemRect.set(getDecoratedLeft(view), getDecoratedTop(view), getDecoratedRight(view), getDecoratedBottom(view));
//            if(!Rect.intersects(displayRect, itemRect)){
////                LogUtils.d(TAG + "--remove--" + getPosition(view));
//                removeAndRecycleView(view, recycler);
//            }else{
////                LogUtils.d(TAG + "--detach--" + getPosition(view));
//                detachAndScrapView(view, recycler);
//            }
//        }
////        detachAndScrapAttachedViews(recycler);
//
//        //将这个item布局出来
//        if (dy < 0) {
//            //向下滑动
//            for (int i = 0; i < allItemFrames.size(); i++) {
//                if (Rect.intersects(displayRect, allItemFrames.get(i))) {
//                    View view = recycler.getViewForPosition(i);
//                    measureChildWithMargins(view, 0, 0);
//                    addView(view);
//                    Rect frame = allItemFrames.get(i);
//                    //将这个item布局出来
//                    layoutDecorated(view, frame.left, frame.top - verticalScrollOffset, frame.right, frame.bottom - verticalScrollOffset);
//                }
//            }
//        } else {
//            offsetLeft = getPaddingLeft();
//            offsetTop = getPaddingTop();
//            //记录上一行最大的子View高度
//            int lastMaxHeight = 0;
//            for (int currentPosition = firstVisiblePosition; currentPosition < getItemCount() && (offsetTop - verticalScrollOffset) < getVerticalSpace(); currentPosition++) {
//
//                Rect frame = allItemFrames.get(currentPosition);
//                if (null == frame) {
//                    frame = new Rect();
//
//                    View view = recycler.getViewForPosition(currentPosition);
//                    measureChildWithMargins(view, 0, 0);
//                    //把宽高拿到，宽高都是包含ItemDecorate的尺寸
//                    int width = getDecoratedMeasuredWidth(view);
//                    int height = getDecoratedMeasuredHeight(view);
//
//                    if (currentPosition == 0) {
//                        //第一个作为标题，独占一行显示
//                        frame.set(offsetLeft, offsetTop, offsetLeft + width, offsetTop + height);
//                        //将横向偏移量重置
//                        offsetLeft = getPaddingLeft();
//                        //将竖直方向偏移量增加height
//                        offsetTop += height;
//                        //计算总高度
//                        totalHeight += height;
//                    } else {
//                        if (offsetLeft + width < getHorizontalSpace()) {
//                            //当前横向偏移量加上当前要被添加的子View的宽度，未超过RecyclerView的横向显示空间，则添加在同一行
//                            frame.set(offsetLeft, offsetTop, offsetLeft + width, offsetTop + height);
//                            //将横向偏移量增加width
//                            offsetLeft += width;
//                            //纵向偏移量不变，总高度也不变
//                            //计算并记录当前行的子View的最大高度值
//                            lastMaxHeight = Math.max(lastMaxHeight, height);
//                        } else {
//                            //当前横向偏移量加上当前要被添加的子View的宽度，超过了RecyclerView的横向显示空间，则添加到下一行
//                            //将横向偏移量重置
//                            offsetLeft = getPaddingLeft();
//                            //将纵向偏移量增加lastMaxHeight
//                            offsetTop += lastMaxHeight;
//                            //将总高度增加lastMaxHeight
//                            totalHeight += lastMaxHeight;
//                            //将上一行的子View的最大高度值重置为0
//                            lastMaxHeight = 0;
//
//                            frame.set(offsetLeft, offsetTop, offsetLeft + width, offsetTop + height);
//                            //将横向偏移量增加width
//                            offsetLeft += width;
//                            //纵向偏移量不变，总高度也不变
//                            //计算并记录当前行的子View的最大高度值
//                            lastMaxHeight = Math.max(lastMaxHeight, height);
//                        }
//                    }
//                    if (Rect.intersects(displayRect, frame)) {
//                        //将这个item布局出来
//                        addView(view);
//                        layoutDecorated(view, frame.left, frame.top - verticalScrollOffset, frame.right, frame.bottom - verticalScrollOffset);
//                    }
//                    //将当前的Item的Rect边界数据保存
//                    allItemFrames.put(currentPosition, frame);
//                } else {
//                    if (Rect.intersects(displayRect, frame)) {
//                        View view = recycler.getViewForPosition(currentPosition);
//                        measureChildWithMargins(view, 0, 0);
//                        addView(view);
//                        //将这个item布局出来
//                        layoutDecorated(view, frame.left, frame.top - verticalScrollOffset, frame.right, frame.bottom - verticalScrollOffset);
//                        offsetLeft = frame.right;
//                        offsetTop = frame.top;
//                        lastMaxHeight = Math.max(lastMaxHeight, frame.bottom - frame.top);
//                    }
//                }
//
//            }
//        }
//        LogUtils.d(TAG + "--recycleAndLayoutItem-use time:" + (System.currentTimeMillis() - startTime));
//    }


    private SparseArray<Rect> mItemRects = new SparseArray<>();//key 是View的position，保存View的bounds ，
    private int mVerticalOffset;//竖直偏移量 每次换行时，要根据这个offset判断
    private int mFirstVisiPos;//屏幕可见的第一个View的Position
    private int mLastVisiPos;//屏幕可见的最后一个View的Position

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        synchronized (DislocationLayoutManager.class) {
            if (getItemCount() == 0) {//没有Item，界面空着吧
                detachAndScrapAttachedViews(recycler);
                return;
            }
            if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
                return;
            }

            //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
            detachAndScrapAttachedViews(recycler);

            //初始化时调用 填充childView
            fill(recycler, state, 0);
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //该方法return的值如果和传进来的dy值不同，RecyclerView就会认为到达边界，就会停止fling并显示边界光晕；
        // 经测试该return值只用来判断边界，没有其他作用；
        // 所以我们就可以在此处修改滑动速度，在未到达边界时返回dy，这样就不会让RecyclerView误认为已到达边界了；
        //因为目前这个LayoutManager在非常快速的滑动RecyclerView时，会出现item位置计算错误的问题，造成item重叠等现象；
        synchronized (DislocationLayoutManager.class) {
            if (dy == 0 || getChildCount() == 0) {
                return 0;
            }
            Log.d("dy", dy + "");
            //实际滑动的距离，可能会在边界处被修复
            int realOffset = dy;
            //记录是否真正到达边界，用于判断该return哪个值
            boolean reachBound = false;

            if(realOffset > 100){
                realOffset = 100;
            }
            if(realOffset < -100){
                realOffset = -100;
            }

            //边界修复代码
            if (mVerticalOffset + realOffset < 0) {//上边界
                realOffset = -mVerticalOffset;
                reachBound = true;
            } else if (realOffset > 0) {//下边界
                //利用最后一个子View比较修正
                View lastChild = getChildAt(getChildCount() - 1);
                if (getPosition(lastChild) >= getItemCount() - 1) {
                    //如果滑动到最底部
                    reachBound = true;
                    if (getDecoratedBottom(lastChild) - getVerticalSpace() < 0) {
                        realOffset = 0;
                    } else {
                        realOffset = Math.min(realOffset, getDecoratedBottom(lastChild) - getVerticalSpace());
                    }
                }
            }
            //位移后会返回一个真实的修正后的移动距离correctedOffset
            int correctedOffset = fill(recycler, state, realOffset);//先填充，再位移。
            if(realOffset != correctedOffset){
                //realOffset和correctedOffset不同，说明真实移动距离被修正了，也说明到达边界了
                realOffset = correctedOffset;
                reachBound = true;
            }

            mVerticalOffset += realOffset;//累加实际滑动距离

            offsetChildrenVertical(-realOffset);//滑动

            Log.d("realOffset", "" + realOffset);

            if(reachBound){
                //到达边界，返回修改后的值，告诉RecyclerView已到达边界
                return realOffset;
            }else{
                //未到边界，返回dy
                return dy;
            }
        }
    }

    /**
     * 填充childView的核心方法,应该先填充，再移动。
     * 在填充时，预先计算dy的在内，如果View越界，回收掉。
     * 一般情况是返回dy，如果出现View数量不足，则返回修正后的dy.
     *
     * @param recycler
     * @param state
     * @param dy       RecyclerView给我们的位移量,+,显示底端， -，显示头部
     * @return 修正以后真正的dy（可能剩余空间不够移动那么多了 所以return <|dy|）
     */
    private synchronized int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        if (state.isPreLayout()) return 0;

        int topOffset = getPaddingTop();
        int leftOffset = getPaddingLeft();
        int lineMaxHeight = 0;

        //回收越界子View
        if (getChildCount() > 0) {//滑动时进来的
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                if (dy > 0) {//需要回收当前屏幕，上越界的View
                    if (getDecoratedBottom(child) - dy < topOffset) {
                        removeAndRecycleView(child, recycler);
                        mFirstVisiPos++;
                        continue;
                    }
                } else if (dy < 0) {//回收当前屏幕，下越界的View
                    if (getDecoratedTop(child) - dy > getHeight() - getPaddingBottom()) {
                        removeAndRecycleView(child, recycler);
                        mLastVisiPos--;
                        continue;
                    }
                }
            }
        }

        //布局子View阶段
        if (dy >= 0) {
            //顺序addChildView

            int minPos = mFirstVisiPos;
            mLastVisiPos = getItemCount() - 1;
            if (getChildCount() > 0) {
                View lastView = getChildAt(getChildCount() - 1);
                minPos = getPosition(lastView) + 1;//从最后一个View+1开始吧
                topOffset = getDecoratedTop(lastView);
                leftOffset = getDecoratedRight(lastView);
                lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(lastView));
            }

            for (int i = minPos; i <= mLastVisiPos; i++) {
                Log.d("position", i + "");
                Log.d("topOffset", "" + topOffset);
                //如果有可重复利用的，则重复利用，并且可以解决在调用NotifyDataSetChanged时，由于onLayoutChildren方法先调用detachAndScrapAttachedViews(recycler)方法，造成上方代码段执行不了，无法正确初始化topOffset，最终导致刷新完界面后，每个item的位置有变动的问题；
                Rect tempRect = mItemRects.get(i);
                if (null != tempRect) {
                    View child = recycler.getViewForPosition(i);
                    addView(child);
                    measureChildWithMargins(child, 0, 0);

                    leftOffset = tempRect.right;
                    topOffset = tempRect.top - mVerticalOffset;
                    lineMaxHeight = Math.max(lineMaxHeight, tempRect.bottom - tempRect.top);

                    //新起一行的时候要判断一下边界
                    if (topOffset - dy > getHeight() - getPaddingBottom()) {
                        //越界了 就回收
                        leftOffset = getPaddingLeft();
                        lineMaxHeight = 0;

                        removeAndRecycleView(child, recycler);
                        mLastVisiPos = i - 1;
                    } else {
                        layoutDecoratedWithMargins(child, tempRect.left, tempRect.top - mVerticalOffset, tempRect.right, tempRect.bottom - mVerticalOffset);
                    }
                } else {
                    //找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
                    View child = recycler.getViewForPosition(i);
                    addView(child);
                    measureChildWithMargins(child, 0, 0);
                    //把宽高拿到，宽高都是包含ItemDecorate的尺寸
                    int width = getDecoratedMeasurementHorizontal(child);
                    int height = getDecoratedMeasurementVertical(child);
                    if (i == 0) {
                        //第一个作为标题，独占一行显示
                        layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));

                        //保存Rect供逆序layout用
                        Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
                        mItemRects.put(i, rect);
                        leftOffset = getPaddingLeft();
                        topOffset += height;
                        lineMaxHeight = 0;
                    } else if (leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {//当前行还排列的下
                        layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));

                        //保存Rect供逆序layout用
                        Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
                        mItemRects.put(i, rect);

                        //改变 left  lineHeight
                        leftOffset += getDecoratedMeasurementHorizontal(child);
                        lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
                    } else {//当前行排列不下
                        //改变top  left  lineHeight
                        leftOffset = getPaddingLeft();
                        topOffset += lineMaxHeight;
                        lineMaxHeight = 0;

                        //新起一行的时候要判断一下边界
                        if (topOffset - dy > getHeight() - getPaddingBottom()) {
                            //越界了 就回收
                            removeAndRecycleView(child, recycler);
                            mLastVisiPos = i - 1;
                        } else {
                            layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));

                            //保存Rect供逆序layout用
                            Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
                            mItemRects.put(i, rect);

                            //改变 left  lineHeight
                            leftOffset += getDecoratedMeasurementHorizontal(child);
                            lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
                        }
                    }
                }


            }
            //添加完后，判断是否已经没有更多的ItemView，并且此时屏幕仍有空白，则需要修正dy
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) >= getItemCount() - 1) {
                //如果滑动到最底部
                if (getDecoratedBottom(lastChild) - getVerticalSpace() < 0) {
                    dy = 0;
                } else {
                    dy = Math.min(dy, getDecoratedBottom(lastChild) - getVerticalSpace());
                }
            }

        } else {
            /**
             * 利用Rect保存子View边界
             * 正序排列时，保存每个子View的Rect，逆序时，直接拿出来layout。
             */
            int maxPos = getItemCount() - 1;
            mFirstVisiPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                maxPos = getPosition(firstView) - 1;
            }
            for (int i = maxPos; i >= mFirstVisiPos; i--) {
                Rect rect = mItemRects.get(i);
                if (null != rect) {
                    if (rect.bottom - mVerticalOffset - dy < getPaddingTop()) {
                        mFirstVisiPos = i + 1;
                        break;
                    } else {
                        View child = recycler.getViewForPosition(i);
                        addView(child, 0);//将View添加至RecyclerView中，childIndex为1，但是View的位置还是由layout的位置决定
                        measureChildWithMargins(child, 0, 0);

                        layoutDecoratedWithMargins(child, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset);
                    }
                }
            }
        }


        Log.d(TAG, "childView count:" + getChildCount());
        Log.d(TAG, "Scrap pool size:" + recycler.getScrapList().size());
        return dy;
    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    /**
     * 求出第一个可视item
     *
     * @return
     */
    private int getFirstVisiblePosition() {
        if (getChildCount() == 0) {
            return 0;
        }
        return getPosition(getChildAt(0));
    }

    /**
     * 获取RecyclerView在垂直方向上的可用空间，即去除了padding后的高度
     *
     * @return
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取RecyclerView在水平方向上的可用空间，即去除了padding后的高度
     *
     * @return
     */
    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Nullable
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return DislocationLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;
        return new PointF(0, direction);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter,
                                 RecyclerView.Adapter newAdapter) {
        //Completely scrap the existing layout
        removeAllViews();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
