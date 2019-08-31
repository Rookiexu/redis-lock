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
    void handle(LockInfo lockInfo, RedisLock redisLock, JoinPoint joinPoint);
}
