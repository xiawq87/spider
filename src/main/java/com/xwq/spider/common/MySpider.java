package com.xwq.spider.common;

import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加监听器
 */
public class MySpider extends Spider {
    private MyListener listener;

    public MySpider(PageProcessor pageProcessor) {
        super(pageProcessor);

        List<SpiderListener> spiderListeners = this.getSpiderListeners();
        if(CollectionUtils.isEmpty(spiderListeners)) {
            spiderListeners = new ArrayList<>();
        }
        listener = new MyListener();
        spiderListeners.add(listener);
        this.setSpiderListeners(spiderListeners);
    }

    public static MySpider create(PageProcessor pageProcessor) {
        return new MySpider(pageProcessor);
    }

    @Override
    public void run() {
        super.run();

        if(scheduler != null && scheduler instanceof DuplicateRemover) {
            ((DuplicateRemover) scheduler).resetDuplicateCheck(this);
        }

        //TODO 爬虫任务完成时处理爬取失败的请求
        List<Request> failRequests = listener.getFailRequests();
        if(CollectionUtils.isNotEmpty(failRequests)) {

        }
    }
}
