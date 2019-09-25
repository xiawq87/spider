package com.xwq.spider.entity;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Novel implements Serializable {
    /**
     * 小说id,自增
     */
    @Id
    private Long id;
    /**
     * 小说名称
     */
    private String name;
    /**
     * 小说作者
     */
    private String author;
    /**
     * 小说字数(万字)
     */
    private Double wordNum;
    /**
     * 小说状态
     */
    private String status;
    /**
     * 小说评分
     */
    private Double score;
    /**
     * 评分人数
     */
    private Integer scorePersonNum;
    /**
     * 最后更新时间
     */
    private String lastUpdateTime;
    /**
     * 小说标签，以逗号,分割
     */
    private String tags;
    /**
     * 小说简介
     */
    private String intro;
}
