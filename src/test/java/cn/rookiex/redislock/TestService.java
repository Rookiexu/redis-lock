package cn.rookiex.redislock;

import cn.rookiex.redislock.annotation.Lock;
import cn.rookiex.redislock.annotation.LockKey;
import cn.rookiex.redislock.enums.LockTimeoutStrategy;
import org.springframework.stereotype.Component;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
@Component
public class TestService {
    @Lock(keys = {"#param"}, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    public String getValue(String param) throws Exception {
        //  if ("sleep".equals(param)) {//线程休眠或者断点阻塞，达到一直占用锁的测试效果
        Thread.sleep(1000 * 3);
        //}
        return "success";
    }

    @Lock(keys = {"#param"}, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    public String setValue(String param) throws Exception {
        //  if ("sleep".equals(param)) {//线程休眠或者断点阻塞，达到一直占用锁的测试效果
        Thread.sleep(1000 * 3);
        //}
        return "success";
    }

    @Lock(keys = {"#userId"})
    public String getValue(String userId, @LockKey int id) throws Exception {
        Thread.sleep(60 * 1000);
        return "success";
    }

    @Lock(keys = {"#userId"})
    public String setValue(String userId, @LockKey int id) throws Exception {
        Thread.sleep(60 * 1000);
        return "success";
    }

    @Lock(keys = {"#userId"})
    public String getValue(String userId,  String id) throws Exception {
        Thread.sleep(60 * 1000);
        return "success";
    }

    @Lock(keys = {"#user.name", "#user.id", "#user.age"})
    public String getValue(User user) throws Exception {
        Thread.sleep(60 * 1000);
        return "success";
    }
}
