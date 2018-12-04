package com.mmd.mjapp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/7/7.
 */
public class PropertyLoad {
    private static Logger logger = LoggerFactory.getLogger(PropertyLoad.class);
    private static Properties properties;

    static {
        loadProps();
    }

    private static void loadProps() {
        logger.info("开始加载系统配置文件");
        properties = new Properties();
        InputStream in = null;
        try {
            in = PropertyLoad.class.getClassLoader().getResourceAsStream("system.properties");
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("加载系统配置失败！" + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("关闭流失败！" + e);
                }
            }
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            loadProps();
        }
        return properties.getProperty(key);
    }

}
