package com.keep.mapper;

import com.keep.Vo.VideosVo;
import com.keep.pojo.Videos;
import com.keep.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    //视频主界面
    public List<VideosVo> queryAllVideos(@Param("videoDesc") String videoDesc,@Param("userId") String userId);

    //用户视频被点赞的累加
    public void addVideoLikeCount(String videoId);

    //用户视频被取消点赞
    public void reduceVideoLikeCount(String videoId);

    //查询我点赞过的视频
    public List<VideosVo> queryMyLikeVideos(@Param("userId") String userId);

    /**
     * @Description: 查询关注的视频
     */
    public List<VideosVo> queryMyFollowVideos(String userId);

    //根据用户Id查询发布的视频
    public List<VideosVo> queryUserIdVideo(String userId);
}