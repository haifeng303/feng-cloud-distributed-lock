package com.fengzai.distributed.client;

import com.fengzai.distributed.config.DistributeLockProperties;
import com.fengzai.distributed.listener.AbstractChildrenListener;
import lombok.Setter;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.lock
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
@Setter
public class ZookeeperClientBuilder {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperClientBuilder.class);

    private ZookeeperClientBuilder() {
    }

    public static ZkClient build(AbstractChildrenListener listener, DistributeLockProperties distributeLockProperties) {
        ZkClient zkClient = null;
        try {
            zkClient = new ZkClient(distributeLockProperties.getZkUrl(), distributeLockProperties.getTimeOut());
        } catch (Exception e) {
            log.error("create bean : zkClient, error!!, error msg ->: {}", e);
        }
        if (null != zkClient) {
            boolean exists = zkClient.exists(File.separator + distributeLockProperties.getParentNode());
            //根节点创建为持久节点
            if (!exists) {
                zkClient.createPersistent(File.separator + distributeLockProperties.getParentNode());
            }
        }
        //添加节点修改或删除事件
        listener.setZkClient(zkClient);
        listener.setParentNode(File.separator + distributeLockProperties.getParentNode());
        zkClient.subscribeChildChanges(File.separator + distributeLockProperties.getParentNode(), listener);
        return zkClient;
    }

}
