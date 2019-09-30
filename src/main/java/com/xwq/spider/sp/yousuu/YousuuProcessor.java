package com.xwq.spider.sp.yousuu;

import com.xwq.spider.dto.NovelDTO;
import com.xwq.spider.entity.Novel;
import com.xwq.spider.util.NumberUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

public class YousuuProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(0).setSleepTime(2000).setTimeOut(60000);

    public static final String TYPE = "type";
    public static final String LIST_TYPE = "list";
    public static final String DETL_TYPE = "detl";

    @Override
    public void process(Page page) {
        // 从Request额外信息中取出页面类型，然后分别处理
        String type = page.getRequest().getExtra(TYPE).toString();

        switch (type) {
            case LIST_TYPE:
                processList(page);
                break;
            case DETL_TYPE:
                processDetl(page);
                break;
            default:
                break;
        }
    }

    /**
     * 处理列表页
     * @param page
     */
    private void processList(Page page) {
        Html html = page.getHtml();
        List<Selectable> bookInfoNodes = html.xpath("//div[@class=\"book-info\"]").nodes();

        List<Novel> novelList = new ArrayList<>();

        for(Selectable node : bookInfoNodes) {
            String novelName = node.xpath("/div/a/text()").toString();
            String novelUrl = node.xpath("/div/a/@href").toString();
            String id = novelUrl.substring(novelUrl.lastIndexOf("/") + 1);

            // 将详情页url添加到调度器
            Request detlRequest = new Request("http://www.yousuu.com/book/" + id);
            detlRequest.putExtra(TYPE, DETL_TYPE);
            page.addTargetRequest(detlRequest);


            // 子节点下标值从1开始
            String author = node.xpath("/div/p[1]/router-link/text()").toString();
            String wordNum = node.xpath("/div/p[1]/span[1]/text()").toString();
            String lastUpdateTime = node.xpath("/div/p[1]/span[2]/text()").toString();
            String status = node.xpath("/div/p[1]/span[3]/text()").toString();

            String scoreStr = node.xpath("/div/p[2]/text()").toString();
            scoreStr = scoreStr.substring("综合评分：".length());
            String[] split = scoreStr.split("\\(");
            Double score = Double.valueOf(split[0]);
            String scorePersonNumStr = split[1].substring(0, split[1].length() - 2);
            Integer scorePersonNum = Integer.valueOf(scorePersonNumStr);

            List<Selectable> tagNodes = node.xpath("/div/p[4]/label").nodes();
            StringBuffer tagBuff = new StringBuffer();
            for(Selectable tagNode : tagNodes) {
                String tag = tagNode.xpath("/label/text()").toString();
                tagBuff.append(tag + ",");
            }

            String tags = null;
            if(tagBuff.length() > 0) {
                tags = tagBuff.substring(0, tagBuff.length()-1);
            }

            Novel novel = new Novel();
            novel.setId(Long.valueOf(id));
            novel.setName(novelName);
            novel.setAuthor(author);
            novel.setWordNum(NumberUtil.getDoubleNumber(wordNum));
            novel.setLastUpdateTime(lastUpdateTime);
            novel.setStatus(status);
            novel.setScore(score);
            novel.setScorePersonNum(scorePersonNum);
            novel.setTags(tags);

            novelList.add(novel);
        }

        page.putField("novelList", novelList);
    }


    /**
     * 处理详情页
     * @param page
     */
    private void processDetl(Page page) {
        Html html = page.getHtml();
        List<Selectable> nodes = html.xpath("//body/*[1]").nodes();
        String script = nodes.get(1).toString();

        int pos1 = script.indexOf("introduction");
        int pos2 = script.indexOf("countWord");

        String intro = script.substring(pos1+15, pos2-3);

        String url = page.getRequest().getUrl();
        String id = url.substring(url.lastIndexOf("/") + 1);

        NovelDTO novelDTO = new NovelDTO(Long.valueOf(id), intro);
        page.putField("novelDTO", novelDTO);
    }


    @Override
    public Site getSite() {
        site.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        site.addHeader("Accept-Encoding", "gzip, deflate");
        site.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        site.addHeader("Cache-Control", "max-age=0");
        site.addHeader("Connection", "keep-alive");
        site.addHeader("Cookie", "Hm_lvt_42e120beff2c918501a12c0d39a4e067=1566530194,1566819135,1566819342,1566963215; Hm_lpvt_42e120beff2c918501a12c0d39a4e067=1566963215");
        site.addHeader("Host", "www.yousuu.com");
        site.addHeader("Upgrade-Insecure-Requests", "1");
        site.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");

        return site;
    }
}
