package com.keep.mapper;

import com.keep.Vo.VideosVo;
import com.keep.pojo.Videos;
import com.keep.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    //视频主界面
    public List<VideosVo> queryAllVideos(@Param("videoDesc") String videoDesc);

    //用户视频被点赞的累加
    public void addVideoLikeCount(String videoId);

    //用户视频被取消点赞
    public void reduceVideoLikeCount(String videoId);
}