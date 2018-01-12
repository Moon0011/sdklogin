package com.qf.sdklogin.http;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.kymjs.rxvolley.client.HttpCallback;
import com.qf.sdklogin.SdkConstant;
import com.qf.sdklogin.util.AuthCodeUtil;
import com.qf.sdklogin.util.RSAUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public abstract class HttpCallbackDecode<E> extends HttpCallback {
	private static final String TAG = HttpCallbackDecode.class.getSimpleName();
	public static final String CODE_RSA_KEY_ERROR = "1001";// 1001 请求KEY错误
															// rsakey 解密错误
	public static final String CODE_SESSION_ERROR = "1002";// 1002 登陆已过期, 重新登录
															// session过期
															// session过期
															// 返回信息CODE
	private boolean showLoading = false;
	private boolean loadingCancel = true;
	private boolean showTs = false;
	private String loadMsg = "加载中，请稍后……";
	private Context activity;
	private String authkey;

	public HttpCallbackDecode(Context activity, String authkey) {
		this.activity = activity;
		this.authkey = authkey;
	}

	@Override
	public final void onSuccess(String t) {
		// Log.e("sdklogin", "onSuccess t= " + t);
		try {
			JSONObject object = new JSONObject(t);
			String data = object.optString("data");
			Integer code = object.optInt("code");
			String msg = object.optString("msg");
			if (code >= 400) {
				onFailure(code + "", msg);
				if (CODE_SESSION_ERROR.equals(code + "")) {// 登陆过期，需要重新登陆
					// HuosdkInnerManager.getInstance().initSdk(HuosdkInnerManager.getInstance().getContext(),
					// new OnInitSdkListener() {
					// @Override
					// public void initSuccess(String code, String msg) {
					// //账号过期，退出登陆
					// HuosdkInnerManager.getInstance().logoutExecute(OnLogoutListener.TYPE_TOKEN_INVALID);
					// }
					// @Override
					// public void initError(String code, String msg) {
					// T.s( HuosdkInnerManager.getInstance().getContext(),msg);
					// }
					// });
				}
				return;
			} else if (TextUtils.isEmpty(data) || "null".equals(data)) {// 数据是null的
				onDataSuccess(null);
				return;
			}
			String decodeAuthData = AuthCodeUtil.authcodeDecode(data, authkey);
			// 使用
			JSONObject jsonObject = new JSONObject(decodeAuthData);
			String sign = jsonObject.optString("sign");
			String responcedata = jsonObject.optString("responcedata");
			// 验证签名
			String rsapublickey = activity.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
					.getString("rsapublickey", "");
			boolean verify = RSAUtils.verify(responcedata.getBytes(), rsapublickey, sign);
			if (verify) {//
				// Log.e("sdklogin", "responcedata =" + responcedata);
				E dataObject = new Gson().fromJson(responcedata, getTClass());
				onDataSuccess(dataObject);
			} else {
				onFailure("-1", "数据认证失败，请稍后再试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			onFailure("-1", "服务器忙，请稍后再试");
		}
	}

	@Override
	public void onSuccess(Map<String, String> headers, byte[] t) {
		super.onSuccess(headers, t);
	}

	/**
	 * 数据解析成功
	 * 
	 * @param data
	 */
	public abstract void onDataSuccess(E data);

	@Override
	public final void onFailure(int errorNo, String strMsg, String completionInfo) {
		Log.d(TAG, "onFailure=" + completionInfo);
		// Toast.makeText(activity, "onFailure completionInfo =" +
		// completionInfo, Toast.LENGTH_SHORT).show();
		Toast.makeText(activity, " ,strMsg =" + strMsg, Toast.LENGTH_SHORT).show();
		onFailure("" + errorNo, "连接失败，请稍后重试！");
	}

	public void onFailure(String code, String msg) {
		Log.d(TAG, "onFailure=" + msg);
		if (showTs) {
			// T.s(activity,msg==null?"连接失败":msg);
		}

	}

	@Override
	public void onPreStart() {
		super.onPreStart();
		if (showLoading) {
			// DialogUtil.showDialog(activity,loadingCancel,loadMsg);
		}
	}

	public void onFinish() {
		super.onFinish();
		// if(DialogUtil.isShowing()){
		// try {
		// DialogUtil.dismissDialog();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	protected Class<E> getTClass() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		Type resultType = type.getActualTypeArguments()[0];
		if (resultType instanceof Class) {
			return (Class<E>) resultType;
		} else {
			// 处理集合
			try {
				Field field = resultType.getClass().getDeclaredField("rawTypeName");
				field.setAccessible(true);
				String rawTypeName = (String) field.get(resultType);
				return (Class<E>) Class.forName(rawTypeName);
			} catch (Exception e) {
				return (Class<E>) Collection.class;
			}
		}
	}

	public String getLoadMsg() {
		return loadMsg;
	}

	public void setLoadMsg(String loadMsg) {
		this.loadMsg = loadMsg;
	}

	public boolean isLoadingCancel() {
		return loadingCancel;
	}

	public void setLoadingCancel(boolean loadingCancel) {
		this.loadingCancel = loadingCancel;
	}

	public boolean isShowLoading() {
		return showLoading;
	}

	public void setShowLoading(boolean showLoading) {
		this.showLoading = showLoading;
	}

	public boolean isShowTs() {
		return showTs;
	}

	public void setShowTs(boolean showTs) {
		this.showTs = showTs;
	}
}
