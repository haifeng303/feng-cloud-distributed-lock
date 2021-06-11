package com.fengzai.distributed.aop;

import com.fengzai.distributed.annotation.DistributeLock;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @PACKAGE_NAME: com.fengzai.distributed.aop
 * @author: rhf
 * @ProjectName: feng-cloud-distributed
 * @description:
 * @DATE: 2021/6/7
 **/
public class DistributedLockAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private Pointcut pointcut;
    private Advice advice;

    public DistributedLockAdvisor(MethodInterceptor methodInterceptor) {
        this.advice = methodInterceptor;
        this.pointcut = buildPointCut();
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    private Pointcut buildPointCut() {
        //param 1 : class annotation ,param 2: method annotation
        AnnotationMatchingPointcut annotationMatchingPointcut = new AnnotationMatchingPointcut(null, DistributeLock.class, true);
        AnnotationMatchingPointcut classMatchingPointcut = AnnotationMatchingPointcut.forMethodAnnotation(DistributeLock.class);
        return new ComposablePointcut(annotationMatchingPointcut).union(classMatchingPointcut);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactory) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
