package cn.rookiex.redislock.handler.lock;

import cn.rookiex.redislock.data.LockInfo;
import cn.rookiex.redislock.lock.RedisLock;
import org.aspectj.lang.JoinPoint;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public interface LockTimeoutHandler {

    /**
     * 处理超时情况下的业务逻辑
     *
     * @param lockInfo  锁信息
     * @param redisLock 锁
     * @param joinPoint 切入点
     */
    void handle(LockInfo lockInfo, RedisLock redisLock, JoinPoint joinPoint);
}
