package com.fengzai.distributed.lock;

import com.fengzai.distributed.constant.LockType;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.lock
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
public interface DistributeLock {
    /**
     * @param nodeName
     * @param timeout
     * @return java.lang.String
     * @description 加锁
     * @date 2021/6/5
     */
    String lock(String nodeName, Long timeout);

    /**
     * @param nodeName
     * @return boolean
     * @description 解锁
     * @date 2021/6/5
     */
    boolean unlock(String nodeName);

    /**
     * @return com.fengzai.distributed.constant.LockType
     * @description 获取锁类型
     * @date 2021/6/7
     */
    LockType getType();
}
