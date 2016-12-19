package com.itzs.testcustomlayoutmanager;

/**
 * 内部资料分类模型
 * Created by zhangshuo on 2016/12/6.
 */
public class CourseClassifyModel {
    private String name;
    private int value;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "CourseClassifyModel{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", isSelected=" + isSelected +
                '}';
    }
}
