package cn.rookiex.redislock.core;

import cn.rookiex.redislock.annotation.Lock;
import cn.rookiex.redislock.data.LockFactory;
import cn.rookiex.redislock.data.LockInfo;
import cn.rookiex.redislock.exception.InvocationException;
import cn.rookiex.redislock.lock.RedisLock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
@Aspect
@Component
@Order(0)//加载顺序
public class LockAspectHandler {
    private static final Logger logger = LoggerFactory.getLogger(LockAspectHandler.class);


    private LockFactory lockFactory;

    private LockInfoProvider lockInfoProvider;

    private ThreadLocal<LockRes> currentThreadLock = new ThreadLocal<>();

    @Autowired
    public void setLockFactory(LockFactory lockFactory) {
        this.lockFactory = lockFactory;
    }

    @Autowired
    public void setLockInfoProvider(LockInfoProvider lockInfoProvider) {
        this.lockInfoProvider = lockInfoProvider;
    }

    @Around(value = "@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, lock);
        logger.info("lockInfo ==> " + lockInfo.toString());
        currentThreadLock.set(new LockRes(lockInfo, false));
        RedisLock redisLock = lockFactory.getLock(lockInfo);
        boolean lockRes = redisLock.acquire();

        //如果获取锁失败了，则进入失败的处理逻辑
        if (!lockRes) {
            if (logger.isWarnEnabled()) {
                logger.warn("Timeout while acquiring Lock({})", lockInfo.getName());
            }
            //如果自定义了获取锁失败的处理策略，则执行自定义的降级处理策略
            if (!StringUtils.isEmpty(lock.customLockTimeoutStrategy())) {

                return handleCustomLockTimeout(lock.customLockTimeoutStrategy(), joinPoint);

            } else {
                //否则执行预定义的执行策略
                //注意：如果没有指定预定义的策略，默认的策略为静默啥不做处理
                lock.lockTimeoutStrategy().handle(lockInfo, redisLock, joinPoint);
            }
        }

        currentThreadLock.get().setLock(redisLock);
        currentThreadLock.get().setRes(true);

        return joinPoint.proceed();
    }

    @AfterReturning(value = "@annotation(lock)")
    public void afterReturning(JoinPoint joinPoint, Lock lock) throws Throwable {
        releaseLock(lock, joinPoint);
        cleanUpThreadLocal();
    }

    @AfterThrowing(value = "@annotation(lock)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Lock lock, Throwable ex) throws Throwable {

        releaseLock(lock, joinPoint);
        cleanUpThreadLocal();
        throw ex;
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res = null;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new InvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /**
     * 释放锁
     */
    private void releaseLock(Lock klock, JoinPoint joinPoint) throws Throwable {
        LockRes lockRes = currentThreadLock.get();
        if (lockRes.getRes()) {
            boolean releaseRes = currentThreadLock.get().getLock().release();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {
                handleReleaseTimeout(klock, lockRes.getLockInfo(), joinPoint);
            }
        }
    }


    /**
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(Lock lock, LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {

        if (logger.isWarnEnabled()) {
            logger.warn("Timeout while release Lock({})", lockInfo.getName());
        }

        if (!StringUtils.isEmpty(lock.customReleaseTimeoutStrategy())) {

            handleCustomReleaseTimeout(lock.customReleaseTimeoutStrategy(), joinPoint);

        } else {
            lock.releaseTimeoutStrategy().handle(lockInfo);
        }

    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new InvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private class LockRes {

        private LockInfo lockInfo;
        private RedisLock lock;
        private Boolean res;

        LockRes(LockInfo lockInfo, Boolean res) {
            this.lockInfo = lockInfo;
            this.res = res;
        }

        LockInfo getLockInfo() {
            return lockInfo;
        }

        public RedisLock getLock() {
            return lock;
        }

        public void setLock(RedisLock lock) {
            this.lock = lock;
        }

        Boolean getRes() {
            return res;
        }

        void setRes(Boolean res) {
            this.res = res;
        }

        void setLockInfo(LockInfo lockInfo) {
            this.lockInfo = lockInfo;
        }
    }

    // avoid memory leak
    private void cleanUpThreadLocal() {
        currentThreadLock.remove();
    }
}
