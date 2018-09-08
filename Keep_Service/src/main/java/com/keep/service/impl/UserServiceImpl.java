package com.keep.service.impl;

import com.keep.mapper.UsersMapper;
import com.keep.pojo.Users;
import com.keep.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by Administrator on 2018/9/3.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

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

        Example example=new Example(Users.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user=usersMapper.selectOneByExample(example);
        return user;
    }


}
