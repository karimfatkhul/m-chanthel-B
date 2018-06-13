package com.solusi247.fatkhul.chanthelbeta.helper;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface CopyApi {
    @GET("/chanthelAPI/index.php")
    Call<CopyResponse> getCopy(@QueryMap HashMap<String, String> params);
}
