package com.solusi247.fatkhul.chanthelbeta.helper;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface CutApi {
    @GET("/chanthelAPI/index.php")
    Call<CutResponse> getCut(@QueryMap HashMap<String, String> params);
}
