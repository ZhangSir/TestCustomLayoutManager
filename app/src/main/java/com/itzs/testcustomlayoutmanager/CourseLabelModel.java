package com.itzs.testcustomlayoutmanager;

/**
 * 内部资料标签模型
 * Created by zhangshuo on 2016/12/6.
 */
public class CourseLabelModel {
    private int id;
    private String uuid;
    private String labelName;
    private long createTime;
    private long modifyTime;
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "CourseLabelModel{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", labelName='" + labelName + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", isSelected=" + isSelected +
                '}';
    }
}
