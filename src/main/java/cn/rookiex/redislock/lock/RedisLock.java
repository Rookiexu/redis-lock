package cn.rookiex.redislock.lock;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public interface RedisLock {

    /**
     * 获取锁结果
     *
     * @return 获取锁结果
     */
    boolean acquire();


    /**
     * 释放锁结果
     * @return 释放锁结果
     */
    boolean release();
}
