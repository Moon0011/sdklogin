package com.qf.sdklogin.http;

import java.util.ArrayList;
import java.util.Random;

import com.kymjs.rxvolley.client.HttpParams;
import com.kymjs.rxvolley.toolbox.HttpParamsEntry;
import com.qf.sdklogin.SdkConstant;
import com.qf.sdklogin.util.AuthCodeUtil;
import com.qf.sdklogin.util.RSAUtils;

import android.content.Context;
import android.util.Log;

public class HttpParamsBuild {

	private static final String TAG = HttpParamsBuild.class.getSimpleName();
	private static String randChDict = "qwertyuiopasdfghjklzxcvbnm123456789QWERTYUIOPASDFGHJKLZXCVBNM";
	private String jsonParam = "";
	private String authkey;
	private final ArrayList<HttpParamsEntry> mHeaders = new ArrayList<>(4);
	private HttpParams httpParams;
	private Context mContext;

	public HttpParamsBuild(String jsonParam, Context context) {
		this.jsonParam = jsonParam;
		this.mContext = context;
		encodeData();
	}

	private void encodeData() {
		Long servertimeinterval = mContext.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
				.getLong("servertimeinterval", 0);
		String clientid = mContext.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("clientid",
				"");
		String clientkey = mContext.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS).getString("clientkey",
				"");
		String rsapublickey = mContext.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
				.getString("rsapublickey", "");
		Log.e("sdklogin", "rsapublickey= " + rsapublickey);
		httpParams = new HttpParams();
		String randCh = getRandCh(16);
		// 生成key
		// client_id 与 时间戳 与 rand16 使用下划线(_)连接，得到 rsakey
		long time = System.currentTimeMillis() + servertimeinterval;
		StringBuffer keyBuffer = new StringBuffer(clientid).append("_").append(time).append("_").append(randCh);
		String key = null;
		try {
			key = new String(RSAUtils.encryptByPublicKey(keyBuffer.toString().getBytes(), rsapublickey), "utf-8");
			// 生成key
			httpParams.put("key", key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 生成加密数据
		// 6、2中的rand16与client_key组成对称加密参数 authkey ([client_key]_rand16)
		// 7、将requestdata与 authkey 对称加密并 URLencoding 得到请求参数 `data`
		StringBuffer dataKeyBuffer = new StringBuffer(clientkey).append(randCh);
		Log.e("sdklogin", "=====clientkey=== " + clientkey + " ,randCh =" + randCh);
		this.authkey = dataKeyBuffer.toString();
		String data = AuthCodeUtil.authcodeEncode(jsonParam, authkey);
		httpParams.put("data", data);
		for (HttpParamsEntry httpParamsEntry : mHeaders) {
			httpParams.putHeaders(httpParamsEntry.k, httpParamsEntry.v);
		}
	}

	public HttpParams getHttpParams() {
		return httpParams;
	}

	public String getAuthkey() {
		return authkey;
	}

	/**
	 * 随机从randChDict字典里获取length长度的字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandCh(int length) {
		int dictLength = randChDict.length();
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(randChDict.charAt(random.nextInt(dictLength)));
		}
		return buffer.toString();
	}
}
