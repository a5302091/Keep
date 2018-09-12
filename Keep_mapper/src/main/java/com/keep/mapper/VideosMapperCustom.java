package com.keep.mapper;

import com.keep.Vo.VideosVo;
import com.keep.pojo.Videos;
import com.keep.utils.MyMapper;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    //视频主界面
    public List<VideosVo> queryAllVideos();

}