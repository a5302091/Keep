package com.keep.service;

import com.keep.pojo.Users;
import com.keep.pojo.UsersReport;
import com.keep.utils.KeepJSONResult;

/**
 * Created by Administrator on 2018/9/3.
 */
public interface UserService {

    //检验用户名是否存在
    public boolean queryUsernameIsExist(String username);

    //用户注册
    public void save(Users user);

    //用户登录
    public Users login(String username, String password);

    //用户头像修改
    public void updateUserInfo(Users user);

    //查询用户信息
    public Users query(String userId);


    //查询用户是否对视频进行了点赞显示
    public boolean isUserLikeVideo(String userId, String videoId);

    //用户获得关注和粉丝
    public void saveUserFanRelation(String userId, String fanId);


    //用户减少关注和粉丝
    public void deleteUserFanRelation(String userId, String fanId);

    //当前登录用户是否关注该视频作者
    public boolean queryIfFollow(String userId, String fanId);

    //举报视频
    public void reportUser(UsersReport usersReport);
}
