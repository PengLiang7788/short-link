package com.example.shortlink.app.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;


/**
 * @author 彭亮
 * @create 2023-01-18 15:21
 */
@Slf4j
public class FutureTest {

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Future<String> future = executorService.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "异步线程测试";
        });

        // 轮询获取结果
        while (true){
            if (future.isDone()){
                System.out.println(future.get());
                break;
            }
        }
    }

    @Test
    public void testFuture1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
            }
            return "短链平台";
        });

        System.out.println("future:"+future.get());
    }


}
