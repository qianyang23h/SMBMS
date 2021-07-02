package com.sun.servlet.user;

import com.mysql.jdbc.StringUtils;
import com.sun.pojo.User;
import com.sun.service.user.UserServiceImpl;
import com.sun.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // 从session中拿id
//        Object user = req.getSession().getAttribute(Constants.USER_SESSION);
//
//        String newpassword = req.getParameter("newpassword");
//        System.out.println("UserServlet newpassword: " + newpassword);
//        System.out.println(!StringUtils.isNullOrEmpty(newpassword));
//        System.out.println(user);
//        System.out.println("==============");
//
//        if (user != null && !StringUtils.isNullOrEmpty(newpassword)) {  // 用户存在 并 新密码不等于空
//            UserServiceImpl userService = new UserServiceImpl();
//            boolean flag = userService.updatePwd(((User) user).getId(), newpassword);
//            if (flag) {  // 修改成功
//                req.setAttribute("message", "修改成功，请退出重新登陆");
//                req.getSession().removeAttribute(Constants.USER_SESSION);
//            } else {  // 修改密码失败
//                req.setAttribute("message", "修改密码失败");
//            }
//        } else {
//            req.setAttribute("message", "新密码有误");
//        }
//        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);

        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        System.out.println("UserServlet newpassword: " + newpassword);
        System.out.println("id: " + ((User)o).getId());
        boolean flag = false;
        if (o != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserServiceImpl userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功,请退出并使用新密码重新登录！");
                req.getSession().removeAttribute(Constants.USER_SESSION);//session注销
            } else {
                req.setAttribute("message", "修改密码失败！");
            }
        } else {
            req.setAttribute("message", "修改密码失败！");
        }
        //修改成功后转发就行了
        req.getRequestDispatcher("/jsp/pwdmodify.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
