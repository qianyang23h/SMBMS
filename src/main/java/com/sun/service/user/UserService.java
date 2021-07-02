package com.sun.service.user;

import com.sun.pojo.User;

import java.util.List;

public interface UserService {

    // 用户登陆
    public User login(String userCode, String password);


    // 根据用户id修改密码
    public boolean updatePwd(int id, String password);


    // 查询记录数
    public int getUserCount(String username, int userRole);


    // 通过条件查询-userList
    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize);
}
