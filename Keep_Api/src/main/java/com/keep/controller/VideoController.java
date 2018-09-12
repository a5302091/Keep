package com.keep.controller;

import com.keep.pojo.Bgm;
import com.keep.pojo.Users;
import com.keep.pojo.Videos;
import com.keep.service.BgmService;
import com.keep.service.VideoService;
import com.keep.utils.*;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;


@RestController
@RequestMapping("/video")
@Api(value = "用户视频接口", tags = {"上传视频"})
public class VideoController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    /**
     * 用户视频上传
     */
    @ApiOperation(value = "用户上传视频的接口", notes = "用户上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "bgmID", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")

    })

    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public KeepJSONResult upload(String userId, String bgmId,
                                 double videoSeconds, int videoWidth,
                                 int videoHeight, String desc,
                                 @ApiParam(value = "小视频", required = true)
                                         MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return KeepJSONResult.errorMsg("未获取到userId");
        }

        //保存至数据库中的路径
        String uploadPathDB = "/" + userId + "/video";
        //封面保存路径
        String coverPathDB = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        //文件最终的保存地址
        String Path = "";
        try {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                String coverPrefix = fileName.split("\\.")[0];
                if (StringUtils.isNotBlank(fileName)) {
                    //文件最终的保存地址
                    Path = Const.FILE_SPACE + uploadPathDB + "/" + fileName;
                    //数据库的保存路径
                    uploadPathDB += ("/" + fileName);
                    //封面保存的路径
                    coverPathDB = coverPathDB + "/" + coverPrefix + ".jpg";

                    File outfile = new File(Path);
                    if (outfile.getParentFile() != null || outfile.getParentFile().isDirectory()) {
                        //创建父类文件夹
                        outfile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outfile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return KeepJSONResult.errorMsg("未获取到userId");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return KeepJSONResult.errorMsg("上传失败");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //判断BGMID是否为空 如果不为空 就查询bgm信息,合成视频
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgm(bgmId);
            String mp3InputPath = Const.FILE_SPACE + bgm.getPath();

            MergeVideoMp3 mergeVideoMp3 = new MergeVideoMp3(Const.FLEX_EXE);
            String videoInputPath = Path;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            Path = Const.FILE_SPACE + uploadPathDB;
            mergeVideoMp3.convertor(videoInputPath, mp3InputPath, videoSeconds, Path);
        }
        System.out.print("uploadPathDB=" + uploadPathDB);
        System.out.print("Path=" + Path);

        //对视频进行封面截图
        MergeVideoMp3 ffmpeg = new MergeVideoMp3(Const.FLEX_EXE);
        ffmpeg.convertorTP(Path, Const.FILE_SPACE + coverPathDB);

        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setCoverPath(coverPathDB);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);


        return KeepJSONResult.ok(videoId);
    }

    /**
     * 上传视频封面
     */
    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public KeepJSONResult uploadCover(String userId,
                                      String videoId,
                                      @ApiParam(value = "视频封面", required = true)
                                              MultipartFile file) throws Exception {

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return KeepJSONResult.errorMsg("视频主键id和用户id不能为空...");
        }

        // 文件保存的命名空间
//		String fileSpace = "C:/imooc_videos_dev";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalCoverPath = "";
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {

                    finalCoverPath = Const.FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return KeepJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return KeepJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        videoService.updateVideo(videoId, uploadPathDB);

        return KeepJSONResult.ok();
    }


    //分页查询视频列表
    @PostMapping("/showAll")
    public KeepJSONResult showAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {

        PagedResult allVideos = videoService.getAllVideos(page, pageSize);
        return KeepJSONResult.ok(allVideos);

    }
}
