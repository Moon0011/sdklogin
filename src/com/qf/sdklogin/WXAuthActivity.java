package com.qf.sdklogin;

import com.qf.sdklogin.bean.DeviceBean;
import com.qf.sdklogin.bean.ThirdLoginRequestBean;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WXAuthActivity extends Activity {
	// AppID wx6af2ec99d9833b59
	// AppSecret wx6af2ec99d9833b59
	private IWXAPI api;
	private ThirdLoginRequestBean thirdLoginRequestBean = new ThirdLoginRequestBean();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getBoolean("isWXFirstLogin", true)) {
				final SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wx_sdklogin";
				api.sendReq(req);
				api.registerApp(SdkConstant.WX_APP_ID);
				SharedPreferences.Editor editor = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).edit();
				editor.putBoolean("isWXFirstLogin", false);
				editor.commit();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, SdkConstant.WX_APP_ID);
		Intent intent = getIntent();
		SdkConstant.HS_APPID = intent.getStringExtra("appid");
		SdkConstant.FROM = intent.getStringExtra("from");
		SdkConstant.userToken = intent.getStringExtra("usertoken");
		SdkConstant.packageName = intent.getStringExtra("packagename");
		SdkConstant.deviceBean = (DeviceBean) intent.getSerializableExtra("devicebean");
		SdkConstant.HS_CLIENTID = intent.getStringExtra("clientid");
		SdkConstant.HS_CLIENTKEY = intent.getStringExtra("clientkey");
		SdkConstant.SERVER_TIME_INTERVAL = intent.getLongExtra("servertimeinterval", 0);
		SdkConstant.RSA_PUBLIC_KEY = intent.getStringExtra("rsapublickey");

		thirdLoginRequestBean.setApp_id(SdkConstant.HS_APPID);
		thirdLoginRequestBean.setFrom(SdkConstant.FROM);
		thirdLoginRequestBean.setUser_token(SdkConstant.userToken);
		thirdLoginRequestBean.setPackagename(SdkConstant.packageName);
		thirdLoginRequestBean.setDevice(SdkConstant.deviceBean);
		thirdLoginRequestBean.setClient_id(SdkConstant.HS_CLIENTID);

		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setDevice_id(intent.getStringExtra("device_id"));
		deviceBean.setUserua(intent.getStringExtra("userua"));
		deviceBean.setIpaddrid(intent.getStringExtra("ipaddrid"));
		deviceBean.setDeviceinfo(intent.getStringExtra("deviceinfo"));
		deviceBean.setIdfv(intent.getStringExtra("idfv"));
		deviceBean.setIdfa(intent.getStringExtra("idfa"));
		deviceBean.setLocal_ip(intent.getStringExtra("local_ip"));
		deviceBean.setMac(intent.getStringExtra("mac"));
		SdkConstant.deviceBean = deviceBean;
		thirdLoginRequestBean.setDevice(SdkConstant.deviceBean);

		if (null != intent && intent.getBooleanExtra("isLoginWX", false)) {
			Message msg = mHandler.obtainMessage();
			mHandler.sendMessageDelayed(msg, 3000);
		}
	}
}
