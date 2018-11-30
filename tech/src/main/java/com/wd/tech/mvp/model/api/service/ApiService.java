package com.wd.tech.mvp.model.api.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("tool/verify/v1/pay")
    public Observable<ResponseBody> getpay(@Header("userId") int userId, @Header("sessionId") String sessionId, @Field("orderId") String orderId, @Field("payType") String payType);
}
