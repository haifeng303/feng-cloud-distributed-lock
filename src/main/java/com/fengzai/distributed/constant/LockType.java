package com.fengzai.distributed.constant;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.constant
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
public enum LockType {
    REDIS_LOCK(0, "redis lock"),
    ZOOKEEPER_LOCK(1, "zookeeper lock");
    private int value;
    private String label;

    LockType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
