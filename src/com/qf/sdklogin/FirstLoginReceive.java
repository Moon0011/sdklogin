package com.qf.sdklogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class FirstLoginReceive extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		if (arg1.getIntExtra("type", -1) == 1) {
			SharedPreferences.Editor editor = context.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
					.edit();
			editor.putBoolean("isQqFirstLogin", true);
			editor.commit();
		} else if (arg1.getIntExtra("type", -1) == 2) {
			SharedPreferences.Editor editor = context.getSharedPreferences("sdklogin", Context.MODE_MULTI_PROCESS)
					.edit();
			editor.putBoolean("isWXFirstLogin", true);
			editor.commit();
		}
	}
}
