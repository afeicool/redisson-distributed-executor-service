package vip.afei.redisson.distributed.executorservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.executor.TaskFailureListener;
import org.redisson.api.executor.TaskFinishedListener;
import org.redisson.api.executor.TaskStartedListener;
import org.redisson.api.executor.TaskSuccessListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class RedissonDistributedExecutorServiceApplication implements CommandLineRunner {
    private final RedissonClient redisson;

    public static void main(String[] args) {
        SpringApplication.run(RedissonDistributedExecutorServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (Objects.equals("worker", args[0])) {
            startWorks();
        } else if (Objects.equals("producer", args[0])) {
            startProducer();
        }

    }

    private void startProducer() throws InterruptedException, ExecutionException {
        ExecutorOptions options = executorOptions();
        RExecutorService executorService = redisson.getExecutorService("myExecutor", options);

        List<Callable<Long>> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(new CallableTask());
        }


        List<Future<Long>> futures = executorService.invokeAll(tasks);


        for (Future<Long> future : futures) {
            RExecutorFuture<Long> rFuture = (RExecutorFuture<Long>) future;
            log.info("taskId - {}: result is {}", rFuture.getTaskId(), rFuture.get());
        }


    }

    private ExecutorOptions executorOptions() {
        ExecutorOptions options = ExecutorOptions.defaults();
        options.taskRetryInterval(10, TimeUnit.MINUTES);
        return options;
    }

    private void startWorks() {
        // 初始化计数器
        RAtomicLong atomic = redisson.getAtomicLong("myAtomic");
        atomic.set(0);

        WorkerOptions options = getWorkerOptions();
        RExecutorService executor = redisson.getExecutorService("myExecutor");
        executor.registerWorkers(options);
    }

    private WorkerOptions getWorkerOptions() {
        return WorkerOptions.defaults()
                .workers(2)
                .taskTimeout(60, TimeUnit.SECONDS)
                .addListener(new TaskSuccessListener() {
                    @Override
                    public <T> void onSucceeded(String s, T t) {
                        log.info("taskId - {}: result is {}", s, t);
                    }
                })
                .addListener((TaskFailureListener) (s, throwable) -> log.error("taskId - {}: error is {}", s, throwable.getMessage()))
                .addListener((TaskStartedListener) s -> log.info("taskId - {}: started", s))
                .addListener((TaskFinishedListener) s -> log.info("taskId - {}: finished", s));
    }
}
