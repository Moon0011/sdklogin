package com.qf.sdklogin.wxapi;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
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

	private Context mContext;
	private ThirdLoginRequestBean thirdLoginRequestBean = new ThirdLoginRequestBean();
	private DeviceBean deviceBean;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getBoolean("isWXFirstLogin", true)) {
				final SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wx_sdklogin";
				api.sendReq(req);
				SharedPreferences.Editor editor = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).edit();
				editor.putString("appid", SdkConstant.HS_APPID);
				editor.putString("from", SdkConstant.FROM);
				editor.putString("usertoken", SdkConstant.userToken);
				editor.putString("packagename", SdkConstant.packageName);
				editor.putString("clientid", SdkConstant.HS_CLIENTID);
				editor.putString("clientkey", SdkConstant.HS_CLIENTKEY);
				editor.putLong("servertimeinterval", SdkConstant.SERVER_TIME_INTERVAL);
				editor.putString("rsapublickey", SdkConstant.RSA_PUBLIC_KEY);

				editor.putString("device_id", deviceBean.getDevice_id());
				editor.putString("userua", deviceBean.getUserua());
				editor.putString("ipaddrid", deviceBean.getIpaddrid());
				editor.putString("deviceinfo", deviceBean.getDeviceinfo());
				editor.putString("idfv", deviceBean.getIdfv());
				editor.putString("idfa", deviceBean.getIdfa());
				editor.putString("local_ip", deviceBean.getLocal_ip());
				editor.putString("mac", deviceBean.getMac());

				editor.putBoolean("isWXFirstLogin", false);
				editor.commit();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		api = WXAPIFactory.createWXAPI(this, SdkConstant.WX_APP_ID);
		api.handleIntent(getIntent(), this);
		Intent intent = getIntent();
		SdkConstant.HS_APPID = intent.getStringExtra("appid");
		SdkConstant.FROM = intent.getStringExtra("from");
		SdkConstant.userToken = intent.getStringExtra("usertoken");
		SdkConstant.packageName = intent.getStringExtra("packagename");
		SdkConstant.HS_CLIENTID = intent.getStringExtra("clientid");

		SdkConstant.HS_CLIENTKEY = intent.getStringExtra("clientkey");
		SdkConstant.SERVER_TIME_INTERVAL = intent.getLongExtra("servertimeinterval", 0);
		SdkConstant.RSA_PUBLIC_KEY = intent.getStringExtra("rsapublickey");
		deviceBean = new DeviceBean();
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
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "您取消了授权";
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				WXEntryActivity.this.finish();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "授权被拒绝";
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				WXEntryActivity.this.finish();
				break;
			default:
				result = "授权失败";
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				WXEntryActivity.this.finish();
				break;
			}
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	private void getAccessToken(String code) {
		String url = String.format(SdkConstant.WX_GET_ACCESS_TOKEN, SdkConstant.WX_APP_ID, SdkConstant.WX_APP_SECRET,
				code);
		RxVolley.get(url, new HttpCallback() {
			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(String t) {
				if (!t.contains("errcode")) {
					String appid = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("appid", "");
					String from = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("from", "");
					String usertoken = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
							.getString("usertoken", "");
					String packagename = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
							.getString("packagename", "");
					String clientid = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("clientid",
							"");

					DeviceBean deviceBean = new DeviceBean();
					String device_id = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
							.getString("device_id", "");
					String userua = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("userua",
							"");
					String ipaddrid = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("ipaddrid",
							"");
					String deviceinfo = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
							.getString("deviceinfo", "");
					String idfv = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("idfv", "");
					String idfa = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("idfa", "");
					String local_ip = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("local_ip",
							"");
					String mac = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("mac", "");
					deviceBean.setDevice_id(device_id);
					deviceBean.setUserua(userua);
					deviceBean.setIpaddrid(ipaddrid);
					deviceBean.setDeviceinfo(deviceinfo);
					deviceBean.setIdfv(idfv);
					deviceBean.setIdfa(idfa);
					deviceBean.setLocal_ip(local_ip);
					deviceBean.setMac(mac);

					WxRespBean resbean = GsonUtil.getGson().fromJson(t, WxRespBean.class);
					thirdLoginRequestBean.setAccess_token(resbean.getAccess_token());
					thirdLoginRequestBean.setExpires_date(String.valueOf(resbean.getExpires_in()));
					thirdLoginRequestBean.setUserfrom(SdkConstant.LOGIN_WX + "");
					thirdLoginRequestBean.setApp_id(appid);
					thirdLoginRequestBean.setFrom(from);
					thirdLoginRequestBean.setUser_token(usertoken);
					thirdLoginRequestBean.setPackagename(packagename);
					thirdLoginRequestBean.setClient_id(clientid);
					thirdLoginRequestBean.setDevice(deviceBean);
					getUserInfo(resbean.getAccess_token(), resbean.getOpenid());
				}
			}
		});
	}

	private void getUserInfo(String accessToken, String openid) {
		String url = String.format(SdkConstant.WX_GET_USER_INFO, accessToken, openid);
		RxVolley.get(url, new HttpCallback() {
			@Override
			public void onSuccess(String t) {
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
		HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(thirdLoginRequestBean),
				mContext);
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<LoginResultBean>(mContext,
				httpParamsBuild.getAuthkey()) {
			@Override
			public void onDataSuccess(LoginResultBean data) {
				Intent intent = new Intent("com.qf.sdklogin.fornotice");
				intent.putExtra("usertoken", data.getCp_user_token());
				intent.putExtra("from", SdkConstant.LOGIN_WX);
				sendBroadcast(intent);
				WXEntryActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}

			@Override
			public void onFailure(String code, String msg) {
				Log.e("WXEntryActivity", "submitThirdLogin onFailure msg =" + msg + " ,code =" + code);
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
		RxVolley.post(SdkConstant.QF_LOGIN_OAUTH, httpParamsBuild.getHttpParams(), httpCallbackDecode);
	}
}
