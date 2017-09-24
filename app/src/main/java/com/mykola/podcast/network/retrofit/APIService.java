package com.mykola.podcast.network.retrofit;

import com.mykola.podcast.models.Podcast;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface APIService {

    @GET("radio-t")
    Call<List<Podcast>> getPodcasts();

}
