package com.no.hurdles.spray.dao;

import com.no.hurdles.spray.model.MergeRules;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MergeRulesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MergeRules record);

    int insertSelective(MergeRules record);

    MergeRules selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MergeRules record);

    int updateByPrimaryKey(MergeRules record);

    @Select("SELECT * FROM `merge_rules` WHERE p_id = #{pId} AND source_branch = #{sourceBranch} AND `status` = 1")
    List<MergeRules> selectByPidAndSourceBranch(@Param("pId") Long pId, @Param("sourceBranch") String sourceBranch);
}