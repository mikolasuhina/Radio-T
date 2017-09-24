package com.mykola.podcast.network.retrofit;


public interface APICallbacks<T> {
    void onResponse(T response);

    void onFailure(Throwable t);
}
