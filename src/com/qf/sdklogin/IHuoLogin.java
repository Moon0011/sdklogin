package com.qf.sdklogin;

import com.qf.sdklogin.bean.ThirdLoginRequestBean;

import android.content.Context;
import android.content.Intent;
 
public abstract class IHuoLogin {
    public static final int LOGIN_DEF = 1;
    public static final int LOGIN_QQ = 2;
    public static final int LOGIN_WX = 3;
    public static final int LOGIN_XLWB = 4;

    public abstract void loginByThird(Context context, int accountType, IHuoLoginListener iHuoLoginListener);

    /**
     * 初始化，在设置参数前需初始化
     *
     * @param context
     */
    public abstract void initShareSdk(Context context);

    public abstract void initQQ(Context context, String appId, String appKey);

    public abstract void initWx(Context context, String appId, String appSecret);

    public abstract void initXinNan(String appKey, String appSecret, String redirectUrl);

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void logout(int from);

    public interface IHuoLoginListener {
        int CODE_SUCCESS = 1;
        int CODE_CANCEL = 0;
        int CODE_FAIL = -1;

        void onLoginResult(int code, String msg, ThirdLoginRequestBean thirdLoginRequestBean);
    }
}
