package com.itdr.utils;

import com.itdr.pojo.Users;
import com.itdr.pojo.vo.UserVo;

public class ObjectToVoUtil {
    public static UserVo UserToUserVo(Users u){
        UserVo uv = new UserVo ();
        uv.setId (u.getId ());
        uv.setUsername (u.getUsername ());
        uv.setEmail (u.getEmail ());
        uv.setPhone (u.getPhone ());
        uv.setCreateTime (u.getCreateTime ());
        uv.setUpdateTime (u.getUpdateTime ());
        return uv;
    }
}
