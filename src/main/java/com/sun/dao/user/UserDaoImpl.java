package com.sun.dao.user;

import com.sun.dao.BaseDao;
import com.sun.pojo.User;
import org.junit.Test;

import java.sql.*;

public class UserDaoImpl implements UserDao{


    @Override
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet= null;
        User user = null;


        if(connection != null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};

            resultSet = BaseDao.execute(connection, resultSet, preparedStatement, sql, params);

            if(resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getTimestamp("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getTimestamp("modifyDate"));
            }
            // connection可能还存在业务，先不关
            BaseDao.release(null, preparedStatement, resultSet);
        }
        return user;

    }

    @Override
    public int updatePwd(Connection connection, int id, String password) throws SQLException {
        PreparedStatement preparedStatement = null;
        int i = 0;

        if(connection != null){
            String sql = "update smbms_user set userPassword=? where id=?";
            Object[] params = {password, id};
            i = BaseDao.execute(connection, preparedStatement, sql, params);
        }
        BaseDao.release(null, preparedStatement, null);
        return i;
    }


    @Test
    public void test() throws SQLException {
        UserDaoImpl userDao = new UserDaoImpl();
        Connection connection = BaseDao.getConnection();
        int i = userDao.updatePwd(connection, 15, "123123123");
        System.out.println(i);
    }
}
