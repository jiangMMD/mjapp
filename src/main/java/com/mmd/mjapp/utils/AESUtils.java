package com.mmd.mjapp.utils;

import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * AES 对称加解密
 * Created by Administrator on 2017/9/13.
 */
public class AESUtils {

    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 初始化密钥
     *
     * @return byte[] 密钥
     * @throws Exception
     */
    public static byte[] initSecretKey() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小
        //AES 要求密钥长度为 128
        kg.init(128);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 转换密钥
     *
     * @param key   二进制密钥
     * @return 密钥
     */
    private static Key toKey(byte[] key){
        //生成密钥
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密字符串
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptToHex(String data, String key) throws Exception {
        if(StringUtils.isEmpty(data)) {
            throw new Exception("加密数据不能为空");
        }
        if(key == null) {
            throw new Exception("秘钥不能为空");
        }
        byte[] bts =  key.getBytes();
        KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);// 创建AES的Key生产者
        //判断系统
        if(isOSLinux()) {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            kgen.init(128, secureRandom);
        }else{
            kgen.init(128, new SecureRandom(key.getBytes()));
        }
        //生成128位秘钥
        SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
        Key toKey = toKey(secretKey.getEncoded());
        byte[] enBtyes = encrypt(data.getBytes(), toKey);
        return bytesTOHexStr(enBtyes);
    }

    /**
     * 解密AES加密之后的字符数据
     * @param data
     * @param key
     * @return
     */
    public static String decryptWithHex(String data, String key) throws Exception {
        byte[] bytes  =hexStringToBytes(data);

        byte[] bts =  key.getBytes();
        KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);// 创建AES的Key生产者
        //生成128位秘钥
        //判断系统
        if(isOSLinux()) {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            kgen.init(128, secureRandom);
        }else{
            kgen.init(128, new SecureRandom(key.getBytes()));
        }
        SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
        Key toKey = toKey(secretKey.getEncoded());
        byte[] bytes1 = decrypt(bytes, toKey.getEncoded());
        return new String(bytes1);
    }

    public static boolean isOSLinux() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        System.out.println(os);
        if (os != null && os.toLowerCase().contains("linux")) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 加密
     *
     * @param data  待加密数据
     * @param key   密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data,Key key) throws Exception {
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data  待加密数据
     * @param key   二进制密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data,byte[] key) throws Exception {
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
    }


    /**
     * 加密
     *
     * @param data  待加密数据
     * @param key   二进制密钥
     * @param cipherAlgorithm   加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception {
        //还原密钥
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }

    /**
     * 加密
     *
     * @param data  待加密数据
     * @param key   密钥
     * @param cipherAlgorithm   加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }



    /**
     * 解密
     *
     * @param data  待解密数据
     * @param key   二进制密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data,byte[] key) throws Exception {
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data  待解密数据
     * @param key   密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data,Key key) throws Exception {
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data  待解密数据
     * @param key   二进制密钥
     * @param cipherAlgorithm   加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception {
        //还原密钥
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    /**
     * 解密
     *
     * @param data  待解密数据
     * @param key   密钥
     * @param cipherAlgorithm   加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }


    private static String showByteArray(byte[] data){
        if(null == data){
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for(byte b:data){
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }

    /**
     * 16进制字符转换为btyes[];
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * bytes[] 转换为16进制字符串
     * @param src
     * @return
     */
    private static String bytesTOHexStr(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        String res = encryptToHex("您好", "123");
        System.out.println(res);
        System.out.println(decryptWithHex(res, "123"));
    }

}
