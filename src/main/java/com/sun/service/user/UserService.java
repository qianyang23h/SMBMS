package com.sun.service.user;

import com.sun.pojo.User;

public interface UserService {

    // 用户登陆
    public User login(String userCode, String password);


    // 根据用户id修改密码
    public boolean updatePwd(int id, String password);
}
