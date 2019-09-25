package com.xwq.spider.common;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义监听器，统计处理request的成功数和失败数，同时将处理失败的request收集起来
 */
public class MyListener implements SpiderListener {
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);

    private List<Request> failRequests = new CopyOnWriteArrayList<>();

    @Override
    public void onSuccess(Request request) {
        successCount.incrementAndGet();
    }

    @Override
    public void onError(Request request) {
        failRequests.add(request);
        failCount.incrementAndGet();
    }

    public AtomicInteger getSuccessCount() {
        return successCount;
    }

    public AtomicInteger getFailCount() {
        return failCount;
    }

    public List<Request> getFailRequests() {
        return failRequests;
    }
}
