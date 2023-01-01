package com.example.shortlink.common.util;

import com.example.shortlink.common.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.Signature;
import java.util.Date;

/**
 * @author 彭亮
 * @create 2023-01-01 12:31
 */
@Slf4j
public class JWTUtil {

    /**
     * 主题
     */
    private static final String SUBJECT = "link";

    /**
     * 加密密钥
     */
    private static final String SECRET = "10086";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "short-link";

    /**
     * token 过期时间 7天
     */
    private static final long EXPIRED = 1000 * 60 * 60 * 24 * 7;

    /**
     * 生成token
     *
     * @param loginUser
     * @return
     */
    public static String geneJsonWebToken(LoginUser loginUser) {
        if (loginUser == null) {
            throw new NullPointerException("对象为空");
        }
        String token = Jwts.builder().setSubject(SUBJECT)
                // 设置payload
                .claim("head_img", loginUser.getHeadImg())
                .claim("account_no", loginUser.getAccountNo())
                .claim("username", loginUser.getUsername())
                .claim("mail", loginUser.getMail())
                .claim("phone", loginUser.getPhone())
                .claim("auth", loginUser.getAuth())
                // 设置发布时间
                .setIssuedAt(new Date())
                // 设置过期时间
                .setExpiration(new Date(CommonUtil.getCurrentTimestamp() + EXPIRED))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        token = TOKEN_PREFIX + token;
        return token;
    }

    /**
     * token解密
     *
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJwt(token.replace(TOKEN_PREFIX, "")).getBody();
            return claims;
        } catch (Exception e) {
            log.error("jwt 解密失败");
            return null;
        }

    }

}
