package com.weibo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkUtil {
	public final static int NONE = 0; // ÎÞÍø
	public final static int WIFI = 1; // Wi-Fi
	public final static int MOBILE = 2; // 3G,GPRS

	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		State state = connManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return MOBILE;
		}

		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return WIFI;
		}
		return NONE;
	}
}
