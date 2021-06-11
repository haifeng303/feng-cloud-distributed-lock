package com.fengzai.distributed.aop;

import com.fengzai.common.exception.CommonException;
import com.fengzai.common.res.GlobalResponseEnum;
import com.fengzai.distributed.annotation.DistributeLock;
import com.fengzai.distributed.client.DistributedLockSelector;
import com.fengzai.distributed.config.DistributeLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.aop
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
@Slf4j
public class DistributedLockInterceptor implements MethodInterceptor {

    private DistributedLockSelector distributedLockSelector;
    private DistributeLockProperties distributeLockProperties;

    public DistributedLockInterceptor(DistributedLockSelector distributedLockSelector
            , DistributeLockProperties distributeLockProperties) {
        this.distributedLockSelector = distributedLockSelector;
        this.distributeLockProperties = distributeLockProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        DistributeLock distributeLock = method.getDeclaredAnnotation(DistributeLock.class);
        com.fengzai.distributed.lock.DistributeLock lock = distributedLockSelector.getLock(distributeLock.type());
        if (null == lock) {
            log.info(String.format("not config distribute lock, should config lock type is : %s", distributeLock.type().getLabel()));
            throw new CommonException(GlobalResponseEnum.CONFIG_ERROR);
        }
        String lockPath = null;
        try {
            lockPath = lock.lock(distributeLock.name(), distributeLock.waitTime());
            if (null != lockPath) {
                return invocation.proceed();
            }
        } catch (Throwable throwable) {
            log.error("error executing business logic, the error ->: {}", throwable);
        } finally {
            if (null != lockPath) {
                lock.unlock(lockPath);
            }
        }

        throw new CommonException(GlobalResponseEnum.SYS_ERROR);

    }
}
