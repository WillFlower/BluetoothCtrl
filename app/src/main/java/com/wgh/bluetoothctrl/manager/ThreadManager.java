package com.wgh.bluetoothctrl.manager;

import com.wgh.bluetoothctrl.tools.ULog;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by WGH on 2017/4/15.
 */

public class ThreadManager {
    private static ThreadPool mThreadPool;

    public static ThreadPool getInstance() {
        if (mThreadPool == null) {
            synchronized (ThreadManager.class) {
                if (mThreadPool == null) {
                    // Get number of processors.
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    // According to the number of cpu,
                    // calculate the reasonable number of concurrent threads.
                    int threadNum = cpuNum * 2 + 1;
                    ULog.i("cpu num : " + cpuNum);
                    mThreadPool = new ThreadPool(threadNum, threadNum, 0L);
                }
            }
        }
        return mThreadPool;
    }

    public static class ThreadPool {
        private ThreadPoolExecutor mExecutor;
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        private ThreadPool(int corePoolSize, int maximumPoolSize,
                           long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                ULog.e("runnable == null");
                return;
            }
            if (mExecutor == null) {
                mExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                        TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(),
                        Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy()
                );
            }
            mExecutor.execute(runnable);
        }

        public void cancel(Runnable runnable) {
            if (mExecutor != null) {
                mExecutor.getQueue().remove(runnable);
            }
        }

        public void stop(Runnable runnable) {
            if (mExecutor != null) {
                mExecutor.remove(runnable);
            }
        }
    }
}
