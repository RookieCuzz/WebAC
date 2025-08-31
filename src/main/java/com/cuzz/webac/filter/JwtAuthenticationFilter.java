package com.cuzz.webac.filter;

import com.cuzz.webac.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;



    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService= jwtService;

    }
    /**
     *
     * @param request  请求
     * @param response  返回载荷(可以修改)
     * @param filterChain  过滤器链(分前向后向)
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        // 获取请求的URI
        String requestPath = request.getRequestURI();

        // 排除 /login 路径和其他不需要验证的路径
        if ("/login".equals(requestPath) || "/register".equals(requestPath)) {
            filterChain.doFilter(request, response); // 直接放行，不需要JWT验证
            // 继续处理其他请求
            return;
        }

        // 获取 Authorization 头部的 JWT token
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // 验证 JWT
                String username = jwtService.validateToken(token);
                // 在这里可以设置用户身份信息（示例中忽略）
                System.out.println("Authenticated user: " + username);
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        // 继续处理其他请求
        filterChain.doFilter(request, response);
    }
}
