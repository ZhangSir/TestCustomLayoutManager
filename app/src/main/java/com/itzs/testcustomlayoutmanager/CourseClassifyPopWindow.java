package com.itzs.testcustomlayoutmanager;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部资料页面分类和标签筛选的PopWindow
 */
public class CourseClassifyPopWindow {

    private final String TAG = CourseClassifyPopWindow.class.getSimpleName();

    private Context mContext;

    private View popView;
    private FABRecyclerView frvClassify;
    private FABRecyclerView frvLabel;
    private TextView tvCancel;
    private TextView tvConfirm;
    private PopupWindow popWindow;

    private List<CourseClassifyModel> listClassify;
    private List<CourseLabelModel> listLabel;

    private CourseClassifyAdapter classifyAdapter;
    private CourseLabelAdapter labelAdapter;

    /**
     * 记录当前选中的分类
     */
    private CourseClassifyModel selectedClassify;

    /**
     * 记录当前选中的标签列表
     */
    private List<CourseLabelModel> selectedLabels;

    /**
     * 默认分类
     */
    private CourseClassifyModel defaultClassify;

    /**
     * 默认标签
     */
    private CourseLabelModel defaultLabel;

    private OnItemSelectedListener onItemSelectedListener;

    public CourseClassifyPopWindow(Context context) {
        this.mContext = context;
        listClassify = new ArrayList<>();
        listLabel = new ArrayList<>();
        selectedLabels = new ArrayList<>();

        defaultClassify = new CourseClassifyModel();
        defaultClassify.setValue(-1);
        defaultClassify.setName(mContext.getString(R.string.label_all));

        defaultLabel = new CourseLabelModel();
        defaultLabel.setId(-1);
        defaultLabel.setLabelName(mContext.getString(R.string.label_all));

    }

    /**
     * 创建PopWindow
     *
     * @return
     */
    public void createPopWindow() {

        popView = View.inflate(mContext, R.layout.layout_pop_course_classify, null);
        frvClassify = (FABRecyclerView) popView.findViewById(R.id.frv_pop_course_classify_left);
        frvLabel = (FABRecyclerView) popView.findViewById(R.id.frv_pop_course_classify_right);
        tvCancel = (TextView) popView.findViewById(R.id.tv_pop_course_classify_cancel);
        tvConfirm = (TextView) popView.findViewById(R.id.tv_pop_course_classify_confirm);

        classifyAdapter = new CourseClassifyAdapter(mContext, listClassify);
        labelAdapter = new CourseLabelAdapter(mContext, listLabel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        frvClassify.setLayoutManager(layoutManager);
        frvClassify.setItemAnimator(new DefaultItemAnimator());
        frvClassify.setAdapter(classifyAdapter);

        DislocationLayoutManager dislocationLayoutManager = new DislocationLayoutManager();
        frvLabel.setLayoutManager(dislocationLayoutManager);
        frvLabel.setItemAnimator(new DefaultItemAnimator());
        frvLabel.setAdapter(labelAdapter);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemSelectedListener) {
                    onItemSelectedListener.onCancel();
                }
                hidePopWindow();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemSelectedListener) {
                    onItemSelectedListener.onConfirm(selectedClassify, selectedLabels);
                }

                hidePopWindow();
            }
        });

        classifyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (selectedClassify.getValue() != listClassify.get(position).getValue()) {
                    //点击了非当前选中分类
                    selectClassifyData(listClassify, position);
                    classifyAdapter.notifyDataSetChanged();
                    selectedClassify = listClassify.get(position);
                    requestLabel();
                } else {
                    frvLabel.smoothScrollToPosition(0);
                }
            }
        });

        labelAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CourseLabelModel model = listLabel.get(position);
                if (model.getId() == defaultLabel.getId()) {
                    //如果当前点击item是默认项“全部”
                    if (defaultLabel.isSelected()) {
                        //默认项“全部”本就是选中状态，不做任何操作

                    } else {
                        //默认项“全部”本是未选中状态
                        for (int i = 0; i < selectedLabels.size(); i++) {
                            selectedLabels.get(i).setSelected(false);
                        }
                        selectedLabels.clear();
                        defaultLabel.setSelected(true);
                        selectedLabels.add(defaultLabel);
                        labelAdapter.notifyDataSetChanged();
                    }
                } else {

                    if (model.isSelected()) {
                        //当前点击item本就是选中状态，则取消选中
                        selectedLabels.remove(model);
                        model.setSelected(false);

                        if (selectedLabels.size() == 0) {
                            //如果此时标签选中列表已经空了，则默认选中默认项“不限”
                            defaultLabel.setSelected(true);
                            selectedLabels.add(defaultLabel);
                        }
                    } else {
                        //当前点击item本是未选中状态, 则选中
                        if (defaultLabel.isSelected()) {
                            //默认项“全部”是选中状态
                            selectedLabels.clear();
                            defaultLabel.setSelected(false);
                        }

                        model.setSelected(true);
                        selectedLabels.add(model);
                    }

                    labelAdapter.notifyDataSetChanged();
                }
            }
        });


        popView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                final int x = (int) event.getX();
                final int y = (int) event.getY();

                if ((event.getAction() == MotionEvent.ACTION_DOWN)
                        && ((x < 0) || (x >= v.getWidth()) || (y < 0) || (y >= v.getHeight()))) {
                    if (null != onItemSelectedListener) {
                        onItemSelectedListener.onCancel();
                    }
                    hidePopWindow();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (null != onItemSelectedListener) {
                        onItemSelectedListener.onCancel();
                    }
                    hidePopWindow();
                    return true;
                } else {
                    return popView.onTouchEvent(event);
                }
            }
        });
        popView.setFocusableInTouchMode(true);//设置此属性是为了让popView的返回键监听器生效，否则无法监听
        popView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popWindow != null && popWindow.isShowing()) {
                        if (null != onItemSelectedListener) {
                            onItemSelectedListener.onCancel();
                        }
                        hidePopWindow();
                        return true;
                    }
                }
                return false;
            }
        });

        popWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        /*设置背景为空，解决点击PopupWindow中的RadioButton是RadioButton的背景变为透明的问题*/
        popWindow.setBackgroundDrawable(null);
        /**
         * 设置PopupWindow外部区域是否可触摸
         */
        popWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        popWindow.setTouchable(true); // 设置PopupWindow可触摸
        popWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        /* 监听popwindow，当PopWindow消失时，将副栏的TAB中的RadioButton恢复原状*/
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub

            }
        });

        requestClassify();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 刷新列表显示状态
     */
    public void notifyDataSetChanged() {
        if (null != classifyAdapter) classifyAdapter.notifyDataSetChanged();
    }

    public void showPopWindow(View anchor) {
        if (null == popWindow) return;
        popWindow.showAsDropDown(anchor);
    }

    public void hidePopWindow() {
        if (null == popWindow) return;
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    public int[] getTotalHeightofListView(ListView listView) {
        int[] wh = new int[2];
        ListAdapter mAdapter = (ListAdapter) listView.getAdapter();
        if (mAdapter == null) {
            return wh;
        }
        int width = 0;
        int totalHeight = 0;
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(w, h);
            if (width < mView.getMeasuredWidth()) {
                width = mView.getMeasuredWidth();
            }
            totalHeight += mView.getMeasuredHeight();
        }

        wh[0] = width;
        wh[1] = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        return wh;
    }

    /**
     * 选择数据
     *
     * @param listModel 数据列表
     * @param position  要选择的数据的位置
     */
    public void selectClassifyData(List<CourseClassifyModel> listModel, int position) {
        if (null != listModel && listModel.size() > 0) {
            for (int i = 0; i < listModel.size(); i++) {
                listModel.get(i).setSelected(false);
            }
            listModel.get(position).setSelected(true);
        }
    }

    public void requestClassify() {
        this.listClassify.clear();
        this.listClassify.add(defaultClassify);
        defaultClassify.setSelected(true);
        selectedClassify = defaultClassify;

        for (int i = 0; i < 50; i++) {
            CourseClassifyModel model = new CourseClassifyModel();
            model.setValue(i);
            model.setName("分类" + i);
            listClassify.add(model);
        }
        classifyAdapter.notifyDataSetChanged();

        requestLabel();
    }

    /**
     * 请求标签
     */
    private void requestLabel() {
        listLabel.clear();
        this.listLabel.add(defaultLabel);
        resetSelectedLabel();

        for (int i = 1; i < 1000; i++) {
            CourseLabelModel model = new CourseLabelModel();
            model.setId(i);
            model.setLabelName("标签" + i);
            listLabel.add(model);
        }
        frvLabel.setLayoutManager(new DislocationLayoutManager());
        labelAdapter.notifyDataSetChanged();
    }

    /**
     * 重置选中标签
     */
    private void resetSelectedLabel() {
        selectedLabels.clear();
        defaultLabel.setSelected(true);
        selectedLabels.add(defaultLabel);
    }

    public class CourseClassifyAdapter extends RecyclerView.Adapter<ClassifyHolder> {

        private Context mContext;
        private List<CourseClassifyModel> listClassify;

        private OnItemClickListener itemClickListener;

        public CourseClassifyAdapter(Context mContext, List<CourseClassifyModel> list) {
            this.mContext = mContext;
            this.listClassify = list;
        }

        @Override
        public ClassifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_pop_course_classify_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            return new ClassifyHolder(view);
        }

        @Override
        public void onBindViewHolder(ClassifyHolder holder, final int position) {
            holder.itemView.setTag(position);
            CourseClassifyModel model = listClassify.get(position);

            holder.tvName.setText(model.getName());

            holder.tvName.setSelected(model.isSelected());

        }

        @Override
        public int getItemCount() {
            return listClassify.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }


        public void deleteItem(int position) {
            this.notifyItemRemoved(position);
            this.listClassify.remove(position);
            this.notifyItemRangeChanged(position, getItemCount());
        }

        public void addItem(int position, CourseClassifyModel model) {
            this.notifyItemInserted(position);
            this.listClassify.add(position, model);
            this.notifyItemRangeChanged(position, getItemCount());
        }

        public void refreshItem(int position) {
            this.notifyItemChanged(position);
        }

    }

    class ClassifyHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ClassifyHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_pop_course_classify_item);
        }
    }

    public class CourseLabelAdapter extends RecyclerView.Adapter<LabelHolder> {

        private Context mContext;
        private List<CourseLabelModel> listLabel;

        private OnItemClickListener itemClickListener;

        public CourseLabelAdapter(Context mContext, List<CourseLabelModel> list) {
            this.mContext = mContext;
            this.listLabel = list;
        }

        @Override
        public LabelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_pop_course_classify_label_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            return new LabelHolder(view);
        }

        @Override
        public void onBindViewHolder(LabelHolder holder, final int position) {
            holder.itemView.setTag(position);
            CourseLabelModel model = listLabel.get(position);

            holder.tvName.setText(model.getLabelName());
            holder.tvName.setSelected(model.isSelected());
        }

        @Override
        public int getItemCount() {
            return listLabel.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }


        public void deleteItem(int position) {
            this.notifyItemRemoved(position);
            this.listLabel.remove(position);
            this.notifyItemRangeChanged(position, getItemCount());
        }

        public void addItem(int position, CourseLabelModel model) {
            this.notifyItemInserted(position);
            this.listLabel.add(position, model);
            this.notifyItemRangeChanged(position, getItemCount());
        }

        public void refreshItem(int position) {
            this.notifyItemChanged(position);
        }

    }

    class LabelHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public LabelHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_pop_course_classify_label_item);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public interface OnItemSelectedListener {
        void onConfirm(CourseClassifyModel selectedClassify, List<CourseLabelModel> selectedLabels);

        void onCancel();
    }
}
