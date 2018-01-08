package com.qf.sdklogin;

import org.json.JSONException;
import org.json.JSONObject;

import com.kymjs.rxvolley.RxVolley;
import com.qf.sdklogin.bean.DeviceBean;
import com.qf.sdklogin.bean.LoginResultBean;
import com.qf.sdklogin.bean.ThirdLoginRequestBean;
import com.qf.sdklogin.http.HttpCallbackDecode;
import com.qf.sdklogin.http.HttpParamsBuild;
import com.qf.sdklogin.util.GsonUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/3.
 */
public class QQAuthActivity extends Activity {
	public static Tencent mTencent;
	private UserInfo mInfo;
	private Context mContext;
	private ThirdLoginRequestBean thirdLoginRequestBean = new ThirdLoginRequestBean();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getBoolean("isQqFirstLogin", true)) {
				if (!mTencent.isSessionValid()) {
					mTencent.login(QQAuthActivity.this, "all", loginListener);
					SharedPreferences.Editor editor = getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
							.edit();
					editor.putBoolean("isQqFirstLogin", false);
					editor.commit();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		if (mTencent == null) {
			mTencent = Tencent.createInstance(SdkConstant.QQ_APP_ID, this);
		}
		Intent intent = getIntent();
		SdkConstant.HS_APPID = intent.getStringExtra("appid");
		SdkConstant.FROM = intent.getStringExtra("from");
		SdkConstant.userToken = intent.getStringExtra("usertoken");
		SdkConstant.packageName = intent.getStringExtra("packagename");
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

		if (null != intent && intent.getBooleanExtra("isLoginQQ", false)) {
			Message msg = mHandler.obtainMessage();
			mHandler.sendMessageDelayed(msg, 3000);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
			Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
		}
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			try {
				if (null != thirdLoginRequestBean) {
					thirdLoginRequestBean.setAccess_token(values.getString("access_token"));
					thirdLoginRequestBean.setUserfrom(SdkConstant.LOGIN_QQ + "");
					thirdLoginRequestBean.setExpires_date(values.getString("expires_time"));
					thirdLoginRequestBean.setOpenid(values.getString("openid"));
					thirdLoginRequestBean.setIntroducer("qfgames");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			initOpenidAndToken(values);
			updateUserInfo();
		}
	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				return;
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
			mTencent.logout(mContext);
		}

		@Override
		public void onCancel() {
			mTencent.logout(mContext);
		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {
					Log.e("QQAuthActivity", "onError =" + e.errorMessage);
				}

				@Override
				public void onComplete(final Object response) {
					JSONObject json = (JSONObject) response;
					try {
						if (null != thirdLoginRequestBean) {
							thirdLoginRequestBean.setNickname(json.getString("nickname"));
							thirdLoginRequestBean.setHead_img(json.getString("figureurl_qq_2"));
							submitThirdLogin(thirdLoginRequestBean);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onCancel() {
					Log.e("QQAuthActivity", "onCancel");
				}
			};
			mInfo = new UserInfo(mContext, mTencent.getQQToken());
			mInfo.getUserInfo(listener);
		}
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
				intent.putExtra("from", SdkConstant.LOGIN_QQ);
				sendBroadcast(intent);
				QQAuthActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}

			@Override
			public void onFailure(String code, String msg) {
				Log.e("QQAuthActivity", "onFailure msg =" + msg + " ,code =" + code);
				QQAuthActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				super.onFailure(code, msg);
			}
		};
		httpCallbackDecode.setShowTs(true);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(false);
		httpCallbackDecode.setLoadMsg("ÕýÔÚµÇÂ¼...");
		RxVolley.post(SdkConstant.QF_LOGIN_OAUTH, httpParamsBuild.getHttpParams(), httpCallbackDecode);
	}
}
