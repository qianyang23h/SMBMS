package com.sun.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.StringUtils;
import com.sun.pojo.Role;
import com.sun.pojo.User;
import com.sun.service.role.RoleServiceImpl;
import com.sun.service.user.UserServiceImpl;
import com.sun.utils.Constants;
import com.sun.utils.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method!=null && method.equals("savepwd")) {
            this.updatePwd(req, resp);
        }
        else if(method!=null && method.equals("pwdmodify")) {
            System.out.println("修改密码");
            this.pwdModify(req, resp);
        }
        else if (method!=null && method.equals("query")) {
            this.query(req, resp);
        }
        else if (method!=null && method.equals("add"))
            this.add(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    // 增加用户
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // 1. 获取用户前端的数据（查询）
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        // 2. 将前端的用户数据封装到user对象中
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        // 3.调用业务层代码
        UserServiceImpl userService = new UserServiceImpl();
        boolean flag = userService.add(user);
        if(flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        }
        else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }



    // 重点、难点
    public void query(HttpServletRequest req, HttpServletResponse resp) {
        // 查询用户列表
        // 1.获取前端form表单的数据
        String queryUserName = req.getParameter("queryUserName");
        String queryUserRoleTemp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");

        // 定义一些全局变量
        int queryUserRole = 0;
        int pageSize = Constants.PAGE_SIZE;
        int currentPageNo = 1;

        // 调用业务层UserService
        UserServiceImpl userService = new UserServiceImpl();

        // 对前端数据进行检查
        if( queryUserName == null){
            queryUserName = "";
        }

        if (queryUserRoleTemp != null && !queryUserRoleTemp.equals("")) {
            queryUserRole = Integer.parseInt(queryUserRoleTemp);  // queryUserRole  0,1,2,3(默认值为0)
        }

        if (pageIndex != null) {  // pageIndex前端设定了默认为1
            currentPageNo = Integer.parseInt(pageIndex);  // currentPageNo 1,2,3,...(默认值1)
        }

        // 获取用户总数
        int userCount = userService.getUserCount(queryUserName, queryUserRole);

        //
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(pageSize);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setTotalCount(userCount);

        int totalPageCount = pageSupport.getTotalPageCount();
        if (totalPageCount < 1) {  // 如果页面小于1，显示第一页
            currentPageNo = 1;
        }
        else if (currentPageNo > totalPageCount) {  // 如果当前页面大于总页面数，显示最后一页
            currentPageNo = totalPageCount;
        }


        // 获取用户列表展示
        List<User> userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);


        // 调用业务层RoleService
        RoleServiceImpl roleService = new RoleServiceImpl();
        // 获取角色列表
        List<Role> roleList = roleService.getRoleList();


        req.setAttribute("roleList", roleList);
        req.setAttribute("totalCount", userCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);

        // 返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
