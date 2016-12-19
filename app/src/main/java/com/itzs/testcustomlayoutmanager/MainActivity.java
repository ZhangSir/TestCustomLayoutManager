package com.itzs.testcustomlayoutmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CourseClassifyPopWindow popWindowClassify;
    private TextView tvFilterClassify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFilterClassify = (TextView) findViewById(R.id.tv_internal_data_filter_classify);
        popWindowClassify = new CourseClassifyPopWindow(this);
        popWindowClassify.createPopWindow();

        tvFilterClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvFilterClassify.isSelected()) {
                    //关闭菜单
                    popWindowClassify.hidePopWindow();
                    tvFilterClassify.setSelected(false);
                } else {
                    //打开菜单
                    popWindowClassify.showPopWindow((View) tvFilterClassify.getParent().getParent());
                    tvFilterClassify.setSelected(true);
                }
            }
        });
        popWindowClassify.setOnItemSelectedListener(new CourseClassifyPopWindow.OnItemSelectedListener() {
            @Override
            public void onConfirm(CourseClassifyModel selectedClassify, List<CourseLabelModel> selectedLabels) {
                //选中某个条件
                tvFilterClassify.setSelected(false);
            }

            @Override
            public void onCancel() {
                //取消
                tvFilterClassify.setSelected(false);
            }
        });
    }
}
