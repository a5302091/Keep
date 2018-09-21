package com.keep.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.keep.Vo.CommentsVO;
import com.keep.Vo.VideosVo;
import com.keep.mapper.*;
import com.keep.pojo.*;
import com.keep.service.VideoService;
import com.keep.utils.PagedResult;
import com.keep.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2018/9/3.
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videoMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustomer commentsMapperCustomer;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    //保存上传的视频
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos videos) {
        String id = sid.nextShort();
        videos.setId(id);
        videoMapper.insertSelective(videos);

        return id;
    }

    //更新视频封面
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String CoverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(CoverPath);
        videoMapper.updateByPrimaryKeySelective(videos);
    }

    //根据用户id查询他发布的视频


    //分页查询视频列表
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord,
                                    Integer page, Integer pageSize) {

        // 保存热搜词
        String desc = video.getVideoDesc();
        String userId = video.getUserId();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            record.setId(recordId);
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryAllVideos(desc, userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    //查询热门词语
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotAll() {
        return searchRecordsMapper.getHotAll();
    }

    //用户视频粉丝数量增加
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        // 1. 保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        // 2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        // 3. 用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }


    //用户视频粉丝数量减少
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {

        //1.删除用户和视频的 喜欢点赞关联
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        usersLikeVideosMapper.deleteByExample(example);

        //2.视频喜欢数量减少
        videosMapperCustom.reduceVideoLikeCount(videoId);

        //3.用户粉丝数量减少
        usersMapper.reduceReceiveLikeCount(videoCreaterId);

    }

    //查询我点赞过的视频
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    //根据用户Id查询发布的视频作品
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryUserIdVideo(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryUserIdVideo(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    //视频留言
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comments) {

        String id = sid.nextShort();
        comments.setId(id);
        comments.setCreateTime(new Date());

        commentsMapper.insert(comments);

    }

    //分页查询视频留言
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<CommentsVO> list = commentsMapperCustomer.queryComments(videoId);
        for (CommentsVO c : list) {
            String format = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(format);
        }

        PageInfo<CommentsVO> pageInfo = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }


}
