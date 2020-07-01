package com.mystaffroom.fragments;

import com.mystaffroom.notifications.MyResponse;
import com.mystaffroom.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                "Content-Type:application/json",
                "Authorization:key=<PASTE YOUR SERVER KEY FROM FIREBASE -> PROJECT SETTINGS -> CLOUD MESSAAGING>"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
