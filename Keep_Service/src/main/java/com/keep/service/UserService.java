package com.keep.service;

import com.keep.pojo.Users;
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
}
