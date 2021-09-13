package com.example.uhealth.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {
    private static CachedThreadPool poolManager = null;

    private ExecutorService executorService;

    // private constructor
    private CachedThreadPool(){
        executorService = Executors.newCachedThreadPool();
    }

    public static CachedThreadPool getInstance() {
        if (poolManager == null){
            poolManager = new CachedThreadPool();
        }
        return poolManager;
    }

    public void add_run(Runnable task){
        executorService.execute(task);
    }
}
