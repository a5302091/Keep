package com.keep.mapper;

import com.keep.Vo.CommentsVO;
import com.keep.pojo.Comments;
import com.keep.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustomer extends MyMapper<Comments> {

    //查询视频评论
    public List<CommentsVO>  queryComments(String videoId);
}