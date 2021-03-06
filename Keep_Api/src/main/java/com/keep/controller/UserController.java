package com.keep.controller;

import com.keep.Vo.UsersVo;
import com.keep.pojo.Users;
import com.keep.service.UserService;
import com.keep.utils.Const;
import com.keep.utils.KeepJSONResult;
import com.keep.utils.MD5Utils;
import com.keep.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2018/9/3.
 */
@RestController
@Api(value = "用户相关接口", tags = {"注册,登录"})
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redis;

    @ApiOperation(value = "用户注册接口", notes = "用户注册")
    @PostMapping("/regist")
    public KeepJSONResult regist(@RequestBody Users user) throws Exception {

        //校验用户是否为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return KeepJSONResult.errorMsg("用户名或者密码不能为空");
        }

        //校验用户名是否存在
        boolean queryUsernameIsExist = userService.queryUsernameIsExist(user.getUsername());

        //进行注册
        if (!queryUsernameIsExist) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.save(user);
        } else {
            return KeepJSONResult.errorMsg("用户名已经存在");
        }
        user.setPassword("");

        //将注册的token加入redis
        UsersVo usersVo = setRedissUser(user);
        return KeepJSONResult.ok(usersVo);

    }

    //将用户session存入redis
    public UsersVo setRedissUser(Users users) {
        String userToken = java.util.UUID.randomUUID().toString();
        redis.set(Const.USER_REDIS_SESSION + ":" + users.getId(), userToken, 1000 * 60 * 10);

        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(users, usersVo);
        usersVo.setUserToken(userToken);
        return usersVo;
    }

    /**
     * 用户登录
     */
    @ApiOperation(value = "用户登录的接口", notes = "用户名的接口")
    @PostMapping("/login")
    public KeepJSONResult login(@RequestBody Users user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();

        //判断用户名密码是否为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return KeepJSONResult.errorMsg("用户名或密码不能为空");
        }
        Thread.sleep(2000);

        //判断用户名是否存在
        Users login = userService.login(username, MD5Utils.getMD5Str(password));
        if (login != null) {
            login.setPassword("");
            UsersVo usersVo = setRedissUser(login);
            return KeepJSONResult.ok(usersVo);
        } else {
            return KeepJSONResult.errorMsg("用户名或密码错误");
        }
    }

    /**
     * 用户注销
     */
    @ApiOperation(value = "用户注销的接口", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    @PostMapping("/logout")
    public KeepJSONResult logout(String userId) throws Exception {
        redis.del(Const.USER_REDIS_SESSION + ":" + userId);
        return KeepJSONResult.ok("用户注销成功");
    }

    /**
     * 用户头像上传
     */
    @ApiOperation(value = "用户上传头像的接口", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    @PostMapping("/upload")
    public KeepJSONResult upload(String userId, @RequestParam("file") MultipartFile[] file) throws Exception {


        if (StringUtils.isBlank(userId)) {
            return KeepJSONResult.errorMsg("未获取到userId");
        }

        //文件保存的命名空间
        String fileSpace = "C:/Keep";

        //保存至数据库中的路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file.length > 0 && file != null) {
                String fileName = file[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件最终的保存地址
                    String Path = fileSpace + uploadPathDB + "/" + fileName;
                    //数据库的保存路径
                    uploadPathDB += ("/" + fileName);

                    File outfile = new File(Path);
                    if (outfile.getParentFile() != null || outfile.getParentFile().isDirectory()) {
                        //创建父类文件夹
                        outfile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outfile);
                    inputStream = file[0].getInputStream();
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

        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        userService.updateUserInfo(users);

        return KeepJSONResult.ok(uploadPathDB);
    }


    /**
     * 用户信息查询
     */
    @ApiOperation(value = "用户信息查询", notes = "用户信息查询")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    @PostMapping("/query")
    public KeepJSONResult query(String userId) throws Exception {

        Users query = userService.query(userId);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(query, usersVo);

        return KeepJSONResult.ok(usersVo);
    }

}