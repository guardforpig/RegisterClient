package cn.edu.xmu.oomall.activity.model.bo;


public enum AdvanceSaleStates {
    /**
     * DRAFT活动在草稿状态
     * OFFLINE活动在下线状态
     * ONLINE活动在上线状态
     */
    DRAFT((byte)0,"草稿"),
    OFFLINE((byte)1,"下线"),
    ONLINE((byte)2, "上线");

    private Byte code;
    private String value;
    AdvanceSaleStates(Byte code, String value) {
        this.code=code;
        this.value=value;
    }

    public Byte getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
