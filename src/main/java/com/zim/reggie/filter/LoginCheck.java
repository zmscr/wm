package com.zim.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.zim.reggie.common.BaseContext;
import com.zim.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否登录
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheck implements Filter {
    //路劲匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse rep = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String requestURI = req.getRequestURI();

        log.info("拦截到请求:{}",requestURI);
        //1.1定义无需处理的数据

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.检查本次请求是否登陆
        boolean check = check(urls, requestURI);



        //3.不需要处理，直接放行
        if (check) {
            log.info("本次请求不需要处理:{}",requestURI);
            filterChain.doFilter(req, rep);
            return;
        }

        //4.判断登录状态，如果登录则放行，否则登录失败
        if (req.getSession().getAttribute("employee") != null){
            log.info("用户已登录:{}",req.getSession().getAttribute("employee"));

            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(req, rep);
            return;
        }

        //4.1手机端
        if (req.getSession().getAttribute("user") != null){
            log.info("用户已登录:{}",req.getSession().getAttribute("user"));

            Long empId = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(req, rep);
            return;
        }

        log.info("用户未登录");
        //5. 未登录则返回未登录结果
        rep.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    //路径匹配，检查本次请求是否需要放行
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
