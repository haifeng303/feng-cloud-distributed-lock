package com.fengzai.distributed.annotation;

import com.fengzai.distributed.constant.LockType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.annotation
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/5
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributeLock {
    /**
     * lock type
     * default used redis lock
     */
    LockType type() default LockType.REDIS_LOCK;

    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

    /**
     * wait time
     */
    long waitTime() default 0;

}
