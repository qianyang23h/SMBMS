package com.sun.dao.user;

import com.sun.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    // 得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode) throws SQLException;

    // 修改当前密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException;

    // 根据用户名或角色查询用户总数
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException;

    // 通过条件查询-userList
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException;

    // 添加用户信息
    public int add(Connection connection, User user) throws SQLException;


}
