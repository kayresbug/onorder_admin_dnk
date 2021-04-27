package com.daon.admin_onorder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InterfaceApi {
    @GET("order")
    Call<JsonArray> getOrder(@Query("storecode") String code);

    @GET("service")
    Call<JsonArray> getService(@Query("storecode") String code);

    @FormUrlEncoded
    @POST("update_order")
    Call<JsonObject> updateOrder(@Field("storecode") String code, @Field("num") String num);

    @FormUrlEncoded
    @POST("update_service")
    Call<JsonObject> updateService(@Field("storecode") String code, @Field("num") String num);

    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> LoginService(@Field("id") String id, @Field("pass") String pass, @Field("fcm") String fcm);

    @FormUrlEncoded
    @POST("login_fcm")
    Call<JsonObject> setFcm(@Field("id") String id, @Field("pass") String pass, @Field("fcm") String fcm);

}
