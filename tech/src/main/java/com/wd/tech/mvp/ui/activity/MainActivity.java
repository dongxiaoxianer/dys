package com.wd.tech.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wd.tech.R;
import com.wd.tech.di.component.DaggerMainComponent;
import com.wd.tech.di.module.MainModule;
import com.wd.tech.mvp.contract.MainContract;
import com.wd.tech.mvp.presenter.MainPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_pay)
    Button btnPay;
    private IWXAPI api;
    private static final int WX_PAY_OK = 0;
    private static final int ALI_PAY_OK = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //微信
                case WX_PAY_OK:
                    String json = (String) msg.obj;
                    Toast.makeText(MainActivity.this, "支付信息返回", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        //拿预支付结果信息给微信
                        PayReq request = new PayReq();
                        request.appId = jsonObject.getString("appId");
                        request.partnerId = jsonObject.getString("partnerId");
                        request.prepayId = jsonObject.getString("prepayId");
                        request.packageValue = jsonObject.getString("packageValue");
                        request.nonceStr = jsonObject.getString("nonceStr");
                        request.timeStamp = jsonObject.getString("timeStamp");
                        request.sign = jsonObject.getString("sign");
                        api.sendReq(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("xxx", e.getMessage());
                    }
                    break;
            }

        }
    };

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //  商户APP工程中引入微信JAR包，调用API前，需要先向微信注册您的APPID
        api = WXAPIFactory.createWXAPI(MainActivity.this, Constants.APP_ID, true);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);//wxb3852e6a6b7d9516
        String s = MD5("381001tech");
        Log.d(TAG, "initData: "+s);
    }
    /**
     *  MD5加密
     * @param sourceStr
     * @return
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //发起登录请求
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                //Constants.wx_api.sendReq(req);
                //api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
                api.sendReq(req);
                break;
            case R.id.btn_pay:
                mPresenter.getPayInfo(38,"154287075155638","20181122151747531","1");
                break;
        }
    }

    @Override
    public void showData(String responseString) {
        Log.d(TAG, "showData: "+responseString);
        Message message = new Message();
        message.what = WX_PAY_OK;
        message.obj = responseString;
        handler.sendMessage(message);
    }

    @Override
    public void showDataZfb(String responseString) {
        Log.d(TAG, "showDataZfb: "+responseString);
    }
}
