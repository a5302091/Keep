package com.keep.service.impl;

import com.keep.mapper.UsersFansMapper;
import com.keep.mapper.UsersLikeVideosMapper;
import com.keep.mapper.UsersMapper;
import com.keep.pojo.Users;
import com.keep.pojo.UsersFans;
import com.keep.pojo.UsersLikeVideos;
import com.keep.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Administrator on 2018/9/3.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private Sid sid;

    //检验用户名是否存在
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Users user = new Users();
        user.setUsername(username);

        Users result = usersMapper.selectOne(user);

        return result == null ? false : true;
    }

    //用户注册
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(Users user) {

        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users login(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        Users users = usersMapper.selectOneByExample(example);
        return users;
    }

    //用户头像修改
    @Override
    public void updateUserInfo(Users user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, example);
    }

    //查询用户个人信息
    @Override
    public Users query(String userId) {

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", userId);
        Users user = usersMapper.selectOneByExample(example);
        return user;
    }

    //查询当前登录用户是否对视频进行点赞
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);
        if (usersLikeVideos != null && usersLikeVideos.size() > 0) {
            return true;
        }
        return false;
    }


    //用户个人被关注 粉丝数量增加
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {

        String id = sid.nextShort();
        UsersFans userFans = new UsersFans();
        userFans.setId(id);
        userFans.setUserId(userId);
        userFans.setFanId(fanId);

        usersFansMapper.insert(userFans);

        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);
    }


    //用户个人被关注 粉丝数量减少
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        usersFansMapper.deleteByExample(example);
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);


    }

    //当前登录用户是否关注该视频作者
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryIfFollow(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> usersFanses = usersFansMapper.selectByExample(example);
        if (usersFanses != null && !usersFanses.isEmpty() && usersFanses.size() > 0) {
            return true;
        }
             return false;
    }

}
