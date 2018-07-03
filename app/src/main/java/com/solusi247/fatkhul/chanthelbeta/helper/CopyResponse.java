package com.solusi247.fatkhul.chanthelbeta.helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CopyResponse {
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
