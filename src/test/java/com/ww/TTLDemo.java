package com.ww;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class TTLDemo {


    // 需要注意的是，使用TTL的时候，要想传递的值不出问题，线程池必须得用TTL加一层代理（下面会讲这样做的目的）
    private static ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));


    private static ThreadLocal ttl = new TransmittableThreadLocal<>(); //这里采用TTL的实现


    private static ThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();

    @Test
    public void test() {
        new Thread(() -> {

            String mainThreadName = "main_01";

            ttl.set(1);

            executorService.execute(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之前(1), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            });

            executorService.execute(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之前(1), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            });

            executorService.execute(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之前(1), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            });

            sleep(10L); //确保上面的会在tl.set执行之前执行

            ttl.set(2); // 等上面的线程池第一次启用完了，父线程再给自己赋值

            executorService.execute(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之后(2), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            });

            executorService.execute(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之后(2), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            });

            executorService.execute(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之后(2), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            });

            System.out.println(String.format("线程名称-%s, 变量值=%s", Thread.currentThread().getName(), ttl.get()));

        }).start();

        sleep(1000L);

    }


    @Test
    public void t2() {
        new Thread(() -> {

            String mainThreadName = "main_01";

            ttl.set(1);

            new Thread(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之前(1), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            }).start();

            new Thread(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之前(1), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            }).start();

            new Thread(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之前(1), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            }).start();

            sleep(10L); //确保上面的会在tl.set执行之前执行

            ttl.set(2); // 等上面的线程池第一次启用完了，父线程再给自己赋值

            new Thread(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之后(2), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            }).start();

            new Thread(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之后(2), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            }).start();

            new Thread(() -> {
                sleep(1L);
                System.out.println(String.format("本地变量改变之后(2), 父线程名称-%s, 子线程名称-%s, 变量值=%s", mainThreadName, Thread.currentThread().getName(), ttl.get()));
            }).start();

            System.out.println(String.format("线程名称-%s, 变量值=%s", Thread.currentThread().getName(), ttl.get()));

        }).start();

        sleep(1000L);
    }


    @Test
    public void t3() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 3, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

//        ExecutorService threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor1);

        transmittableThreadLocal.set("父线程设定的值");
        System.out.println(Thread.currentThread().getName() + "线程====>" + transmittableThreadLocal.get());

        threadPoolExecutor.execute(() -> {
            sleep(1L);
            System.out.println(Thread.currentThread().getName() + "------" + transmittableThreadLocal.get());
        });

        threadPoolExecutor.execute(() -> {
            sleep(1L);
            System.out.println(Thread.currentThread().getName() + "------" + transmittableThreadLocal.get());
        });


        sleep(10L); //确保上面的会在tl.set执行之前执行

//        threadPoolExecutor.execute(() -> {
//            sleep(1L);
//            System.out.println(Thread.currentThread().getName() + "线程====>修改父线程设定的值");
//            transmittableThreadLocal.set("修改父线程设定的值");
//        });

        System.out.println("main线程修改父线程设定的值");
        transmittableThreadLocal.set("修改父线程设定的值");

        threadPoolExecutor.execute(() -> {
            sleep(1L);
            System.out.println(Thread.currentThread().getName() + "------" + transmittableThreadLocal.get());
        });
        threadPoolExecutor.execute(() -> {
            sleep(1L);
            System.out.println(Thread.currentThread().getName() + "------" + transmittableThreadLocal.get());
        });
        threadPoolExecutor.execute(() -> {
            sleep(1L);
            System.out.println(Thread.currentThread().getName() + "------" + transmittableThreadLocal.get());
        });

//        System.out.println(Thread.currentThread().getName() + "线程====>" + transmittableThreadLocal.get());

        sleep(1000L);

    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
