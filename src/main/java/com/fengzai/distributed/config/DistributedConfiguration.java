package com.fengzai.distributed.config;

import com.fengzai.distributed.aop.DistributedLockAdvisor;
import com.fengzai.distributed.aop.DistributedLockInterceptor;
import com.fengzai.distributed.client.DistributedLockSelector;
import com.fengzai.distributed.client.ZookeeperClientBuilder;
import com.fengzai.distributed.listener.AbstractChildrenListener;
import com.fengzai.distributed.listener.ZkClientChildrenListener;
import com.fengzai.distributed.lock.DistributeLock;
import com.fengzai.distributed.lock.RedisLock;
import com.fengzai.distributed.lock.ZookeeperLock;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.config
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
@EnableConfigurationProperties({DistributeLockProperties.class})
public class DistributedConfiguration {
    @Autowired
    private DistributeLockProperties distributeLockProperties;

    @Bean(name = "redisLock")
    @ConditionalOnProperty(prefix = "lock.redis", name = "enable", havingValue = "true")
    @DependsOn("redisTemplate")
    public DistributeLock redisLock() {
        return new RedisLock();
    }

    @Bean(name = "zkClient")
    @ConditionalOnProperty(prefix = "lock.zk", name = "urls")
    public ZkClient zkClient() {
        AbstractChildrenListener zkClientDataListener = new ZkClientChildrenListener();

        return ZookeeperClientBuilder.build(zkClientDataListener, distributeLockProperties);
    }

    @Bean(name = "zookeeperLock")
    @ConditionalOnBean(ZkClient.class)
    @DependsOn({"zkClient"})
    public DistributeLock zookeeperLock() {
        return new ZookeeperLock();
    }

    @Bean
    @DependsOn({"zookeeperLock", "redisLock"})
    public DistributedLockSelector distributedLockSelector() {
        return new DistributedLockSelector();
    }

    @Bean
    public DistributedLockAdvisor distributedLockAdvisor(@Qualifier("distributedLockSelector") DistributedLockSelector distributedLockSelector) {
        DistributedLockInterceptor distributedLockInterceptor = new DistributedLockInterceptor(distributedLockSelector, distributeLockProperties);
        return new DistributedLockAdvisor(distributedLockInterceptor);
    }
}
