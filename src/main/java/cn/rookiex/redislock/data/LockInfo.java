package cn.rookiex.redislock.data;

import cn.rookiex.redislock.enums.LockType;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public class LockInfo {
    private LockType type;
    private String name;
    private long waitTime;
    private long leaseTime;

    public LockInfo() {
    }

    public LockInfo(LockType type, String name, long waitTime, long leaseTime) {
        this.type = type;
        this.name = name;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public LockType getType() {
        return type;
    }

    public void setType(LockType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LockInfo{" +
            "type=" + type +
            ", name='" + name + '\'' +
            ", waitTime=" + waitTime +
            ", leaseTime=" + leaseTime +
            '}';
    }
}
