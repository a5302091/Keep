package com.keep.service;

import com.keep.pojo.Bgm;
import com.keep.pojo.Videos;
import com.keep.utils.PagedResult;

import java.util.List;

/**
 * Created by Administrator on 2018/9/3.
 */
public interface VideoService {

    //保存视频至数据库
    public String saveVideo(Videos videos);

    //更新视频封面
    public void updateVideo(String videoId,String CoverPath);

    //分页查询
    public PagedResult getAllVideos(Integer pageNum,Integer pageSize);

}
