package com.xwq.spider.mapper;

import com.xwq.spider.entity.Novel;
import com.xwq.spider.util.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NovelMapper extends MyMapper<Novel> {
    void batchInsert(List<Novel> novels);
}
