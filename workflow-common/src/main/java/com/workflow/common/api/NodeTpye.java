package com.workflow.common.api;

/**
 * 节点类型枚举类
 * auther wanli on 2020/7/16
 */
public enum NodeTpye {
    SENDER(0,"发起人"),
    APPROVAL(1,"审核人"),
    COPYER(2,"抄送人"),
    CONDETION(3,"条件"),
    ROUTE(4,"路由");

    private int type;
    private String desc;
    NodeTpye(int type,String desc){
        this.type=type;
        this.desc=desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
