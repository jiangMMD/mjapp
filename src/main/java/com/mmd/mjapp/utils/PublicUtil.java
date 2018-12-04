package com.mmd.mjapp.utils;


import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/4/19.
 */
public class PublicUtil {

    private PublicUtil() {
    }

    private static int seqno = 0;

    public static String getUID(String user_no) throws Exception {

        return dupStr(user_no, 10) + String.valueOf(System.currentTimeMillis()).substring(1, 10) + dupStr(getSeqno(), 5);

    }

    public static String getNoID(String user_no) throws Exception {

        return String.valueOf(System.currentTimeMillis()).substring(1, 10) + dupStr(getSeqno(), 5);

    }

    private static synchronized String getSeqno() {
        seqno++;
        if (seqno == 10000000) {
            seqno = 1;
            return "0000001";

        } else {
            return String.valueOf(seqno);
        }

    }

    public static String getFileNm(String user_no, String file_type) {

        return user_no + file_type + dupStr(getSeqno(), 7);

    }

    private static synchronized String getBusno(String business_no) {
        int bussiness = Integer.parseInt(business_no);
        bussiness++;
        if (bussiness == 1000000) {
            bussiness = 1;
            return "0000001";

        } else {
            return String.valueOf(bussiness);
        }
    }

    /**
     * 字符串组合，不够添0
     *
     * @param str
     * @param length
     * @return
     */
    public static String dupStr(String str, int length) {
        StringBuffer sb = new StringBuffer();

        int len = str.length();
        if (len >= length)
            return str.substring(len - length, len);

        len = length - len;
        for (int i = 0; i < len; i++) {
            sb.append("0");
        }
        sb.append(str);

        return sb.toString();
    }


    public static String getDate(String... params) {
        Date dateNow = new Date();
        String format = "yyyy-MM-dd";
        if (params.length > 0) {
            format = params[0];
        }
        SimpleDateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(format);
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        return dateFormat.format(dateNow);
    }

    /**
     * 获取当前时间，格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDateTime() {
        Date dateNow = new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(dateNow);
    }

    /**
     * 获取当前日期的下几天日期
     *
     * @return
     */
    public static String getAssignDate(int nextday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + nextday);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 获取Map单列集合
     *
     * @param lists
     * @param key
     * @return
     */
    public static List<Object> toSingList(List<Map<String, Object>> lists, String key) {
        List<Object> list = new ArrayList();
        for (Map<String, Object> map : lists) {
            list.add(map.get(key));
        }
        return list;
    }

    /**
     * 获取Map单列集合
     *
     * @param lists
     * @param key
     * @return
     */
    public static List<String> toSingListwithStr(List<Map<String, Object>> lists, String key) {
        List<String> list = new ArrayList();
        for (Map<String, Object> map : lists) {
            list.add(String.valueOf(map.get(key)));
        }
        return list;
    }

    public static String getIds(String ids) {
        if (ids == null || ids.length() == 0)
            return "";
        StringBuffer idBuffer = new StringBuffer();
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            if (i == 0) {
                idBuffer.append("'" + id[i] + "'");
            } else {
                idBuffer.append(",'" + id[i] + "'");
            }
        }
        return idBuffer.toString();
    }

    public static List<String> toListByIds(String ids) {
        List<String> list = new ArrayList<>();
        if (!StringUtils.isEmpty(ids)) {
            String[] idArray = ids.split(",");
            list = Arrays.asList(idArray);
        }
        return list;
    }

    public static Set<String> toSetByIds(String ids) {
        return new HashSet<>(toListByIds(ids));
    }

    public static Date strDateToDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toDayStartDateTime(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dateStr + " 00:00:00";
    }

    public static String toDayEndDateTime(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dateStr + " 23:59:59";
    }

    public static String mapValToString(Object obj) {
        if (obj != null) {
            return obj.toString();
        } else {
            return "";
        }
    }


    /**
     * Map转实体类
     *
     * @param map    需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空
     * @param entity 需要转化成的实体类
     * @return
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) throws Exception {
        T t = null;
        t = entity.newInstance();
        for (Field field : entity.getDeclaredFields()) {
            if (map.containsKey(field.getName())) {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object object = map.get(field.getName());
                if (object != null && field.getType().isAssignableFrom(object.getClass())) {
                    field.set(t, object);
                }
                field.setAccessible(flag);
            }
        }
        return t;
    }

    //将objMap直接转换为stringmap, 不做任何转换操作
    public static Map<String, String> objMapToStrMap(Map<String, Object> params) {
        Map<String, String> returnMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                returnMap.put(entry.getKey(), "");
            } else {
                returnMap.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return returnMap;
    }

    public static Map<String, Object> strMapToObjMap(Map<String, String> params) {
        Map<String, Object> returnMap = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }

    public static String toSqlId(String ids) {
        if (ids == null || ids.length() == 0)
            return "";
        StringBuffer idBuffer = new StringBuffer();
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            if (i == 0) {
                idBuffer.append("'" + id[i]);
            } else {
                idBuffer.append("," + id[i]);
            }
        }
        if (id.length > 0) {
            idBuffer.append("'");
        }
        return idBuffer.toString();
    }

    /**
     * @Date:15:50 2017/8/15
     * @Description:获取请求所属ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    public static boolean hasTrim(String shipType) {
        if (shipType.contains(",")) {
            String[] ss = shipType.split(",");
            for (String s : ss) {
                if (StringUtils.isEmpty(s)) {
                    return true;
                }
            }
            return false;
        } else {
            return StringUtils.isEmpty(shipType);
        }
    }

    public static Date getNowDateNoHSM() {
        String dataStr = PublicUtil.getDate("yyyy-MM-dd");
        Date date = PublicUtil.strDateToDate(dataStr, "yyyy-MM-dd");
        return date;
    }

    /**
     * 去除特殊字符
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String toSql(String str) {
        if (str == null)
            return "";
        else {
            str = str.trim();
            return replaceAll(str, "'", "''");
        }
    }

    /**
     * Method replaceAll.
     *
     * @param original
     * @param regex
     * @param replacement
     * @return String
     */
    public static String replaceAll(
            String original,
            String regex,
            String replacement) {

        String sResult = "";
        int nLeft;
        int nRight;
        int nLen = regex.length();
        nLeft = original.indexOf(regex);
        if (nLeft < 0)
            return original;
        while (nLeft >= 0) {
            sResult = sResult + original.substring(0, nLeft);
            nRight = nLeft + nLen;
            sResult += replacement;
            original = original.substring(nRight);
            nLeft = original.indexOf(regex);
        }
        sResult = sResult + original;
        return sResult;

    }

    public static Date getDateWithPattern(String dateStr, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(dateStr);
    }

    /**
     * 对象转换为Map
     *
     * @param object
     * @return
     */
    public static Map<String, Object> objToMap(Object object, boolean hasNull) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (hasNull) {
                //如果可以为空
                returnMap.put(field.getName(), field.get(object));
            } else {
                if (field.get(object) != null) {
                    returnMap.put(field.getName(), field.get(object));
                }
            }
        }
        return returnMap;
    }

    /**
     * 获取下几个月的日期
     */
    public static String getNextMonthDay(int monthNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + monthNum);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 判断对象是否是null或empty
     */
    public static boolean isEmptyObj(Object obj) {
        return obj == null || "".equals(obj);
    }

}
