package cn.rookiex.redislock.enums;

import cn.rookiex.redislock.data.LockInfo;
import cn.rookiex.redislock.exception.TimeoutException;
import cn.rookiex.redislock.handler.lock.LockTimeoutHandler;
import cn.rookiex.redislock.lock.RedisLock;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.TimeUnit;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public enum LockTimeoutStrategy implements LockTimeoutHandler {
    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, RedisLock redisLock, JoinPoint joinPoint) {
            // do nothing
        }
    },

    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo, RedisLock redisLock, JoinPoint joinPoint) {

            String errorMsg = String.format("Failed to acquire RedisLock(%s) with timeout(%ds)", lockInfo.getName(), lockInfo.getWaitTime());
            throw new TimeoutException(errorMsg);
        }
    },

    /**
     * 一直阻塞，直到获得锁，在太多的尝试后，仍会报错
     */
    KEEP_ACQUIRE() {

        private static final long DEFAULT_INTERVAL = 100L;

        private static final long DEFAULT_MAX_INTERVAL = 3 * 60 * 1000L;

        @Override
        public void handle(LockInfo lockInfo, RedisLock redisLock, JoinPoint joinPoint) {

            long interval = DEFAULT_INTERVAL;

            while(!redisLock.acquire()) {

                if(interval > DEFAULT_MAX_INTERVAL) {
                    String errorMsg = String.format("Failed to acquire RedisLock(%s) after too many times, this may because dead redislock occurs.",
                        lockInfo.getName());
                    throw new TimeoutException(errorMsg);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    interval <<= 1;
                } catch (InterruptedException e) {
                    throw new TimeoutException("Failed to acquire RedisLock", e);
                }
            }
        }
    }
}
