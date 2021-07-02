package com.sun.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.sun.pojo.User;
import com.sun.service.user.UserServiceImpl;
import com.sun.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        }
        else if(method.equals("pwdmodify") && method !=null) {
            System.out.println("修改密码");
            this.pwdModify(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    // 修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp){
        // 从session中拿id
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");

        boolean flag = false;
        if (o != null && newpassword!=null) {
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
        try {
            req.getRequestDispatcher("/jsp/pwdmodify.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 验证旧密码  session有用户的密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");  // 这个oldpassword是拿的ajax保存的

        HashMap<String, String> resultMap = new HashMap<>();
        // 这里是根据pwdmodify.js中的ajax代码写的
        if(o == null){  // session失效
            resultMap.put("result","sessionerror");
        }
        else if (StringUtils.isNullOrEmpty(oldpassword)) {  // 输入密码为空
            System.out.println("密码为空");
            resultMap.put("result","error");
        }
        else {
            String userPassword = ((User) o).getUserPassword();
            if (oldpassword.equals(userPassword)){  // 密码正确
                System.out.println("密码正确");
                resultMap.put("result","true");
            }
            else {  // 密码错误
                System.out.println("密码错误");
                resultMap.put("result","false");
            }
        }
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
