package com.qf.sdklogin.bean;

import com.qf.sdklogin.SdkConstant;

public class ThirdLoginRequestBean {
	private String openid;// 是 STRING 第三方openid
	private String access_token;// 是 STRING 第三方登陆token
	private String nickname;// 否 STRING 昵称
	private String head_img;// 否 STRING 头像URL
	private String expires_date;// 是 INT 过期时间 不过期为0
	private String userfrom;// 是 INT 1试玩 2 qq 3 微信 4微博
	private String introducer;// 否 STRING 介绍人 2017-03-11 吉米项目添加
	private String app_id = SdkConstant.HS_APPID; // 是 INT 游戏ID
	private String client_id = SdkConstant.HS_CLIENTID; // 是 INT 客户端ID
	private String from = SdkConstant.FROM; // 是 INT 来源信息
											// 1-WEB、2-WAP、3-Android、4-IOS、5-WP
	private String agentgame = SdkConstant.HS_AGENT; // 是 STRING 玩家所属渠道 默认为’’
	private String user_token = SdkConstant.userToken; // 是 STRING 此次连接token
	private long timestamp = 0; // 是 STRING 客户端时间戳 timestamp
	private DeviceBean device = SdkConstant.deviceBean;
	private String packagename = SdkConstant.packageName;// app 包名(add
	private String unionid;

	public ThirdLoginRequestBean() {
		timestamp = System.currentTimeMillis() + SdkConstant.SERVER_TIME_INTERVAL;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getExpires_date() {
		return expires_date;
	}

	public void setExpires_date(String expires_date) {
		this.expires_date = expires_date;
	}

	public String getUserfrom() {
		return userfrom;
	}

	public void setUserfrom(String userfrom) {
		this.userfrom = userfrom;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAgentgame() {
		return agentgame;
	}

	public void setAgentgame(String agentgame) {
		this.agentgame = agentgame;
	}

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public DeviceBean getDevice() {
		return device;
	}

	public void setDevice(DeviceBean device) {
		this.device = device;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	@Override
	public String toString() {
		return "ThirdLoginRequestBean [openid=" + openid + ", access_token=" + access_token + ", nickname=" + nickname
				+ ", head_img=" + head_img + ", expires_date=" + expires_date + ", userfrom=" + userfrom
				+ ", introducer=" + introducer + ", app_id=" + app_id + ", client_id=" + client_id + ", from=" + from
				+ ", agentgame=" + agentgame + ", user_token=" + user_token + ", timestamp=" + timestamp + ", device="
				+ device + ", packagename=" + packagename + ", unionid=" + unionid + "]";
	}

}
