package com.mmd.mjapp.controller;

import com.mmd.mjapp.constant.FileConstant;
import com.mmd.mjapp.model.OperInfo;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.UserService;
import com.mmd.mjapp.utils.AESUtils;
import com.mmd.mjapp.utils.PropertyLoad;
import com.mmd.mjapp.utils.PublicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取验证码token
     * @return
     * @throws Exception
     */
    @PostMapping("/getYzmToken")
    public Result getYzmToken() throws Exception {
        //获取验证码token
        String yzmRandom = "YZM" + RandomStringUtils.randomNumeric(8);
        String aesCode = AESUtils.encryptToHex(yzmRandom, FileConstant.AESKey);
        return new Result(1, "查询成功！", aesCode);
    }

    /**
     * 客户注册
     * @return
     */
    @PostMapping("/regist")
    public Result regist(@RequestBody OperInfo operInfo) throws Exception {
        return userService.regist(operInfo);
    }

    /**
     * 发送注册时的验证码，
     */
    @PostMapping("/sendRegistCode")
    public Result sendRegistCode(@RequestBody OperInfo operInfo) throws Exception {
        return userService.sendRegistCode(operInfo);
    }

    /**
     * 发送验证码， 登录时
     */
    @PostMapping("/sendLoginCode")
    public Result sendLoginCode(@RequestBody OperInfo operInfo) throws Exception {
        return userService.sendLoginCode(operInfo);
    }

    /**
     * 客户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody OperInfo operInfo) throws Exception {
        return userService.login(operInfo);
    }


    /**
     * 校验身份证号码是否正确
     *
     * @param identity
     * @return
     */
    private boolean checkIdentity(String identity) {
        System.out.println(identity);
        if (StringUtils.isEmpty(identity)) {
            return false;
        }
        String regex = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(identity);
        return m.matches();
    }

    /**
     * 处理文件，
     */
    private String dealFile(MultipartFile file, String dir) throws IOException {
        if (file == null) {
            return null;
        }
        String file_sourcename = file.getOriginalFilename();
        String ref = file_sourcename.substring(file_sourcename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replaceAll("_", "").substring(0, 8);
        String filename = uuid + ref;
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File bookFile = new File(dir + File.separator + filename);
        file.transferTo(bookFile);
        return filename;
    }



    /**************************用户修改信息模块，需要更新redis中的信息*******************************/
    @RequestMapping("/updateHeadImg")
    public Result updateHeadImg(MultipartFile headImgFile) throws IOException {
        if(headImgFile == null || headImgFile.isEmpty()) {
            return new Result().fail("头像图片不能为空");
        }else if(headImgFile.getSize() > 1024*1024*2) {
            return new Result().fail("请上传不大于2M的图片");
        }else if(!isImg(headImgFile)) {
            return new Result().fail("请上传PNG、JPG格式的文件！");
        }
        String filename = dealFile(headImgFile, PropertyLoad.getProperty("user.imgDir"));
        String url = PropertyLoad.getProperty("user.ngImg") + filename;
        return userService.updateHeadImg(url);
    }

    //检查文件是否是图片
    private boolean isImg(MultipartFile headImgFile) {
        String filename = headImgFile.getOriginalFilename();
        String extUpp = filename.substring(filename.lastIndexOf(".") + 1);
        System.out.println(extUpp);
        return extUpp.toUpperCase().matches("^[(JPRG)|JPG)|(PNG)|(GIF)]+$");
    }

    /**
     * 关联MMD
     * @param params
     * @return
     */
    @RequestMapping("/revelanceMMD")
    public Result revelanceMMD(@RequestBody Map<String, Object> params) {
        return userService.revelanceMMD(params);
    }

    /**
     * 查询MMD余额
     */
    @RequestMapping("/queryMMDNum")
    public Result queryMMDNum() {
        return userService.queryMMDNum();
    }


}