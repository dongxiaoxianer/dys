package com.wd.tech.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.wd.tech.mvp.contract.MainContract;
import com.wd.tech.mvp.model.api.service.ApiService;

import io.reactivex.Observable;
import okhttp3.ResponseBody;


@ActivityScope
public class MainModel extends BaseModel implements MainContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public MainModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<ResponseBody> getPay(int userId, String sessionId, String orderId, String payType) {
        ApiService cllApiService = mRepositoryManager.obtainRetrofitService(ApiService.class);
        Observable<ResponseBody> observable = cllApiService.getpay(userId, sessionId, orderId, payType);
        return observable;
    }

    @Override
    public Observable<ResponseBody> getPayZfb(int userId, String sessionId, String orderId, String payType) {
        ApiService cllApiService = mRepositoryManager.obtainRetrofitService(ApiService.class);
        Observable<ResponseBody> observable = cllApiService.getpay(userId, sessionId, orderId, payType);
        return observable;
    }
}