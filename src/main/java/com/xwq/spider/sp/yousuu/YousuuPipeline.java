package com.xwq.spider.sp.yousuu;

import com.xwq.spider.dto.NovelDTO;
import com.xwq.spider.entity.Novel;
import com.xwq.spider.mapper.NovelMapper;
import com.xwq.spider.util.SpringContextUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * 持久化小说数据
 */
public class YousuuPipeline implements Pipeline {
    private NovelMapper novelMapper = SpringContextUtil.getBean(NovelMapper.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        //从列表页提取出除小说简介以外的所有信息，批量插入
        Object novelListObj = resultItems.get("novelList");
        if(null != novelListObj) {
            List<Novel> novelList = (List<Novel>) novelListObj;
            if(CollectionUtils.isNotEmpty(novelList)) {
                novelMapper.batchInsert(novelList);
            }
        }

        //从详情页提取出小说简介信息，更新
        Object novelDTOObj = resultItems.get("novelDTO");
        if(null != novelDTOObj) {
            NovelDTO novelDTO = (NovelDTO) novelDTOObj;

            Novel novel = new Novel();
            BeanUtils.copyProperties(novelDTO, novel);
            novelMapper.updateByPrimaryKeySelective(novel);
        }
    }
}
