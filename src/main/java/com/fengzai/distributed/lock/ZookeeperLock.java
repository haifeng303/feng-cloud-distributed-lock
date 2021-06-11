package com.fengzai.distributed.lock;

import com.fengzai.distributed.config.DistributeLockProperties;
import com.fengzai.distributed.constant.LockType;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.lock
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
@Slf4j
public class ZookeeperLock implements DistributeLock, BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;

    private ZkClient zkClient;

    private DistributeLockProperties distributeLockProperties;


    @Override
    public String lock(String nodePrefix, Long timeout) {
        try {
            return tryLock(File.separator + distributeLockProperties.getParentNode() + File.separator + nodePrefix, timeout);
        } catch (Exception e) {
            log.error("lock acquisition failed， error msg ->: {}", e);
        }
        return null;
    }

    @Override
    public boolean unlock(String nodeName) {
        log.info("delete current node, the name is {}", nodeName);
        Map<String, CountDownLatch> waitMap = DistributeLockProperties.waitMap;
        List<String> children = zkClient.getChildren(File.separator + distributeLockProperties.getParentNode());
        if (children.size() > 1) {
            Collections.sort(children);
            int next = Collections.binarySearch(children, nodeName) + 1;
            String nextNode = children.get(next);
            CountDownLatch countDownLatch = waitMap.get(nextNode);
            if (null != countDownLatch) {
                countDownLatch.countDown();
                waitMap.remove(nextNode);
            }
        }
        return zkClient.delete(nodeName);
    }

    @Override
    public LockType getType() {
        return LockType.ZOOKEEPER_LOCK;
    }

    /**
     * @param nodePrefix
     * @param timeout
     * @return java.lang.String
     * @description attempts to acquire the lock,
     * returns the node name if the lock is obtained, or null if not
     * @date 2021/6/5
     */
    private String tryLock(String nodePrefix, Long timeout) throws InterruptedException {
        String currentNode = zkClient.createEphemeralSequential(nodePrefix, "临时节点");

        log.info("current thread name is: " + Thread.currentThread().getName() + " , current node is: " + currentNode);
        // If the current node is the smallest node, the lock is acquired successfully
        if (compareNode(currentNode)) {
            return currentNode;
        }
        //wait
        return waitLock(currentNode, timeout);
    }

    /**
     * @param currentNode
     * @return boolean
     * @description compares whether the current node is the smallest
     * @date 2021/6/5
     */
    private synchronized boolean compareNode(String currentNode) {
        List<String> children = zkClient.getChildren(File.separator + distributeLockProperties.getParentNode());
        //sort
        Collections.sort(children);
        if (currentNode.equals(File.separator + distributeLockProperties.getParentNode() + File.separator + children.get(0))) {
            return true;
        }
        return false;
    }

    /**
     * @param currentNode
     * @param timeout
     * @return java.lang.String
     * @description wait for the specified time. Timeout returns null
     * @date 2021/6/5
     */
    private String waitLock(String currentNode, Long timeout) throws InterruptedException {
        Map<String, CountDownLatch> waitMap = DistributeLockProperties.waitMap;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        waitMap.put(currentNode, countDownLatch);
        countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        if (compareNode(currentNode)) {
            return currentNode;
        }
        log.info("timeout delete current node, the name is {}", currentNode);
        zkClient.delete(currentNode);
        countDownLatch = null;
        waitMap.remove(currentNode);
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zkClient = beanFactory.getBean(ZkClient.class);
        distributeLockProperties = beanFactory.getBean(DistributeLockProperties.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
