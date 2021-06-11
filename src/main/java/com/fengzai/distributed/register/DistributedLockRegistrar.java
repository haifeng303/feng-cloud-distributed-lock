package com.fengzai.distributed.register;


import com.fengzai.distributed.config.DistributedConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.register
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
public class DistributedLockRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
        RootBeanDefinition beanDefinition = new RootBeanDefinition(DistributedConfiguration.class);
        registry.registerBeanDefinition("init distributed lock plugin >>>", beanDefinition);
    }
}
