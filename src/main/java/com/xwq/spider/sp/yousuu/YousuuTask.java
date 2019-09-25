package com.xwq.spider.sp.yousuu;

import com.xwq.spider.common.MyDownloader;
import com.xwq.spider.common.MySpider;
import com.xwq.spider.common.RedisScheduler;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;

@Component
public class YousuuTask {

    private static final String SITE_CODE = "yousuu";

    private static final String URL = "http://www.yousuu.com/bookstore/?type&tag&countWord&status&update&sort&page=";
    private static final int PAGE_SIZE = 20;


    public void doTask() {
        MySpider mySpider = MySpider.create(new YousuuProcessor());

        mySpider.setDownloader(new MyDownloader(SITE_CODE));
        mySpider.setScheduler(new RedisScheduler(SITE_CODE));
        mySpider.addPipeline(new YousuuPipeline());

        mySpider.thread(10);

        int totalPage = 8187;

        for(int i=1; i<=totalPage; i++) {
            Request request = new Request(URL + i);
            request.putExtra(YousuuProcessor.TYPE, YousuuProcessor.LIST_TYPE);
            mySpider.addRequest(request);
        }

        mySpider.run();
    }
}
