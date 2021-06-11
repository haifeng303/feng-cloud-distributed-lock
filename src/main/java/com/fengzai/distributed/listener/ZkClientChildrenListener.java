package com.fengzai.distributed.listener;

import com.fengzai.distributed.config.DistributeLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.listener
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
@Slf4j
public class ZkClientChildrenListener extends AbstractChildrenListener {

    public void handlerDeletedProcess(String s) throws Exception {
        Map<String, CountDownLatch> waitMap = DistributeLockProperties.waitMap;
        //an empty set when initialized
        if (!CollectionUtils.isEmpty(waitMap)) {
            List<String> children = getZkClient().getChildren(getParentNode());
            //no other nodes are not processed
            if (!CollectionUtils.isEmpty(children)) {
                Collections.sort(children);
                String firstNode = children.get(0);
                //not delete node timeout
                if (s.compareTo(firstNode) < 0) {
                    CountDownLatch countDownLatch = waitMap.get(firstNode);
                    if (null != countDownLatch) {
                        countDownLatch.countDown();
                        waitMap.remove(firstNode);
                    }
                }
            }

        }
    }

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        log.info("node change is：currentChildrenList：" + currentChilds);
    }
}
