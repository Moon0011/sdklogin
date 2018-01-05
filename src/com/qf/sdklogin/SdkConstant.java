package com.qf.sdklogin;

import com.qf.sdklogin.bean.DeviceBean;

public class SdkConstant {
	public static String HS_APPID = "";
	public static String HS_CLIENTID = "";//
	public static String HS_CLIENTKEY = "";
	public static String HS_APPKEY = "";
	public static String HS_AGENT = "";
	public static String FROM = "3";// 1-WEB、2-WAP、3-Android、4-IOS、5-WP
	public static String RSA_PUBLIC_KEY;// rsa密钥
	public static String userToken = "";// 初始化注入
	public static long SERVER_TIME_INTERVAL = 0;// 服务器时间-本地时间
	public static DeviceBean deviceBean;
	public static String packageName = "";// 包名
	public static String QQ_APP_ID = "101453900";
	public static String WX_APP_ID = "wx6af2ec99d9833b59";
	public static String WX_APP_SECRET = "5ef500d044dd91c00ad82ce8924c0b58";

	public static String QF_LOGIN_OAUTH = "http://aqfsdk.520cai.cn/api/v7/user/loginoauth";

	// 微信接口
	public static String WX_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	public static String WX_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

	public static final int LOGIN_DEF = 1;
	public static final int LOGIN_QQ = 2;
	public static final int LOGIN_WX = 3;
	public static final int LOGIN_XLWB = 4;
}
