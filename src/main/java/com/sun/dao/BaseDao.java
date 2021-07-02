package com.sun.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

// 操作数据库公共类
public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    // 静态代码块，类加载时初始化
    static {

        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        Properties properties = new Properties();

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");

    }

    // 获取数据库的连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // 编写查询公共类
    public  static ResultSet execute(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement, String sql, Object[] params) throws SQLException {
        // 预编译的sql，执行不用传参
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            // setObject，占位符从1开始
            preparedStatement.setObject(i+1, params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }


    // 编写增删改公共方法
    // 重载 execute 参数不一样
    public  static int execute(Connection connection, PreparedStatement preparedStatement, String sql, Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            // setObject，占位符从1开始
            preparedStatement.setObject(i+1, params[i]);
        }
        int updateRow = preparedStatement.executeUpdate();
        return updateRow;
    }

    // 关闭连接
    public static boolean release(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        boolean flag = true;  // 释放是否成功的flag

        if(preparedStatement != null){
            try {
                preparedStatement.close();
                // GC
                preparedStatement = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if(resultSet != null) {
            try {
                resultSet.close();
                resultSet = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        if(connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }

}
