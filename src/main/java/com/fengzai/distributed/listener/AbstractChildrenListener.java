package com.fengzai.distributed.listener;

import lombok.Data;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.listener
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
@Data
public abstract class AbstractChildrenListener implements IZkChildListener {
    private ZkClient zkClient;
    private String parentNode;

    public AbstractChildrenListener() {
    }

    public AbstractChildrenListener(ZkClient zkClient, String parentNode) {
        this.zkClient = zkClient;
        this.parentNode = parentNode;
    }
}
