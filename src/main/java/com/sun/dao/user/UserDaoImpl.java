package com.sun.dao.user;

import com.mysql.jdbc.StringUtils;
import com.sun.dao.BaseDao;
import com.sun.pojo.User;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    // 根据用户名或角色查询用户总数（最难的sql）
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count = 0;

        if(connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u, smbms_role r where r.id=u.userRole");

            ArrayList<Object> paramsList = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.userName like ?");
                paramsList.add("%" + username + "%");
            }

            if(userRole > 0) {
                sql.append(" and u.userRole = ?");
                paramsList.add(userRole);
            }

            // 将ArrayList转化为数组
            Object[] params = paramsList.toArray();

            // 打印完整的SQL语句
            System.out.println("UserDaoImpl->getUserCount:"+ sql.toString());


            rs = BaseDao.execute(connection, rs, preparedStatement, sql.toString(), params);
            if (rs.next()){
                count = rs.getInt("count");
            }
            BaseDao.release(null, preparedStatement, rs);
        }
        return count;
    }


    @Override
    // 通过条件查询-userList
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<User> userList = new ArrayList<>();

        if (connection!= null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> paramList = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.userName like ?");
                paramList.add("%" + username + "%");
            }

            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                paramList.add(userRole);
            }

            //在mysql数据库中，分页使用 limit startIndex，pageSize ; 总数
            sql.append(" order by creationDate DESC limit ?, ?");
            int offset = (currentPageNo - 1) * pageSize;
            paramList.add(offset);
            paramList.add(pageSize);

            Object[] params = paramList.toArray();

            // 打印完整的SQL语句
            System.out.println("UserDaoImpl->getUserList:"+ sql.toString());

            resultSet = BaseDao.execute(connection, resultSet, preparedStatement, sql.toString(), params);

            while (resultSet.next()) {
                User _user = new User();
                _user.setId(resultSet.getInt("id"));
                _user.setUserCode(resultSet.getString("userCode"));
                _user.setUserName(resultSet.getString("userName"));
                _user.setGender(resultSet.getInt("gender"));
                _user.setBirthday(resultSet.getDate("birthday"));
                _user.setPhone(resultSet.getString("phone"));
                _user.setUserRole(resultSet.getInt("userRole"));
                _user.setUserRoleName(resultSet.getString("userRoleName"));
                userList.add(_user);
            }

            BaseDao.release(null, preparedStatement, resultSet);
        }

        return userList;
    }


    @Override
    public int add(Connection connection, User user) throws SQLException {
        PreparedStatement preparedStatement = null;
        int updateRows = 0;

        if(connection != null){
           String sql = "insert into smbms_user (userCode,userName,userPassword," +
                   "userRole,gender,birthday,phone,address,creationDate,createdBy) " +
                   "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateRows = BaseDao.execute(connection, preparedStatement, sql, params);
        }
        return updateRows;
    }

    @Test
    public void test() throws SQLException {
        UserDaoImpl userDao = new UserDaoImpl();
        Connection connection = BaseDao.getConnection();
        List<User> userList = userDao.getUserList(connection, "", 0, 1, 5);
        System.out.println(userList.size());
    }
}
