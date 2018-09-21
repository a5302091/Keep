package com.keep.service;

import com.github.pagehelper.Page;
import com.keep.pojo.Comments;
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
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId);

    //查询我点赞过的视频
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 查询我关注的人的视频列表
     */
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    //根据用户ID查询他发布的视频
    public PagedResult queryUserIdVideo(String userId, Integer page, Integer pageSize);

    //视频留言
    public void saveComment(Comments comments);

    //分页查看视频留言
    public PagedResult getAllComments(String videoId,Integer page,Integer pageSize);


}
