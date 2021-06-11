package com.fengzai.distributed.annotation;

import com.fengzai.distributed.register.DistributedLockRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.annotation
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Import(value = {DistributedLockRegistrar.class})
public @interface EnableLock {
}
