package com.wd.tech.app;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.log.RequestInterceptor;
import com.jess.arms.integration.ConfigModule;
import com.jess.arms.utils.ArmsUtils;
import com.squareup.leakcanary.RefWatcher;
import com.wd.tech.BuildConfig;
import com.wd.tech.mvp.model.api.Api;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * ================================================
 * App 的全局配置信息在此配置, 需要将此实现类声明到 AndroidManifest 中
 * ConfigModule 的实现类可以有无数多个, 在 Application 中只是注册回调, 并不会影响性能 (多个 ConfigModule 在多 Module 环境下尤为受用)
 *
 * @see com.jess.arms.base.delegate.AppDelegate
 * @see com.jess.arms.integration.ManifestParser
 * Created by MVPArmsTemplate
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public final class GlobalConfiguration implements ConfigModule {

    //    public static String sDomain = Api.APP_DOMAIN;
String CER_CLIENT ="-----BEGIN CERTIFICATE-----\n" +
        "MIIDXTCCAkUCCQDNhr7+xMtU3jANBgkqhkiG9w0BAQUFADBoMQswCQYDVQQGEwJD\n" +
        "TjELMAkGA1UECAwCeDExCzAJBgNVBAcMAngyMQswCQYDVQQKDAJ4MzELMAkGA1UE\n" +
        "CwwCeDUxCzAJBgNVBAMMAmJ3MRgwFgYJKoZIhvcNAQkBFgkxQDE2My5jb20wHhcN\n" +
        "MTgwOTE3MTEyNjI2WhcNMjgwOTE0MTEyNjI2WjB5MQswCQYDVQQGEwJDTjELMAkG\n" +
        "A1UECAwCeDExCzAJBgNVBAcMAngyMQ4wDAYDVQQKDAViYXdlaTEPMA0GA1UECwwG\n" +
        "YmF3ZWkyMQswCQYDVQQDDAJidzEiMCAGCSqGSIb3DQEJARYTMTg2MDAxNTE1NjhA\n" +
        "MTYzLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMRn5BnuG5Qm\n" +
        "GBJV+aBdVpCPkXNs7sCZPpKT6K6gi1t2L88DOiZqsIi+06KsN55w/51YeuAHYq9w\n" +
        "QFx9X34eFB/n//SKA/qkWznxZdtsAJUD3hkKkR3jhj+JP1EZWxwgIP5Dp1RyuBxH\n" +
        "gEoe7UmK9o/V2hJ3HTAYF20vQquFltucl5svnmtQvF4aofhFQ3gqXYvXD6pxcIuI\n" +
        "UOePK49hnlxz7v5t5s/0VXHHz+5THsEg14oW+kAPFKVPS59tjQV7LzDMXjunEBzc\n" +
        "A/Jslafx32BF4Fy1aCbWCmIJSKou9MBnrP1MuheIpMO1qEMBXx/9MuLMFdnyj20N\n" +
        "9M+WlaMBiMUCAwEAATANBgkqhkiG9w0BAQUFAAOCAQEAJf/W2zTuf9D36js7766T\n" +
        "xpfWCVy0POqkdXNKvPThd/U6Qwi2QXc0CmNvr02lfVRu11cX4inR9RiJUXWoeG7J\n" +
        "DDWBSBPKTpeF8+k2w+DjDAkE3mj3iCQdeydkhCUYquSxtFNC6mFZ9zrkMs7sGuBc\n" +
        "GoDnueL8B2IiNfLtA3vUzvAkqh9b7rOBk1VXem4JFnIoisFufdzH1RhNWxZTgtlG\n" +
        "+Po5VSrMpKgtPYLHFIprMIUwGfW7j36hhvnEArEVXLWjY3hhNvyJ4jBf0WRp44GA\n" +
        "8OZ1zDEyVxxtOAQXXlfiYusPuy5Wup2P7RYo17xMVoHeQg6yF+iszlBHoJ5250iv\n" +
        "kA==\n" +
        "-----END CERTIFICATE-----\n";
    private TrustManager tm;
    private SSLContext sc;


    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        try {

            sc = SSLContext.getInstance("TLS");
            //信任证书管理,这个是由我们自己生成的,信任我们自己的服务器证书
            tm = new MyTrustManager(readCert(CER_CLIENT));
            sc.init(null,  new TrustManager[] {tm}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!BuildConfig.LOG_DEBUG) { //Release 时,让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE);
        }

        builder.baseurl(Api.BASE_URL)
                //强烈建议自己自定义图片加载逻辑,因为默认提供的 GlideImageLoaderStrategy 并不能满足复杂的需求
                //请参考 https://github.com/JessYanCoding/MVPArms/wiki#3.4
//                .imageLoaderStrategy(new CustomLoaderStrategy())

                //想支持多 BaseUrl, 以及运行时动态切换任意一个 BaseUrl, 请使用 https://github.com/JessYanCoding/RetrofitUrlManager
                //如果 BaseUrl 在 App 启动时不能确定, 需要请求服务器接口动态获取, 请使用以下代码
                //以下方式是 Arms 框架自带的切换 BaseUrl 的方式, 在整个 App 生命周期内只能切换一次, 若需要无限次的切换 BaseUrl, 以及各种复杂的应用场景还是需要使用 RetrofitUrlManager 框架
                //以下代码只是配置, 还要使用 Okhttp (AppComponent中提供) 请求服务器获取到正确的 BaseUrl 后赋值给 GlobalConfiguration.sDomain
                //切记整个过程必须在第一次调用 Retrofit 接口之前完成, 如果已经调用过 Retrofit 接口, 此种方式将不能切换 BaseUrl
//                .baseurl(new BaseUrl() {
//                    @Override
//                    public HttpUrl url() {
//                        return HttpUrl.parse(sDomain);
//                    }
//                })

                //可根据当前项目的情况以及环境为框架某些部件提供自定义的缓存策略, 具有强大的扩展性
//                .cacheFactory(new Cache.Factory() {
//                    @NonNull
//                    @Override
//                    public Cache build(CacheType type) {
//                        switch (type.getCacheTypeId()){
//                            case CacheType.EXTRAS_TYPE_ID:
//                                return new IntelligentCache(500);
//                            case CacheType.CACHE_SERVICE_CACHE_TYPE_ID:
//                                return new Cache(type.calculateCacheSize(context));//自定义 Cache
//                            default:
//                                return new LruCache(200);
//                        }
//                    }
//                })

                //若觉得框架默认的打印格式并不能满足自己的需求, 可自行扩展自己理想的打印格式 (以下只是简单实现)
//                .formatPrinter(new FormatPrinter() {
//                    @Override
//                    public void printJsonRequest(Request request, String bodyString) {
//                        Timber.i("printJsonRequest:" + bodyString);
//                    }
//
//                    @Override
//                    public void printFileRequest(Request request) {
//                        Timber.i("printFileRequest:" + request.url().toString());
//                    }
//
//                    @Override
//                    public void printJsonResponse(long chainMs, boolean isSuccessful, int code,
//                                                  String headers, MediaType contentType, String bodyString,
//                                                  List<String> segments, String message, String responseUrl) {
//                        Timber.i("printJsonResponse:" + bodyString);
//                    }
//
//                    @Override
//                    public void printFileResponse(long chainMs, boolean isSuccessful, int code, String headers,
//                                                  List<String> segments, String message, String responseUrl) {
//                        Timber.i("printFileResponse:" + responseUrl);
//                    }
//                })

                // 这里提供一个全局处理 Http 请求和响应结果的处理类,可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
                .globalHttpHandler(new GlobalHttpHandlerImpl(context))
                // 用来处理 rxjava 中发生的所有错误,rxjava 中发生的每个错误都会回调此接口
                // rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效
                .responseErrorListener(new ResponseErrorListenerImpl())
                .gsonConfiguration((context1, gsonBuilder) -> {//这里可以自己自定义配置Gson的参数
                    gsonBuilder
                            .serializeNulls()//支持序列化null的参数
                            .enableComplexMapKeySerialization();//支持将序列化key为object的map,默认只能序列化key为string的map
                })
                .retrofitConfiguration((context1, retrofitBuilder) -> {//这里可以自己自定义配置 Retrofit 的参数, 甚至您可以替换框架配置好的 OkHttpClient 对象 (但是不建议这样做, 这样做您将损失框架提供的很多功能)
//                    retrofitBuilder.addConverterFactory(FastJsonConverterFactory.create());//比如使用fastjson替代gson
                })
                .okhttpConfiguration((context1, okhttpBuilder) -> {//这里可以自己自定义配置Okhttp的参数
                    okhttpBuilder.sslSocketFactory(sc.getSocketFactory(), (X509TrustManager) tm)
                            .hostnameVerifier(hostnameVerifier)
                    ; //支持 Https,详情请百度
                    //okhttpBuilder.writeTimeout(10, TimeUnit.SECONDS);
                    //使用一行代码监听 Retrofit／Okhttp 上传下载进度监听,以及 Glide 加载进度监听 详细使用方法查看 https://github.com/JessYanCoding/ProgressManager
//                    ProgressManager.getInstance().with(okhttpBuilder);
                    //让 Retrofit 同时支持多个 BaseUrl 以及动态改变 BaseUrl. 详细使用请方法查看 https://github.com/JessYanCoding/RetrofitUrlManager
//                    RetrofitUrlManager.getInstance().with(okhttpBuilder);
                })
                .rxCacheConfiguration((context1, rxCacheBuilder) -> {//这里可以自己自定义配置 RxCache 的参数
                    rxCacheBuilder.useExpiredDataIfLoaderNotAvailable(true);
                    // 想自定义 RxCache 的缓存文件夹或者解析方式, 如改成 fastjson, 请 return rxCacheBuilder.persistence(cacheDirectory, new FastJsonSpeaker());
                    // 否则请 return null;
                    return null;
                });
    }

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        // AppLifecycles 的所有方法都会在基类 Application 的对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        // 可以根据不同的逻辑添加多个实现类
        lifecycles.add(new AppLifecyclesImpl());
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        // ActivityLifecycleCallbacks 的所有方法都会在 Activity (包括三方库) 的对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        // 可以根据不同的逻辑添加多个实现类
        lifecycles.add(new ActivityLifecycleCallbacksImpl());
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        lifecycles.add(new FragmentManager.FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                // 在配置变化的时候将这个 Fragment 保存下来,在 Activity 由于配置变化重建时重复利用已经创建的 Fragment。
                // https://developer.android.com/reference/android/app/Fragment.html?hl=zh-cn#setRetainInstance(boolean)
                // 如果在 XML 中使用 <Fragment/> 标签,的方式创建 Fragment 请务必在标签中加上 android:id 或者 android:tag 属性,否则 setRetainInstance(true) 无效
                // 在 Activity 中绑定少量的 Fragment 建议这样做,如果需要绑定较多的 Fragment 不建议设置此参数,如 ViewPager 需要展示较多 Fragment
                f.setRetainInstance(true);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                ((RefWatcher) ArmsUtils
                        .obtainAppComponentFromContext(f.getActivity())
                        .extras()
                        .get(RefWatcher.class.getName()))
                        .watch(f);
            }
        });
    }

    //主机地址验证
    static final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

class MyTrustManager implements X509TrustManager {
    X509Certificate cert;

    MyTrustManager(X509Certificate cert) {
        this.cert = cert;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // 我们在客户端只做服务器端证书校验。
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // 确认服务器端证书和代码中 hard code 的 CRT 证书相同。
        if (chain[0].equals(this.cert)) {
            Log.i("Jin", "checkServerTrusted Certificate from server is valid!");
            return;// found match
        }
        throw new CertificateException("checkServerTrusted No trusted server cert found!");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

    private static X509Certificate readCert(String cer) {
        if (cer == null || cer.trim().isEmpty())
            return null;
        InputStream caInput = new ByteArrayInputStream(cer.getBytes());
        X509Certificate cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(caInput);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (caInput != null) {
                    caInput.close();
                }
            } catch (Throwable ex) {
            }
        }
        return cert;
    }


}
