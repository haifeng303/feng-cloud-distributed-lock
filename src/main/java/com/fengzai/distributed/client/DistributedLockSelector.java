package com.fengzai.distributed.client;

import com.fengzai.distributed.constant.LockType;
import com.fengzai.distributed.lock.DistributeLock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.client
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
public class DistributedLockSelector implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    public Map<LockType, DistributeLock> distributeLockMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public DistributeLock getLock(LockType lockType) {
        return distributeLockMap.get(lockType);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DistributeLock> beansOfType = applicationContext.getBeansOfType(DistributeLock.class);
        for (DistributeLock distributeLock : beansOfType.values()) {
            distributeLockMap.put(distributeLock.getType(), distributeLock);
        }
    }
}
