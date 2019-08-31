package cn.rookiex.redislock.enums;

import cn.rookiex.redislock.data.LockInfo;
import cn.rookiex.redislock.exception.TimeoutException;
import cn.rookiex.redislock.handler.release.ReleaseTimeoutHandler;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public enum ReleaseTimeoutStrategy implements ReleaseTimeoutHandler{
    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo) {

            String errorMsg = String.format("Found RedisLock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            throw new TimeoutException(errorMsg);
        }
    }
}
