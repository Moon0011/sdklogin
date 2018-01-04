package com.qf.sdklogin.bean;

/**
 * Created by liu hong liang on 2017/5/23.
 */

public class ThirdLoginInfo {
    private String oauth_type	;//int	第三方类�? 2 qq 3 微信 4微博
    private String oauth_appid	;//STRING	第三方提供的 appid
    private String oauth_appsecret	;//STRING	第三方提供的 appsecret
    private String oauth_redirecturl	;//STRING	第三方提供的 redirecturl

    public String getOauth_type() {
        return oauth_type;
    }

    public void setOauth_type(String oauth_type) {
        this.oauth_type = oauth_type;
    }

    public String getOauth_appid() {
        return oauth_appid;
    }

    public void setOauth_appid(String oauth_appid) {
        this.oauth_appid = oauth_appid;
    }

    public String getOauth_appsecret() {
        return oauth_appsecret;
    }

    public void setOauth_appsecret(String oauth_appsecret) {
        this.oauth_appsecret = oauth_appsecret;
    }

    public String getOauth_redirecturl() {
        return oauth_redirecturl;
    }

    public void setOauth_redirecturl(String oauth_redirecturl) {
        this.oauth_redirecturl = oauth_redirecturl;
    }
}
