package com.keep.mapper;

import com.keep.pojo.Users;
import com.keep.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

        //用户视频点赞数量累加
        public void addReceiveLikeCount(String userId);

        //用户视频点赞数量累减
        public void reduceReceiveLikeCount(String userId);

        //用户获得的粉丝增加
        public void addFansCount(String userId);

        //用户获得的粉丝减少
        public void reduceFansCount(String userId);

        //用户关注数量增加
        public void addFollersCount(String userId);

        //用户关注数量减少
        public void reduceFollersCount(String userId);


}