package com.keep.service.impl;

import com.keep.mapper.BgmMapper;
import com.keep.pojo.Bgm;
import com.keep.service.BgmService;
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
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;


    //查询背景音乐列表
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }
}
