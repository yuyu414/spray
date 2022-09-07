package com.no.hurdles.spray.dao;

import com.no.hurdles.spray.model.ProjectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProjectInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectInfo record);

    int insertSelective(ProjectInfo record);

    ProjectInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectInfo record);

    int updateByPrimaryKey(ProjectInfo record);

    @Select("SELECT * FROM `project_info` WHERE project_id = #{projectId} AND `status` = 1")
    ProjectInfo selectByProjectId(@Param("projectId") String projectId);
}