package com.keep.service;

import com.keep.pojo.Bgm;

import java.util.List;

/**
 * Created by Administrator on 2018/9/3.
 */
public interface BgmService {

    //查询背景音乐列表
    public List<Bgm> queryBgmList();

    //根据id查询bgm信息
    public Bgm queryBgm(String bgmId);

}
