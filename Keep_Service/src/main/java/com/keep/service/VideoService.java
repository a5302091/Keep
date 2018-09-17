package com.keep.service;

import com.keep.pojo.Videos;
import com.keep.utils.PagedResult;

import java.util.List;

public interface VideoService {

    //保存视频至数据库
    public String saveVideo(Videos videos);

    //更新视频封面
    public void updateVideo(String videoId, String CoverPath);

    //分页查询
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer pageNum, Integer pageSize);

    //搜索热门词语
    public List<String> getHotAll();

    //用户点赞的视频的
    public void userLikeVideo(String userId, String videoId, String videoCreaterId);

    //用户取消点赞视频的接口
    public void userUnLikeVideo(String userId,String videoId,String videoCreaterId);


}
