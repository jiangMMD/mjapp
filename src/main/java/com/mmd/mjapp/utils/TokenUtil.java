package com.mmd.mjapp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by Administrator on 2017/9/29.
 */
public class TokenUtil {
    private static Logger log = LoggerFactory.getLogger(TokenUtil.class);
    private static String randomKey = "xiaoxiangzu";
    private static Key key = getKeyInstance();

    private static Key getKeyInstance() {
        byte[] bytes = Base64Codec.BASE64.decode(randomKey);
        SecretKey secretKey = new SecretKeySpec(bytes, "AES");
        return secretKey;
    }


    //生成token
    public static String createJavaWebToken(String userId, String subject) {
        Claims defaultClaims = new DefaultClaims();
        defaultClaims.setId(userId);
        defaultClaims.setSubject(subject);
        return Jwts.builder().setClaims(defaultClaims).signWith(SignatureAlgorithm.HS256, key).compact();
    }

    //生成刷新的token
    public static String createRefrestToken() {
        return Jwts.builder().setPayload(RandomStringUtils.random(16)).signWith(SignatureAlgorithm.HS256, key).compact();
    }


    public static Claims verifyJavaWebToken(String jwt) {
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
