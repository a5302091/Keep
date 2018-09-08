package com.keep.controller;

import com.keep.service.BgmService;
import com.keep.utils.KeepJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "背景音乐相关接口", tags = {"用户上传视频后可以选择的背景音乐"})
@RequestMapping("/bgm/")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    /**
     * 查询背景音乐列表
     *
     * @return
     */
    @ApiOperation(value = "查询所有背景音乐列表的接口", notes = "查询所有背景音乐列表的接口")
    @PostMapping("/list")
    public KeepJSONResult selectBgmAll() {
        return KeepJSONResult.ok(bgmService.queryBgmList());
    }
}
