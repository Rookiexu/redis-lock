package cn.rookiex.redislock.lock;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public interface RedisLock {
    boolean acquire();

    boolean release();
}
