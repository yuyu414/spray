package com.no.hurdles.spray.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * merge_rules
 * @author 
 */
@Data
public class MergeRules implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * project_info.id
     */
    private Long pId;

    /**
     * sourceBranch
     */
    private String sourceBranch;

    /**
     * targetBranch
     */
    private String targetBranch;

    /**
     * 1-正常 0-删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}