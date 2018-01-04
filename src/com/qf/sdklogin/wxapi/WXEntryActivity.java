package com.qf.sdklogin.wxapi;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.qf.sdklogin.IHuoLogin;
import com.qf.sdklogin.SdkConstant;
import com.qf.sdklogin.bean.DeviceBean;
import com.qf.sdklogin.bean.LoginResultBean;
import com.qf.sdklogin.bean.ThirdLoginRequestBean;
import com.qf.sdklogin.bean.WXUserInfoBean;
import com.qf.sdklogin.bean.WxRespBean;
import com.qf.sdklogin.http.HttpCallbackDecode;
import com.qf.sdklogin.http.HttpParamsBuild;
import com.qf.sdklogin.util.GsonUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	public static final String APP_ID = "wx6af2ec99d9833b59";
	public static final String APP_SECRET = "5ef500d044dd91c00ad82ce8924c0b58";
	private Context mContext;
	private ThirdLoginRequestBean thirdLoginRequestBean = new ThirdLoginRequestBean();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getBoolean("isWXFirstLogin", true)) {
				final SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wx_sdklogin";
				api.sendReq(req);
				api.registerApp(APP_ID);
				SharedPreferences.Editor editor = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).edit();
				editor.putBoolean("isWXFirstLogin", false);
				editor.commit();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		api = WXAPIFactory.createWXAPI(this, APP_ID);
		api.handleIntent(getIntent(), this);

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

		if (null != intent && intent.getBooleanExtra("isLoginWX", false)) {
			Message msg = mHandler.obtainMessage();
			mHandler.sendMessageDelayed(msg, 3000);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	public void onResp(BaseResp resp) {
		if (resp instanceof SendAuth.Resp) {
			String result;
			switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				String code = ((SendAuth.Resp) resp).code;
				getAccessToken(code);
				WXEntryActivity.this.finish();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "您取消了授权";
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				close();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "授权被拒绝";
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				close();
				break;
			default:
				result = "授权失败";
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				close();
				break;
			}
		} else {
			close();
		}
	}

	private void close() {
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	private void getAccessToken(String code) {
		String url = String.format(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
				APP_ID, APP_SECRET, code);
		RxVolley.get(url, new HttpCallback() {
			@Override
			public void onSuccess(String t) {
				// Toast.makeText(mContext, "t =" + t,
				// Toast.LENGTH_SHORT).show();
				if (!t.contains("errcode")) {
					WxRespBean resbean = GsonUtil.getGson().fromJson(t, WxRespBean.class);
					thirdLoginRequestBean.setAccess_token(resbean.getAccess_token());
					thirdLoginRequestBean.setExpires_date(String.valueOf(resbean.getExpires_in()));
					thirdLoginRequestBean.setUserfrom(IHuoLogin.LOGIN_WX + "");
					thirdLoginRequestBean.setApp_id(SdkConstant.HS_APPID);
					thirdLoginRequestBean.setFrom(SdkConstant.FROM);
					thirdLoginRequestBean.setUser_token(SdkConstant.userToken);
					thirdLoginRequestBean.setPackagename(SdkConstant.packageName);
					thirdLoginRequestBean.setDevice(SdkConstant.deviceBean);
					thirdLoginRequestBean.setClient_id(SdkConstant.HS_CLIENTID);
					thirdLoginRequestBean.setDevice(SdkConstant.deviceBean);
					getUserInfo(resbean.getAccess_token(), resbean.getOpenid());
				}
			}
		});
	}

	private void getUserInfo(String accessToken, String openid) {
		String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s", accessToken,
				openid);
		RxVolley.get(url, new HttpCallback() {
			@Override
			public void onSuccess(String t) {
				Toast.makeText(mContext, "t2 =" + t, Toast.LENGTH_SHORT).show();
				WXUserInfoBean userinfo = GsonUtil.getGson().fromJson(t, WXUserInfoBean.class);
				thirdLoginRequestBean.setUnionid(userinfo.getUnionid());
				thirdLoginRequestBean.setNickname(userinfo.getNickname());
				thirdLoginRequestBean.setHead_img(userinfo.getHeadimgurl());
				thirdLoginRequestBean.setOpenid(userinfo.getOpenid());
				thirdLoginRequestBean.setIntroducer("qfgames");
				submitThirdLogin(thirdLoginRequestBean);
			}
		});
	}

	private void submitThirdLogin(ThirdLoginRequestBean thirdLoginRequestBean) {
		HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(thirdLoginRequestBean));
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<LoginResultBean>(mContext,
				httpParamsBuild.getAuthkey()) {
			@Override
			public void onDataSuccess(LoginResultBean data) {
				Log.e("WXEntryActivity", "onDataSuccess");
				Intent intent = new Intent("com.qf.sdklogin.fornotice");
				intent.putExtra("usertoken", data.getCp_user_token());
				intent.putExtra("from", IHuoLogin.LOGIN_WX);
				sendBroadcast(intent);
				WXEntryActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}

			@Override
			public void onFailure(String code, String msg) {
				Log.e("WXEntryActivity", "onFailure msg =" + msg + " ,code =" + code);
				WXEntryActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				super.onFailure(code, msg);
			}
		};
		httpCallbackDecode.setShowTs(true);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(false);
		httpCallbackDecode.setLoadMsg("正在登录...");
		RxVolley.post("http://aqfsdk.520cai.cn/api/v7/user/loginoauth", httpParamsBuild.getHttpParams(),
				httpCallbackDecode);
	}
}
