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

public class YousuuPipeline implements Pipeline {
    private NovelMapper novelMapper = SpringContextUtil.getBean(NovelMapper.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        Object novelListObj = resultItems.get("novelList");
        if(null != novelListObj) {
            List<Novel> novelList = (List<Novel>) novelListObj;
            if(CollectionUtils.isNotEmpty(novelList)) {
                novelMapper.batchInsert(novelList);
            }
        }

        Object novelDTOObj = resultItems.get("novelDTO");
        if(null != novelDTOObj) {
            NovelDTO novelDTO = (NovelDTO) novelDTOObj;

            Novel novel = new Novel();
            BeanUtils.copyProperties(novelDTO, novel);
            novelMapper.updateByPrimaryKeySelective(novel);
        }
    }
}
