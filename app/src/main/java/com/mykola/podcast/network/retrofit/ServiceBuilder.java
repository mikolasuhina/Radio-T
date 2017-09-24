package com.mykola.podcast.network.retrofit;

import retrofit2.Retrofit;

public class ServiceBuilder {

    private static final String BASE_URL = "http://feeds.rucast.net/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(new XmlConverter())
            .build();

    private static APIService myApi = retrofit.create(APIService.class);


    public static APIService getApi() {
        return myApi;
    }
}
