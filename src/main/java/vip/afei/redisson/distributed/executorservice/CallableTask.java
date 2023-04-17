package vip.afei.redisson.distributed.executorservice;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.util.concurrent.Callable;

public class CallableTask implements Callable<Long> {

    @RInject
    private RedissonClient redissonClient;

    @RInject
    private String taskId;

    @Override
    public Long call() throws Exception {
        RAtomicLong atomic = redissonClient.getAtomicLong("myAtomic");
        return atomic.addAndGet(1);
    }

}
