package com.xwq.spider.common;

import com.alibaba.fastjson.JSON;
import com.xwq.spider.util.SpringContextUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * 自定义调度器，将下载器传递过来的请求保存到redis中，进行url去重，弹出请求
 */
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    /**
     * 用于存放url的队列
     */
    private static final String QUEUE_PREFIX = "queue_";

    /**
     * 用于对url去重
     */
    private static final String SET_PREFIX = "set_";

    /**
     * 用于保存request的额外参数
     */
    private static final String ITEM_PREFIX = "item_";


    private RedisTemplate redisTemplate;
    private String keyPrefix;

    public RedisScheduler(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
    }


    protected String getSetKey(Task task) {
        return StringUtils.join(keyPrefix, ":", SET_PREFIX, task.getUUID());
    }

    protected String getQueueKey(Task task) {
        return StringUtils.join(keyPrefix, ":", QUEUE_PREFIX, task.getUUID());
    }

    protected String getItemKey(Task task) {
        return StringUtils.join(keyPrefix, ":", ITEM_PREFIX, task.getUUID());
    }


    @Override
    public void resetDuplicateCheck(Task task) {
        redisTemplate.delete(getSetKey(task));
    }


    @Override
    public boolean isDuplicate(Request request, Task task) {
        String setKey = getSetKey(task);
        BoundSetOperations setOps = redisTemplate.boundSetOps(setKey);
        Boolean isMember = setOps.isMember(request.getUrl());

        if(Boolean.FALSE.equals(isMember)) {
            // 将url加入到redis set中
            setOps.add(request.getUrl());
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        // 将url推入redis队列中
        String queueKey = getQueueKey(task);
        BoundListOperations listOps = redisTemplate.boundListOps(queueKey);
        listOps.rightPush(request.getUrl());

        // 将request的额外参数保存到redis hash中
        if(MapUtils.isNotEmpty(request.getExtras())) {
            String itemKey = getItemKey(task);
            BoundHashOperations hashOps = redisTemplate.boundHashOps(itemKey);
            hashOps.put(request.getUrl(), JSON.toJSONString(request));
        }
    }


    @Override
    public Request poll(Task task) {
        // 从队列中弹出一个url
        String queueKey = getQueueKey(task);
        BoundListOperations listOps = redisTemplate.boundListOps(queueKey);
        Object url = listOps.leftPop();
        if(url == null) {
            return null;
        }

        // 从redis hash中取出带额外参数的request返回
        String itemKey = getItemKey(task);
        BoundHashOperations hashOps = redisTemplate.boundHashOps(itemKey);
        Object requestJsonObj = hashOps.get(url);
        if(requestJsonObj != null) {
            Request request = JSON.parseObject(requestJsonObj.toString(), Request.class);
            return request;
        }

        return new Request(url.toString());
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        String queueKey = getQueueKey(task);
        BoundListOperations listOps = redisTemplate.boundListOps(queueKey);
        return listOps.size().intValue();
    }


    @Override
    public int getTotalRequestsCount(Task task) {
        String setKey = getSetKey(task);
        BoundSetOperations setOps = redisTemplate.boundSetOps(setKey);
        return setOps.size().intValue();
    }
}