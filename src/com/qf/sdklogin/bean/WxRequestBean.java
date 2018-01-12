package com.qf.sdklogin.bean;

import com.qf.sdklogin.SdkConstant;

public class WxRequestBean {
	private String app_id = SdkConstant.HS_APPID; // �� INT ��ϷID
	private String client_id = SdkConstant.HS_CLIENTID; // �� INT �ͻ���ID
	private String from = SdkConstant.FROM; // �� INT ��Դ��Ϣ
											// 1-WEB��2-WAP��3-Android��4-IOS��5-WP
	private String agentgame = SdkConstant.HS_AGENT; // �� STRING ����������� Ĭ��Ϊ����
	private String user_token = SdkConstant.userToken; // �� STRING �˴�����token
	private long timestamp = 0; // �� STRING �ͻ���ʱ��� timestamp
	private DeviceBean device = SdkConstant.deviceBean;
	private String packagename = SdkConstant.packageName;
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	@Override
	public String toString() {
		return "WxRequestBean [app_id=" + app_id + ", client_id=" + client_id + ", from=" + from + ", agentgame="
				+ agentgame + ", user_token=" + user_token + ", timestamp=" + timestamp + ", device=" + device
				+ ", packagename=" + packagename + ", code=" + code + "]";
	}

}
