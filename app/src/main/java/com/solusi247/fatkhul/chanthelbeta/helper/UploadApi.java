package com.solusi247.fatkhul.chanthelbeta.helper;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApi {
    @Multipart
    @POST("/chanthelAPI/index.php")
    Call<UploadResponse> submitData(@Part MultipartBody.Part file,
                                    @Part("u") RequestBody userName,
                                    @Part("p") RequestBody password,
                                    @Part("act") RequestBody action,
                                    @Part("fname") RequestBody fileName,
                                    @Part("pid") RequestBody folderId);
}