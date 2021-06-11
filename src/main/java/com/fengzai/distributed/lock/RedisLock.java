package com.fengzai.distributed.lock;

import com.fengzai.distributed.config.DistributeLockProperties;
import com.fengzai.distributed.constant.LockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
public class RedisLock implements DistributeLock, BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;

    private DistributeLockProperties distributeLockProperties;

    private RedisTemplate redisTemplate;


    @Override
    public String lock(String nodeName, Long timeout) {
        String key = distributeLockProperties.getParentNode() + ":" + nodeName;
        if (tryLock(nodeName, distributeLockProperties.getRedisExpire())) {
            log.info("create redis lock ，lock name is ->: {}", key);
            return key;
        } else {
            try {
                if (waitLock(key, timeout)) {
                    return key;
                }
            } catch (InterruptedException e) {
                log.error("redis lock wait error, the error msg is ->: {}", e);
            }
        }
        log.info("create redis lock error ，the lock is occupied");

        return null;
    }

    private boolean tryLock(String key, Integer redisExpire) {
        long threadId = Thread.currentThread().getId();
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, threadId, redisExpire, TimeUnit.MILLISECONDS);
        return flag;
    }

    private synchronized boolean waitLock(String key, Long timeout) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        Map<String, CountDownLatch> waitMap = DistributeLockProperties.waitMap;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        waitMap.put("" + threadId, countDownLatch);
        countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        if (tryLock(key, distributeLockProperties.getRedisExpire())) {
            return true;
        }
        log.info("timeout delete current node, the name is {}", key);
        countDownLatch = null;
        waitMap.remove(threadId);
        return false;
    }

    @Override
    public boolean unlock(String nodeName) {
        String key = distributeLockProperties.getParentNode() + ":" + nodeName;
        log.info("delete lock key is ->: {}", key);
        if (distributeLockProperties.getOpenLuaScript()) {
            long threadId = Thread.currentThread().getId();

            String luaScript = "local in = ARGV[1] local curr=redis.call('get',KEYS[1]) " +
                    "if in ==curr then redis.call('del', KEYS[1]) end return 'OK'";
            RedisScript redisScript = RedisScript.of(luaScript);
            Object execute = redisTemplate.execute(redisScript, Collections.singletonList(key), Collections.singleton(threadId));
            if ("OK".equals(execute)) {
                nextTx();
                log.info("used lua script delete lock success -----");
                return true;
            }
        } else {
            nextTx();
            return redisTemplate.delete(key);
        }
        log.info("delete lock failure -----");
        return false;
    }

    @Override
    public LockType getType() {
        return LockType.REDIS_LOCK;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        distributeLockProperties = beanFactory.getBean(DistributeLockProperties.class);
        redisTemplate = beanFactory.getBean("redisTemplate", RedisTemplate.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * @return void
     * @description begin the next transaction logic
     * @date 2021/6/9
     */
    private void nextTx() {
        Map<String, CountDownLatch> waitMap = DistributeLockProperties.waitMap;
        if (!CollectionUtils.isEmpty(waitMap)) {
            Set<String> waitThreads = waitMap.keySet();
            ArrayList<String> var_1 = new ArrayList<>(waitThreads);
            Collections.sort(var_1);
            CountDownLatch countDownLatch = waitMap.get(var_1.get(0));
            if (null != countDownLatch) {
                countDownLatch.countDown();
                waitMap.remove(var_1.get(0));
            }

        }


    }
}
