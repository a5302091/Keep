package com.keep.mapper;

import com.keep.pojo.SearchRecords;
import com.keep.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    //热搜词语查询
    public List<String> getHotAll();
}