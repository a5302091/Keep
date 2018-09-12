package com.keep.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.keep.Vo.VideosVo;
import com.keep.mapper.VideosMapper;
import com.keep.mapper.VideosMapperCustom;
import com.keep.pojo.Videos;
import com.keep.service.VideoService;
import com.keep.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by Administrator on 2018/9/3.
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videoMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

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

    //分页查询视频列表
    @Override
    public PagedResult getAllVideos(Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryAllVideos();

        PageInfo<VideosVo> pageInfo = new PageInfo(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());
        return pagedResult;
    }


}
