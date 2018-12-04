package com.mmd.mjapp.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.mmd.mjapp.constant.SmsConstant;
import com.mmd.mjapp.pjo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 阿里短信发送服务
 */
public class SendSmsUtil {

    private static Logger logger = LoggerFactory.getLogger(SendSmsUtil.class);
    static {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");
    }

    public static void main(String[] args) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "嵇亮");
        params.put("book_no", "SZ-XXZ-20180526-001");
        params.put("time", PublicUtil.getDate("yyyy-MM-dd"));
        params.put("money", "925.00");
        //15262928357
        Result result = send("13788957291", "SMS_136176440", JSON.toJSONString(params));
        System.out.println(result.getMessage());
    }


    //初始化ascClient需要的几个参数
    private final static String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    private final static String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）

    //替换成你的AK
    private final static String accessKeyId = "LTAIdF00HsZdZi3k";
    private final static String accessKeySecret = "NBb4yxDYb5El4vMyRkGxIlWv0crwvA";

    public static Result send(String phone, String templateCode, String json) throws ClientException {
        logger.info("--------------------开始发送短信,手机号:"+phone+"---------------------");
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(SmsConstant.SIGNNAME);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(json);

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//        request.setOutId("yourOutId");
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        String code = sendSmsResponse.getCode();
        String msg = "";
        switch (code) {
            case "OK":
                msg = "短信发送成功";
                break;
            case "isp.RAM_PERMISSION_DENY":
                msg = "RAM权限DENY";
                break;
            case "isv.OUT_OF_SERVICE":
                msg = "业务停机";
                break;
            case "isv.PRODUCT_UN_SUBSCRIPT":
                msg = "未开通云通信产品的阿里云客户";
                break;
            case "isv.PRODUCT_UNSUBSCRIBE":
                msg = "产品未开通";
                break;
            case "isv.ACCOUNT_NOT_EXISTS":
                msg = "账户不存在";
                break;
            case "isv.ACCOUNT_ABNORMAL":
                msg = "账户异常";
                break;
            case "isv.SMS_TEMPLATE_ILLEGAL":
                msg = "短信模板不合法";
                break;
            case "isv.SMS_SIGNATURE_ILLEGAL":
                msg = "短信签名不合法";
                break;
            case "isv.INVALID_PARAMETERS":
                msg = "参数异常";
                break;
            case "isp.SYSTEM_ERROR":
                msg = "系统错误";
                break;
            case "isv.MOBILE_NUMBER_ILLEGAL":
                msg = "非法手机号";
                break;
            case "isv.MOBILE_COUNT_OVER_LIMIT":
                msg = "手机号码数量超过限制";
                break;
            case "isv.TEMPLATE_MISSING_PARAMETERS":
                msg = "模板缺少变量";
                break;
            case "isv.BUSINESS_LIMIT_CONTROL":
//                msg = "业务限流";
                msg = "你的短信发送过于频繁，请稍后尝试";
                break;
            case "isv.INVALID_JSON_PARAM":
                msg = "JSON参数不合法，只接受字符串值";
                break;
            case "isv.BLACK_KEY_CONTROL_LIMIT":
                msg = "黑名单管控";
                break;
            case "isv.PARAM_LENGTH_LIMIT":
                msg = "参数超出长度限制";
                break;
            case "isv.PARAM_NOT_SUPPORT_URL":
                msg = "不支持URL";
                break;
            case "isv.AMOUNT_NOT_ENOUGH":
                msg = "账户余额不足";
                break;
            default:
                msg = "未知错误";
                break;
        }
        if (Objects.equals(msg, "短信发送成功")) {
            return new Result(1, msg);
        }
        logger.info("--------------------发送短信结束, 结果"+msg+"---------------------");
        return new Result(0, "短信发送失败：" + msg);
    }

}
