package com.aitos.wxhackathonapp.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    private static final int TIMEOUT_READ = 30;//读取超时
    private static final int TIMEOUT_CONNECT = 10;//连接超时
    private static final int TIMEOUT_WRITE = 60;//写入超时

    private OkHttpUtils() {
        init();
    }

    public static synchronized OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 构造函数初始化
     */
    private void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //超时时间
        builder.readTimeout(TIMEOUT_READ, TimeUnit.SECONDS);
        builder.connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS);

        //协议
        List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(Protocol.HTTP_1_1);
        protocols.add(Protocol.HTTP_2);
        builder.protocols(protocols);

        //ssl
        //builder.sslSocketFactory();

        // hostname verify
        builder.hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        //cookie 自动存储
        //CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
        //builder.cookieJar(cookieJar);

        mOkHttpClient = builder.build();
    }

    public void doGetRequest(String url, Callback responseCallback) {
        doAsyncGetRequest(url, null, null, responseCallback);
    }

    public void doPostRequest(String url, Map<String, String> bodyParams, Callback responseCallback) {
        doAsyncPostRequest(url, null, bodyParams, responseCallback);
    }

    public void doPostRequest(String url, Map<String, String> headerParams,
                              Map<String, String> bodyParams, Callback responseCallback) {
        doAsyncPostRequest(url, headerParams, bodyParams, responseCallback);
    }

    public void doJsonRequest(String url, String jsonData, Callback responseCallback) {
        doAsyncJsonRequest(url, null, jsonData, responseCallback);
    }

    public void doJsonRequest(String url, Map<String, String> headerParams, String jsonData,
                              Callback responseCallback) {
        doAsyncJsonRequest(url, headerParams, jsonData, responseCallback);
    }

    /**
     * 设置Headers
     *
     * @param params
     * @return
     */
    private Headers setHeaders(Map<String, String> params) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                headerBuilder.add(key, params.get(key));
            }
        }
        return headerBuilder.build();
    }

    /**
     * 格式化URL拼接参数
     *
     * @param params
     * @return
     */
    private String formatUrlParams(Map<String, String> params) {
        StringBuffer param = new StringBuffer();
        int i = 0;
        if (params == null) {
            return param.toString();
        }
        for (String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            try {
                param.append(key);
                param.append("=");
                param.append(URLEncoder.encode(params.get(key), "UTF-8"));  //字符串拼接
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        return param.toString();
    }

    /**
     * 格式化post表单请求参数
     *
     * @param params
     * @return
     */
    private RequestBody formatPostBody(Map<String, String> params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                formBodyBuilder.add(key, params.get(key));
            }
        }
        return formBodyBuilder.build();
    }

    /**
     * Get同步请求
     *
     * @param url
     * @param urlParams
     * @param headerParams
     * @return
     */
    private String doSyncGetRequest(String url, Map<String, String> urlParams, Map<String,
            String> headerParams) {
        String result = "";
        Request request = new Request.Builder().url(url + formatUrlParams(urlParams)).headers
                (setHeaders(headerParams)).get().build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            result = response.body().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 异步Get请求
     *
     * @param url
     * @param urlParams
     * @param headerParams
     * @return
     */
    private void doAsyncGetRequest(String url, Map<String, String> urlParams, Map<String, String>
            headerParams, Callback responseCallback) {
        Request request = new Request.Builder().url(url + formatUrlParams(urlParams)).headers
                (setHeaders(headerParams)).get().build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(responseCallback);
    }

    /**
     * 异步Post请求
     *
     * @param url
     * @param headerParams
     * @param bodyParams
     * @return
     */
    private void doAsyncPostRequest(String url, Map<String, String> headerParams, Map<String,
            String> bodyParams, Callback responseCallback) {
        Request request = new Request.Builder().url(url).headers(setHeaders(headerParams)).post
                (formatPostBody(bodyParams)).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(responseCallback);
    }

    /**
     * JSON异步post请求
     *
     * @param url
     * @param headerParams
     * @param jsonData
     * @return
     */
    private void doAsyncJsonRequest(String url, Map<String, String> headerParams, String
            jsonData, Callback responseCallback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(mediaType, jsonData);
        Request request = new Request.Builder().url(url).headers(setHeaders(headerParams)).post
                (requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(responseCallback);
    }

}
