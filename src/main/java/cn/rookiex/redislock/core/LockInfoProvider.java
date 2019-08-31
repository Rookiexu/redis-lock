package cn.rookiex.redislock.core;

import cn.rookiex.redislock.annotation.Lock;
import cn.rookiex.redislock.data.LockConfig;
import cn.rookiex.redislock.data.LockInfo;
import cn.rookiex.redislock.enums.LockType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public class LockInfoProvider {
    private static final String LOCK_NAME_PREFIX = "lock";
    private static final String LOCK_NAME_SEPARATOR = ".";


    @Autowired
    private LockConfig lockConfig;

    @Autowired
    private BusinessKeyProvider businessKeyProvider;

    private static final Logger logger = LoggerFactory.getLogger(LockInfoProvider.class);

    LockInfo get(ProceedingJoinPoint joinPoint, Lock lock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LockType type= lock.lockType();
        String businessKeyName=businessKeyProvider.getKeyName(joinPoint,lock);
        //锁的名字，锁的粒度就是这里控制的
        String lockName = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(lock.name(), signature) + businessKeyName;
        long waitTime = getWaitTime(lock);
        long leaseTime = getLeaseTime(lock);
        //如果占用锁的时间设计不合理，则打印相应的警告提示
        if(leaseTime == -1 && logger.isWarnEnabled()) {
            logger.warn("Trying to acquire RedisLock({}) with no expiration, " +
                "Klock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                "This may cause dead lock in some circumstances.", lockName);
        }
        return new LockInfo(type,lockName,waitTime,leaseTime);
    }

    /**
     * 获取锁的name，如果没有指定，则按全类名拼接方法名处理
     * @param annotationName
     * @param signature
     * @return
     */
    private String getName(String annotationName, MethodSignature signature) {
        if (annotationName.isEmpty()) {
            return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        } else {
            return annotationName;
        }
    }


    private long getWaitTime(Lock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
            lockConfig.getWaitTime() : lock.waitTime();
    }

    private long getLeaseTime(Lock lock) {
        return lock.leaseTime() == Long.MIN_VALUE ?
            lockConfig.getLeaseTime() : lock.leaseTime();
    }
}
