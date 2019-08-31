package cn.rookiex.redislock.enums;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public enum LockType {
    /**
     * 可重入锁
     */
    Reentrant,
    /**
     * 公平锁
     */
    Fair,
    /**
     * 读锁
     */
    Read,
    /**
     * 写锁
     */
    Write;

    LockType() {
    }
}
