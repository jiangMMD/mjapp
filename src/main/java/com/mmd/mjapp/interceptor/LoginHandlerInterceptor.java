package com.mmd.mjapp.interceptor;

import com.alibaba.fastjson.JSON;
import com.mmd.mjapp.config.RedisClient;
import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.dao.UserDao;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.utils.TokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserDao userDao;

    /**
     * 检测session中是否存有对应的token
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String token = request.getHeader("token");
        if (token != null) {
            //校验token值。
            Claims claims = TokenUtil.verifyJavaWebToken(token);
            if (claims != null) {
                //查询该token是否存在
                String id = claims.getId();
                if (!StringUtils.isEmpty(id)) {
                    User user = redisUtils.getUserInfo(id);
                    if (user != null) {
                        request.setAttribute("user", user);
                        request.setAttribute("token", token);
                        return true;
                    }
                }
            }
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            noAccess(response);
            return false;
        }
        boolean flg = false;
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if ("token".equals(name)) {
                String tokenVal = cookie.getValue();
                //校验token值。
                Claims claims = TokenUtil.verifyJavaWebToken(tokenVal);
                //说明该token是合法值
                if (claims != null) {
                    //查询该token是否存在
//                    String id = redisClient.get(tokenVal);
                    String id = claims.getId();
                    System.out.println("客户ID:" +id);
                    if (!StringUtils.isEmpty(id)) {
//                        User user = (User) redisClient.getObj("user_" + id);
                        User user = redisUtils.getUserInfo(id);
                        if (user != null) {
                            request.setAttribute("user", user);
                            request.setAttribute("token", tokenVal);
                            flg = true;
                        }
                    } else {
                        //根据该tokenVal去数据库加载数据， 如果该token存在于用户数据表中，那么直接重新加载用户数据到redis中去
                        User userDb = userDao.getUserWithToken(tokenVal);
                        redisUtils.setUserInfo(userDb);
                        request.setAttribute("user", userDb);
                        request.setAttribute("token", tokenVal);
                        flg = true;
                    }
                }
            }
        }
        if (!flg) {
            noAccess(response);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }


    public static void noAccess(HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", "Access Token not allowed");
        outJson(response, result);
    }

    /****
     * 输出Map
     */
    public static void outJson(HttpServletResponse response, Object object) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String json = JSON.toJSONString(object);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Pragma", "No-cache");
            response.setContentLength(json.getBytes("UTF-8").length);
            out.print(json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
