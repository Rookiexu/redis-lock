package cn.rookiex.redislock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class LockAutoConfigurationTest {
    public static void main(String[] args) {
        SpringApplication.run(LockAutoConfigurationTest.class, args);
    }

}