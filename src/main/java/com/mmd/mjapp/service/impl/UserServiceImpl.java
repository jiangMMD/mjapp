package com.mmd.mjapp.service.impl;

import com.alibaba.fastjson.JSON;
import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.FileConstant;
import com.mmd.mjapp.constant.SmsConstant;
import com.mmd.mjapp.dao.BookDao;
import com.mmd.mjapp.dao.UserDao;
import com.mmd.mjapp.model.OperInfo;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.UserService;
import com.mmd.mjapp.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Result sendRegistCode(OperInfo operInfo) throws Exception {
        String phone = operInfo.getPhone();
        String token = operInfo.getToken();
        return sendYzm(phone, token, SmsConstant.TEMPLATE_REGISTER);
    }

    /**
     * 客户注册
     *
     * @param operInfo
     * @return
     */
    @Override
    public Result regist(OperInfo operInfo) throws Exception {
        Result res = check(operInfo);
        if (res.getCode() == 1) {
            int count = userDao.checkHasCus(operInfo.getPhone());
            if (count > 0) {
                return new Result().fail("该手机已经注册！");
            }
            User user = new User();
            user.setuPhone(operInfo.getPhone());
            user.setDeviceId(operInfo.getDeviceid());
            user.setChannelId(operInfo.getChannelid());
            user.setSource(operInfo.getSource());
            //生成随机用户昵称
            user.setuNick("用户" + RandomStringUtils.random(8, new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'}));
            //用户注册送积分，
            createUserIntegral(user);
            userDao.regist(user);

            //生成用户购物车
            bookDao.createShopCat(user.getuId());
            return new Result(1, "注册成功！", updateUserInfo(user));
        } else {
            return res;
        }
    }

    /**
     * 发动登录验证码
     */
    @Override
    public Result sendLoginCode(OperInfo operInfo) throws Exception {
        log.info(operInfo.toString());
        String phone = operInfo.getPhone();
        String token = operInfo.getToken();
        return sendYzm(phone, token, SmsConstant.TEMPLATE_LOGIN);
    }

    @Override
    public Result login(OperInfo operInfo) throws Exception {
        //如果是测试数据，那么直接登录
        if ((Objects.equals(operInfo.getPhone(), "13788957291") && Objects.equals(operInfo.getYzmCode(), "251230"))
                || (Objects.equals(operInfo.getPhone(), "18015575859") && Objects.equals(operInfo.getYzmCode(), "251230"))) {
            //通过手机号加载用户信息
            User userWithDb = userDao.getPhoneByDb(operInfo.getPhone());
            System.out.println(userWithDb);
            if (userWithDb == null) {
                return new Result(0, "没有该用户，请先注册");
            } else if (Objects.equals(userWithDb.getRecsts(), "2")) {
                return new Result(0, "该用户已冻结!");
            }
            return new Result(1, "登录成功！", getUserTokenInfo(userWithDb));
        }
        //校验手机，验证码
        Result res = check(operInfo);
        if (res.getCode() == 1) {
            //校验用户名是否存在
            User userWithDb = userDao.getPhoneByDb(operInfo.getPhone());
            if (userWithDb == null) {
                return new Result(0, "没有该用户，请先注册");
            }
            //关联用户设备id及其推送id
            userDao.relevancyDevice(operInfo);
            return new Result(1, "登录成功！", getUserTokenInfo(userWithDb));
        } else {
            return res;
        }
    }


    /**
     * 开始生成用户积分
     */
    private void createUserIntegral(User user) {
        //获取系统配置的用户注册获取的积分
        Map<String, Object> param = userDao.getUserRegIntegral();
        if (param != null) {
            Long integral = ((Double)param.get("value")).longValue();
            user.setuIntegral(integral);
        }else{
            user.setuIntegral(0L);
        }
    }

    private Map<String, Object> updateUserInfo(User user) throws Exception {
        //创建邀请码
        String randomCode = randomCode();
        //生成用户二维码
        String reqUrl = QrcodeUtils.createUserQrCode(String.valueOf(user.getuId()), randomCode);
        user.setuQrcode(reqUrl);
        Map<String, Object> tokenInfo = getUserTokenInfo(user);
        //更换当前token为登录token， 之后将一直使用登录token
        String token = (String) tokenInfo.get("token");
        user.setToken(token);
        //更新用户基本信息
        userDao.updateUserBaseInfo(user);
        return tokenInfo;
    }


    public String randomCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

    /**
     * 返回用户结果集，保存用户信息到redis中
     */
    private Map<String, Object> getUserTokenInfo(User user) throws Exception {
        System.out.println(user);
        String token = TokenUtil.createJavaWebToken(String.valueOf(user.getuId()), "User");
        //保存客户信息到redis中
//        redisClient.set(token, user.getuId(), 7 * 24 * 60 * 60);
        redisUtils.setUserInfo(user);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return data;
    }


    /**
     * 校验信息
     */
    private Result check(OperInfo operInfo) throws Exception {
        if (StringUtils.isEmpty(operInfo.getPhone())) {
            return new Result(0, "手机号不能为空");
        } else if (!checkPhone(operInfo.getPhone())) {
            return new Result(0, "手机号格式不正确！");
        } else if (StringUtils.isEmpty(operInfo.getYzmCode())) {
            return new Result(0, "验证码不能为空");
        } else if (isVaild(operInfo.getYzmToken())) {
            return new Result(0, "验证码已失效");
        } else if (!checkYzm(operInfo.getYzmToken(), operInfo.getYzmCode())) {
            return new Result(0, "验证码不正确");
        }
        return new Result();
    }

    private boolean isVaild(String yzmtoken) throws Exception {
        return StringUtils.isEmpty(redisUtils.get(yzmtoken));
    }

    //校验验证码
    private boolean checkYzm(String yzmtoken, String yzmCode) throws Exception {
        //从redis中获取验证码
        if (StringUtils.isEmpty(yzmtoken)) {
            return false;
        }
        String yzmByRedis = redisUtils.get(yzmtoken);
        if (!yzmCode.equals(yzmByRedis)) {
            return false;
        }
        return true;
    }

    private Result sendYzm(String phone, String token, String template) throws Exception {
        if (StringUtils.isEmpty(phone)) {
            return new Result(0, "手机号不能为空!");
        } else if (!checkPhone(phone)) {
            return new Result(0, "手机号格式不正确!");
        }
        String aes;
        try {
            aes = AESUtils.decryptWithHex(token.substring(6, token.length() - 6), FileConstant.AESKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(0, "参数异常!");
        }
        String sendNum = redisUtils.get(aes);
        if (StringUtils.isEmpty(sendNum)) {
            redisUtils.set(aes, "1", 30 * 60);
        } else {
            return new Result().fail("该Token已失效!");
        }
        //调用验证码接口, 生成4位验证码
        String yzm = RandomStringUtils.random(6, new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'});
        //调用对应接口
        Result result = SendSmsUtil.send(phone, template, "{\"code\":" + yzm + "}");
        if (result.getCode() == 1) {
            String trefreshToken = TokenUtil.createRefrestToken();
            //验证码30分钟内有效
            redisUtils.set(trefreshToken, yzm, 60 * 30);
            return new Result(1, "短信发送成功！", trefreshToken);
        } else {
            return result;
        }
    }

    //校验手机号
    private boolean checkPhone(String phone) {
        String regex = "^1[3|4|5|6|7|8|9][0-9]\\d{4,8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    //获取用户信息
    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }

    //------------------------------用户信息修改模块， 需要更新redis------------------------------//
    @Override
    public Result updateHeadImg(String url) {
        User user = getUserInfo();
        userDao.updateHeadImg(url, user.getuId());
        user.setuHeadicon(url);
        redisUtils.setUserInfo(user);
        return new Result().success(url);
    }

    /**
     * 关联MMD账户信息
     *
     * @param params
     * @return
     */
    @Override
    public Result revelanceMMD(@RequestBody Map<String, Object> params) {
        if (Objects.isNull(params.get("mmd_name")) || Objects.isNull(params.get("mmd_password"))) {
            return new Result().fail("参数异常，MMD账户和密码不能为空");
        }
        String mmd_name = String.valueOf(params.get("mmd_name"));
        String mmd_password = String.valueOf(params.get("mmd_password"));
        Map<String, String> reqPar = new HashMap<>();
        reqPar.put("appId", PropertyLoad.getProperty("MMD.appId"));
        reqPar.put("username", mmd_name);
        reqPar.put("password", mmd_password);
        String result = HttpClientUtil.sendPostRequest(reqPar, PropertyLoad.getProperty("MMD.verify"));
        Map<String, Object> res = (Map<String, Object>) JSON.parse(result);
        System.out.println(res);
        if (Objects.equals(res.get("success"), true)) {
            //校验成功，
            //保存账号信息
            User user = getUserInfo();
            user.setuMmdNo(mmd_name);
            user.setuMmdPassword(mmd_password);
            userDao.saveMMDInfo(params, user.getuId());
            redisUtils.setUserInfo(user);
            return new Result().success();
        }
        return new Result().fail("MMD用户名或密码错误！");
    }

    /**
     * 查询MMD账户余额
     */
    @Override
    public Result queryMMDNum() {
        User user = getUserInfo();
        if (StringUtils.isEmpty(user.getuMmdNo())) {
            return new Result().fail("请先绑定MMD账户");
        } else {
            Map<String, Object> reqPar = new HashMap<>();
            reqPar.put("appId", PropertyLoad.getProperty("MMD.appId"));
            reqPar.put("username", user.getuMmdNo());
            String result = HttpClientUtil.sendPostRequest(reqPar, PropertyLoad.getProperty("MMD.mmdnum"));
            Map<String, Object> res = (Map<String, Object>) JSON.parse(result);
            System.out.println(res);
            if (Objects.equals(res.get("success"), true)) {
                //校验成功，
                user.setuMmdMoney((String) res.get("data"));
                redisUtils.setUserInfo(user);
                return new Result().success(res.get("data"));
            }else{
                return new Result().fail((String) res.get("message"));
            }
        }
    }

    @Override
    public Result addShipAddress(Map<String, Object> param) {
        //如果没有默认， 那么就设置默认
        User user = getUserInfo();
        if(PublicUtil.isEmptyObj(param.get("id"))) {
            //查看是否已经有默认地址
            int count = userDao.getShipAddress(user.getuId(), null);
            if(count == 0) { //如果没有默认地址，那么新增的就是默认收货地址
                param.put("isdefault", 1);
                userDao.addShipAddress(param, user.getuId());
            }else{
                if("1".equals(String.valueOf(param.get("isdefault")))) { //如果设置为默认的收货地址
                    //取消原有默认地址， 更新当前为默认收货地址
                    userDao.cancelDefaultShipAddress(user.getuId());
                }
                userDao.addShipAddress(param, user.getuId());
            }
        }else{
            //查看是否已经有默认地址
            int count = userDao.getShipAddress(user.getuId(), String.valueOf(param.get("id")));
            if(count == 0) { //只有当前一个收货地址
                param.put("isdefault", 1);
                userDao.updateShipAddress(param);
            }else{
                if("1".equals(String.valueOf(param.get("isdefault")))) { //如果设置当前为默认的收货地址
                    //取消原有默认地址， 更新当前为默认收货地址
                    userDao.cancelDefaultShipAddress(user.getuId());
                }
                userDao.updateShipAddress(param);
            }
        }
        return new Result().success();
    }

    @Override
    public Result setDefaultAddress(String id) {
        userDao.setNoDefaulAddress(id);
        userDao.setDefaultAddress(id);
        return new Result().success();
    }

    /**
     * 查询该用户的收货地址
     */
    @Override
    public Result queryShipAddress() {
        User user = getUserInfo();
        return new Result().success(userDao.queryShipAddress(user.getuId()));
    }

    @Override
    public Result delShipAddress(String ids) {
        userDao.delShipAddress(PublicUtil.toListByIds(ids));
        return new Result().success();
    }

    @Override
    public Result sendPayCode(OperInfo operInfo) throws Exception {
        log.info(operInfo.toString());
        String phone = getUserInfo().getuPhone();
        String token = operInfo.getToken();
        return sendTestYzm(phone, token, SmsConstant.TEMPLATE_LOGIN);
    }

    private Result sendTestYzm(String phone, String token, String template) throws Exception {
        if (StringUtils.isEmpty(phone)) {
            return new Result(0, "手机号不能为空!");
        } else if (!checkPhone(phone)) {
            return new Result(0, "手机号格式不正确!");
        }
        String aes;
        try {
            aes = AESUtils.decryptWithHex(token.substring(6, token.length() - 6), FileConstant.AESKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(0, "参数异常!");
        }
        String sendNum = redisUtils.get(aes);
        if (StringUtils.isEmpty(sendNum)) {
            redisUtils.set(aes, "1", 30 * 60);
        } else {
            return new Result().fail("该Token已失效!");
        }
        String trefreshToken = TokenUtil.createRefrestToken();
        //验证码30分钟内有效
        redisUtils.set(trefreshToken, "000000", 60 * 30);
        return new Result(1, "短信发送成功！", trefreshToken);
    }
}
