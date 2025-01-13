package com.cuzz.webac.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
public class IpBlacklistFilter implements Filter {

    // 模拟的黑名单 IP 列表，可以改为从数据库或配置文件中读取
    private final List<String> blacklistIps = Arrays.asList("192.168.1.100", "10.0.0.50");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 可以在这里进行初始化操作
    }

//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        // 获取请求的 IP 地址
//        String remoteIp = httpRequest.getRemoteAddr();
//
//        // 判断是否在黑名单中
//        if (blacklistIps.contains(remoteIp)) {
//            // 如果在黑名单中，拒绝请求
//            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 返回 403 Forbidden
//            httpResponse.getWriter().write("Your IP is blacklisted.");
//            return;  // 拦截请求，不继续往下执行
//        }
//
//        // 如果不在黑名单中，继续请求处理链
//        chain.doFilter(request, response);
//    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        System.out.println("LoveYou");
        // 获取请求的 IP 地址
        String remoteIp = httpRequest.getRemoteAddr();

        // 判断是否在黑名单中
        if (blacklistIps.contains(remoteIp)) {
            // 如果在黑名单中，拒绝请求
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 返回 403 Forbidden
            httpResponse.getWriter().write("Your IP is blacklisted.");
            return;  // 拦截请求，不继续往下执行
        }

        // 如果不在黑名单中，继续请求处理链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 可以在这里进行资源释放
    }
}
