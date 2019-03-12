package com.dcdz.huigucloud;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dcdz.loglibrary.CrashHandler;
import com.dcdz.loglibrary.Log4jConfigure;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.model.HttpParams;

import org.apache.log4j.Logger;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by LJW on 2019/3/12.
 */
public class MyApplication extends Application {

    private static Application app = null;
    //初始化Logger
    protected static Logger log = Logger.getLogger(MyApplication.class);

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        //初始化LogLibrary
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        new Thread(){
            @Override
            public void run() {
                Log4jConfigure.configure(getFilesDir().getAbsolutePath());
                log.info("configure log4j ok");
            }
        }.start();

        //初始化数据库LitePal
        LitePal.initialize(this);

        //初始化网络请求
        EasyHttp.init(this);
        //这里涉及到安全我把url去掉了，demo都是调试通的
        String Url = "https://api.hik-cloud.com/";
        //设置请求头
//        HttpHeaders headers = new HttpHeaders();
        //设置请求参数
//        HttpParams params = new HttpParams();
        //基础配置
        EasyHttp.getInstance()
                .debug("RxEasyHttp", true)
                .setReadTimeOut(60 * 1000)
                .setWriteTimeOut(60 * 1000)
                .setConnectTimeout(60 * 1000)
                .setRetryCount(3)//默认网络不好自动重试3次
                .setRetryDelay(500)//每次延时500ms重试
                .setRetryIncreaseDelay(500)//每次延时叠加500ms
                .setBaseUrl(Url)
                .setCacheDiskConverter(new SerializableDiskConverter())//默认缓存使用序列化转化
                .setCacheMaxSize(50 * 1024 * 1024)//设置缓存大小为50M
                .setCacheVersion(1)//缓存版本为1
//                .setHostnameVerifier(new UnSafeHostnameVerifier(Url))//全局访问规则
                .setCertificates() //信任所有证书
                .addInterceptor(urlInterceptor); //拦截器
//                .addCommonHeaders(headers)//设置全局公共头
//                .addCommonParams(params);//设置全局公共参数
        //.addConverterFactory(GsonConverterFactory.create(gson))//本框架没有采用Retrofit的Gson转化，所以不用配置
    }

    /**
     * 获取Application的Context
     **/
    public static Context getAppContext() {
        if (app == null)
            return null;
        return app.getApplicationContext();
    }


    /**
     * 打印请求的URL地址拦截器
     */
    public static final Interceptor urlInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request request = chain.request();
            Buffer requestBuffer = new Buffer();
            if (request.body() != null) {
                request.body().writeTo(requestBuffer);
            } else {
                Log.d("LogTAG", "request.body() == null");
            }
            //打印url信息
            Log.w("Request URL : ", "intercept: " + request.url() + (request.body() != null ? "?" + _parseParams(request.body(), requestBuffer) : ""));
            final Response response = chain.proceed(request);

            return response;
        }
    };
    @NonNull
    private static String _parseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8");
        }
        return "null";
    }

}
