package cn.rookiex.redislock.lock;

import cn.rookiex.redislock.data.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public class ReentrantLock implements RedisLock {
    private RLock rLock;

    private final LockInfo lockInfo;

    private RedissonClient redissonClient;

    public ReentrantLock(RedissonClient redissonClient,LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }
    @Override
    public boolean acquire() {
        try {
            rLock = redissonClient.getLock(lockInfo.getName());
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release() {
        if(rLock.isHeldByCurrentThread()){
            try {
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }
        return false;
    }
    public String getKey(){
        return this.lockInfo.getName();
    }
}
