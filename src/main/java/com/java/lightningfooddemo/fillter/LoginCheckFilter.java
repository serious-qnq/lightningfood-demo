package com.java.lightningfooddemo.fillter;

import com.alibaba.fastjson.JSON;
import com.java.lightningfooddemo.common.BaseContextUtils;
import com.java.lightningfooddemo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
@Component
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override//过滤器
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到的请求:{}", requestURI);


        String[] urls = new String[]{
                "/employee/login", "/employee/logout",
                "/backend/**", "/front/**", "/common/**",
                "/user/sendMsg", "/user/login"
        };

        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //如果不需要处理 直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //1判断是否登录 如果已经登录了 直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录,用户id为{}", request.getSession().getAttribute("employee"));

            //把id存进线程里
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContextUtils.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }
        //2判断是否登录 如果已经登录了 直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录,用户id为{}", request.getSession().getAttribute("user"));

            //把id存进线程里
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContextUtils.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //如果未登录 则返回未登录结果，通过输出流方式向客户端响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }


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
