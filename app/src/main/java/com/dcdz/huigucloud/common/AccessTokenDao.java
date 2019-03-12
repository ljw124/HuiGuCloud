package com.dcdz.huigucloud.common;

import org.litepal.crud.LitePalSupport;

/**
 * Created by LJW on 2019/3/12.
 */
public class AccessTokenDao extends LitePalSupport {

    private String accessToken;
    private long expiresTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }
}
