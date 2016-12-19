# TestCustomLayoutManager
自定义LayoutManager实现横向瀑布流
一 关键重写方法：
1、generateDefaultLayoutParams();
如果没有特殊需求，大部分情况下，我们只需要如下重写该方法即可。
@Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

2、onLayoutChildren();
该方法是LayoutManager的入口。它会在如下情况下被调用： 
1 在RecyclerView初始化时，会被调用两次。 
2 在调用adapter.notifyDataSetChanged()时，会被调用。 
3 在调用setAdapter替换Adapter时,会被调用。 
4 在RecyclerView执行动画时，它也会被调用。 
即RecyclerView 初始化 、 数据源改变时 都会被调用。 
它相当于ViewGroup的onLayout()方法，所以我们需要在里面layout当前屏幕可见的所有子View，千万不要layout出所有的子View。如果在这里绘制所有的子View，那么在我们每次调用NotifyDataSetChanged方法时，就会重新绘制所有的子View，如果有一万条数据，那么将会等待5S左右的时间，ANR!
3、竖直滚动需要 重写canScrollVertically()和scrollVerticallyBy()
[java] view plain copy 在CODE上查看代码片派生到我的代码片
@Override  
public boolean canScrollVertically() {  
    return true;  
}  

在canScrollVertically（）方法中，我们要实现滚动、重绘、子View回收和重用，控制滚动速度；
滚动和重绘：
滚动时需要注意边界判断；
子View 的回收和重用：
一个View只是暂时被清除掉，稍后立刻就要用到，使用detach。它会被缓存进scrapCache的区域。 
一个View 不再显示在屏幕上，需要被清除掉，并且下次再显示它的时机目前未知 ，使用remove。它会被以viewType分组，缓存进RecyclerViewPool里。 
注意：一个View只被detach，没有被recycle的话，不会放进RecyclerViewPool里，会一直存在recycler的scrap 中。

这里引出一个平时没有关注细节，即RecyclerView.Adapter的getItemViewType()方法;如果重写这个方法如下的话：

            @Override
            public int getItemViewType(int position) {
                return position;
            }

这样每一个ItemViewType都不一样，RecyclerView不会有任何的复用，因为每一个ItemView在RecyclerViewPool里都找不到可以复用的holder，ItemView有n个，onCreateViewHolder方法会执行n次。

控制滚动速度：

该方法return的值如果和传进来的dy值不同，RecyclerView就会认为到达边界，就会停止fling并显示边界光晕；

经测试该return值只用来判断边界，没有其他作用；

所以我们就可以在此处修改滑动速度，在未到达边界时返回dy，这样就不会让RecyclerView误认为已到达边界了；




二 常用API：

布局API:

//找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
View view = recycler.getViewForPosition(xxx);  //获取postion为xxx的View

addView(view);//将View添加至RecyclerView中，
addView(child, 0);//将View添加至RecyclerView中，childIndex为0，但是View的位置还是由layout的位置决定，该方法在逆序layout子View时有大用

measureChildWithMargins(scrap, 0, 0);//测量View,这个方法会考虑到View的ItemDecoration以及Margin

//将ViewLayout出来，显示在屏幕上，内部会自动追加上该View的ItemDecoration和Margin。此时我们的View已经可见了
layoutDecoratedWithMargins(view, leftOffset, topOffset,
                        leftOffset + getDecoratedMeasuredWidth(view),
                        topOffset + getDecoratedMeasuredHeight(view));

回收API：

detachAndScrapAttachedViews(recycler);//detach轻量回收所有View
detachAndScrapView(view, recycler);//detach轻量回收指定View

// recycle真的回收一个View ，该View再次回来需要执行onBindViewHolder方法
removeAndRecycleView(View child, Recycler recycler)
removeAndRecycleAllViews(Recycler recycler);

detachView(view);//超级轻量回收一个View,马上就要添加回来
attachView(view);//将上个方法detach的View attach回来
recycler.recycleView(viewCache.valueAt(i));//detachView 后 没有attachView的话 就要真的回收掉他们

移动子ViewAPI:

offsetChildrenVertical(-dy); // 竖直平移容器内的item 
offsetChildrenHorizontal(-dx);//水平平移容器内的item

工具API：

public int getPosition(View view)//获取某个view 的 layoutPosition，很有用的方法，却鲜(没)有文章提及，是我翻看源码找到的。

//以下方法会我们考虑ItemDecoration的存在，但部分函数没有考虑margin的存在
getDecoratedLeft(view)=view.getLeft()
getDecoratedTop(view)=view.getTop()
getDecoratedRight(view)=view.getRight()
getDecoratedBottom(view)=view.getBottom()
getDecoratedMeasuredHeight(view)=view.getMeasuredWidth()
getDecoratedMeasuredHeight(view)=view.getMeasuredHeight()

//由于上述方法没有考虑margin的存在，所以我参考LinearLayoutManager的源码：
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
三 Demo的说明：
实现功能：实现横向流式布局，实现了子View 的回收和重用，实现了SmoothScrollToPosition功能，实现了NotifyDataSetChanged方法更新数据的功能；
存在缺陷：如果更换数据（特指某Position上的子View的大小可能改变），需要给RecyclerView重新new一个LayoutManager，否则显示会有问题；
未实现ScrollToPosition功能；未实现定向更新功能；

注：有关于该组件在首次非常快速滑动时，可能出现子View位置计算错误的问题（推测应该是滑动过快，而计算并未能实时完成，最终造成位置计算错误），已通过控制滑动速度和fling速度解决；
![image1](https://github.com/ZhangSir/TestCustomLayoutManager/blob/master/Screenshot_2016-12-19-16-34-17.png)
![image2](https://github.com/ZhangSir/TestCustomLayoutManager/blob/master/Screenshot_2016-12-19-16-34-17.png)


