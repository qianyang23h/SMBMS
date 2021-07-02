package com.sun.filter;

import com.sun.pojo.User;
import com.sun.utils.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 过滤器， 从session中获取用户
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);
        if(user == null) {  // 已经被移除或者注销了
            resp.sendRedirect("/SMBMS/error.jsp");
        }
        else {
            chain.doFilter(request,response);  //让我们的请求继续走，如果不写，程序到这里就被拦截停止！
        }

    }

    @Override
    public void destroy() {

    }
}
