package cn.rookiex.redislock;

import cn.rookiex.redislock.core.BusinessKeyProvider;
import cn.rookiex.redislock.core.LockAspectHandler;
import cn.rookiex.redislock.core.LockInfoProvider;
import cn.rookiex.redislock.data.LockConfig;
import cn.rookiex.redislock.data.LockFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
@Configuration
@ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(LockConfig.class)
@Import(LockAspectHandler.class)
public class LockAutoConfiguration {

    private LockConfig lockConfig;

    @Autowired
    public void setLockConfig(LockConfig lockConfig) {
        this.lockConfig = lockConfig;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    RedissonClient redisson() throws Exception {
        Config config = new Config();
        if(lockConfig.getClusterServer()!=null){
            config.useClusterServers().setPassword(lockConfig.getPassword())
                .addNodeAddress(lockConfig.getClusterServer().getNodeAddresses());
        }else {
            config.useSingleServer().setAddress(lockConfig.getAddress())
                .setDatabase(lockConfig.getDatabase())
                .setPassword(lockConfig.getPassword());
        }
        Codec codec=(Codec) ClassUtils.forName(lockConfig.getCodec(),ClassUtils.getDefaultClassLoader()).newInstance();
        config.setCodec(codec);
        config.setEventLoopGroup(new NioEventLoopGroup());
        return Redisson.create(config);
    }

    @Bean
    public LockInfoProvider lockInfoProvider(){
        return new LockInfoProvider();
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider(){
        return new BusinessKeyProvider();
    }

    @Bean
    public LockFactory lockFactory(){
        return new LockFactory();
    }

//    @Bean
//    public LockAspectHandler lockAspectHandler(){
//        return new LockAspectHandler();
//    }

}
