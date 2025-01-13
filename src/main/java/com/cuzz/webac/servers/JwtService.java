package com.cuzz.webac.servers;

import com.cuzz.webac.utils.constants.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    // 生成JWT的方法
    public String generateToken(String username) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + jwtConfig.getExpirationTime();
        Date expirationDate = new Date(expMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey())
                .compact();
    }

    // 验证JWT并获取用户名
    public String validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
