package cn.rookiex.redislock.handler.release;

import cn.rookiex.redislock.data.LockInfo;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public interface ReleaseTimeoutHandler {

    /**
     * 释放锁超时处理
     * @param lockInfo 锁信息
     */
    void handle(LockInfo lockInfo);
}
