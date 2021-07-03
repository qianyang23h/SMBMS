package com.sun.service.user;

import com.sun.dao.BaseDao;
import com.sun.dao.user.UserDao;
import com.sun.dao.user.UserDaoImpl;
import com.sun.pojo.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService{

    // 业务层都会调用dao层，所以我们要引入Dao层
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }


    // 用户登陆
    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            // 通过业务层调用对应的具体数据库操作
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            BaseDao.release(connection, null, null);
        }
        return user;
    }


    // 修改密码
    @Override
    public boolean updatePwd(int id, String password) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
//            System.out.println("i:" + userDao.updatePwd(connection, id, password));
            if(userDao.updatePwd(connection, id, password) > 0) {  // 说明执行成功
                flag = true;
                System.out.println("i:" + userDao.updatePwd(connection, id, password));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.release(connection,null, null);
        }
        return flag;
    }


    // 增加用户
    @Override
    public boolean add(User user) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);   //关闭自动事务提交  手动提交
            int updateRows = userDao.add(connection, user);
            connection.commit();  // 手动提交事务
            if (updateRows > 0) {
                flag = true;
                System.out.println("add success!");
            }
            else {
                System.out.println("add failed!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            //如果失败则回滚
            try {
                System.out.println("rollback==================");
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            BaseDao.release(connection, null, null);
        }
        return flag;
    }

    // 查询记录数
    @Override
    public int getUserCount(String username, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, username, userRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.release(connection, null, null);
        }
        return count;
    }

    // 获取用户列表
    @Override
    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, username, userRole, currentPageNo, pageSize);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.release(connection, null, null);
        }
        return userList;
    }

    @Test
    // 使用junit做测试
    public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = userService.getUserList("", 0, 1, 5);
        System.out.println(userList.size());
    }
}
