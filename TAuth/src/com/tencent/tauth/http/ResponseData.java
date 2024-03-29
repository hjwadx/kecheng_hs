package com.tencent.tauth.http;

public final class ResponseData {
    public ResponseData(int statusCode, byte[] responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    private int statusCode;
    private byte[] responseBody;

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }
}