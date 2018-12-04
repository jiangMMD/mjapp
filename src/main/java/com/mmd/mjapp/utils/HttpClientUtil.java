package com.mmd.mjapp.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


/**
 * 发送http请求
 *
 * @author xhb
 */
public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    static PoolingHttpClientConnectionManager cm = null;

    static HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount > 5) { //重试的次数
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                //超时
                return false;
            }
            if (exception instanceof UnknownHostException) {
                //目标服务不可达
                return false;
            }
            if (exception instanceof HttpHostConnectException) {
                //连接被拒绝
                return false;
            }
            if (exception instanceof SSLException) {
                //SSL捂手异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {//再次尝试
                return true;
            }
            return false;
        }
    };

    static {
        ConnectionSocketFactory factory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory layfactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", factory).register("https", layfactory).build();
        cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(50); //设置最大连接数
        cm.setDefaultMaxPerRoute(20); // 每个路由最大连接数
        //cm.setMaxPerRoute(new HttpRoute(host), 200); //设置目标主机的最大连接数
    }

    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm).setRetryHandler(retryHandler).build();
        return httpClient;
    }

    public static String sendPostRequestByCNWeb(Map params, String url) {
        HttpPost httpPost;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String result;
        try {
            long s1 = System.currentTimeMillis();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(3000).build();
            httpPost = new HttpPost(url); //含有中文的话需要处理
            httpPost.setConfig(config);
            httpPost.setEntity(new UrlEncodedFormEntity(getparams(params), "UTF-8"));
            response = getHttpClient().execute(httpPost);
            entity = response.getEntity();
            long s2 = System.currentTimeMillis();
            System.out.println("创建服务时间：" + (s2 - s1));
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    log.error("关闭entity流出现异常。" + e);
                    throw new RuntimeException("关闭entity流出现异常。" + e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new RuntimeException("关闭response流出现异常。" + e);
                }
            }
        }
        return result;
    }

    /**
     * 使用代理进行httpPost请求
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String sendPostRqByProxy(String url, Map params, String proxyIp, int proxyport, boolean isHttps) {
        HttpClientBuilder httpClientBuilder;
        CloseableHttpClient client;
        HttpHost httpHost;
        HttpHost proxy;
        HttpPost httpPost;
        String result;
        CloseableHttpResponse response = null;
        try {
            httpClientBuilder = HttpClientBuilder.create();
            client = httpClientBuilder.build();

            //目标请求的主机
            httpHost = new HttpHost(url, 80, isHttps ? "https" : "http");
            //设置代理地址、端口、协议类型
            proxy = new HttpHost(proxyIp, proxyport, isHttps ? "https" : "http");
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            httpPost = new HttpPost(url); //含有中文的话需要处理
            httpPost.setConfig(config);
            httpPost.setEntity(new UrlEncodedFormEntity(getparams(params), "UTF-8"));
            response = client.execute(httpHost, httpPost);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new RuntimeException("关闭response流出现异常。" + e);
                }
            }
        }
        return result;
    }

    /**
     * 发送简单post请求
     */
    @SuppressWarnings("rawtypes")
    public static String sendPostRequest(Map params, String url) {
        CloseableHttpClient hc;
        HttpPost httpPost;
        String result;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            hc = getHttpClient();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(20000).setSocketTimeout(20000).build();
            httpPost = new HttpPost(url); //含有中文的话需要处理
            httpPost.setConfig(config);
            httpPost.setEntity(new UrlEncodedFormEntity(getparams(params), "UTF-8"));
            response = hc.execute(httpPost);
            entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    log.error("关闭entity流出现异常。" + e);
                    throw new RuntimeException("关闭entity流出现异常。" + e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new RuntimeException("关闭response流出现异常。" + e);
                }
            }
        }
        return result;
    }

    /**
     * 发送简单get请求
     *
     * @param url
     * @return
     */
    public static String sendGetRequest(String url) {
        CloseableHttpClient hc = null;
        HttpGet httpGet = null;
        String result = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            hc = HttpClients.custom().setRetryHandler(retryHandler).build();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(3000).build();
            httpGet = new HttpGet(url); //含有中文的话需要处理
            httpGet.setConfig(config);
            response = hc.execute(httpGet);
            entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    log.error("关闭entity流出现异常。" + e);
                    throw new RuntimeException("关闭entity流出现异常。" + e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new RuntimeException("关闭response流出现异常。" + e);
                }
            }
        }
        return result;
    }


    @SuppressWarnings("rawtypes")
    public static List<NameValuePair> getparams(Map params) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        if (params != null) {
            Iterator iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = params.get(key);
                formparams.add(new BasicNameValuePair(key.toString(), value.toString()));
            }
        }
        return formparams;
    }

    /**
     * 多线程post请求
     *
     * @param reqList {url:请求地址; <br>params:{k:v == 请求参数}; <br>encode:编码;
     *                <br>otherMethod:{class:对应类的Class,method:方法,reftype:反射参数类型,refparam:反射对应方法参数}}; 当存在该参数时，会调用对应的方法
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object[] threadPost(List<Map<String, Object>> reqList) {
        Object[] results = new Object[reqList.size()]; //返回的结果集
        CloseableHttpClient httpClient = getHttpClient();
        try {
            PostThread[] postThreads = new PostThread[reqList.size()];
            for (int i = 0; i < reqList.size(); i++) {
                Map<String, Object> req = reqList.get(i);
                if (req.get("otherMethod") != null) {
                    postThreads[i] = new PostThread((Map<String, Object>) req.get("otherMethod"), i + 1);
                } else {
                    HttpPost httpPost = new HttpPost((String) req.get("url"));
                    postThreads[i] = new PostThread(httpClient, httpPost, (Map<String, Object>) req.get("params"), (String) req.get("encode"), i + 1);
                }
            }
            //开启所有线程
            for (PostThread pt : postThreads) {
                pt.start();
            }
            //等待所有线程执行完毕
            for (PostThread pt : postThreads) {
                pt.join();
            }
            for (int i = 0; i < reqList.size(); i++) {
                results[i] = postThreads[i].call();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("多线程Post请求异常--" + e);
        }
        return results;
    }


    public static String[] threadGet(List<Map<String, Object>> reqList) {
        String[] results = new String[reqList.size()];
        CloseableHttpClient httpclient = getHttpClient();
        try {
            GetThread[] getThreads = new GetThread[reqList.size()];
            for (int i = 0; i < reqList.size(); i++) {
                Map<String, Object> req = reqList.get(i);
                HttpGet get = new HttpGet((String) req.get("url"));
                getThreads[i] = new GetThread(httpclient, get, i + 1);
            }
            //执行线程
            for (GetThread gt : getThreads) {
                gt.start();
            }
            //设置所有线程执行完毕之后再执行后续代码
            for (GetThread gt : getThreads) {
                gt.join();
            }
            for (int i = 0; i < reqList.size(); i++) {
                results[i] = getThreads[i].call();
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("多线程get请求异常：" + e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }


    /**
     * post请求多线程回调
     *
     * @author xhb
     * <p>
     * 2016年8月5日
     */
    static class PostThread extends Thread implements Callable<Object> {
        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpPost httpPost;
        private Object result = null;
        private final int id;
        private Map<String, Object> map = null;

        public PostThread(CloseableHttpClient closehttpClient, HttpPost httpPost, Map<String, Object> params, String encode, int id) throws UnsupportedEncodingException {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setSocketTimeout(5000)
                    .build();
            httpPost.setConfig(requestConfig);
            encode = encode == null ? "UTF-8" : encode;
            List<NameValuePair> pairs = getparams(params);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, encode));
            }
            this.httpClient = closehttpClient;
            this.httpPost = httpPost;
            this.context = new BasicHttpContext();
            this.id = id;
        }

        //{class:对应类的Class,method:方法,reftype:反射参数类型,refparam:反射对应方法参数}}
        public PostThread(Map<String, Object> params, int id) {
            this.httpClient = null;
            this.httpPost = null;
            this.context = null;
            this.id = id;
            this.map = params;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void run() {
            if (map != null) {
                Class clazz = (Class) map.get("class");
                String methodName = (String) map.get("method");
                try {
                    Method method = clazz.getDeclaredMethod(methodName, (Class[]) map.get("reftype"));
                    Object obj = null;
                    if (map.get("refparam").getClass().isArray()) {
                        obj = method.invoke(clazz.newInstance(), (Object[]) map.get("refparam"));
                    } else {
                        obj = method.invoke(clazz.newInstance(), map.get("refparam"));
                    }
                    result = obj;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(map.get("refparam"));
                    log.error("thread--" + id + "反射出现错误：" + e);
                    throw new RuntimeException(e);
                }
            } else {
                CloseableHttpResponse response = null;
                try {
                    response = httpClient.execute(httpPost, context);
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        result = EntityUtils.toString(entity);
                    }
                } catch (Exception e) {
                    log.error("thread--" + id + "出现异常--" + e);
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e) {
                            log.error("关闭response流出现异常--" + e);
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        @Override
        public Object call() throws Exception {
            return result;
        }

    }

    /**
     * get请求多线程。
     *
     * @author xhb
     * <p>
     * 2016年8月5日
     */
    static class GetThread extends Thread implements Callable<String> {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private final int id;
        private String result = null;

        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, int id) {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
            this.id = id;
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run() {
            try {
                System.out.println(id + " - about to get something from " + httpget.getURI());
                CloseableHttpResponse response = httpClient.execute(httpget, context);
                try {
                    System.out.println(id + " - get executed");
                    // get the response body as an array of bytes
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        result = EntityUtils.toString(entity);
                    }
                } finally {
                    response.close();
                }
            } catch (Exception e) {
                System.out.println(id + " - error: " + e);
            }
        }

        @Override
        public String call() throws Exception {
            return result;
        }
    }

    /**
     * 通过cookie进行访问
     * params == {map中对应信息说明：
     * <br>key1--> cookieUrl:需要获取cookie的url; <br> key2 --> reqParm : 该对应网站的请求参数;--类型为：Map
     * <br> key3 --> headParm : 该对应网站的请求头信息 }
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String sendPostByCookie(ArrayList<Map<String, Object>> params, String encode) {
        CloseableHttpClient hc = null;
        HttpPost httpPost = null;
        String result = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            hc = getHttpClient();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
            if (params == null) {
                throw new RuntimeException("请正确传入参数");
            }
            encode = encode == null ? "UTF-8" : encode;
            for (int i = 0; i < params.size(); i++) {
                httpPost = new HttpPost(params.get(i).get("url").toString());
                httpPost.setConfig(config);
                if (params.get(i).get("reqParm") != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(getparams((Map<String, Object>) params.get(i).get("reqParm")), encode));
                }
                response = hc.execute(httpPost);
                if (response.getStatusLine().getStatusCode() != 200) {
                    break;
                }
                if (i == params.size() - 1) { //最后一个返回数据
                    result = EntityUtils.toString(response.getEntity());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    log.error("关闭entity流出现异常。" + e);
                    throw new RuntimeException("关闭entity流出现异常。" + e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new RuntimeException("关闭response流出现异常。" + e);
                }
            }
            if (hc != null) {
                try {
                    hc.close();
                } catch (IOException e) {
                    log.error("关闭HttpClient出现异常。" + e);
                    throw new RuntimeException("关闭HttpClient出现异常。" + e);
                }
            }
        }
        return result;
    }

    /**
     * 发送多个请求地址，每个请求地址开启一个线程，所有请求执行完，封装结果集返回
     *
     * @return
     */
    public static List<String> sendMultipleReqGet(List<String> urls) throws Exception {
        List<FutureTask<String>> futureTasks = new ArrayList<>();
        for (final String url : urls) {
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() {
                    return sendGetRequest(url);
                }
            };
            FutureTask<String> futureTask = new FutureTask<>(callable);
            futureTasks.add(futureTask);
            new Thread(futureTask).start();
        }
        List<String> returnResult = new ArrayList<>();
        for (FutureTask<String> futureTask : futureTasks) {
            returnResult.add(futureTask.get());
        }
        return returnResult;
    }

    public static String sendPostRequest(String json, Map<String, String> reqMap, String url) throws Exception {
        CloseableHttpClient hc;
        HttpPost httpPost;
        String result;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            hc = getHttpClient();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(20000).setSocketTimeout(20000).build();
            httpPost = new HttpPost(url); //含有中文的话需要处理
            httpPost.setConfig(config);
            if (!StringUtils.isEmpty(json)) {
                httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
            }
            if (reqMap != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(getparams(reqMap), "UTF-8"));
            }
            response = hc.execute(httpPost);
            entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    log.error("关闭entity流出现异常。" + e);
                    throw new Exception("关闭entity流出现异常。" + e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new Exception("关闭response流出现异常。" + e);
                }
            }
        }
        return result;
    }

    /**
     * 发送post请求， 并使用json 请求体参数
     *
     * @param json
     * @param url
     * @return
     */
    public static String sendPostWithBodyParams(String json, String url) throws Exception {
        CloseableHttpClient hc;
        HttpPost httpPost;
        String result;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            hc = getHttpClient();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(20000).setSocketTimeout(20000).build();
            httpPost = new HttpPost(url); //含有中文的话需要处理
            httpPost.setConfig(config);
            httpPost.setHeader("content-type", "application/json");
            httpPost.setEntity(new StringEntity(json, Charset.forName("utf-8")));
            response = hc.execute(httpPost);
            entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new Exception(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    log.error("关闭entity流出现异常。" + e);
                    throw new Exception("关闭entity流出现异常。" + e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭response流出现异常。" + e);
                    throw new Exception("关闭response流出现异常。" + e);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String result = sendGetRequest("http://180.169.169.116:9090/SDLWeb/UploadYLDServlet");
        System.out.println(result);
    }
}
