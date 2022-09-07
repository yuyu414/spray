package com.no.hurdles.spray.dao;

import com.no.hurdles.spray.model.MergeRules;

public interface MergeRulesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MergeRules record);

    int insertSelective(MergeRules record);

    MergeRules selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MergeRules record);

    int updateByPrimaryKey(MergeRules record);
}