package cn.rookiex.redislock.handler.release;

import cn.rookiex.redislock.data.LockInfo;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public interface ReleaseTimeoutHandler {
    void handle(LockInfo lockInfo);
}
