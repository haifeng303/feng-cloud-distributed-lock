package com.fengzai.distributed.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @PACKAGE_NAME: com.fengzai.distributed
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
@Data
@ConfigurationProperties(prefix = "lock")
public class DistributeLockProperties {

    /**
     * root object
     */
    @Value("${lock.parentNode}")
    private String parentNode;

    /**
     * used lua script
     */
    @Value("${lock.redis.openLuaScript}")
    private Boolean openLuaScript;

    /**
     * redis key time expire
     */
    @Value("${lock.redis.expire}")
    private Integer redisExpire = 10000;


    /**
     * zk集群地址，等多个用','隔开
     * 例如192.168.30.101:2181,192.168.30.102:2181
     */
    @Value("${lock.zk.urls}")
    private String zkUrl;
    /**
     * zk链接超时时间
     */
    @Value("${lock.zk.timeOut}")
    private Integer timeOut = 6000;


    /**
     * cache local event
     */
    public static Map<String, CountDownLatch> waitMap = new ConcurrentHashMap<>();
}
