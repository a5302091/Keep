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
import org.springframework.web.bind.annotation.*;
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
    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public KeepJSONResult upload(String userId,
                                 String bgmId, double videoSeconds,
                                 int videoWidth, int videoHeight,
                                 String desc,
                                 @ApiParam(value = "短视频", required = true)
                                         MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return KeepJSONResult.errorMsg("用户id不能为空...");
        }

        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalVideoPath = "";
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();
                // abc.mp4
                String arrayFilenameItem[] = fileName.split("\\.");
                String fileNamePrefix = "";
                for (int i = 0; i < arrayFilenameItem.length - 1; i++) {
                    fileNamePrefix += arrayFilenameItem[i];
                }
                // fix bug: 解决小程序端OK，PC端不OK的bug，原因：PC端和小程序端对临时视频的命名不同
//				String fileNamePrefix = fileName.split("\\.")[0];

                if (StringUtils.isNotBlank(fileName)) {

                    finalVideoPath = Const.FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
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

        // 判断bgmId是否为空，如果不为空，
        // 那就查询bgm的信息，并且合并视频，生产新的视频
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgm(bgmId);
            String mp3InputPath = Const.FILE_SPACE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(Const.FLEX_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = Const.FILE_SPACE + uploadPathDB;
            tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
        }
        System.out.println("uploadPathDB=" + uploadPathDB);
        System.out.println("finalVideoPath=" + finalVideoPath);

        // 对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(Const.FLEX_EXE);
        videoInfo.getCover(finalVideoPath, Const.FILE_SPACE + coverPathDB);

        // 保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);

        return KeepJSONResult.ok(videoId);
    }

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
    public KeepJSONResult showAll(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "pageSize", defaultValue = "20") int pageSize, @RequestBody Videos video, Integer isSaveRecord) {


        PagedResult allVideos = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
        return KeepJSONResult.ok(allVideos);

    }

    //搜索热门词语0.0.0.00000
    //分页查询视频列表
    @PostMapping("/hot")
    public KeepJSONResult hot() {
        return KeepJSONResult.ok(videoService.getHotAll());
    }


    //用户视频喜欢数量增加
    @PostMapping("/userLike")
    public KeepJSONResult userLike(String userId, String videoId, String videoCreaterId) {
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return KeepJSONResult.ok();
    }


    //用户视频喜欢数量减少
    @PostMapping("/userUnLike")
    public KeepJSONResult userUnLike(String userId, String videoId, String videoCreaterId) {
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return KeepJSONResult.ok();
    }
}
