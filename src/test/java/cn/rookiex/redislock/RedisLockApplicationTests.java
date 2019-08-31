package cn.rookiex.redislock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LockAutoConfigurationTest.class)
public class RedisLockApplicationTests {

    private TestService testService;

    @Autowired
    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    @Test
	public void contextLoads() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.setValue("sleep");
                System.err.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + new Date().toLocaleString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        executorService.awaitTermination(30, TimeUnit.SECONDS);
	}


    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm1()throws Exception{
        String result=testService.getValue("user1",1);
        Assert.assertEquals(result,"success");
    }

    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm2()throws Exception{
        String result=testService.getValue("user1","----");
        Assert.assertEquals(result,"success");
    }

    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm4()throws Exception{
        String result=testService.getValue(new User(3,"kl"));
        Assert.assertEquals(result,"success");
    }

}