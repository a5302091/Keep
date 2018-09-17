package com.keep.Vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;

public class PusherVideo {
    public UsersVo publisher;

    public boolean userLikeVideo;

    public UsersVo getPublisher() {
        return publisher;
    }

    public void setPublisher(UsersVo publisher) {
        this.publisher = publisher;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }
}