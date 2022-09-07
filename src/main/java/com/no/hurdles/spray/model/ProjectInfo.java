package com.no.hurdles.spray.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * project_info
 * @author 
 */
@Data
public class ProjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * namespace
     */
    private String namespace;

    /**
     * 项目名称
     */
    private String name;

    /**
     * gitLab中对应的项目id
     */
    private String projectId;

    /**
     * 项目的git地址
     */
    private String projectUrl;

    /**
     * server的地址，例如：https://gitlab.com
     */
    private String serverUrl;

    /**
     * 授权秘钥
     */
    private String accessToken;

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